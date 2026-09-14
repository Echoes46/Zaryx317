# Slayer task audit

Audited 160 original entries across 10 masters; consolidated 15 duplicates into 145 unique assignments, merging accepted aliases. Compared names with NPC definitions and coordinates with both active spawn formats and existing boss entrances. This verifies destination areas/planes; live collision, pathfinding and encounter-entry testing remains necessary.

Corrections: Hellhound aliases and legacy compatibility; master-specific saved task restoration; missing Revenant imp/Hellhound aliases; numeric Cyclops alias removed; Dagannoth Mother removed from Kings task; Shaman moved to existing 1465,3685,0 entrance; Zilyana moved to outer door 2909,5265,0; Sarachnis/Nightmare/Dagannoth Kings aligned with existing teleport menu. Invalid teleport records are rejected before coordinate indexing.

Konar checkLocation currently returns true: location restrictions were already disabled. This update does not silently re-enable them. Wilderness Slayer still requires Wilderness kills.

Deploy server jar and etc/cfg/slayer_masters.json. No client or cache change.

| Master | Task | Destination | Evidence |
|---|---|---|---|
| 401 | cave crawler | 2788,9998,0 | Matching NPC 406 within 2 tiles, same plane |
| 401 | cave bug | 2779,10014,0 | Matching NPC 481 within 1 tiles, same plane |
| 401 | cockatrice | 2794,10035,0 | Matching NPC 419 within 3 tiles, same plane |
| 401 | rockslug | 2771,10031,0 | Matching NPC 421 within 2 tiles, same plane |
| 401 | hobgoblin | 2524,4775,0 | Matching NPC 2241 within 3 tiles, same plane |
| 401 | possessed pickaxe | 1640,10035,0 | Matching NPC 7268 within 2 tiles, same plane |
| 401 | magic axe | 1640,10035,0 | Matching NPC 7269 within 1 tiles, same plane |
| 401 | cow | 3259,3261,0 | Matching NPC 2791 within 2 tiles, same plane |
| 401 | rock crab | 2673,3710,0 | Matching NPC 100 within 4 tiles, same plane |
| 401 | crawling hand | 3440,3572,0 | Matching NPC 448 within 2 tiles, same plane |
| 401 | ghost | 2901,9849,0 | Matching NPC 85 within 2 tiles, same plane |
| 401 | hill giant | 2524,4775,0 | Matching NPC 2098 within 6 tiles, same plane |
| 401 | skeleton | 2884,9812,0 | Matching NPC 70 within 1 tiles, same plane |
| 401 | chaos druid | 3115,9929,0 | Matching NPC 520 within 2 tiles, same plane |
| 401 | moss giant | 3157,9903,0 | Matching NPC 2090 within 2 tiles, same plane |
| 402 | basilisk | 2746,10011,0 | Matching NPC 417 within 2 tiles, same plane |
| 402 | ice warrior | 3054,9578,0 | Matching NPC 2841 within 4 tiles, same plane |
| 402 | ice giant | 3054,9578,0 | Matching NPC 2085 within 6 tiles, same plane |
| 402 | baby blue dragon | 2908,9802,0 | Matching NPC 241 within 2 tiles, same plane |
| 402 | bloodveld | 3422,3572,1 | Matching NPC 484 within 2 tiles, same plane |
| 402 | cyclops | 1649,10002,0 | Matching NPC 2235 within 8 tiles, same plane |
| 402 | ankou | 1642,9996,0 | Matching NPC 2514 within 4 tiles, same plane |
| 402 | fire giant | 1632,10062,0 | Matching NPC 2084 within 2 tiles, same plane |
| 402 | infernal mage | 3435,3570,1 | Matching NPC 446 within 2 tiles, same plane |
| 402 | jelly | 1687,10000,0 | Matching NPC 7277 within 3 tiles, same plane |
| 402 | lesser demon | 2932,9808,0 | Matching NPC 2006 within 1 tiles, same plane |
| 402 | pyrefiend | 2761,9997,0 | Matching NPC 435 within 2 tiles, same plane |
| 402 | cave horror | 3747,9374,0 | Matching NPC 1047 within 7 tiles, same plane |
| 402 | dagannoth | 1913,4367,0 | Matching NPC 975 within 9 tiles, same plane |
| 402 | turoth | 2721,10007,0 | Matching NPC 427 within 4 tiles, same plane |
| 405 | blue dragon | 2908,9802,0 | Matching NPC 241 within 2 tiles, same plane |
| 405 | abyssal demon | 3420,3568,2 | Matching NPC 415 within 2 tiles, same plane |
| 405 | aviansie | 2865,5313,2 | Matching NPC 3169 within 1 tiles, same plane |
| 405 | black demon | 2707,9484,0 | Matching NPC 1432 within 3 tiles, same plane |
| 405 | black dragon | 2835,9821,0 | Matching NPC 259 within 3 tiles, same plane |
| 405 | bloodveld | 3422,3572,1 | Matching NPC 484 within 2 tiles, same plane |
| 405 | cave horror | 2800,10021,0 | Matching NPC 1047 within 2 tiles, same plane |
| 405 | cave kraken | 2288,10008,0 | Matching NPC 492 within 3 tiles, same plane |
| 405 | dagannoth | 1913,4367,0 | Matching NPC 975 within 9 tiles, same plane |
| 405 | dark beast | 1621,10060,0 | Matching NPC 4005 within 4 tiles, same plane |
| 405 | dust devil | 1715,10019,0 | Matching NPC 423 within 1 tiles, same plane |
| 405 | fire giant | 1632,10062,0 | Matching NPC 2084 within 2 tiles, same plane |
| 405 | gargoyle | 3442,3545,2 | Matching NPC 1543 within 3 tiles, same plane |
| 405 | greater demon | 2634,9503,2 | Matching NPC 2026 within 5 tiles, same plane |
| 405 | hellhound | 2429,9777,0 | Matching NPC 135 within 2 tiles, same plane |
| 405 | elf warrior | 2897,2725,0 | Matching NPC 3428 within 1 tiles, same plane |
| 405 | iron dragon | 2742,9431,0 | Matching NPC 273 within 1 tiles, same plane |
| 405 | kurask | 2699,9999,0 | Matching NPC 411 within 3 tiles, same plane |
| 405 | mithril dragon | 1745,5339,0 | Matching NPC 2919 within 4 tiles, same plane |
| 405 | nechryael | 3442,3565,2 | Matching NPC 8 within 1 tiles, same plane |
| 405 | smoke devil | 2404,9415,0 | Matching NPC 498 within 6 tiles, same plane |
| 405 | steel dragon | 2711,9432,0 | Matching NPC 274 within 3 tiles, same plane |
| 405 | twisted banshee | 1617,9997,0 | Matching NPC 7272 within 5 tiles, same plane |
| 405 | jelly | 1687,10000,0 | Matching NPC 7277 within 3 tiles, same plane |
| 6797 | sarachnis | 1842,9926,0 | Existing Sarachnis entrance; dynamically spawned encounter |
| 6797 | vorkath | 2272,4050,0 | Matching NPC 8026 within 15 tiles, same plane |
| 6797 | the nightmare | 3808,9755,1 | Existing Nightmare entrance; dynamically spawned encounter |
| 6797 | kree'arra | 2839,5293,2 | Matching NPC 3162 within 6 tiles, same plane |
| 6797 | corporeal beast | 2968,4384,2 | Matching NPC 319 within 15 tiles, same plane |
| 6797 | general graardor | 2860,5354,2 | Matching NPC 2215 within 10 tiles, same plane |
| 6797 | k'ril tsutsaroth | 2925,5335,2 | Matching NPC 3129 within 10 tiles, same plane |
| 6797 | commander zilyana | 2909,5265,0 | Matching NPC 2205 within 11 tiles, same plane |
| 6797 | venenatis | 3345,3754,0 | Matching NPC 6610 within 11 tiles, same plane |
| 6797 | thermonuclear smoke devil | 2404,9417,0 | Matching NPC 499 within 41 tiles, same plane (shared area entrance) |
| 6797 | king black dragon | 3005,3849,0 | Wilderness surface trapdoor/lever entrance |
| 6797 | barrelchest | 2903,3612,0 | Matching NPC 6342 within 7 tiles, same plane |
| 6797 | giant mole | 2996,3376,0 | Falador park dig entrance |
| 6797 | alchemical hydra | 1311,10225,0 | Matching NPC 8609 within 9 tiles, same plane |
| 6797 | zulrah | 2203,3056,0 | Zul-Andra boat entrance |
| 6797 | cerberus | 1310,1248,0 | Existing Cerberus entrance; dynamically spawned encounter |
| 6797 | lizardman shaman | 1465,3685,0 | Matching NPC 6767 within 29 tiles, same plane |
| 6797 | demonic gorilla | 2119,5660,0 | Matching NPC 7144 within 9 tiles, same plane |
| 6797 | kalphite queen | 3507,9494,0 | Matching NPC 963 within 25 tiles, same plane |
| 6797 | dagannoth kings | 1913,4367,0 | Existing dungeon entrance; object 10230 enters room at 2899,4449 |
| 6797 | abyssal sire | 3106,4832,0 | Matching NPC 5890 within 14 tiles, same plane |
| 7663 | lava dragon | 3202,3859,0 | Matching NPC 6593 within 12 tiles, same plane |
| 7663 | vet'ion | 3197,3791,0 | Matching NPC 6611 within 14 tiles, same plane |
| 7663 | barrow | 3120,4111,0 | Matching NPC 1675 within 7 tiles, same plane |
| 7663 | elder chaos druid | 3236,3630,0 | Matching NPC 6607 within 8 tiles, same plane |
| 7663 | callisto | 3325,3845,0 | Matching NPC 6503 within 13 tiles, same plane |
| 7663 | scorpia | 3233,3948,0 | Existing surface cave entrance |
| 7663 | venenatis | 3345,3754,0 | Matching NPC 6610 within 11 tiles, same plane |
| 7663 | chaos elemental | 3285,3922,0 | Matching NPC 2054 within 10 tiles, same plane |
| 7663 | chaos fanatic | 2978,3833,0 | Matching NPC 6619 within 15 tiles, same plane |
| 7663 | crazy archaeologist | 2980,3712,0 | Matching NPC 6618 within 7 tiles, same plane |
| 7663 | revenant | 3249,10142,0 | Matching NPC 7932 within 3 tiles, same plane |
| 7663 | green dragon | 2966,3615,0 | Matching NPC 264 within 7 tiles, same plane |
| 7663 | skeleton | 3103,3543,0 | Matching NPC 70 within 2 tiles, same plane |
| 7663 | lesser demon | 3288,3884,0 | Matching NPC 2006 within 2 tiles, same plane |
| 7663 | ice warrior | 2957,3895,0 | Matching NPC 2841 within 2 tiles, same plane |
| 7663 | ankou | 2968,3748,0 | Matching NPC 2514 within 2 tiles, same plane |
| 7663 | ent | 3231,3692,0 | Matching NPC 6594 within 4 tiles, same plane |
| 7663 | hellhound | 3178,3950,0 | Matching NPC 135 within 2 tiles, same plane |
| 7663 | mammoth | 3163,3620,0 | Matching NPC 6604 within 3 tiles, same plane |
| 7663 | dark warrior | 3011,3632,0 | Matching NPC 6606 within 2 tiles, same plane |
| 7663 | greater nechryael | 3406,10145,0 | Matching NPC 7278 within 67 tiles, same plane (shared area entrance) |
| 7663 | dust devil | 3406,10145,0 | Matching NPC 423 within 27 tiles, same plane |
| 7663 | jelly | 3406,10145,0 | Matching NPC 437 within 40 tiles, same plane |
| 7663 | abyssal demon | 3406,10145,0 | Matching NPC 415 within 61 tiles, same plane (shared area entrance) |
| 603 | cave kraken | 2288,10008,0 | Matching NPC 492 within 3 tiles, same plane |
| 8761 | crystalline rat | 3225,12439,0 | Matching NPC 9026 within 7 tiles, same plane |
| 8761 | crystalline spider | 3225,12439,0 | Matching NPC 9027 within 26 tiles, same plane |
| 8761 | crystalline bat | 3225,12439,0 | Matching NPC 9028 within 14 tiles, same plane |
| 8761 | crystalline unicorn | 3225,12439,0 | Matching NPC 9029 within 25 tiles, same plane |
| 8761 | crystalline scorpion | 3225,12439,0 | Matching NPC 9030 within 50 tiles, same plane (shared area entrance) |
| 8761 | crystalline wolf | 3225,12439,0 | Matching NPC 9031 within 66 tiles, same plane (shared area entrance) |
| 8761 | crystalline bear | 3225,12439,0 | Matching NPC 9032 within 14 tiles, same plane |
| 8761 | crystalline dragon | 3225,12439,0 | Matching NPC 9033 within 33 tiles, same plane |
| 8761 | crystalline dark beast | 3225,12439,0 | Matching NPC 9034 within 41 tiles, same plane (shared area entrance) |
| 5870 | cerberus | 1310,1248,0 | Existing Cerberus entrance; dynamically spawned encounter |
| 8605 | alchemical hydra | 1311,10225,0 | Matching NPC 8609 within 9 tiles, same plane |
| 8623 | abyssal demon | 3420,3568,2 | Matching NPC 415 within 2 tiles, same plane |
| 8623 | adamant dragon | 1567,5074,0 | Matching NPC 8030 within 14 tiles, same plane |
| 8623 | undead druid | 1800,9948,0 | Matching NPC 2145 within 6 tiles, same plane |
| 8623 | aviansie | 2865,5313,2 | Matching NPC 3169 within 1 tiles, same plane |
| 8623 | basilisk | 2746,10011,0 | Matching NPC 417 within 2 tiles, same plane |
| 8623 | black demon | 2707,9484,0 | Matching NPC 1432 within 3 tiles, same plane |
| 8623 | black dragon | 2835,9821,0 | Matching NPC 259 within 3 tiles, same plane |
| 8623 | bloodveld | 3422,3572,1 | Matching NPC 484 within 2 tiles, same plane |
| 8623 | blue dragon | 2908,9802,0 | Matching NPC 241 within 2 tiles, same plane |
| 8623 | brine rat | 2696,10121,0 | Matching NPC 4501 within 12 tiles, same plane |
| 8623 | bronze dragon | 1648,10098,0 | Matching NPC 270 within 2 tiles, same plane |
| 8623 | cave kraken | 2288,10008,0 | Matching NPC 492 within 3 tiles, same plane |
| 8623 | dagannoth | 1913,4367,0 | Matching NPC 975 within 9 tiles, same plane |
| 8623 | dark beast | 1621,10060,0 | Matching NPC 4005 within 4 tiles, same plane |
| 8623 | drake | 1312,10227,1 | Matching NPC 8612 within 4 tiles, same plane |
| 8623 | dust devil | 1715,10019,0 | Matching NPC 423 within 1 tiles, same plane |
| 8623 | fire giant | 1632,10062,0 | Matching NPC 2084 within 2 tiles, same plane |
| 8623 | gargoyle | 3442,3545,2 | Matching NPC 1543 within 3 tiles, same plane |
| 8623 | greater demon | 2634,9503,2 | Matching NPC 2026 within 5 tiles, same plane |
| 8623 | hellhound | 2429,9777,0 | Matching NPC 135 within 2 tiles, same plane |
| 8623 | hydra | 1311,10225,0 | Matching NPC 8609 within 9 tiles, same plane |
| 8623 | iron dragon | 2742,9431,0 | Matching NPC 273 within 1 tiles, same plane |
| 8623 | jelly | 1687,10000,0 | Matching NPC 7277 within 3 tiles, same plane |
| 8623 | kurask | 2699,9999,0 | Matching NPC 411 within 3 tiles, same plane |
| 8623 | lizardman shaman | 1465,3685,0 | Matching NPC 6767 within 29 tiles, same plane |
| 8623 | mithril dragon | 1745,5339,0 | Matching NPC 2919 within 4 tiles, same plane |
| 8623 | nechryael | 3442,3565,2 | Matching NPC 8 within 1 tiles, same plane |
| 8623 | red dragon | 1614,10074,0 | Matching NPC 7274 within 2 tiles, same plane |
| 8623 | rune dragon | 1567,5074,0 | Matching NPC 8031 within 15 tiles, same plane |
| 8623 | smoke devil | 2404,9415,0 | Matching NPC 498 within 6 tiles, same plane |
| 8623 | steel dragon | 2711,9432,0 | Matching NPC 274 within 3 tiles, same plane |
| 8623 | mountain troll | 2867,3594,0 | Matching NPC 942 within 1 tiles, same plane |
| 8623 | turoth | 2721,10007,0 | Matching NPC 427 within 4 tiles, same plane |
| 8623 | wyrm | 1281,10191,0 | Matching NPC 8610 within 7 tiles, same plane |

Duplicate consolidation preserves the first configured task range/XP and destination, and unions eligible NPC aliases. Existing remaining task counts are not reset. This also removes duplicate weighting from assignment selection.
