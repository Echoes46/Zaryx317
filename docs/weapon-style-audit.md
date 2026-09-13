# Weapon attack-style audit — 2026-09-13

Scope: all 1,076 items with equipment slot 3 in `etc/cfg/item/item_stats.json`, existing weapon tables, poison aliases, and all 16 combat interface roots in `WeaponInterface`. This is a source/data and cached-interface audit, not a live attack test of every weapon.

## Corrections

- Added 342 explicit variant mappings in `WeaponStyleMappings`. Examples include bronze battleaxes, steel 2h swords, basic staffs, white weapons, degraded Barrows weapons, crystal weapon variants, AOE bows/staffs, and Wraith weapons. These are ID mappings; item names are not used to guess weapon styles at runtime.
- Moved ornamented dragon pickaxe 25376 from AXE to PICKAXE.
- Moved Tekko-kagi 33808 from WARHAMMER to CLAWS, consistent with its server definition describing a bladed claw weapon.
- Reject conflicting registrations instead of silently letting a later weapon family overwrite an earlier one.
- Clamp invalid attack-style indices when changing weapon families, including negative indices.
- The earlier starter-bow correction is already included in the audit's baseline, so it is additional to these 344 changed entries.

Existing ammunition, charged-state restrictions, special attacks, damage, and projectile code are not expanded by these style mappings. Depleted variants receiving a matching interface do not thereby gain charged attacks.

## Combat interface collision

Decoded the actual local cache's interface archive and walked all 16 combat roots. Cached descendants in the old companion range included 22845 through 22879 and affected stab/slash/crush swords, spears, whips, claws, thrown weapons, unarmed, and other panels. The companion journal and tracker now use 61200–61348 on both server and client. This range was unused in the cache and had no existing source-level widget registrations. Loading the new companion journal preserves every pre-existing cached widget.

Both server and client must be updated together for the relocated companion interface. No cache replacement is required.

## Remaining limitations

180 weapon-slot items retain the existing unarmed fallback. They are explicitly recorded in `weapon-style-audit.json`; they are not all certified as correct. Many are held cosmetics, quest props, flowers, banners, greegrees, fishing gear, and unfinished/depleted items. Do not describe this audit as making every item combat-complete.

Notable remaining weapon-specific work:

- Bulwarks 21015/28682 and Gregg's eastdoor 25604: need a dedicated interface and verified block-mode behavior, not a generic melee mapping.
- Salamanders 10146–10148 and swamp lizard 10149: need verified melee/ranged/magic selection and fuel handling.
- Custom firearms 33178–33182: require confirmation of their actual attack/ammunition implementation before choosing a style family.
- Dual macuahuitl 28997: dedicated style/attack behavior requires review.
- Novelty launchers, holy water, mud pie, and other special held items require their own behavior review if these are to be playable.
- `test wep` 35558 has no clear production weapon family.

The JSON audit contains every weapon-slot ID, item-stat name, previous style, resulting style, and disposition. A regression test checks this inventory against runtime resolution and requires newly added weapon-slot IDs to be reviewed.

Validation: server tests cover family/variant resolution, poisoned variants, weapon switching, and complete inventory coverage. The cache check confirms all original combat widgets survive companion-journal initialization. Live testing should include representative melee, ranged, staff, Barrows, and custom weapons before release.
