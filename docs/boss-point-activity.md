# Boss-point activity and donation scrolls

Donation scrolls 956, 6769, 2403, 2396, 786, 761, 607 and 608 are untradable. This applies to existing and newly earned scrolls, including purchased donation scrolls. Redemption is unchanged. Existing trading-post offers cannot be purchased; sellers can cancel them to reclaim their scrolls.

Boss kill and raid-completion points require gameplay input within the preceding fifteen minutes. Walk, NPC/object interaction, item use, equipment changes, gameplay buttons, dialogue and manual spell/special-attack inputs qualify. Auto-retaliation, server-side combat/following, pets, mouse movement/click telemetry, region loads, idle and keepalive packets do not qualify. Logging in alone grants no activity credit.

An inactive award is skipped, with one pause notification. A later eligible award announces resumption. No skipped points are accumulated. Buchu cannot override inactivity. Kill messages use the actual awarded amount. Existing balances and configured rates are unchanged. Jar exchanges and historical refunds are not combat awards and retain their existing behavior.

This is an unattended-play deterrent, not bot detection: scripted gameplay packets can still imitate input. Activity is session-only and uses a monotonic clock. Gameplay packets refresh activity before handling so a reward claimed through dialogue counts that interaction.

Deploy the rebuilt server with `etc/cfg/item/item_definitions.yaml` and restart. No client update is needed. Verify trade, trading-post offer cancellation, scroll redemption, and Corp pause/resume in World 2 before live rollout.
