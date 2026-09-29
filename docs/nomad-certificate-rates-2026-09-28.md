# Nomad certificate rates

Existing dedicated boss Nomad tables now use per-certificate base probabilities:

| Points | Item | Tier | Base chance |
| --- | --- | --- | --- |
| 10,000 | 691 | Uncommon | 1/20 |
| 25,000 | 692 | Uncommon | 1/40 |
| 50,000 | 693 | Uncommon | 1/60 |
| 250,000 | 696 | Rare | 1/100 |
| 1,000,000 | 33428 | Very rare | 1/250 |

Each configured certificate entry has `chance` (the 1/N denominator) and `rarity`.
These are per-item odds, not a shared table chance divided by the number of entries.
One uniform roll selects among disjoint reward intervals, preserving at most one
currency reward per kill. Quantities remain 1–25 and doubling remains capped at 25.
Loose point entries retain their previous odds and 1–2 quantity cap. Ordinary loot,
legacy tables without a dedicated Nomad section, and equipment-generated rewards
are unchanged. No new certificate denominations were added to any boss.

The 10M certificate is no longer awarded by any NPC. Bloodthirsty Abomination and
Jack-o-Kraken now award the 1M certificate at 1/250. Minotaur already had that 1M
reward, so its duplicate 10M entry was removed. Existing 10M certificates remain
redeemable for their face value so previously acquired items retain their value.

Certificate drops were removed completely from Dagannoth Supreme, Corporeal Beast,
Cerberus, The Nightmare, Nex, Experiment No. 2, Galvek, Queen Latsyrc, Avatar of
Creation, and Avatar of Destruction. Nex's separate loose-points reward remains.

The usual rarity bonus applies: at +100% displayed drop bonus, uncommon chance is
multiplied by 1.75, rare by 2, and very rare by 2.25. The viewer uses these same
probabilities, rounding the displayed denominator up to a whole number.

Barrelchest's normal spawn now gives 10k certificates at 1/20 before bonuses.
The donor Slayer area's existing 25% loot restriction remains: the earlier fix
uses probabilistic rounding rather than truncating all rolls to zero, giving
1/80 certificate odds there on ordinary kills, before bonuses. Other instances
outside that area's boundary do not receive this restriction.

Validation covers the complete runtime drop-table loader, configured rates and
tiers, Barrelchest's runtime lookup, deterministic probability coverage for mixed
certificate tables with and without bonuses, quantity caps, extra-roll suppression,
donor Slayer rounding, drop announcements, and teleport drop previews.

Deploy the rebuilt server JAR and the updated `etc/cfg/drops` files together, then
restart using the normal server procedure. Older JARs ignore the new per-item
settings. This change was built and tested locally; it has not been deployed.
