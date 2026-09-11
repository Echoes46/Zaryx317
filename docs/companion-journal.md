# Companion Journal

`::pet` inspects the summoned companion, or opens Browse when none is summoned.
`::pet kratos` searches names. Exact item-ID search and IDs beside names are
restricted to accounts explicitly holding the Server Owner rank (`STAFF_MANAGER`).
Opening the command resets filters. Browse lists registered companions once,
alphabetically, nine per page. Cosmetic forms remain separate entries with shared
family progression. Entries displayed as "Dwarf remains" are excluded from browsing
and searching; their items and saved progression are untouched.

- Browse: select a companion, including missing companions.
- Progress: saved XP, next level, milestone thresholds, current additional bonuses.
- Perks: existing abilities and their conditions.
- Compare: pin one companion, select another through Browse, then compare their
  saved levels, additional bonuses and base perks. Long comparisons have multiple
  scrollable pages; Previous/Next turns those pages.

Previous/Next changes pages in Browse and companions in Progress/Perks.
Ownership cycles All/Owned/Missing; role cycles All/Combat/Skilling/Utility.
Role categories overlap: Combat includes progression damage or existing combat
perks; Skilling means a specific skill affinity; Utility includes loot, recovery
or utility abilities. These filters describe benefits, not exclusive pet classes.

Owned counts the exact item in inventory, equipment, personal bank, looting bag,
or as an active follower. Other storage/shared banks are excluded. Saved XP never implies ownership and is never transferred.
Browsing does not summon pets, award XP or activate perks. State is account-local
and transient; existing progression persistence remains unchanged.

The Sources tab has been removed because its references do not consistently
provide actionable acquisition guidance. Its old button ID is reserved and ignored.
This update does not rebalance pets or change XP earning rules.

## Release

Requires both server and client updates. No cache assets or cache-version change.
Server: `build/libs/Zaryx-Server.jar` with existing runtime dependencies/resources.
Client: Windows `build/libs/Zaryx-Client.jar`, macOS `Zaryx-Client-mac.jar`, Linux
`Zaryx-Client-linux.jar`. The older `Zaryx-Client-all.jar` is not this release.
Do not distribute the journal server against the previous abilities-only client.

Validation: 25 server tests and 15 client tests passed, including catalog coverage,
ownership versus saved XP, search, role filters, comparison descriptions, source
metadata, closed-interface button guards and client widget wiring/bounds. The
Browse layout was also rendered offline with the actual cache fonts. Live-game
validation remains: search, filters, paging, pin/compare, scrolling and relog.
