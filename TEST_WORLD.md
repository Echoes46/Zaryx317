# On-demand World 2

Use the same Git repository for both worlds. World 1 uses port 43596; World 2 uses 43598 on zaryx.ddns.net. The updated client login World button switches between them and labels World 2 TEST WORLD. World 1 remains the default on client launch.

## First setup on the VPS

1. Install JDK 11 and set JAVA_HOME if it is outside the standard Java or Zaryx cache locations.
2. Run `Prepare Test Server.bat` from the server checkout. It builds separately in build-test-world and copies libraries and game data into worlds/test. Gradle dependencies must already be available locally for the offline build.
3. Run `Add Test Account.bat` while World 2 is stopped. Enter a name and a separate test password. Optionally grant Owner rights on TEST only.
4. Allow inbound TCP port 43598 in the VPS firewall/provider firewall so approved testers can connect.
5. Run `Start Test Server.bat` when testing is needed. Close it with Ctrl+C when finished. World 1 continues independently.
6. Distribute the rebuilt client JAR so testers can select World 2.

## Access and isolation

Only names in worlds/test/approved-testers.txt with a provisioned test account and correct password can log in. No automatic registration. Remove a name to block its next login; stop/restart World 2 to immediately disconnect an existing session. Do not copy production player saves into the test runtime. Account provisioning never overwrites an existing account.

World 2 uses its own working directory, saves, logs, embedded database and copied configuration. It does not load the live config.yaml. Voting, donations, hiscores and Discord are disabled, and a JDK 11 network guard prevents outbound connections as a backstop. Incoming game connections remain enabled. Ordinary gameplay uses PUBLIC rules, not accelerated debug rules. Test rewards stay in test saves.

## Updating and holiday testing

Stop World 2 and run Prepare Test Server.bat after source updates. It refreshes the test libraries and static cfg/mapdata, preserving test accounts, mutable refs and the test holiday/owner-lock properties. Live runtime files are not replaced by this build. Review other custom test cfg changes before preparing because those are refreshed from source.

Edit worlds/test/etc/cfg/holiday-events.properties to enable Halloween or Christmas, then start World 2. This does not enable events on World 1. See HOLIDAY_EVENTS.md for event details.

The worlds folder is excluded from Git. No second repository is needed. Do not deploy worlds/test as the live server directory.
