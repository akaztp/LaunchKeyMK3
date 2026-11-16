// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.view;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


/**
 * Remembers the pad colours of the last drum machine on the selected track, by MIDI note, so that
 * the pads can keep showing them while a track without a drum machine is selected.
 *
 * @author akaztp
 */
public class DrumPadColorMemory
{
    private static final int NUM_NOTES = 128;

    private final String []  colors    = new String [NUM_NOTES];
    private String           source;


    /**
     * Remember the colours of some pads of a drum machine. If they come from another drum machine
     * than the colours remembered so far, those are forgotten first.
     *
     * @param source Identifies the drum machine, for example by its track
     * @param firstNote The MIDI note of the first pad
     * @param padColors The colour IDs of the pads, null for an empty pad
     */
    public void remember (final String source, final int firstNote, final String [] padColors)
    {
        if (!Objects.equals (source, this.source))
        {
            Arrays.fill (this.colors, null);
            this.source = source;
        }

        for (int i = 0; i < padColors.length; i++)
        {
            final int note = firstNote + i;
            if (note >= 0 && note < NUM_NOTES)
                this.colors[note] = padColors[i];
        }
    }


    /**
     * Get the remembered colour of the pad that plays a MIDI note.
     *
     * @param note The MIDI note
     * @return The colour ID, or empty if the pad was empty or has not been seen
     */
    public Optional<String> getColor (final int note)
    {
        if (note < 0 || note >= NUM_NOTES)
            return Optional.empty ();
        return Optional.ofNullable (this.colors[note]);
    }
}
