# Upgrade balance audit

Removed dashboard routes to Wraith Staff (33433), Wraith Bow (33434), and undefined terminal scythe (39001). Existing owned items are not deleted. Wraith inventory/equipped models are empty in the installed cache and available cache archives.

Equipment changes preserve existing bonuses and attack speed along retained paths; equal combat upgrades gain a small stat benefit. Matchup-specific special effects can still differ. Wealth rings and miscellaneous upgrades use utility benefits.

| ID | Item | Changes |
|---|---|---|
| 13372 | Juan Gloves | str: 15 -> 27 |
| 20657 | Ring of suffering (ri) | dstab: 20 -> 21 |
| 24182 | Void knight gloves (l) | dstab: 6 -> 7 |
| 24183 | Void mage helm (l) | dstab: 6 -> 7 |
| 24184 | Void ranger helm (l) | dstab: 6 -> 7 |
| 24185 | Void melee helm (l) | dstab: 6 -> 7 |
| 25739 | Sanguine Scythe of vitur | str: 90 -> 105 |
| 26469 | Elite void top (or) | dstab: 45 -> 48 |
| 26714 | Armadyl helmet (or) | astab: -10 -> -5, aslash: -10 -> -5, acrush: -10 -> -5, amagic: -10 -> -5 |
| 26715 | Armadyl chestplate (or) | astab: -14 -> -7, aslash: -14 -> -7, acrush: -14 -> -7, amagic: -30 -> -15, dstab: 38 -> 56, dslash: 39 -> 48 |
| 26716 | Armadyl chainskirt (or) | astab: -12 -> -6, aslash: -12 -> -6, acrush: -12 -> -6, amagic: -20 -> -10 |
| 26718 | Bandos chestplate (or) | amagic: -30 -> -15, arange: -20 -> -10, dmagic: -12 -> -6 |
| 26719 | Bandos tassets (or) | amagic: -50 -> -21, arange: -20 -> -7, dmagic: -10 -> -4 |
| 27235 | Masori Mask (f) | amagic: -1 -> 0 |
| 27238 | Masori body (f) | amagic: -4 -> 0 |
| 27241 | Masori chaps (f) | amagic: -2 -> 0 |
| 27251 | Elidins' ward (f) | dstab: -40 -> 53, dslash: -40 -> 55, dcrush: -40 -> 73, dmagic: -40 -> 30, drange: -40 -> 52 |
| 27275 | Tumeken's Shadow | amagic: 212 -> 237, dmagic: 20 -> 50, mdmg: 42 -> 84 |
| 33153 | Malevolent helm | amagic: -9 -> -5, arange: -9 -> -5, dstab: 81 -> 136, dslash: 80 -> 120, dcrush: 82 -> 122, dmagic: -9 -> -2, drange: 77 -> 127, str: 9 -> 32 |
| 33154 | Malevolent cuirass | amagic: -28 -> -18, arange: -24 -> -14, dstab: 127 -> 197, dslash: 121 -> 181, dcrush: 127 -> 187, dmagic: -21 -> -11, drange: 152 -> 172, str: 7 -> 32 |
| 33155 | Malevolent greaves | amagic: -57 -> -47, arange: -40 -> -30, dstab: 97 -> 167, dslash: 88 -> 158, dcrush: 89 -> 159, drange: 112 -> 181, str: 5 -> 32 |
| 33151 | Sirenic mask | arange: 0 -> 70, dstab: 0 -> 80, dslash: 0 -> 80, dcrush: 0 -> 80, dmagic: 0 -> 80, drange: 0 -> 80, rstr: 0 -> 22, prayer: 0 -> 1 |
| 33150 | Sirenic hauberk | arange: 0 -> 70, dstab: 0 -> 80, dslash: 0 -> 80, dcrush: 0 -> 80, dmagic: 0 -> 80, drange: 0 -> 80, rstr: 0 -> 32, prayer: 0 -> 1 |
| 33152 | Sirenic chaps | arange: 0 -> 70, dstab: 0 -> 80, dslash: 0 -> 80, dcrush: 0 -> 80, dmagic: 0 -> 80, drange: 0 -> 80, rstr: 0 -> 22, prayer: 0 -> 1 |
| 33199 | Reaper Mask | amagic: 0 -> 70, dstab: 0 -> 79, dslash: 0 -> 75, dcrush: 0 -> 75, dmagic: 0 -> 75, drange: 0 -> 75, mdmg: 0 -> 83, prayer: 0 -> 1 |
| 33200 | Reaper Chest | amagic: 0 -> 70, dstab: 0 -> 79, dslash: 0 -> 75, dcrush: 0 -> 75, dmagic: 0 -> 75, drange: 0 -> 75, mdmg: 0 -> 83, prayer: 0 -> 1 |
| 33201 | Reaper Bottoms | amagic: 0 -> 70, dstab: 0 -> 79, dslash: 0 -> 75, dcrush: 0 -> 75, dmagic: 0 -> 75, drange: 0 -> 75, mdmg: 0 -> 83, prayer: 0 -> 1 |
| 33205 | Demonx Staff | amagic: 200 -> 249, mdmg: 58 -> 89 |
| 27624 | Ancient sceptre | str: 0 -> 50 |
| 27428 | Hood of ruin | amagic: 17 -> 66, dstab: 22 -> 75, dslash: 17 -> 75, dcrush: 20 -> 75, dmagic: 8 -> 75, mdmg: 8 -> 79, drange: 0 -> 75, prayer: 0 -> 1 |
| 27432 | Robe bottom of ruin | amagic: 55 -> 66, arange: -5 -> -1, dstab: 58 -> 79, dslash: 40 -> 75, dcrush: 60 -> 75, dmagic: 25 -> 75, mdmg: 9 -> 79, drange: 0 -> 75, prayer: 0 -> 1 |
| 27430 | Robe top of ruin | amagic: 36 -> 66, arange: -2 -> -1, dstab: 58 -> 79, dslash: 42 -> 75, dcrush: 65 -> 75, dmagic: 28 -> 75, mdmg: 9 -> 79, drange: 0 -> 75, prayer: 0 -> 1 |
| 28254 | Sanguine torva full helm | amagic: -45 -> -5, arange: -45 -> -5, dmagic: -42 -> -2 |
| 28256 | Sanguine torva platebody | amagic: -48 -> -18, arange: -44 -> -14, dmagic: -31 -> -11 |
| 28258 | Sanguine torva platelegs | amagic: -127 -> -47, arange: -110 -> -30, dmagic: -99 -> -9 |
| 27552 | Avernic defender 6 | dmagic: -64 -> -5, astab: 0 -> 30, aslash: 0 -> 29, acrush: 0 -> 28 |
| 27410 | Adventurer's boots (t3) | astab: 0 -> 15, aslash: 0 -> 15, acrush: 0 -> 15, amagic: 0 -> 15, arange: 0 -> 15, dstab: 0 -> 34, dslash: 0 -> 32, dcrush: 0 -> 32, dmagic: 0 -> 20, drange: 0 -> 24, str: 0 -> 12, rstr: 0 -> 12, mdmg: 0 -> 12, prayer: 0 -> 5 |
| 28945 | Echo boots | amagic: -3 -> 15, arange: -1 -> 15, dmagic: -3 -> 20, prayer: 1 -> 5, str: 3 -> 11, astab: 0 -> 15, aslash: 0 -> 15, acrush: 0 -> 15, rstr: 0 -> 11, mdmg: 0 -> 11 |
| 28307 | Ultor ring | dcrush: 0 -> 8 |
| 5060 | Konar's Axe (Blessed) | str: 42 -> 45 |
| 28069 | Cursed Torso | drange: 142 -> 150 |
| 28310 | Venator Ring | drange: 0 -> 8 |
| 28313 | Magus Ring | dmagic: 0 -> 12 |
| 28316 | Bellator Ring | dslash: 0 -> 8 |

