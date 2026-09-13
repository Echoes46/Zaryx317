# Special weapon combat update

Update server and client together. New combat panels are built by client code; no cache replacement is required. Widget range 61400–61454 was checked against the local cache archive and existing source registrations. Existing combat widgets are preserved.

## Implemented

- Bulwarks (21015, 28682, 25604): two-handed; Pummel is an accurate crush attack, Block prevents attacks and reduces incoming NPC combat hits by 20% after eight ticks. Protection does not reduce player damage, poison, or environmental damage. Changing bulwarks restarts activation; leaving Block imposes an eight-tick attack delay. Ordinary attack speed is seven ticks.
- Salamanders (10146–10149): two-handed, adjacent attacks; Scorch uses melee/slash and Strength XP, Flare uses ranged damage and Ranged XP, Blaze uses magic damage and Magic XP. Scorch/Blaze take five ticks; Flare takes four. Every attack consumes one equipped tar, including Scorch. The last tar retains its strength bonus for that attack. Tar is not recovered as an arrow drop.
- Swamp lizard uses guam tar (10142), orange salamander marrentill tar (10143), red salamander tarromin tar (10144), black salamander harralander tar (10145). Missing or incorrect ammo blocks the attack. Ordinary click-cast spells remain ordinary spells and require their runes.
- Blaze uses a dedicated internal attack record, not a previously selected spell. Base magic damage scales with visible Magic level and the salamander's 56/59/77/92 magic strength value. It awards two base Magic XP per damage before server multipliers, with normal Hitpoints XP.
- Dual macuahuitl (28997): two-handed, four-tick attacks, Pound/Pummel/Spike/Block mace styles. Maximum damage is split into floor/ceiling halves. The second accuracy check only succeeds if the first succeeds; a first hit that rolls zero damage does not itself prevent the second hit. NPCs receive the second hit one tick later. Both hits pass through the existing melee hit adjustments, share the normal attack cooldown, and award XP for their combined damage.
- Attack validation now propagates weapon validation failures. NPC damage formula paths now read the actual target instead of a static Nightmare NPC reference.

The owner excluded firearm IDs 33178–33182 because they display as dwarf remains. This update does not add their combat support.

## Scope and verification

The follow-up adds Shield Bash and Blood Infusion, plus the Bloodrager set effect. Existing server damage multipliers remain in use. Defence-derived Pummel strength scaling remains separate from this special-attack/set-effect update.

## Special attacks and Blood Moon

- Shield Bash costs 50% energy, has 20% increased accuracy, and damages the primary target plus up to nine legal secondary targets within five tiles horizontally and vertically. Secondary targets require multi-combat, the same instance and height, and the normal attack eligibility checks. NPC-targeted attacks stay on NPCs; player-targeted attacks stay on players. Single combat hits only the primary target.
- Shield Bash drains 5% of the target's strongest offensive style on impact, including zero-damage hits. Melee uses average Attack/Strength and drains both; ranged and magic drain their respective level. Defence and Prayer are untouched. Changes apply to the individual NPC's combat definition, never a shared template. Ties prefer melee, then ranged.
- Blood Infusion costs 25% energy and requires the equipped helm 29028, chestplate 29022, tassets 29025, and dual macuahuitl 28997. It sacrifices floor(current HP / 4) once before rolling damage. The sacrifice bypasses armour reduction, cannot kill the player, and is not charged on invalid set/energy checks.
- Blood Infusion raises maximum damage by 25%, adds a minimum roll equal to one fifth of that increased maximum per half-hit, and removes the normal first-hit accuracy prerequisite for the second hit. An accuracy miss still deals zero.
- Bloodrager requires that same full equipped set and weapon. Each successful accuracy roll has a one-in-three chance to reduce the next attack interval by one tick. Blood Infusion guarantees this on a successful accuracy roll. At most one tick is removed per attack, even if both hits trigger. No proc occurs when both accuracy checks fail.
- Bulwarks use the existing cached special-energy bar; dual macuahuitl use the mace special bar. Both use the server's normal special toggle, energy updates, and duel restrictions. The bulwark must be in Pummel mode to attack.

Regression coverage includes button packet decoding and weapon guards, protection timing/switching, NPC versus player/poison damage, tar depletion in every mode, magic selection, weapon family mapping, and dual-hit accuracy/damage/queue timing. Client tests check layout bounds, selected-state config, child registrations, and preservation of existing widgets. Offline rendering uses the actual cache fonts.

In-game acceptance: equip each variant, switch all styles, verify tar decreases by one per attack and stops at zero; verify Block activation and switching delay; attack a normal NPC with macuahuitl and check both hits. Check both fixed and resizable modes with the updated client. Automated/offline checks do not replace this live acceptance pass.

Mechanic references: [Salamander modes](https://oldschool.runescape.wiki/w/Red_salamander), [dual macuahuitl](https://oldschool.runescape.wiki/w/Dual_macuahuitl), [bulwark](https://oldschoolrunescape.fandom.com/wiki/Dinh%27s_bulwark).
