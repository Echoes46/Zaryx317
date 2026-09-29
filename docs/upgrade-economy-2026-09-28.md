# Upgrade progression and certificate spending

## Result

Reviewed all 106 upgrade recipes against the new dedicated boss certificate rates.
Lowered their point fees while retaining base success rates, input destruction on
failure, and existing Fusionist/Fortune/donator protection. The intended progression
is now entry gear at hundreds of thousands of points per attempt, intermediate gear
at several million to tens of millions, and endgame steps at 50M–175M.

The previous table was too punitive for early players and repeated upgrade chains.
The following compares old/new fees using identical intended success odds, holding
Fortune at level 1 (+0.1 percentage points), without protection, donor benefits,
boosting crystals, or Fusionist. These are expected point costs, not guarantees;
they exclude the cost of replacing original base items. Actual Fortune growth makes
long-term costs lower. The former off-by-one success bug slightly improved old live
odds; the comparison isolates fee changes rather than reproducing that bug.

| Route | Old expected points | New expected points |
| --- | ---: | ---: |
| One first-tier Void piece | 3.16M | 0.63M |
| Fury upgrade | 3.62M | 0.72M |
| Ancient staff to sceptre | 84.60M | 8.46M |
| Abyssal tentacle through Bulky Whip | 66.76M | 18.38M |
| Full Ring of Wealth chain through Crate ring | 2.80B | 463.68M |
| Torva helm through Malevolent helm | 1.34B | 461.98M |
| Noxious staff through Demonx staff | 2.45B | 838.23M |
| Base scythe through Demonx scythe | 4.03B | 1.41B |

At fixed level 99 Fortune (12 percentage points of success and 10% failed-item
protection), the last three chains average about 334.05M, 572.40M and 855.71M
respectively. Fortune requirements remain as configured (zero); this is a materials
and currency progression rather than a hard skill-level gate.

For each upgrade, with fee C, success probability p, failed-item protection s and
expected prior-chain fees E, expected chain fees are (C + (1 - (1-p)*s)*E)/p.
The first upgrade uses E=0. The complete fee inventory follows below.

## Certificate income and spending

Certificates dissolve into `foundryPoints`, the same balance spent by the upgrade
table. Each successful certificate drop averages 13 certificates before doubling:

| Denomination | Base drop chance | Average points per eligible kill |
| --- | --- | ---: |
| 10k | 1/20 | 6,500 |
| 25k | 1/40 | 8,125 |
| 50k | 1/60 | 10,833.33 |
| 250k | 1/100 | 32,500 |
| 1M | 1/250 | 52,000 |

Only include denominations actually present on a boss. Mixed-table expectations
add together. Barrelchest has only 10k certificates, giving 6,500 points per normal
kill on average: roughly 97 kills' income for a first-tier Void piece and 111 for
Fury, excluding replacement items. This is a ratio of long-run expectations, not
an exact expected stopping time for a fresh account. Donor Slayer area's 25% loot
restriction multiplies those figures by four on ordinary kills. Other loot,
collection rewards, modifiers, trading and existing dissolve bonuses are excluded.

The 10M certificate is no longer awarded. Bloodthirsty Abomination and Jack-o-Kraken
use the 1M reward at 1/250, and Minotaur retains its existing 1M reward at 1/250.
Previously acquired 10M certificates remain redeemable for 10M points each.
Endgame point-income averages assume many kills.
No measured live kills/hour or player wealth data was available, so this audit does
not claim a particular number of hours to complete progression.

Other uses of the same points include the Nomad shop, prestige perks, trading-post
payments, NPC crafting, and guaranteed Eye of the Corruptor conversions. Those
prices are unchanged. For example, the guaranteed Demonx staff route still costs
500M plus 25 Eyes and a Shadow, versus a 175M attempt with destruction risk in the
upgrade table. The guaranteed route trades a higher fixed price and materials for
certainty. Existing collection-log rewards and non-certificate income are unchanged.

## Correctness fixes

- 1M certificates and legacy 10M certificates dissolve for their face value. They share the smaller
  certificates' exclusions from the Nomad Master multiplier, prestige dissolve bonus,
  and earned-dissolve achievement accounting. Existing Ironman/EliteCent boosts remain.
- Dissolving validates the per-item value before multiplying by the stack, verifies
  the full selected quantity is still held, and checks point-balance overflow.
- Successful upgrades cannot dissolve for more than 20% of their new fee. Lower
  existing values remain lower. This applies to already-owned copies too, since
  dissolve values are item-wide. Without this change, some previously high salvage
  values would exceed the cheaper upgrade fee. No balances or inventories are migrated.
- Actual success rolls now match the displayed percentage, including fractional
  Fortune bonuses. Previously an inclusive integer comparison added excess probability.
- Fusionist-preserved inputs cannot be refunded a second time on a protected failure.
- Upgrade charges and results settle together on the game thread after rechecking
  resources; cancellation/disconnect before settlement does not spend resources.
  Reward delivery uses the existing inventory/bank/drop fallback.