## Combat behavior
- Bulky Whip retains Tentacle Whip's special; infernal axe/pickaxe/harpoon retain their dragon tool specials.
- Mixed Void, Torva/Sanguine/Malevolent and Sirenic/Masori pieces retain the completed lower-tier set effect. Full Malevolent retains Sanguine's combat effect.
- Fortified Masori set modifiers now exceed Sirenic: 55% in the reworked formula, 40% in the legacy max-hit formula (previously 35%/30%).
- Demon X crossbow retains Ascension's ranged modifiers and already shared its special and dragon-target bonus.
- Sanguine/Demonx scythes retain healing and add damage procs; fang upgrades retain their accuracy/special mechanics. Powered-staff and bow formula progression were subsequently corrected; see [weapon damage audit](weapon-damage-audit.md).

## Utility upgrades
Spirit Angler provides 0.825 versus 0.625 additive fishing XP per piece. Golden Prospector provides 0.35 versus 0.25 mining XP per piece and stronger mining modifiers. Infernal tools retain their resource-processing effects. Golden hammer is a utility consumable. Wealth ring tiers already increase drop modifiers through 7%, 9%, 11%, 12%, 15%, then 18% for Crate ring. Greater Skeleton has an additional Slayer XP proc; companion levels remain per-type and are not transferred by upgrading.

