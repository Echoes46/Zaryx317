# Companion progression

## Player behavior

Each account earns a separate level for each companion type. Duplicate items share that type's progress. Cosmetic kitten, golem, rift guardian, Zulrah, Hydra, Vet'ion and Kalphite forms share their family's progress; Dark Core/Corporeal Critter do too. Kratos and Dark Kratos remain separate. Picking up, banking or trading an item does not transfer account-owned progress.

Levels run from 1 to 10. Total XP thresholds are 0, 500, 1,500, 3,500, 6,500, 11,000, 17,000, 25,000, 35,000 and 50,000. Perks improve at levels 3, 5, 7 and 10. Existing perks remain intact at level 1.

Positive hits against combat NPCs award 1–5 companion XP, based on damage. Actual eligible skilling actions award 3 XP. An account-wide three-second cooldown covers both, preventing AOE hits, multi-skill XP drops and pet swapping from multiplying awards. There is no idle, PvP, Wilderness or max-hit dummy training. Lamps and generic XP grants do not award companion XP. Skill-specific companions earn from their associated skill or PvM; general companions earn from all supported skill actions or PvM.

At the theoretical maximum rate, level 10 takes about 8.3 hours of PvM or 13.9 hours of skilling. Real action rates may be slower. Max-level companions keep their recovery rolls without accumulating more XP.

## New bonuses, additional to existing perks

| Tier | Level-1 XP bonus | Drop modifier | Main PvM hit bonus | Second wind |
|---|---:|---:|---:|---|
| Common | 1% | — | — | — |
| Uncommon | 2% | 1% | — | — |
| Rare | 3% | 2% | 1% | — |
| Elite / verified donor-exclusive | 4% | 3% | 2% | 5% chance to restore 1 HP and 1 prayer |

Every milestone adds one percentage point of XP bonus, 0.5 percentage points to applicable drop/damage bonuses, and one percentage point to applicable second-wind chance. Second wind rolls on eligible PvM actions at most once per three seconds; it neither exceeds normal caps nor removes an existing overheal/boost. New damage bonuses exclude PvP and Wilderness. XP bonuses use the standard rate-multiplied award path and do not modify direct XP grants.

The new bonuses require a summoned pet. Existing inventory-based perks continue to work as before. Drop modifiers are additive modifiers, not flat rare-drop probabilities. Existing quantities, prices and reward tables were not changed.

## Acquisition balancing

All 168 registered pet item IDs are explicitly covered by `resources/pet-progression.properties`: 7 common, 17 uncommon, 121 rare and 23 elite profiles. Cosmetic family variants are included in these item-ID counts.

The local direct donor shops contain no registered pet item IDs. Several premium-looking pets have normal drop, exchange, upgrade or reward-box routes. None is labeled donor-exclusive without evidence. A donor-exclusive tier is supported with elite benefits when such an exclusive source is verified. Eleven custom profiles have explicitly unverified routes and provisional tiers; the complete source evidence and balance decisions are in `pet-acquisition-balance.md` and `pet-acquisition-evidence.json`.

Boss/activity tiers also reflect encounter and activity difficulty. PetHandler's generic drop rate alone does not establish practical acquisition time. Source scans are evidence references, not an exhaustive proof that no other route exists.

## Saving and rollout

Progress is serialized under `companion-progress` in the existing atomic player-save snapshot. Accounts without that field start at level 1. Unknown/malformed entries are ignored individually; XP is clamped to 50,000 and duplicate cosmetic entries merge by maximum, never addition.

The existing scrollable `::pet` client panel displays the level, XP, tier, acquisition note, bonuses and next milestone. Reopen the command to refresh it. No new client or cache assets are required if the companion-abilities client is already installed.

Deploy the rebuilt server JAR with its normal runtime dependencies. The profile resource is packaged inside the JAR. Changes are local and have not been committed, pushed or deployed in this task. Existing player-claims edits were not included. Live gameplay/save recovery should be checked before rollout.

Validation: all 19 server tests passed on 2026-09-10. Tests cover independent levels, cosmetic family merging, cooldowns, level boundaries, corrupt/overflowed save data, all pet profiles, capped recovery, active-follower gating, and panel capacity for every pet at each milestone. The final server JAR contains the catalog resource and progression classes. No live login or gameplay session was run.
