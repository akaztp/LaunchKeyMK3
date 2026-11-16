// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.controller;

/**
 * The colours to choose from for the pads which are played in the Drum mode.
 *
 * @author akaztp
 */
public enum DrumPadHitColor
{
    /** White. */
    WHITE ("White", LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_WHITE),
    /** Green, as in DrivenByMoss. */
    GREEN ("Green", LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI),
    /** Red. */
    RED ("Red", LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_HI),
    /** Blue. */
    BLUE ("Blue", LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLUE_HI),
    /** Cyan: the palette has no pure cyan, this is its brightest light blue. */
    CYAN ("Cyan", LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_SKY_HI),
    /** Yellow. */
    YELLOW ("Yellow", LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI),
    /** Magenta. */
    MAGENTA ("Magenta", LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_MAGENTA_HI);


    /** The colour of DrivenByMoss. */
    public static final DrumPadHitColor DEFAULT = GREEN;

    private final String                label;
    private final int                   paletteIndex;


    private DrumPadHitColor (final String label, final int paletteIndex)
    {
        this.label = label;
        this.paletteIndex = paletteIndex;
    }


    /**
     * Get the name shown in the settings.
     *
     * @return The name
     */
    public String getLabel ()
    {
        return this.label;
    }


    /**
     * Get the index of the colour in the Launchkey's palette.
     *
     * @return The index
     */
    public int getPaletteIndex ()
    {
        return this.paletteIndex;
    }


    /**
     * Get the ID under which the colour is registered in the colour manager.
     *
     * @return The ID
     */
    public String getColorId ()
    {
        return "DRUM_PAD_HIT_" + this.name ();
    }


    /**
     * Get the names of all colours, in the order of the settings.
     *
     * @return The names
     */
    public static String [] getLabels ()
    {
        final DrumPadHitColor [] colors = values ();
        final String [] labels = new String [colors.length];
        for (int i = 0; i < colors.length; i++)
            labels[i] = colors[i].label;
        return labels;
    }


    /**
     * Get the colour with a name.
     *
     * @param label The name, as shown in the settings
     * @return The colour, or the default if there is none with that name
     */
    public static DrumPadHitColor fromLabel (final String label)
    {
        for (final DrumPadHitColor color: values ())
        {
            if (color.label.equals (label))
                return color;
        }
        return DEFAULT;
    }
}
