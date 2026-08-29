[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [int[]] $RegionIds,

    [int[]] $Planes = @(0),

    [string] $MapDataDirectory = (Join-Path $PSScriptRoot '..\etc\mapdata'),

    [string] $CacheDirectory,

    [string] $BackupDirectory,

    [switch] $Apply
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Read-Medium {
    param([byte[]] $Bytes, [int] $Offset)
    return (([int] $Bytes[$Offset] -shl 16) -bor
            ([int] $Bytes[$Offset + 1] -shl 8) -bor
            [int] $Bytes[$Offset + 2])
}

function Write-Medium {
    param([byte[]] $Bytes, [int] $Offset, [int] $Value)
    $Bytes[$Offset] = [byte] (($Value -shr 16) -band 0xff)
    $Bytes[$Offset + 1] = [byte] (($Value -shr 8) -band 0xff)
    $Bytes[$Offset + 2] = [byte] ($Value -band 0xff)
}

function Read-UShort {
    param([byte[]] $Bytes, [int] $Offset)
    return ([int] $Bytes[$Offset] -shl 8) -bor [int] $Bytes[$Offset + 1]
}

function Write-UShortToStream {
    param([System.IO.Stream] $Stream, [int] $Value)
    $Stream.WriteByte([byte] (($Value -shr 8) -band 0xff))
    $Stream.WriteByte([byte] ($Value -band 0xff))
}

function Test-ByteArraysEqual {
    param([byte[]] $Left, [byte[]] $Right)

    if ($Left.Length -ne $Right.Length) {
        return $false
    }
    for ($index = 0; $index -lt $Left.Length; $index++) {
        if ($Left[$index] -ne $Right[$index]) {
            return $false
        }
    }
    return $true
}

function Expand-Gzip {
    param([byte[]] $Compressed)

    $input = [System.IO.MemoryStream]::new($Compressed, $false)
    $gzip = [System.IO.Compression.GZipStream]::new(
        $input,
        [System.IO.Compression.CompressionMode]::Decompress
    )
    $output = [System.IO.MemoryStream]::new()
    try {
        $gzip.CopyTo($output)
        return ,$output.ToArray()
    } finally {
        $output.Dispose()
        $gzip.Dispose()
        $input.Dispose()
    }
}

function Compress-Gzip {
    param([byte[]] $Uncompressed)

    $output = [System.IO.MemoryStream]::new()
    $gzip = [System.IO.Compression.GZipStream]::new(
        $output,
        [System.IO.Compression.CompressionLevel]::Optimal,
        $true
    )
    try {
        $gzip.Write($Uncompressed, 0, $Uncompressed.Length)
        $gzip.Dispose()
        return ,$output.ToArray()
    } finally {
        $gzip.Dispose()
        $output.Dispose()
    }
}

function Patch-Landscape {
    param([byte[]] $Landscape, [System.Collections.Generic.HashSet[int]] $TargetPlanes)

    $output = [System.IO.MemoryStream]::new()
    $offset = 0
    $removed = @(0, 0, 0, 0)

    try {
        for ($plane = 0; $plane -lt 4; $plane++) {
            for ($localX = 0; $localX -lt 64; $localX++) {
                for ($localY = 0; $localY -lt 64; $localY++) {
                    while ($true) {
                        if ($offset + 1 -ge $Landscape.Length) {
                            throw "Landscape ended early at plane=$plane x=$localX y=$localY."
                        }

                        $opcode = Read-UShort $Landscape $offset
                        $offset += 2

                        if ($opcode -eq 0) {
                            Write-UShortToStream $output 0
                            break
                        }

                        if ($opcode -eq 1) {
                            if ($offset -ge $Landscape.Length) {
                                throw "Landscape height byte is missing at plane=$plane x=$localX y=$localY."
                            }
                            Write-UShortToStream $output 1
                            $output.WriteByte($Landscape[$offset])
                            $offset++
                            break
                        }

                        if ($opcode -le 49) {
                            if ($offset + 1 -ge $Landscape.Length) {
                                throw "Landscape overlay is incomplete at plane=$plane x=$localX y=$localY."
                            }
                            Write-UShortToStream $output $opcode
                            $output.WriteByte($Landscape[$offset])
                            $output.WriteByte($Landscape[$offset + 1])
                            $offset += 2
                            continue
                        }

                        if ($opcode -le 81 -and $TargetPlanes.Contains($plane)) {
                            $flags = $opcode - 49
                            if (($flags -band 1) -ne 0) {
                                $newFlags = $flags -band (-bnot 1)
                                $removed[$plane]++

                                # A zero tile flag is represented by no flag opcode at all.
                                if ($newFlags -ne 0) {
                                    Write-UShortToStream $output (49 + $newFlags)
                                }
                                continue
                            }
                        }

                        Write-UShortToStream $output $opcode
                    }
                }
            }
        }

        if ($offset -ne $Landscape.Length) {
            throw "Landscape decode consumed $offset of $($Landscape.Length) bytes."
        }

        return [pscustomobject]@{
            Bytes = $output.ToArray()
            RemovedByPlane = $removed
        }
    } finally {
        $output.Dispose()
    }
}

function Get-RegionMappings {
    param([string] $MapIndexPath, [int[]] $WantedRegions)

    [byte[]] $indexBytes = [System.IO.File]::ReadAllBytes($MapIndexPath)
    $entryCount = Read-UShort $indexBytes 0
    $mappings = @{}

    for ($entry = 0; $entry -lt $entryCount; $entry++) {
        $offset = 2 + ($entry * 6)
        $region = Read-UShort $indexBytes $offset
        if ($WantedRegions -contains $region) {
            $mappings[$region] = [pscustomobject]@{
                Region = $region
                Landscape = Read-UShort $indexBytes ($offset + 2)
                Objects = Read-UShort $indexBytes ($offset + 4)
            }
        }
    }

    foreach ($region in $WantedRegions) {
        if (-not $mappings.ContainsKey($region)) {
            throw "Region $region was not found in $MapIndexPath."
        }
    }

    return $mappings
}

function Read-CacheArchive {
    param([string] $Directory, [int] $ArchiveId)

    $indexPath = Join-Path $Directory 'main_file_cache.idx4'
    $dataPath = Join-Path $Directory 'main_file_cache.dat'
    $index = [System.IO.File]::Open($indexPath, 'Open', 'Read', 'ReadWrite')
    $data = [System.IO.File]::Open($dataPath, 'Open', 'Read', 'ReadWrite')

    try {
        [byte[]] $entry = New-Object byte[] 6
        $index.Position = $ArchiveId * 6
        if ($index.Read($entry, 0, 6) -ne 6) {
            throw "Cache archive $ArchiveId has no index entry."
        }

        $size = Read-Medium $entry 0
        $sector = Read-Medium $entry 3
        [byte[]] $archive = New-Object byte[] $size
        $written = 0
        $chunk = 0

        while ($written -lt $size) {
            if ($sector -le 0) {
                throw "Cache archive $ArchiveId ended before all bytes were read."
            }

            $data.Position = $sector * 520L
            [byte[]] $header = New-Object byte[] 8
            if ($data.Read($header, 0, 8) -ne 8) {
                throw "Cache archive $ArchiveId has an incomplete sector header."
            }

            $storedArchive = Read-UShort $header 0
            $storedChunk = Read-UShort $header 2
            $nextSector = Read-Medium $header 4
            $storedIndex = [int] $header[7]

            if ($storedArchive -ne $ArchiveId -or
                $storedChunk -ne $chunk -or
                $storedIndex -ne 5) {
                throw "Invalid cache sector for archive $ArchiveId chunk $chunk."
            }

            $length = [Math]::Min(512, $size - $written)
            if ($data.Read($archive, $written, $length) -ne $length) {
                throw "Cache archive $ArchiveId has an incomplete data sector."
            }

            $written += $length
            $sector = $nextSector
            $chunk++
        }

        return ,$archive
    } finally {
        $data.Dispose()
        $index.Dispose()
    }
}

function Write-CacheArchive {
    param([string] $Directory, [int] $ArchiveId, [byte[]] $Archive)

    $indexPath = Join-Path $Directory 'main_file_cache.idx4'
    $dataPath = Join-Path $Directory 'main_file_cache.dat'
    $index = [System.IO.File]::Open($indexPath, 'Open', 'ReadWrite', 'None')
    $data = [System.IO.File]::Open($dataPath, 'Open', 'ReadWrite', 'None')

    try {
        $firstSector = [int] [Math]::Ceiling($data.Length / 520.0)
        if ($firstSector -eq 0) {
            $firstSector = 1
        }

        $sectorCount = [int] [Math]::Ceiling($Archive.Length / 512.0)
        $written = 0

        for ($chunk = 0; $chunk -lt $sectorCount; $chunk++) {
            $sector = $firstSector + $chunk
            $nextSector = if ($chunk + 1 -lt $sectorCount) { $sector + 1 } else { 0 }
            [byte[]] $block = New-Object byte[] 520

            $block[0] = [byte] (($ArchiveId -shr 8) -band 0xff)
            $block[1] = [byte] ($ArchiveId -band 0xff)
            $block[2] = [byte] (($chunk -shr 8) -band 0xff)
            $block[3] = [byte] ($chunk -band 0xff)
            Write-Medium $block 4 $nextSector
            $block[7] = 5

            $length = [Math]::Min(512, $Archive.Length - $written)
            [Array]::Copy($Archive, $written, $block, 8, $length)
            $data.Position = $sector * 520L
            $data.Write($block, 0, $block.Length)
            $written += $length
        }

        [byte[]] $entry = New-Object byte[] 6
        Write-Medium $entry 0 $Archive.Length
        Write-Medium $entry 3 $firstSector
        $index.Position = $ArchiveId * 6L
        $index.Write($entry, 0, $entry.Length)
        $data.Flush($true)
        $index.Flush($true)
    } finally {
        $data.Dispose()
        $index.Dispose()
    }
}

$mapDataPath = [System.IO.Path]::GetFullPath($MapDataDirectory)
$mapIndexPath = Join-Path $mapDataPath 'map_index'
$index4Path = Join-Path $mapDataPath 'index4'

if (-not (Test-Path -LiteralPath $mapIndexPath -PathType Leaf)) {
    throw "Map index not found: $mapIndexPath"
}
if (-not (Test-Path -LiteralPath $index4Path -PathType Container)) {
    throw "Map archive directory not found: $index4Path"
}

$targetPlanes = [System.Collections.Generic.HashSet[int]]::new()
foreach ($plane in $Planes) {
    if ($plane -lt 0 -or $plane -gt 3) {
        throw "Invalid plane $plane. Expected 0 through 3."
    }
    $null = $targetPlanes.Add($plane)
}

$mappings = Get-RegionMappings $mapIndexPath $RegionIds
$patches = @()

foreach ($region in $RegionIds) {
    $mapping = $mappings[$region]
    $serverArchivePath = Join-Path $index4Path "$($mapping.Landscape).gz"
    if (-not (Test-Path -LiteralPath $serverArchivePath -PathType Leaf)) {
        throw "Landscape archive missing for region $region`: $serverArchivePath"
    }

    [byte[]] $originalCompressed = [System.IO.File]::ReadAllBytes($serverArchivePath)
    [byte[]] $originalLandscape = Expand-Gzip $originalCompressed
    $patched = Patch-Landscape $originalLandscape $targetPlanes
    [byte[]] $patchedCompressed = Compress-Gzip $patched.Bytes

    [byte[]] $originalCacheCompressed = $null
    [byte[]] $patchedCacheCompressed = $null
    $cacheRemovedByPlane = @(0, 0, 0, 0)

    if ($CacheDirectory) {
        $originalCacheCompressed = Read-CacheArchive $CacheDirectory $mapping.Landscape
        [byte[]] $originalCacheLandscape = Expand-Gzip $originalCacheCompressed
        $patchedCache = Patch-Landscape $originalCacheLandscape $targetPlanes
        $cacheRemovedByPlane = $patchedCache.RemovedByPlane

        if (-not (Test-ByteArraysEqual $patchedCache.Bytes $patched.Bytes)) {
            throw "Client archive $($mapping.Landscape) does not produce the same patched landscape as the server."
        }

        $patchedCacheCompressed = Compress-Gzip $patchedCache.Bytes
    }

    $patches += [pscustomobject]@{
        Region = $region
        Landscape = $mapping.Landscape
        Objects = $mapping.Objects
        OriginalCompressed = $originalCompressed
        PatchedCompressed = $patchedCompressed
        RemovedByPlane = $patched.RemovedByPlane
        OriginalCacheCompressed = $originalCacheCompressed
        PatchedCacheCompressed = $patchedCacheCompressed
        CacheRemovedByPlane = $cacheRemovedByPlane
        ServerArchivePath = $serverArchivePath
    }
}

$report = foreach ($patch in $patches) {
    [pscustomobject]@{
        Region = $patch.Region
        Landscape = $patch.Landscape
        Objects = $patch.Objects
        Plane0Removed = $patch.RemovedByPlane[0]
        Plane1Removed = $patch.RemovedByPlane[1]
        Plane2Removed = $patch.RemovedByPlane[2]
        Plane3Removed = $patch.RemovedByPlane[3]
        ClientPlane0Removed = $patch.CacheRemovedByPlane[0]
        OriginalBytes = $patch.OriginalCompressed.Length
        PatchedBytes = $patch.PatchedCompressed.Length
    }
}

if (-not $Apply) {
    $report
    Write-Host 'Dry run only. Pass -Apply to write the patch.' -ForegroundColor Yellow
    return
}

if (-not $BackupDirectory) {
    $timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
    $BackupDirectory = Join-Path (Split-Path -Parent $mapDataPath) "collision-backup-$timestamp"
}
$backupPath = [System.IO.Path]::GetFullPath($BackupDirectory)
$serverBackupPath = Join-Path $backupPath 'server-index4'
$cacheBackupPath = Join-Path $backupPath 'client-index4'
[System.IO.Directory]::CreateDirectory($serverBackupPath) | Out-Null

if ($CacheDirectory) {
    [System.IO.Directory]::CreateDirectory($cacheBackupPath) | Out-Null
    [System.IO.File]::Copy(
        (Join-Path $CacheDirectory 'main_file_cache.idx4'),
        (Join-Path $cacheBackupPath 'main_file_cache.idx4'),
        $false
    )
    $cacheDataLength = (Get-Item -LiteralPath (Join-Path $CacheDirectory 'main_file_cache.dat')).Length
    [System.IO.File]::WriteAllText(
        (Join-Path $cacheBackupPath 'main_file_cache.dat.length'),
        [string] $cacheDataLength
    )
}

foreach ($patch in $patches) {
    $serverBackup = Join-Path $serverBackupPath "$($patch.Landscape).gz"
    [System.IO.File]::WriteAllBytes($serverBackup, $patch.OriginalCompressed)
    [System.IO.File]::WriteAllBytes($patch.ServerArchivePath, $patch.PatchedCompressed)

    if ($CacheDirectory) {
        $clientBackup = Join-Path $cacheBackupPath "$($patch.Landscape).gz"
        [System.IO.File]::WriteAllBytes($clientBackup, $patch.OriginalCacheCompressed)
        Write-CacheArchive $CacheDirectory $patch.Landscape $patch.PatchedCacheCompressed
    }
}

$manifest = [pscustomobject]@{
    CreatedAt = (Get-Date).ToString('o')
    MapDataDirectory = $mapDataPath
    CacheDirectory = if ($CacheDirectory) { [System.IO.Path]::GetFullPath($CacheDirectory) } else { $null }
    Regions = $report
}
$manifest | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath (Join-Path $backupPath 'manifest.json') -Encoding UTF8

$report
Write-Host "Backup created at: $backupPath" -ForegroundColor Green
