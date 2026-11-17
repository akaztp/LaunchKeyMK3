# LaunchKey Mk3 (ztp) for Bitwig Studio

A Bitwig Studio controller extension for the **Novation Launchkey MK3 keyboards (25, 37, 49, 61 and 88 keys)**, based on the Launchkey Mk3 support in [DrivenByMoss](https://github.com/git-moss/DrivenByMoss).

> **Credits.** This project is a derivative of the Launchkey Mk3 driver in [DrivenByMoss](https://github.com/git-moss/DrivenByMoss) by **Jürgen Moßgraber** ([mossgrabers.de](https://www.mossgrabers.de)), licensed under the LGPL-3.0. All of the original Launchkey Mk3 functionality (session and drum pads, knobs, faders, transport, mixer, device parameters and browser) is his work. If this extension is useful to you, please consider [supporting DrivenByMoss](https://www.mossgrabers.de/Software/Bitwig/Bitwig.html).

## Features

Everything the DrivenByMoss Launchkey Mk3 driver does (see the Novation Launchkey Mk3 chapter of the [DrivenByMoss manual](https://github.com/git-moss/DrivenByMoss/blob/master/DrivenByMoss-Manual.pdf)), plus:

- **Drum pads in a new order**: in the Drum mode, the left and right halves of each row of pads are swapped, compared to DrivenByMoss. With the octave not shifted, the pads play these Drum Machine pads (MIDI note and Bitwig's name):

  | | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 |
  |---|---|---|---|---|---|---|---|---|
  | **Top row** | 48 C2 | 49 C#2 | 50 D2 | 51 D#2 | 44 G#1 | 45 A1 | 46 A#1 | 47 B1 |
  | **Bottom row** | 40 E1 | 41 F1 | 42 F#1 | 43 G1 | 36 C1 | 37 C#1 | 38 D1 | 39 D#1 |

  **Octave Up/Down** moves all pads by 16 notes (4 with Shift), keeping this order. The pad lights follow the order too.
- **The Stop/Solo/Mute button shows the bank of the drum pads**: in the Drum mode, it lights up in one colour for each of the 7 banks of 16 pads that **Octave Up/Down** step through, from the lowest notes to the highest:

  | Bank | 1 | 2 | 3 (default) | 4 | 5 | 6 | 7 |
  |---|---|---|---|---|---|---|---|
  | **First pad** | 4 E-2 | 20 G#-1 | 36 C1 | 52 E2 | 68 G#3 | 84 C5 | 100 E6 |
  | **Colour** | Red | Orange | Yellow | Green | Cyan | Magenta | Blue |

  After **Shift + Octave Up/Down**, which move the pads by 4 notes, the button shows the bank of the first pad. Holding the button still shows the step sequencer settings, as in DrivenByMoss (only when the selected track has a clip).
- **The drum pads keep their colours**: in the Drum mode, the pads show the colours of the Drum Machine on the selected track, as in DrivenByMoss. When you select a track without a Drum Machine, they keep the colours of the last one, instead of turning all yellow.
- **Choose the colour of the pads you play**: in the Drum mode, the pads you hit light up in white, green, red, blue, cyan, yellow or magenta, set in *Pads → Drum Pad Hit Colour*. Green by default, as in DrivenByMoss.
- **No blue selected pad**: in the Drum mode, the last pad you played no longer stays lit in blue, so all pads show their Drum Machine colours. Turn it back on with *Pads → Highlight Selected Drum Pad*.

## Changes from DrivenByMoss

- The extension has its own ID and is called **LaunchKey Mk3 (ztp)**, so it can be installed next to DrivenByMoss.
- **Drum pads on a track without a Drum Machine** show the colours of the last Drum Machine, instead of the dim yellow of empty pads. The colours are those of the last selected track with a Drum Machine, also if it was selected while another pad mode was active. They are remembered by note, so after **Octave Up/Down** the pads show the colours of the pads they play, if that page of the Drum Machine was seen. They don't follow changes to that Drum Machine, such as a pad's mute, while another track is selected.
- **The selected drum pad isn't lit in blue** by default. The last pad played is still selected, and in the Steps mode (the upper button right of the pads) the sequencer still edits its note. Set *Pads → Highlight Selected Drum Pad* to *On* to light it up in blue as in DrivenByMoss.
- **The colour of the played drum pads can be chosen**, instead of always green.
- **The drum pads are in another order**: DrivenByMoss plays C1–G1 on the bottom row and G#1–D#2 on the top row, left to right. Here the left and right halves of each row are swapped, so the bottom row plays E1–G1 then C1–D#1, and the top row C2–D#2 then G#1–B1. The order is set in `DrumPadLayout.java`.
- **The Stop/Solo/Mute button in the Drum mode** lights up in the colour of the bank of the pads, instead of dim grey. What it does when held is unchanged.

## Installation

1. Download `LaunchKeyMK3.bwextension` from the [Releases](https://github.com/akaztp/LaunchKeyMK3/releases) page (or [build it](#building-from-source)).
2. Copy it to `Documents\Bitwig Studio\Extensions` (macOS: `~/Documents/Bitwig Studio/Extensions`).
3. In Bitwig: **Settings → Controllers → Add Controller → Novation → LaunchKey Mk3 (ztp)**. Bitwig finds the keyboard's ports by itself. On Windows they are:
   - MIDI in 1: `MIDIIN2 (LKMK3 MIDI)` (the DAW port)
   - MIDI in 2: `LKMK3 MIDI` (the keys)
   - MIDI out: `MIDIOUT2 (LKMK3 MIDI)`

You can keep DrivenByMoss installed for your other controllers. **Remove DrivenByMoss's own "Launchkey Mk3" controller from the list**, because both would take over the keyboard's DAW port.

## Settings reference

All of these are in Bitwig under **Settings → Controllers → (this controller)**:

- **Pads**
  - *Highlight Selected Drum Pad*: in the Drum mode, light up the selected pad (the last one played) in blue. Off by default.
  - *Drum Pad Hit Colour*: in the Drum mode, the colour of the pads while you hold them: White, Green, Red, Blue, Cyan, Yellow or Magenta. Green by default. The Launchkey's palette has no pure cyan, so Cyan is its bright sky blue.
- The other DrivenByMoss Launchkey Mk3 settings for transport and workflow.

## Status

Tested on a Launchkey MK3 with Bitwig Studio: the new order of the drum pads, the bank colours of the Stop/Solo/Mute button, the drum pad colours of tracks without a Drum Machine, and the *Highlight Selected Drum Pad* and *Drum Pad Hit Colour* settings.

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
| `src/main/java/pt/ztp/launchkey3/view/DrumPadColorMemory.java` | New: the pad colours of the last Drum Machine |
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
