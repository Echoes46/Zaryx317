# Equipment Guide

Open Equipment Stats and choose Equipment Guide, or use ::gearguide.
Both the updated server and client are required; no cache update is required.

- Combat: registered attack modes, XP skills, selected style and bulwark protection status.
- Special: registered special name, live energy and cost. Detailed requirements/effects for Shield Bash and Blood Infusion. Other specials explicitly indicate that secondary-effect descriptions are not yet available.
- Ammo: salamander tar, standard bows, crossbows, ballistas, thrown weapons and supported charged weapons. Compatibility reads existing combat checks without consuming resources.
- Sets: Blood Moon requirements and Bloodrager effect, plus matching Barrows set completion/missing pieces. This is not a complete custom-set encyclopedia.
- Refresh updates equipment and resource values. Values are a snapshot, not a continuously updating combat overlay.
- Numeric item suffixes are restricted to the server Owner rank, matching the companion journal.

Interface range: 61700-61783. Cache decoding verified the range is unused and existing combat widgets are preserved. Client layout was rendered using actual cache fonts at 512x334.

Validation: server and client Gradle test suites and jar builds passed. Added regressions for owner-only IDs, positive ammunition quantities, correct tar/crossbow/ballista checks, Blood Moon requirements, resource preservation and widget navigation. Live client/server interaction still needs an in-game check.
