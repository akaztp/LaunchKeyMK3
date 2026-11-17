// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.view;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class DrumNoteHoldTest
{
    /** B1, which the Launchkey sends as note 36. */
    private static final int   PAD_B1 = 0;
    /** T1, which the Launchkey sends as note 40. */
    private static final int   PAD_T1 = 8;

    private final int []       table  = createTable (0);
    private final DrumNoteHold hold   = new DrumNoteHold ();


    @BeforeEach
    void setUp ()
    {
        this.hold.setKeyTranslationTable (this.table);
    }


    @Test
    void withoutTheButtonThePadsPlayAsUsual ()
    {
        assertEquals (-1, this.hold.padPressed (PAD_B1), "note on as usual");
        assertArrayEquals (this.table, this.hold.getKeyTranslationTable ());
        assertFalse (this.hold.padReleased (PAD_B1), "note off as usual");
        assertArrayEquals (this.table, this.hold.getKeyTranslationTable ());
    }


    @Test
    void aPadHeldWhenTheButtonIsPressedPlaysOn ()
    {
        this.hold.padPressed (PAD_B1);
        this.hold.turnOn ();
        assertEquals (-1, this.hold.getKeyTranslationTable ()[36], "the note off is dropped");

        assertTrue (this.hold.padReleased (PAD_B1), "the note plays on");
        assertTrue (this.hold.isNoteHeld (36));
        assertEquals (-1, this.hold.getKeyTranslationTable ()[36], "the next note on is dropped");
    }


    @Test
    void aPadPressedWhileTheButtonIsHeldPlaysOn ()
    {
        this.hold.turnOn ();
        assertArrayEquals (this.table, this.hold.getKeyTranslationTable (), "no pad is down");

        assertEquals (-1, this.hold.padPressed (PAD_T1), "note on as usual");
        assertEquals (-1, this.hold.getKeyTranslationTable ()[40]);
        assertEquals (36, this.hold.getKeyTranslationTable ()[36], "other pads are not touched");

        assertTrue (this.hold.padReleased (PAD_T1));
        assertTrue (this.hold.isNoteHeld (40));
        assertFalse (this.hold.isNoteHeld (36));
    }


    @Test
    void aPadStillDownWhenTheButtonIsReleasedPlaysOn ()
    {
        this.hold.turnOn ();
        this.hold.padPressed (PAD_B1);
        this.hold.turnOff ();
        assertEquals (-1, this.hold.getKeyTranslationTable ()[36], "the note off is still dropped");

        assertTrue (this.hold.padReleased (PAD_B1), "the note plays on");
        assertTrue (this.hold.isNoteHeld (36));
    }


    @Test
    void aPadPressedAfterTheButtonIsReleasedPlaysAsUsual ()
    {
        this.hold.turnOn ();
        this.hold.turnOff ();

        assertEquals (-1, this.hold.padPressed (PAD_B1));
        assertArrayEquals (this.table, this.hold.getKeyTranslationTable ());
        assertFalse (this.hold.padReleased (PAD_B1), "note off as usual");
    }


    @Test
    void theNextPressStopsTheHeldNote ()
    {
        this.holdNote (PAD_B1);
        this.hold.turnOff ();

        assertEquals (36, this.hold.padPressed (PAD_B1), "a note off instead of the note on");
        assertFalse (this.hold.isNoteHeld (36));
        assertEquals (-1, this.hold.getKeyTranslationTable ()[36], "the release is dropped");

        assertFalse (this.hold.padReleased (PAD_B1));
        assertArrayEquals (this.table, this.hold.getKeyTranslationTable (), "the next press plays as usual");
        assertEquals (-1, this.hold.padPressed (PAD_B1));
        assertFalse (this.hold.padReleased (PAD_B1));
    }


    @Test
    void whileTheButtonIsHeldThePadsAreToggles ()
    {
        this.holdNote (PAD_B1);

        assertEquals (36, this.hold.padPressed (PAD_B1), "off");
        assertFalse (this.hold.padReleased (PAD_B1));
        assertArrayEquals (this.table, this.hold.getKeyTranslationTable ());

        assertEquals (-1, this.hold.padPressed (PAD_B1), "on");
        assertTrue (this.hold.padReleased (PAD_B1));
        assertTrue (this.hold.isNoteHeld (36));
    }


    @Test
    void pressingTheButtonWhileStoppingANoteHoldsNothing ()
    {
        this.holdNote (PAD_B1);
        this.hold.turnOff ();

        this.hold.padPressed (PAD_B1);
        this.hold.turnOn ();
        assertFalse (this.hold.padReleased (PAD_B1));
        assertFalse (this.hold.isNoteHeld (36));
    }


    @Test
    void aHeldNoteIsStoppedFromItsBank ()
    {
        this.holdNote (PAD_B1);
        this.hold.turnOff ();

        // Octave up: B1 plays note 52
        this.hold.setKeyTranslationTable (createTable (16));
        assertEquals (52, this.hold.getKeyTranslationTable ()[36]);
        assertEquals (-1, this.hold.padPressed (PAD_B1), "note on as usual");
        assertFalse (this.hold.padReleased (PAD_B1));

        // Octave down: B1 plays the held note again
        this.hold.setKeyTranslationTable (this.table);
        assertEquals (-1, this.hold.getKeyTranslationTable ()[36]);
        assertEquals (36, this.hold.padPressed (PAD_B1));
    }


    @Test
    void aPadReleasedInAnotherBankHoldsTheNoteWhichItPlayed ()
    {
        this.hold.turnOn ();
        this.hold.padPressed (PAD_B1);
        this.hold.setKeyTranslationTable (createTable (16));
        assertEquals (-1, this.hold.getKeyTranslationTable ()[36], "the note off is still dropped");

        assertTrue (this.hold.padReleased (PAD_B1));
        assertTrue (this.hold.isNoteHeld (36));
        assertFalse (this.hold.isNoteHeld (52));
    }


    @Test
    void aPadWhichPlaysNothingHoldsNothing ()
    {
        final int [] empty = new int [128];
        Arrays.fill (empty, -1);
        this.hold.setKeyTranslationTable (empty);

        this.hold.turnOn ();
        assertEquals (-1, this.hold.padPressed (PAD_B1));
        assertFalse (this.hold.padReleased (PAD_B1));
    }


    @Test
    void resetForgetsThePadsButKeepsTheHeldNotes ()
    {
        this.holdNote (PAD_B1);
        this.hold.padPressed (PAD_T1);
        this.hold.reset ();

        assertFalse (this.hold.isOn ());
        assertTrue (this.hold.isNoteHeld (36));
        assertEquals (-1, this.hold.getKeyTranslationTable ()[36], "B1 still stops its note");
        assertEquals (40, this.hold.getKeyTranslationTable ()[40], "T1 is forgotten");

        this.hold.turnOn ();
        assertEquals (40, this.hold.getKeyTranslationTable ()[40], "T1 is not down anymore");
    }


    @Test
    void theGivenTableIsNotChanged ()
    {
        this.holdNote (PAD_B1);
        this.hold.getKeyTranslationTable ();
        assertEquals (36, this.table[36]);
    }


    /**
     * Turn the hold on, then press and release a pad, so that its note plays on.
     *
     * @param position The position of the pad
     */
    private void holdNote (final int position)
    {
        this.hold.turnOn ();
        this.hold.padPressed (position);
        assertTrue (this.hold.padReleased (position));
    }


    /**
     * Create a key translation table which moves every note.
     *
     * @param offset The number of notes to move up
     * @return The table
     */
    private static int [] createTable (final int offset)
    {
        final int [] table = new int [128];
        for (int i = 0; i < table.length; i++)
            table[i] = i + offset < 128 ? i + offset : -1;
        return table;
    }
}
