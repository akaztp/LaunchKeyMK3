// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.view;

import java.util.Arrays;

import de.mossgrabers.framework.view.Views;
import pt.ztp.launchkey3.controller.LaunchkeyPadGrid;


/**
 * The order of the 16 pads in the Drum mode: which Drum Machine pad each pad plays.
 *
 * A position is the place of a pad: 0-7 are the bottom row and 8-15 the top row, left to right. A
 * Drum Machine pad is counted from the drum offset, which is note 36 (C1) when the octave is not
 * shifted. DrivenByMoss plays the Drum Machine pads 0-7 on the bottom row and 8-15 on the top row;
 * here the left and right halves of each row are swapped.
 *
 * @author akaztp
 */
public final class DrumPadLayout
{
    /** The number of pads. */
    public static final int     NUM_PADS        = 16;

    private static final int    FIRST_GRID_NOTE = 36;

    // @formatter:off
    private static final int [] PAD_AT_POSITION =
    {
        // Bottom row, B1-B8: E1-G1, C1-D#1
         4,  5,  6,  7,  0,  1,  2,  3,
        // Top row, T1-T8: C2-D#2, G#1-B1
        12, 13, 14, 15,  8,  9, 10, 11
    };
    // @formatter:on


    /**
     * Utility class.
     */
    private DrumPadLayout ()
    {
        // Intentionally empty
    }


    /**
     * Get the Drum Machine pad which a pad plays.
     *
     * @param position The position of the pad, 0-7 on the bottom row and 8-15 on the top row
     * @return The Drum Machine pad, 0-15 counted from the drum offset
     */
    public static int getPad (final int position)
    {
        return PAD_AT_POSITION[position];
    }


    /**
     * Get the note which the Launchkey sends for a pad in the Drum mode.
     *
     * @param position The position of the pad, 0-7 on the bottom row and 8-15 on the top row
     * @return The MIDI note
     */
    public static int getLaunchkeyNote (final int position)
    {
        return LaunchkeyPadGrid.translateToController (Views.DRUM, FIRST_GRID_NOTE + position)[1];
    }


    /**
     * Get the MIDI channel on which the Launchkey sends the pads in the Drum mode.
     *
     * @return The MIDI channel, 0-15
     */
    public static int getLaunchkeyChannel ()
    {
        return LaunchkeyPadGrid.translateToController (Views.DRUM, FIRST_GRID_NOTE)[0];
    }


    /**
     * Create the drum matrix for the Scales: for each note which the Launchkey sends in the Drum
     * mode (minus 36), the Drum Machine pad to play.
     *
     * @return The matrix with 64 entries, -1 for notes which the Launchkey doesn't send
     */
    public static int [] createDrumMatrix ()
    {
        final int [] matrix = new int [64];
        Arrays.fill (matrix, -1);
        for (int position = 0; position < NUM_PADS; position++)
            matrix[getLaunchkeyNote (position) - FIRST_GRID_NOTE] = PAD_AT_POSITION[position];
        return matrix;
    }
}
