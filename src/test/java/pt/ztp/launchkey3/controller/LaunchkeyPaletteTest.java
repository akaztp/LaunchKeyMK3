// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import de.mossgrabers.framework.controller.color.ColorEx;


class LaunchkeyPaletteTest
{
    @Test
    void everyPaletteColourIsClosestToItself ()
    {
        // Some colours are in the palette twice, then the first one is taken
        for (int index = 1; index < LaunchkeyPalette.NUM_COLORS; index++)
        {
            final ColorEx color = LaunchkeyPalette.getColor (index);
            final ColorEx nearest = LaunchkeyPalette.getColor (LaunchkeyPalette.getNearestIndex (color));
            assertArrayEquals (color.toDoubleRGB (), nearest.toDoubleRGB (), 1e-9, "index " + index);
        }
    }


    @Test
    void thePadIsNeverTurnedOff ()
    {
        assertNotEquals (LaunchkeyPalette.INDEX_OFF, LaunchkeyPalette.getNearestIndex (ColorEx.BLACK));
    }


    @Test
    void bitwigColoursKeepTheirHue ()
    {
        // Rounded to Bitwig's 28 colours first, the last four lit up as a dim olive green, a pale
        // yellow, a bright green and a pale cyan
        assertEquals (0x06, nearest (0xD92E24), "Red: red");
        assertEquals (0x22, nearest (0x43D2B9), "Moss Green: teal");
        assertEquals (0x69, nearest (0xC69F70), "Brown: brown");
        assertEquals (0x44, nearest (0x00A694), "Bluish Green: dark teal");
        assertEquals (0x1E, nearest (0x3EBB62), "Greenish Blue: green");
    }


    @Test
    void theColourManagerKnowsEveryPaletteColour ()
    {
        final LaunchkeyMk3ColorManager colorManager = new LaunchkeyMk3ColorManager ();
        for (int index = 0; index < LaunchkeyPalette.NUM_COLORS; index++)
            assertEquals (index, colorManager.getColorIndex (LaunchkeyPalette.getColorId (index)));
    }


    private static int nearest (final int rgb)
    {
        return LaunchkeyPalette.getNearestIndex (ColorEx.fromRGB (rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF));
    }
}
