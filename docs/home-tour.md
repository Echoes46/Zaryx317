# Current home tutorial

The first-login tour and the existing staff `::tut` replay use `content/tutorial/HomeTour.java`. Route landmarks are based on `etc/cfg/npc/spawns/home_area.json` and `etc/cfg/obj/global_objects.cfg`, with viewing tiles placed beside the services on plane 0. Start and finish use Configuration.START_LOCATION_X/Y (3093,3511).

| Area | Viewing tile | Landmark evidence |
|---|---|---|
| Daily rewards | 3092,3510 | NPC 7042 at 3090,3510 |
| Companions | 3106,3515 | Pet Collector 8208 at 3106,3517 |
| Reward chests | 3120,3516 | Chest group at 3118-3122,3514-3517 |
| Shops | 3107,3504 | Shopkeepers 1576/1577/1578 at 3110,3503-3505 |
| Slayer | 3104,3493 | Masters including 8623 at 3103,3491 |
| Skilling/deposits | 3122,3494 | Furnace 2030, anvils 2031 and deposit box 29104 |
| Prayer | 3108,3492 | Altar 409 at 3109,3491 |
| Upgrades | 3092,3483 | Machine 30943 at 3091,3482 |
| Spellbooks | 3086,3483 | Occult altar 31858 at 3085,3482 |
| Restoration | 3085,3492 | Pool 39651 at 3083,3492 |
| Trading Post | 3089,3502 | NPC 2897 ring at 3087-3090,3497-3500 |
| Outlast | 3077,3488 | Tournament portal 31622 at 3075,3487 |

First-time players continue to the existing account-mode and XP-rate selection. Skipping the tour still returns them to the home spawn before setup. Replays end without resetting rights, reopening mode selection, changing tutorial completion or granting items, and release the movement/tutorial lock. An explicitly requested tour is also available in test mode, rather than creating an empty replay dialogue.

Regression tests check location bounds, landmarks against the configured spawns, dialogue line limits, the full replay route, return home and lock cleanup. These do not render the game or verify cache collision/camera visibility. In-game acceptance: create a new account, follow the tour in fixed and resizable views, then check account setup; use the existing staff ::tut command to check replay and movement afterwards.

Server-only update: deploy the rebuilt server JAR and restart. No client or cache changes.
