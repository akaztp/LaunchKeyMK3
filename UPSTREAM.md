# Upstream: DrivenByMoss

This project contains code from [DrivenByMoss](https://github.com/git-moss/DrivenByMoss) by Jürgen Moßgraber (LGPL-3.0).

- **Upstream commit**: `824f8740ad01d468e6053ff58cc70170d9ef6308` (2026-11-16, version 26.7.0). The Launchkey Mk3 code last changed upstream in `4bbb242b18f81d34dcf9aaa1886a36b4a2ce7844` (2026-10-26).
- **Required Bitwig API**: 21

## What was taken

| Upstream path | Here | Notes |
|---|---|---|
| `src/main/java/de/mossgrabers/framework` | same | Unchanged, except the patches below |
| `src/main/java/de/mossgrabers/bitwig/framework` | same | Unchanged |
| `src/main/java/de/mossgrabers/controller/novation/launchkey/maxi` | `src/main/java/pt/ztp/launchkey3` | Renamed package, modified (each file is marked "Modified by akaztp") |
| `src/main/java/de/mossgrabers/bitwig/controller/novation/launchkey/LaunchkeyMk3ControllerExtensionDefinition.java` | `src/main/java/pt/ztp/launchkey3/bitwig` | Renamed package, modified |
| `src/main/resources/devices` | same | Unchanged |
| `maven-local-repository/purejavahidapi` | same | Unchanged. The Launchkey doesn't use USB, but the framework needs it to compile |

No other controllers are included. The Launchkey **Mini** Mk3 (`controller/novation/launchkey/mini`) is not included either.

The line endings were converted from CRLF to LF.

## Local patches to the framework

Every patch is marked with a `Local patch (LaunchKeyMK3)` comment. They are the same as patches 1 and 2 of the sibling KompleteKontrolMK1 project.

1. `framework/view/ColorView.java`: casts to `AbstractParameterMode` instead of the Push controller's `DeviceLayerDetailsMode`, so the Push code isn't needed.
2. `framework/view/sequencer/AbstractDrumView.java`: uses the literal values of Maschine's `COLOR_GREEN` (30) and `COLOR_LIME_LO` (33), so the Maschine code isn't needed.

## Changes to the Launchkey Mk3 code

1. `LaunchkeyMk3ControllerDefinition.java`: a new extension ID and the name `Launchkey Mk3 (LK3)`, so this extension can be installed next to DrivenByMoss. The author and version shown in Bitwig come from this project.
2. `bitwig/LaunchkeyMk3ControllerExtensionDefinition.java`: the help link points to this project's README.

The changes in behaviour are listed under "Changes from DrivenByMoss" in [README.md](README.md).

## Updating to a newer DrivenByMoss

1. Clone DrivenByMoss at the new version.
2. Replace `src/main/java/de/mossgrabers/framework` and `src/main/java/de/mossgrabers/bitwig/framework` with the new versions (converted to LF), then re-apply the patches above. Search the new code for `de.mossgrabers.controller` to find any new dependencies on other controllers.
3. Diff the upstream `controller/novation/launchkey/maxi` folder between the old and new commits (`git diff <old> <new> -- src/main/java/de/mossgrabers/controller/novation/launchkey/maxi`), and port relevant changes into `pt/ztp/launchkey3`.
4. Check `pom.xml` (Bitwig API version, JNA version) against upstream, then build and run the tests.
5. Update the commit hashes above.
