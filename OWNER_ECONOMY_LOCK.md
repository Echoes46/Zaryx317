# Restricted Owner account

`chasebanker` receives Owner (rights 3) at login. The restriction matches the login name, not the displayed name.

Edit `etc/cfg/owner-economy-lock.properties` and restart the server:

- `chasebanker.locked=true`: restrict the account (default).
- `chasebanker.locked=false`: restore unrestricted access.

Missing, unreadable, or invalid settings keep the account locked. There is no in-game unlock command.

While locked: no trading, player-item gifting, dropping items, selling to shop stock, trading-post purchases/listings, group-bank deposits/withdrawals, staking, flower poker, dice rolls, blackjack bets, or PvP. Its ground loot cannot be picked up by other players. Existing trading-post offers cannot be bought, but can be cancelled to recover the items.

Commands are restricted to a small allowlist: home, pet, pets, activity, tasks, commands, help, rules, players, staff, time, discord, website (where those commands exist). Other commands, including spawning, giving, rights changes, scripts and impersonation, are denied. This also restricts moderation commands while preserving the Owner crown. Normal personal inventory/bank and PvM remain available.

Deploy the rebuilt server JAR and the configuration file. No client or cache update is required. Verify with two test accounts before enabling the restricted Owner on the live server.
