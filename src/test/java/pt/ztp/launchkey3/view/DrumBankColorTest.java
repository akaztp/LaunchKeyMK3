// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.scale.Scales;
import pt.ztp.launchkey3.controller.LaunchkeyMk3ColorManager;


class DrumBankColorTest
{
    @Test
    void theOctaveButtonsReachSevenBanks ()
    {
        assertEquals (List.of (Integer.valueOf (4), Integer.valueOf (20), Integer.valueOf (36), Integer.valueOf (52), Integer.valueOf (68), Integer.valueOf (84), Integer.valueOf (100)), getBankOffsets ());
        assertEquals (7, DrumBankColor.NUM_BANKS);
    }


    @Test
    void eachBankHasItsOwnColourFromRedToBlue ()
    {
        final List<Integer> colors = new ArrayList<> ();
        for (final Integer offset: getBankOffsets ())
            colors.add (Integer.valueOf (DrumBankColor.getColor (offset.intValue ())));

        assertEquals (List.of (Integer.valueOf (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_HI), Integer.valueOf (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI), Integer.valueOf (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI), Integer.valueOf (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI), Integer.valueOf (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_SKY_HI), Integer.valueOf (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_HI), Integer.valueOf (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI)), colors);
        assertEquals (7, new HashSet<> (colors).size ());
    }


    @Test
    void theDefaultBankIsTheThird ()
    {
        assertEquals (2, DrumBankColor.getBank (Scales.DRUM_NOTE_START));
    }


    @Test
    void padsMovedWithShiftKeepTheColourOfTheBankOfTheFirstPad ()
    {
        assertEquals (2, DrumBankColor.getBank (40));
        assertEquals (2, DrumBankColor.getBank (48));
        assertEquals (3, DrumBankColor.getBank (52));
        assertEquals (1, DrumBankColor.getBank (32));
    }


    @Test
    void offsetsOutsideTheRangeUseTheFirstOrLastBank ()
    {
        assertEquals (0, DrumBankColor.getBank (0));
        assertEquals (6, DrumBankColor.getBank (127));
    }


    /**
     * Step through the banks with the Octave buttons, as the Drum mode does.
     *
     * @return The note of the first pad of each bank, from the lowest to the highest
     */
    private static List<Integer> getBankOffsets ()
    {
        final Scales scales = new Scales (new TwosComplementValueChanger (128, 1), 36, 52, 8, 2);
        final int step = scales.getDrumDefaultOffset ();
        while (scales.canScrollDrumOctaveDown ())
            scales.decDrumOffset (step);

        final List<Integer> offsets = new ArrayList<> ();
        offsets.add (Integer.valueOf (scales.getDrumOffset ()));
        while (scales.canScrollDrumOctaveUp ())
        {
            scales.incDrumOffset (step);
            offsets.add (Integer.valueOf (scales.getDrumOffset ()));
        }
        return offsets;
    }
}
