// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
// Modified by akaztp (2026) for the LaunchKeyMK3 project - see README.md

package pt.ztp.launchkey3.view;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import pt.ztp.launchkey3.LaunchkeyMk3Configuration;
import pt.ztp.launchkey3.controller.LaunchkeyMk3ColorManager;
import pt.ztp.launchkey3.controller.LaunchkeyMk3ControlSurface;
import pt.ztp.launchkey3.controller.LaunchkeyPalette;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;


/**
 * The Drum view. The pads play the Drum Machine pads in the order of DrumPadLayout, and light up in
 * the palette colours closest to theirs (see LaunchkeyPalette). While the selected track has no
 * drum machine, the pads keep the colours of the last drum machine. The
 * Stop/Solo/Mute button lights up in the colour of the bank of the pads (see DrumBankColor). In the
 * Play mode, the pads which are down while it is held work as toggles (see DrumNoteHold).
 *
 * @author Jürgen Moßgraber
 * @author akaztp
 */
public class DrumView extends AbstractDrumView<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    private static final int         NUM_DISPLAY_COLS = 16;

    private boolean                  isPlayMode       = true;
    private final DrumPadColorMemory padColorMemory   = new DrumPadColorMemory ();
    private final DrumNoteHold       noteHold         = new DrumNoteHold ();


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumView (final LaunchkeyMk3ControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 2, DrumView.NUM_DISPLAY_COLS, true);

        this.allRows = 2;
        this.firstPad = ButtonID.PAD17;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final int index = note - this.surface.getPadGrid ().getStartNote ();

        // Follow the pads for the note hold also if the selected track changed meanwhile, so that
        // no key stays filtered out
        final boolean isNoteStopped = this.isPlayMode && this.updateNoteHold (index, velocity > 0);

        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int offsetY = this.scales.getDrumOffset ();

        if (this.isPlayMode)
        {
            // The index is the position of the pad, select the Drum Machine pad which it plays
            this.setSelectedPad (DrumPadLayout.getPad (index), velocity); // 0-15

            // Mark selected note, unless the pad stopped its held note. The pad of a held note
            // stays lit while the note plays on, see getDrumPadColor.
            this.keyManager.setKeyPressed (offsetY + this.getSelectedPad (), isNoteStopped ? 0 : velocity);
        }
        else if (this.isActive ())
        {
            final int x = index % this.numColumns;
            final int y = index / this.numColumns;
            this.handleSequencerArea (index, x, y, offsetY, velocity);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () && this.isPlayMode ? this.scales.getDrumMatrix () : EMPTY_TABLE);
    }


    /**
     * As AbstractView.delayedUpdateNoteMapping, but applies the note hold to the key translation
     * table.
     */
    @Override
    protected void delayedUpdateNoteMapping (final int [] matrix)
    {
        this.surface.scheduleTask (() -> {
            this.keyManager.setNoteMatrix (matrix);
            if (matrix.length == 128)
            {
                this.noteHold.setKeyTranslationTable (this.scales.translateMatrixToGrid (matrix));
                this.surface.setKeyTranslationTable (this.noteHold.getKeyTranslationTable ());
            }
        }, 6);
    }


    /**
     * Send the key translation table with the note hold to the note input, if it changed.
     */
    private void applyNoteMapping ()
    {
        final int [] table = this.noteHold.getKeyTranslationTable ();
        if (!Arrays.equals (table, this.surface.getKeyTranslationTable ()))
            this.surface.setKeyTranslationTable (table);
    }


    /**
     * Tell the note hold that a pad was pressed or released. If the pad was pressed to stop its
     * held note, Bitwig has dropped the note on; send a note off for the held note instead.
     *
     * @param position The position of the pad
     * @param isPressed True if pressed, false if released
     * @return True if the pad was pressed to stop its held note
     */
    private boolean updateNoteHold (final int position, final boolean isPressed)
    {
        boolean isNoteStopped = false;
        if (isPressed)
        {
            final int heldNote = this.noteHold.padPressed (position);
            if (heldNote >= 0)
            {
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_OFF + DrumPadLayout.getLaunchkeyChannel (), heldNote, 0);
                isNoteStopped = true;
            }
        }
        else
            this.noteHold.padReleased (position);

        this.applyNoteMapping ();
        return isNoteStopped;
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        // The next view sets its own key translation table. The held notes play on, and their pads
        // stop them when the Drum mode is back.
        this.noteHold.reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final IDrumDevice primary = this.model.getDrumDevice ();
        final IDrumPadBank drumPadBank = primary.getDrumPadBank ();

        if (this.isPlayMode)
        {
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    // Light each pad in the colour of the Drum Machine pad which it plays
                    final int position = 8 * y + x;
                    padGrid.lightEx (x, 1 - y, this.getDrumPadColor (DrumPadLayout.getPad (position), drumPadBank, false));
                }
            }
            return;
        }

        if (!this.isActive ())
        {
            padGrid.turnOff ();
            return;
        }

        // Paint the sequencer steps
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int channel = this.configuration.getMidiEditChannel ();
        final int selPad = this.getSelectedPad ();
        final List<NotePosition> editNotes = this.getEditNotes ();
        final NotePosition notePosition = new NotePosition (channel, 0, 0);
        for (int col = 0; col < DrumView.NUM_DISPLAY_COLS; col++)
        {
            final int noteRow = offsetY + selPad;
            notePosition.setStep (col);
            notePosition.setNote (noteRow);
            final IStepInfo stepInfo = clip.getStep (notePosition);
            final boolean hilite = col == hiStep;
            final int x = col % GRID_COLUMNS;
            final int y = col / GRID_COLUMNS;

            final Optional<ColorEx> rowColor = this.getPadColor (primary, this.selectedPad);
            padGrid.lightEx (x, y, this.getStepColor (stepInfo, hilite, rowColor, channel, col, noteRow, editNotes));
        }
    }


    /**
     * As AbstractDrumView.getDrumPadColor, but the played pads light up in the colour set in the
     * configuration, the selected pad only lights up if set in the configuration, while the
     * selected track has no drum machine, the pads show the colours of the last one, and the pads
     * without a sound (empty or deactivated) are off instead of dim yellow.
     */
    @Override
    protected String getDrumPadColor (final int index, final IDrumPadBank drumPadBank, final boolean isRecording)
    {
        final LaunchkeyMk3Configuration config = this.surface.getConfiguration ();
        final int note = this.scales.getDrumOffset () + index;

        // Playing note, or held by the Stop/Solo/Mute button?
        if (this.keyManager.isKeyPressed (note) || this.noteHold.isNoteHeld (note))
            return isRecording ? AbstractDrumView.COLOR_PAD_RECORD : config.getDrumPadHitColor ().getColorId ();

        // Selected?
        if (this.selectedPad == index && config.isHighlightSelectedDrumPad ())
            return AbstractDrumView.COLOR_PAD_SELECTED;

        final String color;
        if (this.model.getDrumDevice ().hasDrumPads ())
            color = this.getPadBaseColor (drumPadBank.getItem (index), drumPadBank);
        else
            color = this.padColorMemory.getColor (note).orElse (null);
        if (color != null)
            return color;
        // No sound, no colour
        return AbstractDrumView.COLOR_PAD_OFF;
    }


    /**
     * Remember the pad colours of the drum machine on the selected track, if it has one. Called on
     * each flush, also while another view is active, so that the colours of the last drum machine
     * are known when a track without one is selected.
     */
    public void rememberDrumPadColors ()
    {
        final IDrumDevice primary = this.model.getDrumDevice ();
        if (!primary.hasDrumPads ())
            return;

        final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
        final int firstNote = drumPadBank.getScrollPosition ();
        if (firstNote < 0)
            return;

        final String [] colors = new String [drumPadBank.getPageSize ()];
        for (int i = 0; i < colors.length; i++)
            colors[i] = this.getPadBaseColor (drumPadBank.getItem (i), drumPadBank);

        final ICursorTrack cursorTrack = this.model.getCursorTrack ();
        this.padColorMemory.remember (cursorTrack.getPosition () + ":" + cursorTrack.getName (), firstNote, colors);
    }


    /**
     * Get the colour of a drum pad as AbstractDrumView.getDrumPadColor does, but without the
     * colours of the playing and the selected pad.
     *
     * @param drumPad The drum pad
     * @param drumPadBank The bank which contains the drum pad
     * @return The colour ID, or null if the pad is empty
     */
    private String getPadBaseColor (final IChannel drumPad, final IDrumPadBank drumPadBank)
    {
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return null;
        if (drumPad.isMute () || drumPadBank.hasSoloedPads () && !drumPad.isSolo ())
            return AbstractDrumView.COLOR_PAD_MUTED;
        return this.getPadContentColor (drumPad);
    }


    /**
     * As AbstractDrumView.getPadContentColor, but the pad lights up in the Launchkey palette colour
     * which looks closest to the colour of the Drum Machine pad, instead of first rounding it to one
     * of 28 Bitwig colours.
     */
    @Override
    protected String getPadContentColor (final IChannel drumPad)
    {
        return LaunchkeyPalette.getNearestColorId (drumPad.getColor ());
    }


    /**
     * As AbstractSequencerView.getStepColor, but the steps with notes light up in the Launchkey
     * palette colour which looks closest to the colour of the Drum Machine pad, as the pads do.
     */
    @Override
    protected String getStepColor (final IStepInfo stepInfo, final boolean highlight, final Optional<ColorEx> rowColor, final int channel, final int step, final int note, final List<NotePosition> editNotes)
    {
        // Without the row colour, the steps which would show it get COLOR_CONTENT(_CONT)
        final String color = super.getStepColor (stepInfo, highlight, Optional.empty (), channel, step, note, editNotes);
        if (rowColor.isEmpty ())
            return color;
        if (COLOR_CONTENT.equals (color))
            return LaunchkeyPalette.getNearestColorId (rowColor.get ());
        if (COLOR_CONTENT_CONT.equals (color))
            return LaunchkeyPalette.getNearestColorId (ColorEx.darker (rowColor.get ()));
        return color;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final DrumView view = (DrumView) this.surface.getViewManager ().get (Views.DRUM);

        switch (buttonID)
        {
            case SCENE1:
                return this.isPlayMode ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLUE;

            case SCENE2:
                // The Stop/Solo/Mute button shows the bank of the pads
                return DrumBankColor.getColor (this.scales.getDrumOffset ());

            case ARROW_UP:
                if (this.surface.isShiftPressed ())
                    return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
                return view.isOctaveUpButtonOn () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;

            case ARROW_DOWN:
                if (this.surface.isShiftPressed ())
                    return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
                return view.isOctaveDownButtonOn () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;

            default:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (buttonID == ButtonID.SCENE2)
        {
            this.onStopSoloMuteButton (event);
            return;
        }

        if (event != ButtonEvent.DOWN)
            return;

        final DrumView view = (DrumView) this.surface.getViewManager ().get (Views.DRUM);

        switch (buttonID)
        {
            case SCENE1:
                this.isPlayMode = !this.isPlayMode;
                this.noteHold.reset ();
                this.updateNoteMapping ();
                this.surface.getDisplay ().notify (this.isPlayMode ? "Play / Select" : "Steps");
                break;

            case ARROW_UP:
                view.onOctaveUp (ButtonEvent.DOWN);
                break;

            case ARROW_DOWN:
                view.onOctaveDown (ButtonEvent.DOWN);
                break;

            default:
                // Not used
                break;
        }
    }


    /**
     * The Stop/Solo/Mute button: in the Play mode, the pads which are down while it is held work as
     * toggles. In the Steps mode, it shows the step sequencer settings while held.
     *
     * @param event The button event
     */
    private void onStopSoloMuteButton (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
        {
            if (this.isPlayMode)
            {
                this.noteHold.turnOn ();
                this.applyNoteMapping ();
            }
            else if (this.isActive ())
                this.surface.getViewManager ().setActive (Views.SHIFT);
            return;
        }

        // The pads which are still down stay toggles, so the key translation table doesn't change
        if (event == ButtonEvent.UP)
            this.noteHold.turnOff ();
    }


    /**
     * Check if play mode is active.
     *
     * @return True if play mode is active otherwise the sequencer steps of a note a shown.
     */
    public boolean isPlayMode ()
    {
        return this.isPlayMode;
    }
}