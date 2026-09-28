# Trading post and key investigation

## Trading post: confirmed persistence defects, fixed locally

An offer created for coins (995) retains its currency while in memory, including partial purchases. PlayerSave previously serialized only `offer.isNomad()`. Both coins and platinum (13204) became `false`; loading that field used the legacy constructor, which interprets false as platinum. Saving and reloading therefore changed a GP listing into a platinum listing without changing the numeric price.

Offers now serialize the actual currency ID and reload it explicitly. Legacy true/false records remain readable with their historical meanings. There is no reliable way to distinguish an already-corrupted GP listing from a legitimate old platinum listing in those records. Affected sellers should cancel and relist after the fix; do not bulk-convert old false records. No player saves were edited.

Two related issues were fixed: actual GP proceeds lacked a save/load field, and POSManager.init reset their restored balance to zero. `tpCoins` now persists GP separately from `tpPlat` and `tpNomad`. Sale history now carries the currency ID instead of a boolean, so GP sales are no longer labelled Plat. Previously lost proceeds cannot be reconstructed from a balance that was never saved.

## Tier 1 Slayer key: corrected locally

Item 28416 had the server name Shadow Crusade Key and description "To be used on the Shadow Crusade Chest." SlayerChest actually uses it as KEY1. The server definition now calls it Slayer Key (tier 1), with examine text "A tier 1 Slayer key." The client name was updated to match the other Slayer key tiers. Server examine value/tradability annotations remain intact.

## Warped key: investigated, behavior not changed

Item 3468 is called Warped Key by the client; the server still calls it Silver key black with a generic silver-key description. Its active use is clicking door object 32660 while carrying a key, not using it on a chest. The old raid-chest branch for object 30107 is commented out, including the former common/rare reward selection.

Decoded local server map region 12608 (object map file `etc/mapdata/index4/4642.gz`) contains door 32660 at both (3168, 4102, 0) and (3168, 4126, 0). Both are type 10, facing 0. The only active key handler in ObjectOptionOne always consumes one key and moves the player to (3169, 4104, 0), regardless of which door or which side the player clicked from. It does not implement leaving, open the door, or start/spawn an encounter. A player already at the destination can lose another key with no visible movement; an inside player with no remaining key is refused instead of being allowed to exit. This is a concrete defect consistent with the reported stuck behavior, though the player's exact live interaction was not reproduced.

The destination is south of the configured Revenant Maledictus spawn (NPC 11246 at 3168, 4119, 0). The NPC death handler disables respawn for this ID; the separate roaming MonsterHunt can spawn that ID elsewhere. The door does not check whether the local encounter is available before consuming a key.

Expected correction: recognize the specific entrance/exit and player side, charge a key only on entry, allow a free exit, and verify the destination against live collision. Decide separately whether this arena's Maledictus should respawn or be available only at certain times; do not change roaming-event behavior globally. Current evidence identifies the one-way handler, but does not prove a separate approach/pathfinding failure on the live server.

Warped keys currently roll from the global NPC loot handler when the player has wilderness level 60 or higher (the code tests wildLevel). The base random bound is 350, lowered to 300 with item 10557; Golpar and certain pets can affect quantity/collection.

## Verification

- Five targeted tests passed: repeated offer save/reload for all three currencies, legacy compatibility and invalid currencies, GP coffer restoration above the integer limit, sale history currencies, and the existing snapshot ordering test.
- Server JAR rebuilt; client Java compilation passed.
- Local source/config changes only. No deployment, live gameplay test, player-save migration, or warped-door behavior change was performed.


## Warped-door follow-up (2026-09-28)

Implemented both gates in `WarpedKeyDoors`. Entry consumes one key; leaving is free. The south gate crosses between (3169,4101,0) and (3169,4104,0). The north gate crosses between (3169,4129,0) and (3169,4125,0). Its exterior row at y=4128 contains blocking object 17514, so ClickObject routes to the clear approach at y=4129 instead of waiting for an unreachable tile next to the door. Pending movement ignores repeated clicks, and refused movement does not consume a key.

Daily-task audit found duplicate credit between shared boss rewards and NPCProcess for Nex, Sarachnis and Kalphite Queen, plus an additional duplicate loop in Kalphite Queen. These and Seldaeh now use the common NPC-based counter, which credits each NPC once per player and allows new respawn instances to count. Daily objectives use this counter except Nightmare, Chambers, Theatre of Blood and Inferno, whose separate completion hooks remain excluded from ordinary NPC credit.

Validation: TaskMasterTest covers all daily objectives, repeated reward callbacks, respawns and independent participant credit. WarpedKeyDoorsTest covers entry and exit at both gates, one-key charging, keyless exits, missing keys, repeated clicks and refused movement. Targeted tests and server JAR build passed. Live gameplay was not exercised.
