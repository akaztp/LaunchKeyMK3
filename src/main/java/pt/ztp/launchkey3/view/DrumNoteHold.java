// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.view;

import java.util.Arrays;

import de.mossgrabers.framework.scale.Scales;


/**
 * Holds the notes of the drum pads: the pads which are down while the hold is on (the Stop/Solo/Mute
 * button is held in the Play mode) work as toggles. When such a pad is released, also after the
 * hold was turned off, it doesn't send a note off, so its note plays on. The next press of the pad
 * stops the held note, with or without the hold on.
 *
 * Bitwig receives the pads' notes directly through the key translation table of the note input,
 * before the extension sees them. So the notes are dropped in advance: the keys of the pads whose
 * note off is held back, and of the pads which play a held note, are filtered out (-1) in the
 * table. When such a pad is pressed to stop its held note, Bitwig drops the note on, and the
 * extension sends a note off for the held note instead; the release of that press is dropped too.
 *
 * @author akaztp
 */
public class DrumNoteHold
{
    private static final int NUM_NOTES           = 128;

    private final boolean [] isPadDown           = new boolean [DrumPadLayout.NUM_PADS];
    /** The note which the press of a pad played, -1 if none. */
    private final int []     pressedNote         = new int [DrumPadLayout.NUM_PADS];
    /** The pad is down and its note off will be dropped, so its note plays on. */
    private final boolean [] isNoteOffHeldBack   = new boolean [DrumPadLayout.NUM_PADS];
    /** The pad is down and its press stopped a held note, so its note off will be dropped. */
    private final boolean [] isReleaseDropped    = new boolean [DrumPadLayout.NUM_PADS];
    /** The notes which play on after their pad was released. */
    private final boolean [] isNoteHeld          = new boolean [NUM_NOTES];
    private int []           keyTranslationTable = Scales.getEmptyMatrix ();
    private boolean          isOn;


    /**
     * Constructor.
     */
    public DrumNoteHold ()
    {
        Arrays.fill (this.pressedNote, -1);
    }


    /**
     * Set the key translation table of the pads without the hold.
     *
     * @param table The key translation table with 128 entries, which is not changed
     */
    public void setKeyTranslationTable (final int [] table)
    {
        this.keyTranslationTable = table.clone ();
    }


    /**
     * Get the key translation table with the hold: the keys of the pads whose notes are dropped are
     * filtered out.
     *
     * @return The key translation table with 128 entries
     */
    public int [] getKeyTranslationTable ()
    {
        final int [] result = this.keyTranslationTable.clone ();
        for (int position = 0; position < DrumPadLayout.NUM_PADS; position++)
        {
            final int key = DrumPadLayout.getLaunchkeyNote (position);
            if (this.isNoteOffHeldBack[position] || this.isReleaseDropped[position] || this.isNoteHeld (result[key]))
                result[key] = -1;
        }
        return result;
    }


    /**
     * Is the hold on?
     *
     * @return True if on
     */
    public boolean isOn ()
    {
        return this.isOn;
    }


    /**
     * Does a note play on after its pad was released?
     *
     * @param note The note
     * @return True if the note plays on
     */
    public boolean isNoteHeld (final int note)
    {
        return note >= 0 && note < NUM_NOTES && this.isNoteHeld[note];
    }


    /**
     * Turn the hold on: the pads which are down, and all pads pressed from now on, don't send a
     * note off when released.
     */
    public void turnOn ()
    {
        this.isOn = true;
        for (int position = 0; position < DrumPadLayout.NUM_PADS; position++)
        {
            if (this.isPadDown[position] && this.pressedNote[position] >= 0)
                this.isNoteOffHeldBack[position] = true;
        }
    }


    /**
     * Turn the hold off: the pads pressed from now on play as usual. The pads which are still down
     * stay toggles, so their notes play on when released.
     */
    public void turnOff ()
    {
        this.isOn = false;
    }


    /**
     * A pad is pressed. If it plays a held note, Bitwig has dropped the note on already; then the
     * press stops the held note.
     *
     * @param position The position of the pad, 0-7 on the bottom row and 8-15 on the top row
     * @return The held note which the press stops, to send a note off for, or -1 if the pad plays
     *         as usual
     */
    public int padPressed (final int position)
    {
        this.isPadDown[position] = true;

        final int note = this.keyTranslationTable[DrumPadLayout.getLaunchkeyNote (position)];
        if (this.isNoteHeld (note))
        {
            this.isNoteHeld[note] = false;
            this.pressedNote[position] = -1;
            this.isReleaseDropped[position] = true;
            return note;
        }

        this.pressedNote[position] = note;
        this.isNoteOffHeldBack[position] = this.isOn && note >= 0;
        return -1;
    }


    /**
     * A pad is released. If its note off was held back, Bitwig has dropped it already; then the
     * note plays on until the pad's next press.
     *
     * @param position The position of the pad, 0-7 on the bottom row and 8-15 on the top row
     * @return True if the note plays on
     */
    public boolean padReleased (final int position)
    {
        final boolean isPlayingOn = this.isNoteOffHeldBack[position];
        if (isPlayingOn)
            this.isNoteHeld[this.pressedNote[position]] = true;

        this.isPadDown[position] = false;
        this.pressedNote[position] = -1;
        this.isNoteOffHeldBack[position] = false;
        this.isReleaseDropped[position] = false;
        return isPlayingOn;
    }


    /**
     * Forget the hold and the pads which are down, for example when the Drum mode is left. The
     * notes which play on are kept, so that their pads still stop them.
     */
    public void reset ()
    {
        this.isOn = false;
        Arrays.fill (this.isPadDown, false);
        Arrays.fill (this.pressedNote, -1);
        Arrays.fill (this.isNoteOffHeldBack, false);
        Arrays.fill (this.isReleaseDropped, false);
    }
}
