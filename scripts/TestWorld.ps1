param([ValidateSet('Prepare','Start','Account')][string]$Action='Start')
$ErrorActionPreference='Stop'
$repo=(Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$runtime=Join-Path $repo 'worlds\test'
$candidates=@("$env:JAVA_HOME\bin\java.exe",'C:\Program Files\Java\jdk-11\bin\java.exe',"$env:USERPROFILE\.ZaryxCache_v1\java\jdk-11\bin\java.exe")
$java=$null
foreach($candidate in $candidates){
 if(Test-Path -LiteralPath $candidate){
  $old=$ErrorActionPreference; $ErrorActionPreference='Continue'
  $version=(& $candidate -version 2>&1 | Out-String)
  $ErrorActionPreference=$old
  if($version -match 'version "11\.'){ $java=$candidate; break }
 }
}
if(!$java){throw 'JDK 11 not found. Set JAVA_HOME to your JDK 11 installation.'}
$worlds=Join-Path $repo 'worlds'
foreach($directory in @($worlds,(Join-Path $repo 'etc'))){
 if(Test-Path $directory){
  if((Get-Item $directory).Attributes -band [IO.FileAttributes]::ReparsePoint){throw 'Linked runtime/data directories are not allowed.'}
  if(Get-ChildItem $directory -Recurse -Force | Where-Object { $_.Attributes -band [IO.FileAttributes]::ReparsePoint }){throw 'Linked runtime/data files are not allowed.'}
 }
}
if($Action -eq 'Prepare'){
 if((Test-Path $runtime) -and !(Test-Path "$runtime\.zaryx-test-world")){throw 'Unmarked test directory exists. Move it aside before preparing.'}
 $env:JAVA_HOME=Split-Path (Split-Path $java)
 Push-Location $repo
 try { & .\gradlew.bat -PtestWorldBuild=true test testWorldRuntime --offline --no-daemon; if($LASTEXITCODE){throw 'Build failed'} } finally {Pop-Location}
 $fresh=!(Test-Path $runtime)
 New-Item -ItemType Directory -Force $runtime | Out-Null
 $lock=[IO.File]::Open("$runtime\world.lock",'OpenOrCreate','ReadWrite','None')
 try {
  if($fresh){ Copy-Item "$repo\etc" $runtime -Recurse; Set-Content "$runtime\approved-testers.txt" '# Provision accounts with Add Test Account.bat' }
  else {
   foreach($folder in @('cfg','mapdata')){
    Get-ChildItem "$repo\etc\$folder" -File -Recurse | ForEach-Object {
     $relative=$_.FullName.Substring(("$repo\etc\").Length)
     $dest=Join-Path "$runtime\etc" $relative
     if($_.Name -notin @('holiday-events.properties','owner-economy-lock.properties')){
      New-Item -ItemType Directory -Force (Split-Path $dest) | Out-Null
      if (!(Test-Path $dest) -or (Get-Item $dest).Length -ne $_.Length -or (Get-Item $dest).LastWriteTimeUtc -ne $_.LastWriteTimeUtc) { Copy-Item -LiteralPath $_.FullName -Destination $dest -Force }
     }
    }
   }
  }
  foreach($folder in @('lib','logs','save_files','user_data')){New-Item -ItemType Directory -Force "$runtime\$folder" | Out-Null}
  Get-ChildItem "$runtime\lib" -Filter '*.jar' -File | Remove-Item
  Copy-Item "$repo\build-test-world\test-world-lib\*.jar" "$runtime\lib"
  Set-Content "$runtime\.zaryx-test-world" 'Isolated Zaryx World 2'
 } finally {$lock.Dispose()}
 Write-Host 'Prepared World 2. Add a test account, then run Start Test Server.bat.'
 exit
}
if(!(Test-Path "$runtime\.zaryx-test-world")){throw 'Run Prepare Test Server.bat first.'}
$main=if($Action -eq 'Account'){'io.zaryx.testing.CreateTester'}else{'io.zaryx.Server'}
Push-Location $runtime
try { & $java '-Xmx2048m' '-Dzaryx.testWorld=true' "-Dzaryx.testWorld.root=$runtime" '-cp' "$runtime\lib\*" $main; if($LASTEXITCODE){throw 'World 2 command failed.'} } finally {Pop-Location}