## Deployment
Deploy the server jar AND etc/cfg/item/item_stats.json, and distribute the new client jar for synchronized item hover stats. No cache replacement. Existing Wraith items remain untouched; this removes unavailable destinations from the dashboard, not player inventories. No universal DPS guarantee across every NPC, defence, special attack and loadout is implied.

## Retained recipe inventory

| Recipe | Input | Output | Check |
|---|---|---|---|
| KONAR | 5061 Konar's Axe | 5060 Konar's Axe (Blessed) | Bonus floors + positive improvement |
| ABYSSAL_TENTACLE | 12006 Abyssal tentacle | 26484 Abyssal tentacle (or) | Bonus floors + positive improvement |
| BULKY | 26484 Abyssal tentacle (or) | 39006 Bulky Whip | Bonus floors + positive improvement |
| ARMADYL_GODSWORD | 11802 Armadyl godsword | 20368 Armadyl godsword (or) | Bonus floors + positive improvement |
| SARADOMIN_GODSWORD | 11806 Saradomin godsword | 20372 Saradomin godsword (or) | Bonus floors + positive improvement |
| ZAMORAK_GODSWORD | 11808 Zamorak godsword | 20374 Zamorak godsword (or) | Bonus floors + positive improvement |
| BANDOS_GODSWORD | 11804 Bandos godsword | 20370 Bandos godsword (or) | Bonus floors + positive improvement |
| Dragon_CLAWS | 20784 Dragon claws | 26708 Dragon claws (or) | Bonus floors + positive improvement |
| DRAGON_WARHAMMER | 13576 Dragon warhammer | 26710 Dragon warhammer (or) | Bonus floors + positive improvement |
| Ghrazi_Rapier | 22324 Ghrazi rapier | 25734 Holy Ghrazi rapier | Bonus floors + positive improvement |
| OSMUMTEN_FANG | 26219 Osmumten's fang | 27246 Osmumten's fang (or) | Bonus floors + positive improvement |
| OSMUMTEN_FANGOR | 27246 Osmumten's fang (or) | 33202 Demonx Sword | Bonus floors + positive improvement |
| SCYTHE_OF_VITUR | 22325 Scythe of vitur | 25736 Holy Scythe of vitur | Bonus floors + positive improvement |
| HOLY_SCYTHE_OF_VITUR | 25736 Holy Scythe of vitur | 25739 Sanguine Scythe of vitur | Bonus floors + positive improvement |
| SANGUINGE_SCYTHE | 25739 Sanguine Scythe of vitur | 33203 Demonx Scythe | Bonus floors + positive improvement |
| HEAVY_BALLISTA | 19481 Heavy ballista | 26712 Heavy ballista (or) | Bonus floors + positive improvement |
| DRAGON_HUNTER_CROSSBOW | 21012 Dragon hunter crossbow | 25916 Dragon hunter crossbow (t) | Bonus floors + positive improvement |
| ZARYTE_CROSSBOW | 26374 Zaryte Crossbow | 33206 Ascension crossbow | Bonus floors + positive improvement |
| Ascension | 33206 Ascension crossbow | 26269 Demon X crossbow | Bonus floors + positive improvement |
| Twisted_bow | 20997 Twisted bow | 33058 Seren godbow | Bonus floors + positive improvement |
| SEREN | 33058 Seren godbow | 33207 Demon X Bow | Bonus floors + positive improvement |
| ANCIENT_STAFF | 4675 Ancient staff | 27624 Ancient sceptre | Bonus floors + positive improvement |
| Sanguinesti_Staff | 22323 Sanguinesti staff | 25731 Holy Sanguinesti staff | Bonus floors + positive improvement |
| NOXIOUS_STAFF | 33149 Noxious staff | 27275 Tumeken's Shadow | Bonus floors + positive improvement |
| Tumeken | 27275 Tumeken's Shadow | 33205 Demonx Staff | Bonus floors + positive improvement |
| VOID_MAGE_HELM | 11663 Void mage helm | 24183 Void mage helm (l) | Bonus floors + positive improvement |
| VOID_RANGER_HELM | 11664 Void ranger helm | 24184 Void ranger helm (l) | Bonus floors + positive improvement |
| VOID_MELEE_HELM | 11665 Void melee helm | 24185 Void melee helm (l) | Bonus floors + positive improvement |
| VOID_TOP | 8839 Void knight top | 13072 Elite void top | Bonus floors + positive improvement |
| VOID_BOTTOM | 8840 Void knight robe | 13073 Elite void robe | Bonus floors + positive improvement |
| VOID_KNIGHT_GLOVES | 8842 Void knight gloves | 24182 Void knight gloves (l) | Bonus floors + positive improvement |
| VOID_MAGE_HELM_I | 24183 Void mage helm (l) | 26473 Void mage helm (or) | Bonus floors + positive improvement |
| VOID_RANGER_HELM_I | 24184 Void ranger helm (l) | 26475 Void ranger helm (or) | Bonus floors + positive improvement |
| VOID_MELEE_HELM_I | 24185 Void melee helm (l) | 26477 Void melee helm (or) | Bonus floors + positive improvement |
| ELITE_VOID_TOP | 13072 Elite void top | 26469 Elite void top (or) | Bonus floors + positive improvement |
| ELITE_VOID_ROBE | 13073 Elite void robe | 26471 Elite void robe (or) | Bonus floors + positive improvement |
| VOID_KNIGHT_GLOVES_I | 24182 Void knight gloves (l) | 26467 Void knight gloves (or) | Bonus floors + positive improvement |
| ARMADYL_HELM | 11826 Armadyl helmet | 26714 Armadyl helmet (or) | Bonus floors + positive improvement |
| ARMADYL_BODY | 11828 Armadyl chestplate | 26715 Armadyl chestplate (or) | Bonus floors + positive improvement |
| ARMADYL_LEGS | 11830 Armadyl chainskirt | 26716 Armadyl chainskirt (or) | Bonus floors + positive improvement |
| BANDOS_BODY | 11832 Bandos chestplate | 26718 Bandos chestplate (or) | Bonus floors + positive improvement |
| BANDOS_TASSETS | 11834 Bandos tassets | 26719 Bandos tassets (or) | Bonus floors + positive improvement |
| BANDOS_BOOTS | 11836 Bandos boots | 26720 Bandos boots (or) | Bonus floors + positive improvement |
| ANCESTRAL_HAT | 21018 Ancestral hat | 24664 Twisted Ancestral hat | Bonus floors + positive improvement |
| ANCESTRAL_ROBE_TOP | 21021 Ancestral robe top | 24666 Twisted Ancestral robe top | Bonus floors + positive improvement |
| ANCESTRAL_ROBE_BOTTOM | 21024 Ancestral robe bottom | 24668 Twisted Ancestral robe bottom | Bonus floors + positive improvement |
| PERNIX_HOOD | 33144 Pernix cowl | 33151 Sirenic mask | Bonus floors + positive improvement |
| PERNIX_BODY | 33145 Pernix body | 33150 Sirenic hauberk | Bonus floors + positive improvement |
| PERNIX_BOTTOMS | 33146 Pernis chaps | 33152 Sirenic chaps | Bonus floors + positive improvement |
| TORVA_HELM | 26382 Torva full helm | 28254 Sanguine torva full helm | Bonus floors + positive improvement |
| TORVA_PLATE | 26384 Torva platebody | 28256 Sanguine torva platebody | Bonus floors + positive improvement |
| TORVA_LEGS | 26386 Torva platelegs | 28258 Sanguine torva platelegs | Bonus floors + positive improvement |
| VIRTUS_HELM | 33141 Virtus Mask | 27428 Hood of ruin | Bonus floors + positive improvement |
| VIRTUS_PLATE | 33142 Virtus robe top | 27430 Robe top of ruin | Bonus floors + positive improvement |
| VIRTUS_LEGS | 33143 Virtus robe legs | 27432 Robe bottom of ruin | Bonus floors + positive improvement |
| AZIRHELM | 33151 Sirenic mask | 27235 Masori Mask (f) | Bonus floors + positive improvement |
| AZIRBODY | 33150 Sirenic hauberk | 27238 Masori body (f) | Bonus floors + positive improvement |
| AZIRLEGS | 33152 Sirenic chaps | 27241 Masori chaps (f) | Bonus floors + positive improvement |
| FORCEHELM | 28254 Sanguine torva full helm | 33153 Malevolent helm | Bonus floors + positive improvement |
| FORCEBODY | 28256 Sanguine torva platebody | 33154 Malevolent cuirass | Bonus floors + positive improvement |
| FORCELEGS | 28258 Sanguine torva platelegs | 33155 Malevolent greaves | Bonus floors + positive improvement |
| REAPERHELM | 27428 Hood of ruin | 33199 Reaper Mask | Bonus floors + positive improvement |
| REAPERBODY | 27430 Robe top of ruin | 33200 Reaper Chest | Bonus floors + positive improvement |
| REAPERLEGS | 27432 Robe bottom of ruin | 33201 Reaper Bottoms | Bonus floors + positive improvement |
| TORSO | 28067 Fighter torso (or) | 28069 Cursed Torso | Bonus floors + positive improvement |
| MALEDICTION | 11924 Malediction ward | 12806 Malediction ward (or) | Bonus floors + positive improvement |
| ODIUM | 11926 Odium ward | 12807 Odium ward (or) | Bonus floors + positive improvement |
| Dinhs_Balwark | 21015 Dinh's bulwark | 28682 Dinh's bulwark | Bonus floors + positive improvement |
| ELIDINIS_WARD | 25985 Elidins' ward | 27251 Elidins' ward (f) | Bonus floors + positive improvement |
| ELIDINIS_WARD_F | 27251 Elidins' ward (f) | 27253 Elidins' ward (or) | Bonus floors + positive improvement |
| DEVOUT | 12598 Holy sandals | 22954 Devout boots | Bonus floors + positive improvement |
| ECHO | 22954 Devout boots | 28945 Echo boots | Bonus floors + positive improvement |
| REALMBOOTS | 28945 Echo boots | 27410 Adventurer's boots (t3) | Bonus floors + positive improvement |
| AMULET_OF_FURY | 6585 Amulet of fury | 12436 Amulet of fury (or) | Bonus floors + positive improvement |
| Berserker_necklace | 11128 Berserker necklace | 23240 Berserker necklace (or) | Bonus floors + positive improvement |
| OCCULT_NECKLACE | 12002 Occult necklace | 19720 Occult necklace (or) | Bonus floors + positive improvement |
| AMULET_OF_TORTURE | 19553 Amulet of torture | 20366 Amulet of torture (or) | Bonus floors + positive improvement |
| NECKLACE_OF_ANGUISH | 19547 Necklace of anguish | 22249 Necklace of anguish (or) | Bonus floors + positive improvement |
| TORMENTED_BRACELET | 19544 Tormented bracelet | 23444 Tormented bracelet (or) | Bonus floors + positive improvement |
| SUFFERING | 19710 Ring of suffering (i) | 20657 Ring of suffering (ri) | Bonus floors + positive improvement |
| RING_OF_WEALTH_i | 12785 Ring of wealth (i) | 20790 Ring of wealth (i1) | Utility / skill benefit |
| RING_OF_WEALTH_i_1 | 20790 Ring of wealth (i1) | 20789 Ring of wealth (i2) | Utility / skill benefit |
| RING_OF_WEALTH_i_2 | 20789 Ring of wealth (i2) | 20788 Ring of wealth (i3) | Utility / skill benefit |
| RING_OF_WEALTH_I_3 | 20788 Ring of wealth (i3) | 20787 Ring of wealth (i4) | Utility / skill benefit |
| RING_OF_WEALTH_I_4 | 20787 Ring of wealth (i4) | 20786 Ring of wealth (i5) | Utility / skill benefit |
| BOXRING | 20786 Ring of wealth (i5) | 26939 Crate ring | Utility / skill benefit |
| WARRIOR | 11772 Warrior ring (i) | 28316 Bellator Ring | Bonus floors + positive improvement |
| ZERKER | 11773 Berserker ring (i) | 28307 Ultor ring | Bonus floors + positive improvement |
| SEERS | 11770 Seers ring (i) | 28313 Magus Ring | Bonus floors + positive improvement |
| ARCHERS | 11771 Archers ring (i) | 28310 Venator Ring | Bonus floors + positive improvement |
| BARROWS | 7462 Barrows gloves | 27112 Barrows gloves (wrapped) | Bonus floors + positive improvement |
| WRAPPED | 27112 Barrows gloves (wrapped) | 13372 Juan Gloves | Bonus floors + positive improvement |
| DEFENDER | 22322 Avernic defender | 27552 Avernic defender 6 | Bonus floors + positive improvement |
| ANGLERHAT | 13258 Angler hat | 25592 Spirit Angler Headband | Utility / skill benefit |
| ANGLERTOP | 13259 Angler top | 25594 Spirit Angler Top | Utility / skill benefit |
| ANGLERBOTTOM | 13260 Angler waders | 25596 Spirit Angler Waders | Utility / skill benefit |
| ANGLERBOOTS | 13261 Angler boots | 25598 Spirit Angler Boots | Utility / skill benefit |
| HAMMER | 2347 Hammer | 2949 Golden hammer | Utility / skill benefit |
| DRAGON_AXE | 6739 Dragon axe | 13241 Infernal axe | Utility / skill benefit |
| MININGHAT | 12013 Prospector helmet | 25549 Golden Prospector Helmet | Utility / skill benefit |
| MININGTOP | 12014 Prospector jacket | 25551 Golden Prospector Jacket | Utility / skill benefit |
| MININGBOTTOM | 12015 Prospector legs | 25553 Golden Prospector Legs | Utility / skill benefit |
| MININGBOOTS | 12016 Prospector boots | 25555 Golden Prospector Boots | Utility / skill benefit |
| DRAGON_PICKAXE | 11920 Dragon pickaxe | 13243 Infernal pickaxe | Utility / skill benefit |
| Dragon_harpoon | 21028 Dragon harpoon | 21031 Infernal harpoon | Utility / skill benefit |
| Greater_Skeleton | 33070 Companion/tool | 27889 Companion/tool | Utility / skill benefit |
