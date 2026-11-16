// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.mossgrabers.framework.view.sequencer.AbstractDrumView;


class DrumPadHitColorTest
{
    private final LaunchkeyMk3ColorManager colorManager = new LaunchkeyMk3ColorManager ();


    @Test
    void theSettingListsTheColoursInOrder ()
    {
        assertArrayEquals (new String []
        {
            "White",
            "Green",
            "Red",
            "Blue",
            "Cyan",
            "Yellow",
            "Magenta"
        }, DrumPadHitColor.getLabels ());
    }


    @Test
    void theDefaultIsTheGreenOfDrivenByMoss ()
    {
        assertEquals (DrumPadHitColor.GREEN, DrumPadHitColor.DEFAULT);
        assertEquals (this.colorManager.getColorIndex (AbstractDrumView.COLOR_PAD_PLAY), this.colorManager.getColorIndex (DrumPadHitColor.DEFAULT.getColorId ()));
    }


    @Test
    void eachColourIsRegisteredWithItsOwnPaletteIndex ()
    {
        final Set<Integer> indices = new HashSet<> ();
        for (final DrumPadHitColor color: DrumPadHitColor.values ())
        {
            final int index = this.colorManager.getColorIndex (color.getColorId ());
            assertEquals (color.getPaletteIndex (), index, color.getLabel ());
            indices.add (Integer.valueOf (index));
        }
        assertEquals (DrumPadHitColor.values ().length, indices.size ());
    }


    @Test
    void coloursAreFoundByTheirNames ()
    {
        for (final DrumPadHitColor color: DrumPadHitColor.values ())
            assertEquals (color, DrumPadHitColor.fromLabel (color.getLabel ()));
        assertEquals (DrumPadHitColor.DEFAULT, DrumPadHitColor.fromLabel ("Purple"));
    }
}
