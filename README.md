# LK3 — Launchkey MK3 for Bitwig Studio

A Bitwig Studio controller extension for the **Novation Launchkey MK3 keyboards (25, 37, 49, 61 and 88 keys)**, based on the Launchkey Mk3 support in [DrivenByMoss](https://github.com/git-moss/DrivenByMoss).

> **Credits.** This project is a derivative of the Launchkey Mk3 driver in [DrivenByMoss](https://github.com/git-moss/DrivenByMoss) by **Jürgen Moßgraber** ([mossgrabers.de](https://www.mossgrabers.de)), licensed under the LGPL-3.0. All of the original Launchkey Mk3 functionality (session and drum pads, knobs, faders, transport, mixer, device parameters and browser) is his work. If this extension is useful to you, please consider [supporting DrivenByMoss](https://www.mossgrabers.de/Software/Bitwig/Bitwig.html).

## Features

For now, everything works as in DrivenByMoss's Launchkey Mk3 driver. See the Novation Launchkey Mk3 chapter of the [DrivenByMoss manual](https://github.com/git-moss/DrivenByMoss/blob/master/DrivenByMoss-Manual.pdf).

## Changes from DrivenByMoss

- The extension has its own ID and is called **Launchkey Mk3 (LK3)**, so it can be installed next to DrivenByMoss.

## Installation

1. Download `LaunchKeyMK3.bwextension` from the [Releases](https://github.com/akaztp/LaunchKeyMK3/releases) page (or [build it](#building-from-source)).
2. Copy it to `Documents\Bitwig Studio\Extensions` (macOS: `~/Documents/Bitwig Studio/Extensions`).
3. In Bitwig: **Settings → Controllers → Add Controller → Novation → Launchkey Mk3 (LK3)**. Bitwig finds the keyboard's ports by itself. On Windows they are:
   - MIDI in 1: `MIDIIN2 (LKMK3 MIDI)` (the DAW port)
   - MIDI in 2: `LKMK3 MIDI` (the keys)
   - MIDI out: `MIDIOUT2 (LKMK3 MIDI)`

You can keep DrivenByMoss installed for your other controllers. **Remove DrivenByMoss's own "Launchkey Mk3" controller from the list**, because both would take over the keyboard's DAW port.

## Status

Not tested on a keyboard yet. The code is the same as in DrivenByMoss, apart from the name and ID.

## Building from source

Requires Windows PowerShell. The scripts put a portable JDK 21 (Eclipse Temurin) and Maven into the git-ignored `.tools` folder, and keep Maven's cache there too, so nothing is installed system-wide.

```powershell
powershell -ExecutionPolicy Bypass -File scripts\setup-tools.ps1   # once: downloads JDK + Maven into .tools
powershell -ExecutionPolicy Bypass -File scripts\build.ps1          # builds target\LaunchKeyMK3.bwextension
powershell -ExecutionPolicy Bypass -File scripts\build.ps1 -Install # builds and copies it to Bitwig's Extensions folder
```

The Bitwig Controller API reference ships with Bitwig Studio: `C:\Program Files\Bitwig Studio\resources\doc\control-surface\api\index.html`.

### Project layout

| Path | Contents |
|---|---|
| `src/main/java/pt/ztp/launchkey3` | The Launchkey Mk3 controller, derived from DrivenByMoss's `controller/novation/launchkey/maxi` |
| `src/main/java/pt/ztp/launchkey3/bitwig` | The extension definition that Bitwig loads |
| `src/main/java/de/mossgrabers/framework`, `.../bitwig/framework` | The DrivenByMoss framework, copied with two small patches (see [UPSTREAM.md](UPSTREAM.md)) |
| `maven-local-repository` | purejavahidapi, which is not on Maven Central (copied from DrivenByMoss) |

## License

[LGPL-3.0](LICENSE), like DrivenByMoss.

- Original code: © 2017–2026 Jürgen Moßgraber.
- Modifications and additions: © 2026 akaztp.
- Bundled libraries:
  - [purejavahidapi](https://github.com/nyholku/purejavahidapi): BSD-3-Clause.
  - [JNA](https://github.com/java-native-access/jna): Apache-2.0 / LGPL-2.1.

*Novation, Launchkey and Bitwig Studio are trademarks of their respective owners. This project is not affiliated with or endorsed by them.*
