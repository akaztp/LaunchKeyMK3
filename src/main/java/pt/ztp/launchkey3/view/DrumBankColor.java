// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.view;

import de.mossgrabers.framework.scale.Scales;
import pt.ztp.launchkey3.controller.LaunchkeyMk3ColorManager;


/**
 * The colour of each bank of 16 drum pads, which the Octave buttons step through in the Drum mode.
 * The banks start at the notes 4, 20, 36 (the default), 52, 68, 84 and 100. Their colours are red,
 * orange, yellow, green, cyan, magenta and blue, from the lowest notes to the highest.
 *
 * @author akaztp
 */
public final class DrumBankColor
{
    /** The number of notes in a bank, which the Octave buttons move by. */
    public static final int     BANK_SIZE   = 16;

    /** The number of banks. */
    public static final int     NUM_BANKS   = (Scales.DRUM_NOTE_UPPER - Scales.DRUM_NOTE_LOWER) / BANK_SIZE + 1;

    private static final int [] BANK_COLORS =
    {
        LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_HI,
        LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI,
        LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI,
        LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI,
        LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_SKY_HI,
        LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_HI,
        LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI
    };


    /**
     * Utility class.
     */
    private DrumBankColor ()
    {
        // Intentionally empty
    }


    /**
     * Get the bank of the drum pads. With Shift, the Octave buttons move the pads by 4 notes, so
     * the first pad can lie inside a bank; then the pads are counted to that bank.
     *
     * @param drumOffset The note of the first drum pad
     * @return The bank, 0 for the lowest notes to 6 for the highest
     */
    public static int getBank (final int drumOffset)
    {
        final int bank = Math.floorDiv (drumOffset - Scales.DRUM_NOTE_LOWER, BANK_SIZE);
        return Math.clamp (bank, 0, NUM_BANKS - 1);
    }


    /**
     * Get the colour of the bank of the drum pads.
     *
     * @param drumOffset The note of the first drum pad
     * @return The index of the colour in the Launchkey's palette
     */
    public static int getColor (final int drumOffset)
    {
        return BANK_COLORS[getBank (drumOffset)];
    }
}
