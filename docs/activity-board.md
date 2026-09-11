# Activity Board

Open with `::taskmanager` or the existing Task Master entry. The board displays
the selected assignment's objective, progress, difficulty, time remaining, exact
rewards and travel guidance. Four tabs cover hourly combat, hourly skilling, a
daily combat challenge and a new weekly combat challenge.

## Assignment rules

The existing rolling one-hour and one-day schedules are retained. Weekly tasks
expire seven days after assignment, with three times the selected daily target.
Weekly rewards use the existing daily pool, with two rolls, without multiplying
reward quantities. Existing hourly/daily reward pools and their duplicate-entry
weighting are retained. Rewards are rolled once per assignment and then saved.

The difficulty button cycles the maximum difficulty for future assignments.
It does not replace current tasks or permit free reward rerolls. New accounts
default to Medium. Pools respect Wildyman mode flags and known skill requirements.
Easy combat options now include crabs and hill giants, with additional Medium
Mole/Barrelchest options and low-tier daily challenges. Existing misleading daily
ratings for Inferno, raids and several bosses have been raised for assignment
selection. Existing assignments keep their saved difficulty.

Completed, unclaimed rewards survive expiry and block replacement of that slot
until claimed. Expired incomplete tasks and expired claimed tasks are replaced.
Reset scrolls affect hourly/daily tasks only and cannot discard unclaimed rewards.
The weekly task survives reset scrolls. Pinning is saved per account, limited to
one task, and the client tracker is cleared on logout and hidden during interfaces.

## Claims and persistence

Claims require completion and inventory space for the entire reward bundle.
Partial additions or save failures restore inventory and leave the task claimable.
No task reward falls onto the ground. Repeat claims are rejected. Inventory and
the claimed flag are committed together through the atomic main player snapshot.

The new `activity-board` player-save field contains Base64-encoded task state,
fixed rewards, difficulty preference and pin state. Existing standalone Task Master
JSON is imported only when this field is absent. The old `weekly` flag means daily;
the new weekly challenge uses a separate flag. Legacy claim flags and progress are
preserved. Legacy advertised placeholder rewards are replaced with rolls from the
actual existing payout pools. Standalone task JSON is no longer written.

Combat progress now uses exact NPC matches and explicit Barrows/Dagannoth aliases.
Crazy archaeologist kills no longer count as Dagannoth kills. Activities with
dedicated completion hooks are excluded from generic NPC counting to prevent
double counts. Progress is capped and expired/claimed tasks cannot advance.
Woodcutting progress is attached to awarded logs or successful auto-burning,
instead of being incremented before an action has produced a reward.

## Travel and client

Known destinations are resolved from the existing teleport lists, including boss
and activity aliases. Travel opens and selects that destination; the player uses
the normal teleport button, preserving the usual teleport and entrance rules.
Unmapped activities open the relevant teleport category rather than inventing
coordinates. Detailed skilling routes are not yet mapped for every objective.

Both updated server and client are required because the old Task Master widget
IDs now serve the new board controls. No cache changes. Windows client output is
`build/libs/Zaryx-Client.jar`; macOS and Linux outputs use the corresponding
`-mac` and `-linux` filenames. Do not distribute the older `-all` JAR.

## Verification

Server tests cover independent cycle expiry, held rewards, saved reward/pin
round-trips, legacy migration, difficulty/mode pools, skill eligibility, capped
progress, NPC matching, duplicate claims, partial inventory rollback, failed-save
rollback and retry. Client tests cover widget bounds/button IDs and tracker cleanup.
The board was rendered offline using actual cache fonts to check layout.

In-game checks still required with the matching builds: opening from command and
menu, tab switching/scrolling, pinning, progress updates, travel selection, claiming
with a full inventory, relog persistence and reset-scroll behavior. Code-level
tests do not replace testing every skilling or boss encounter in a running server.
