// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
// Modified by akaztp (2026) for the LaunchKeyMK3 project - see README.md

package pt.ztp.launchkey3;

import java.util.List;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for Launchkey Mk3.
 *
 * @author Jürgen Moßgraber
 * @author akaztp
 */
public class LaunchkeyMk3Configuration extends AbstractConfiguration
{
    /** ID for lighting the selected pad in the Drum mode. */
    public static final Integer HIGHLIGHT_SELECTED_DRUM_PAD = Integer.valueOf (NEXT_SETTING_ID);

    private boolean             isHighlightSelectedDrumPad;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public LaunchkeyMk3Configuration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        // ----------------------------------------------------------------
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateDoubleClickPlayButton (globalSettings);
        this.activateDoubleClickStopButton (globalSettings);
        this.activateRecordButtonSetting (globalSettings);
        this.activateShiftedRecordButtonSetting (globalSettings);

        // ----------------------------------------------------------------
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateIncludeMasterSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateSelectClipOnLaunchSetting (globalSettings);

        // ----------------------------------------------------------------
        // Pads

        final IEnumSetting highlightSelectedDrumPadSetting = globalSettings.getEnumSetting ("Highlight Selected Drum Pad", CATEGORY_PADS, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        highlightSelectedDrumPadSetting.addValueObserver (value -> {
            this.isHighlightSelectedDrumPad = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (HIGHLIGHT_SELECTED_DRUM_PAD);
        });
        this.isSettingActive.add (HIGHLIGHT_SELECTED_DRUM_PAD);
    }


    /**
     * Should the selected pad (the last one played) light up in the Drum mode?
     *
     * @return True to light it up in blue, false to show its normal colour
     */
    public boolean isHighlightSelectedDrumPad ()
    {
        return this.isHighlightSelectedDrumPad;
    }
}