## Deployment and validation

Deploy the rebuilt server JAR with the certificate drop configs from the accompanying
package, then restart through the normal server procedure. No client change is
required for these server-supplied prices and rates. No live deployment was performed.

Tests cover certificate face values, exact success probabilities, all recipe fees
and salvage limits, chain costs and cycle detection, existing equipment progression,
failed-upgrade settlement, disconnect/revalidation behavior, drop rates and caps,
and Fortune benefits. The full suite ran 205 tests: 204 passed. The unchanged
`ForestGuardianBalanceTest.runtimeMagicDefenceMatchesPublishedWeakness` fails with
expected 18,126 versus actual 22,737, and reproduces in an isolated run. Its NPC
configuration and magic-defence code were not modified by this work. All upgrade,
redemption, settlement and certificate tests passed. The server JAR built successfully.

## Complete upgrade fee changes

| Recipe | Old fee | New fee | Base success |
| --- | ---: | ---: | ---: |
| KONAR | 10,000,000 | 2,500,000 | 70% |
| ABYSSAL_TENTACLE | 10,000,000 | 2,500,000 | 69% |
| BULKY | 35,000,000 | 10,000,000 | 74% |
| ARMADYL_GODSWORD | 15,000,000 | 5,000,000 | 59% |
| SARADOMIN_GODSWORD | 10,000,000 | 2,500,000 | 69% |
| ZAMORAK_GODSWORD | 10,000,000 | 2,500,000 | 69% |
| BANDOS_GODSWORD | 10,000,000 | 2,500,000 | 69% |
| Dragon_CLAWS | 15,000,000 | 5,000,000 | 59% |
| DRAGON_WARHAMMER | 15,000,000 | 5,000,000 | 69% |
| Ghrazi_Rapier | 50,000,000 | 15,000,000 | 59% |
| OSMUMTEN_FANG | 275,000,000 | 100,000,000 | 49% |
| OSMUMTEN_FANGOR | 500,000,000 | 175,000,000 | 39% |
| SCYTHE_OF_VITUR | 150,000,000 | 50,000,000 | 59% |
| HOLY_SCYTHE_OF_VITUR | 275,000,000 | 100,000,000 | 49% |
| SANGUINGE_SCYTHE | 500,000,000 | 175,000,000 | 39% |
| HEAVY_BALLISTA | 7,500,000 | 2,500,000 | 79% |
| DRAGON_HUNTER_CROSSBOW | 15,000,000 | 5,000,000 | 69% |
| ZARYTE_CROSSBOW | 175,000,000 | 60,000,000 | 49% |
| Ascension | 500,000,000 | 175,000,000 | 39% |
| Twisted_bow | 175,000,000 | 60,000,000 | 49% |
| SEREN | 500,000,000 | 175,000,000 | 39% |
| ANCIENT_STAFF | 50,000,000 | 5,000,000 | 59% |
| Sanguinesti_Staff | 150,000,000 | 50,000,000 | 49% |
| NOXIOUS_STAFF | 225,000,000 | 75,000,000 | 49% |
| Tumeken | 500,000,000 | 175,000,000 | 39% |
| VOID_MAGE_HELM | 2,500,000 | 500,000 | 79% |
| VOID_RANGER_HELM | 2,500,000 | 500,000 | 79% |
| VOID_MELEE_HELM | 2,500,000 | 500,000 | 79% |
| VOID_TOP | 2,500,000 | 500,000 | 79% |
| VOID_BOTTOM | 2,500,000 | 500,000 | 79% |
| VOID_KNIGHT_GLOVES | 2,500,000 | 500,000 | 79% |
| VOID_MAGE_HELM_I | 10,000,000 | 2,500,000 | 69% |
| VOID_RANGER_HELM_I | 10,000,000 | 2,500,000 | 69% |
| VOID_MELEE_HELM_I | 10,000,000 | 2,500,000 | 69% |
| ELITE_VOID_TOP | 10,000,000 | 2,500,000 | 69% |
| ELITE_VOID_ROBE | 10,000,000 | 2,500,000 | 69% |
| VOID_KNIGHT_GLOVES_I | 10,000,000 | 2,500,000 | 69% |
| ARMADYL_HELM | 50,000,000 | 15,000,000 | 59% |
| ARMADYL_BODY | 50,000,000 | 15,000,000 | 59% |
| ARMADYL_LEGS | 50,000,000 | 15,000,000 | 59% |
| BANDOS_BODY | 50,000,000 | 15,000,000 | 59% |
| BANDOS_TASSETS | 50,000,000 | 15,000,000 | 59% |
| BANDOS_BOOTS | 50,000,000 | 15,000,000 | 59% |
| ANCESTRAL_HAT | 50,000,000 | 15,000,000 | 59% |
| ANCESTRAL_ROBE_TOP | 50,000,000 | 15,000,000 | 59% |
| ANCESTRAL_ROBE_BOTTOM | 50,000,000 | 15,000,000 | 59% |
| PERNIX_HOOD | 150,000,000 | 50,000,000 | 49% |
| PERNIX_BODY | 150,000,000 | 50,000,000 | 49% |
| PERNIX_BOTTOMS | 150,000,000 | 50,000,000 | 49% |
| TORVA_HELM | 150,000,000 | 50,000,000 | 49% |
| TORVA_PLATE | 150,000,000 | 50,000,000 | 49% |
| TORVA_LEGS | 150,000,000 | 50,000,000 | 49% |
| VIRTUS_HELM | 150,000,000 | 50,000,000 | 49% |
| VIRTUS_PLATE | 150,000,000 | 50,000,000 | 49% |
| VIRTUS_LEGS | 150,000,000 | 50,000,000 | 49% |
| AZIRHELM | 350,000,000 | 125,000,000 | 49% |
| AZIRBODY | 350,000,000 | 125,000,000 | 49% |
| AZIRLEGS | 350,000,000 | 125,000,000 | 49% |
| FORCEHELM | 350,000,000 | 125,000,000 | 49% |
| FORCEBODY | 350,000,000 | 125,000,000 | 49% |
| FORCELEGS | 350,000,000 | 125,000,000 | 49% |
| REAPERHELM | 350,000,000 | 125,000,000 | 49% |
| REAPERBODY | 350,000,000 | 125,000,000 | 49% |
| REAPERLEGS | 350,000,000 | 125,000,000 | 49% |
| TORSO | 50,000,000 | 15,000,000 | 59% |
| MALEDICTION | 25,000,000 | 7,500,000 | 59% |
| ODIUM | 25,000,000 | 7,500,000 | 59% |
| Dinhs_Balwark | 75,000,000 | 25,000,000 | 49% |
| ELIDINIS_WARD | 90,000,000 | 30,000,000 | 49% |
| ELIDINIS_WARD_F | 275,000,000 | 100,000,000 | 39% |
| DEVOUT | 50,000,000 | 10,000,000 | 59% |
| ECHO | 100,000,000 | 35,000,000 | 49% |
| REALMBOOTS | 150,000,000 | 75,000,000 | 39% |
| AMULET_OF_FURY | 2,500,000 | 500,000 | 69% |
| Berserker_necklace | 2,500,000 | 500,000 | 69% |
| OCCULT_NECKLACE | 5,000,000 | 1,000,000 | 59% |
| AMULET_OF_TORTURE | 5,000,000 | 1,000,000 | 59% |
| NECKLACE_OF_ANGUISH | 5,000,000 | 1,000,000 | 59% |
| TORMENTED_BRACELET | 10,000,000 | 2,500,000 | 49% |
| SUFFERING | 10,000,000 | 2,500,000 | 59% |
| RING_OF_WEALTH_i | 1,000,000 | 250,000 | 59% |
| RING_OF_WEALTH_i_1 | 2,500,000 | 500,000 | 49% |
| RING_OF_WEALTH_i_2 | 5,000,000 | 1,000,000 | 39% |
| RING_OF_WEALTH_I_3 | 10,000,000 | 2,500,000 | 29% |
| RING_OF_WEALTH_I_4 | 25,000,000 | 5,000,000 | 19% |
| BOXRING | 750,000,000 | 75,000,000 | 60% |
| WARRIOR | 75,000,000 | 25,000,000 | 39% |
| ZERKER | 75,000,000 | 25,000,000 | 39% |
| SEERS | 75,000,000 | 25,000,000 | 39% |
| ARCHERS | 75,000,000 | 25,000,000 | 39% |
| BARROWS | 25,000,000 | 5,000,000 | 49% |
| WRAPPED | 90,000,000 | 30,000,000 | 39% |
| DEFENDER | 150,000,000 | 50,000,000 | 39% |
| ANGLERHAT | 2,500,000 | 500,000 | 59% |
| ANGLERTOP | 2,500,000 | 500,000 | 59% |
| ANGLERBOTTOM | 2,500,000 | 500,000 | 59% |
| ANGLERBOOTS | 2,500,000 | 500,000 | 59% |
| HAMMER | 7,500,000 | 1,000,000 | 29% |
| DRAGON_AXE | 5,000,000 | 1,500,000 | 49% |
| MININGHAT | 2,500,000 | 500,000 | 59% |
| MININGTOP | 2,500,000 | 500,000 | 59% |
| MININGBOTTOM | 2,500,000 | 500,000 | 59% |
| MININGBOOTS | 2,500,000 | 500,000 | 59% |
| DRAGON_PICKAXE | 5,000,000 | 1,500,000 | 49% |
| Dragon_harpoon | 5,000,000 | 1,500,000 | 49% |
| Greater_Skeleton | 99,500,000 | 30,000,000 | 55% |
