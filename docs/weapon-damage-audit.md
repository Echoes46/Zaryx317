# Weapon damage and progression audit

## Scope

Reviewed the active server formulas, player hit dispatcher, weapon speeds, weapon styles, special registrations/implementations, powered attacks, and upgrade recipes. Inventory coverage: 1,076 weapon-slot entries in the existing style audit; 904 resolve to a combat style, while 172 resolve to unarmed/fallback (cosmetics, utility or unresolved entries). The latter are not certified as functional combat weapons. No removed AK/Black AR or Wraith items were reintroduced.

Automated coverage includes:
- Basic finite, nonnegative damage, valid accuracy and positive attack delay for all 904 combat-style entries. This is a formula sanity check, not proof of models, acquisition or every weapon's charge/animation mechanics.
- All 29 weapon-slot upgrade recipes: ordinary damage, accuracy and attack delay at player levels 75/99/120, extra bonuses 0/100/400, and target Magic 1/100/250/350. Standard target defence is 200. Tests use the current runtime formulas and item_stats.json, rather than only comparing displayed bonuses.
- 102 consecutive standard material tiers across 15 weapon families, plus Keris partisan -> corruption -> Demon X spear (104 comparisons), at three bonus levels.
- Powered staff tier checks, preservation of manual-cast modifiers, special damage/miss preservation, existing ranged NPC immunity exclusions, bow dispatcher/formula consistency and ranged tagging of crossbow extra hits.
- Existing weapon-style, ammo, special-weapon, set-effect and upgrade-stat tests remain part of the full suite.

## Fixes

| Weapons | Problem | Result |
|---|---|---|
| Noxious -> Tumeken -> Demon X staff (previous change) | Noxious base 60 versus upgraded bases 30 caused reversals with extra magic damage. | Noxious unchanged; exact upgraded powered attacks use bases 63/66 with shared -0.43 additive adjustment. Manual spells unchanged. |
| Twisted Bow -> Seren -> Demon X Bow, including enchanted Demon X | Only Twisted Bow had target-Magic scaling; dispatcher additionally multiplied its damage by 2.22 and 2.25. | Shared target scaling and a single 2.25 server multiplier in the formula. Upgrades retain a minimum 1.0 target-damage multiplier so low-Magic targets do not newly penalize them. Twisted Bow loses the duplicate 2.22 multiplier. |
| Seren / Demon X bow specials | Fixed 25-50 damage overwrote the combat roll, disregarding gear, accuracy and prior reductions. Demon X also had lower special modifiers. | Preserve calculated damage and misses; both use 1.5 accuracy and 2.5 damage modifiers with inherited target scaling. Demon X retains its cheaper 32.5% cost versus Seren's 50%. |
| Beckoning Bow | Fixed 130-400 special damage overwrote the combat roll. | Uses its existing 3.0 accuracy / 2.2 damage modifiers and normal gear scaling. Existing listed NPC immunities preserved. |
| Keris partisan / corruption / Demon X spear | Special overwrote damage with 100 regardless of equipment or accuracy. | Uses existing 1.5 accuracy / 2.0 damage modifiers. Demon X's Arbo-specific NPC effect remains. |
| Vesta's longsword | Special multiplier 0.20 produced 20% of normal damage. | 1.20 multiplier, a 20% bonus. |
| Statius's warhammer | Special multiplier 0.25 produced 25% of normal damage. | 1.25 multiplier, a 25% bonus; defence-drain behavior unchanged. |
| Ascension / Demon X crossbow | Extra special hits were tagged melee and awarded Attack XP. | Extra hits are ranged and award Ranged XP. Existing hit splitting/cost preserved. |

No additional ordinary damage/accuracy/speed reversal was found in the tested melee/crossbow recipe matrix. Scythe size-based hits and upgraded healing/damage procs, fang accuracy mechanics, inherited whip/godsword/claw specials and utility specials were reviewed; their intended distinct effects remain. Flat damage on Axe of Araphel is part of its debuff/damage-over-time special and was not converted to a burst-damage special.

## Balance and verification limits

Twisted Bow is deliberately weaker than its previous double-multiplied state. Uncapped custom specials can now hit below or above their old fixed rolls depending on gear, level, target reductions and accuracy. This is not a claim that each weapon beats every unrelated weapon, nor that every individual hit beats the previous tier. Attack speed, enemy weaknesses, accuracy, multihits, special cost and utility still matter. No live in-game playtest or exhaustive NPC/loadout simulation was performed. Formula tests with target Magic 350 outside the existing XERIC boundary still use the normal cap of 250; the boundary definitions were not changed.

## Deployment

Server-only source changes. Full offline test suite and jar build passed. Deploy build/libs/Zaryx-Server.jar (or rebuild on the VPS) and restart. No item stats, client JAR or cache update is required for this audit.
