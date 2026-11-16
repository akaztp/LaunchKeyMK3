// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.view;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.Views;
import pt.ztp.launchkey3.controller.LaunchkeyPadGrid;


class DrumPadLayoutTest
{
    @Test
    void eachDrumMachinePadIsPlayedByOnePad ()
    {
        final Set<Integer> pads = new HashSet<> ();
        for (int position = 0; position < DrumPadLayout.NUM_PADS; position++)
            pads.add (Integer.valueOf (DrumPadLayout.getPad (position)));
        assertEquals (DrumPadLayout.NUM_PADS, pads.size ());
        for (int pad = 0; pad < DrumPadLayout.NUM_PADS; pad++)
            assertTrue (pads.contains (Integer.valueOf (pad)), "pad " + pad);
    }


    @Test
    void theHalvesOfEachRowAreSwapped ()
    {
        assertArrayEquals (new int []
        {
            40,
            41,
            42,
            43,
            36,
            37,
            38,
            39
        }, this.getPlayedNotes (0), "bottom row, B1-B8");
        assertArrayEquals (new int []
        {
            48,
            49,
            50,
            51,
            44,
            45,
            46,
            47
        }, this.getPlayedNotes (8), "top row, T1-T8");
    }


    @Test
    void thePadsLightUpInTheColourOfTheDrumMachinePadTheyPlay ()
    {
        final int [] noteMap = createScales ().getDrumMatrix ();
        for (int position = 0; position < DrumPadLayout.NUM_PADS; position++)
        {
            final int launchkeyNote = LaunchkeyPadGrid.translateToController (Views.DRUM, 36 + position)[1];
            assertEquals (36 + DrumPadLayout.getPad (position), noteMap[launchkeyNote], "position " + position);
        }
    }


    @Test
    void theOctaveButtonsMoveAllPads ()
    {
        final Scales scales = createScales ();
        scales.incDrumOffset (16);
        final int [] noteMap = scales.getDrumMatrix ();
        final int bottomLeftNote = LaunchkeyPadGrid.translateToController (Views.DRUM, 36)[1];
        assertEquals (40 + 16, noteMap[bottomLeftNote]);
    }


    /**
     * Get the notes which Bitwig receives from the 8 pads of a row, when the octave is not
     * shifted.
     *
     * @param firstPosition 0 for the bottom row, 8 for the top row
     * @return The notes, left to right
     */
    private int [] getPlayedNotes (final int firstPosition)
    {
        final int [] noteMap = createScales ().getDrumMatrix ();
        final int [] notes = new int [8];
        for (int x = 0; x < 8; x++)
        {
            final int launchkeyNote = LaunchkeyPadGrid.translateToController (Views.DRUM, 36 + firstPosition + x)[1];
            notes[x] = noteMap[launchkeyNote];
        }
        return notes;
    }


    /**
     * Create the Scales as the controller setup does.
     *
     * @return The scales
     */
    private static Scales createScales ()
    {
        final Scales scales = new Scales (new TwosComplementValueChanger (128, 1), 36, 52, 8, 2);
        scales.setDrumMatrix (DrumPadLayout.createDrumMatrix ());
        scales.setDrumNoteEnd (52);
        return scales;
    }
}
