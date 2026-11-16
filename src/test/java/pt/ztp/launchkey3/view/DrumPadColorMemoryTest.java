// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;


class DrumPadColorMemoryTest
{
    @Test
    void nothingIsRememberedAtFirst ()
    {
        final DrumPadColorMemory memory = new DrumPadColorMemory ();
        assertEquals (Optional.empty (), memory.getColor (36));
    }


    @Test
    void padColoursAreRememberedByNote ()
    {
        final DrumPadColorMemory memory = new DrumPadColorMemory ();
        memory.remember ("0:Drums", 36, new String []
        {
            "RED",
            null,
            "BLUE"
        });

        assertEquals (Optional.of ("RED"), memory.getColor (36));
        assertEquals (Optional.empty (), memory.getColor (37), "empty pad");
        assertEquals (Optional.of ("BLUE"), memory.getColor (38));
        assertEquals (Optional.empty (), memory.getColor (39), "not seen");
    }


    @Test
    void pagesOfTheSameDrumMachineAreKept ()
    {
        final DrumPadColorMemory memory = new DrumPadColorMemory ();
        memory.remember ("0:Drums", 36, new String []
        {
            "RED"
        });
        memory.remember ("0:Drums", 52, new String []
        {
            "BLUE"
        });

        assertEquals (Optional.of ("RED"), memory.getColor (36));
        assertEquals (Optional.of ("BLUE"), memory.getColor (52));
    }


    @Test
    void anotherDrumMachineReplacesAllColours ()
    {
        final DrumPadColorMemory memory = new DrumPadColorMemory ();
        memory.remember ("0:Drums", 36, new String []
        {
            "RED"
        });
        memory.remember ("0:Drums", 52, new String []
        {
            "BLUE"
        });
        memory.remember ("3:Percussion", 36, new String []
        {
            "GREEN"
        });

        assertEquals (Optional.of ("GREEN"), memory.getColor (36));
        assertEquals (Optional.empty (), memory.getColor (52), "page of the previous drum machine");
    }


    @Test
    void notesOutsideTheMidiRangeAreIgnored ()
    {
        final DrumPadColorMemory memory = new DrumPadColorMemory ();
        memory.remember ("0:Drums", 120, new String []
        {
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I"
        });

        assertEquals (Optional.of ("H"), memory.getColor (127));
        assertEquals (Optional.empty (), memory.getColor (128));
        assertEquals (Optional.empty (), memory.getColor (-1));
    }
}
