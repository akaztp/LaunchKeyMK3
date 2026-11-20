// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.controller;

import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * The 128 colours of the Launchkey MK3's palette, to light a pad in the palette colour which looks
 * closest to an RGB colour, such as the colour of a Drum Machine pad in Bitwig.
 *
 * The RGB values are read from the colour palette in Novation's Launchkey MK3 Programmer's Reference
 * Guide. The swatches there are drawn lightened, black as 0x60 and white as 0xFD, which is undone
 * here; the values are within a few steps of the original ones.
 *
 * The closest colour is the one with the smallest distance in the CIELAB colour space (CIE76),
 * which follows how different two colours look better than a distance of the RGB values.
 *
 * @author akaztp
 */
public final class LaunchkeyPalette
{
    /** The number of colours in the palette. */
    public static final int      NUM_COLORS      = 128;

    /** The palette index which turns the pad off. */
    public static final int      INDEX_OFF       = 0;

    private static final String  COLOR_ID_PREFIX = "LAUNCHKEY_PALETTE_";

    // @formatter:off
    private static final int []  RGB             =
    {
        // 00-07
        0x000000, 0x858585, 0xC9C9C9, 0xFFFFFF, 0xFD8587, 0xFC0000, 0xC60000, 0x840000,
        // 08-0F
        0xFFEDBE, 0xFD8500, 0xC84600, 0x842200, 0xFDE56A, 0xFFFF00, 0xC9C905, 0x848500,
        // 10-17
        0xC9FD6A, 0x9FFF00, 0x6AC900, 0x348500, 0x9FFF85, 0x00FD00, 0x00C900, 0x008500,
        // 18-1F
        0x9EFF9E, 0x00FF47, 0x00C922, 0x008410, 0x9FFFAF, 0x00FDAE, 0x00C86A, 0x008536,
        // 20-27
        0x9EFDEC, 0x00FFDB, 0x00C89C, 0x008454, 0x9EEDFD, 0x00E3FD, 0x00A6C9, 0x006885,
        // 28-2F
        0x9FC9FD, 0x00A6FD, 0x0068C8, 0x003684, 0x6647FA, 0x0000FA, 0x0000C6, 0x000084,
        // 30-37
        0xAC85FD, 0x6800FD, 0x3400C8, 0x220084, 0xFD87FD, 0xFD00FD, 0xC900C8, 0x850084,
        // 38-3F
        0xFD87BC, 0xFD009E, 0xC80066, 0x840044, 0xFD2400, 0xDB8500, 0xC89E00, 0x686800,
        // 40-47
        0x008400, 0x008546, 0x0046BB, 0x0000FC, 0x008584, 0x4600EA, 0xAE879E, 0x462236,
        // 48-4F
        0xFD0000, 0xEDFF6B, 0xE5FC00, 0xAFFF00, 0x24C800, 0x00FDAE, 0x00DDFF, 0x0068FC,
        // 50-57
        0x4600FC, 0xAE00FA, 0xE347C9, 0x662200, 0xFD6A00, 0xC9F700, 0xBEFD47, 0x00FF00,
        // 58-5F
        0x87FD6A, 0xAFFABE, 0x87FFF0, 0xAED5FD, 0x689EF0, 0xBC9FF5, 0xF547FD, 0xFD00AE,
        // 60-67
        0xFD9E00, 0xEDE500, 0xD5FF00, 0xC9AE00, 0x856800, 0x008F24, 0x249C46, 0x363468,
        // 68-6F
        0x3646AC, 0xAC7736, 0xC60000, 0xF4856A, 0xF59124, 0xFDED49, 0xDFF76A, 0xBEE525,
        // 70-77
        0x343468, 0xF7F7BC, 0xC9FAD6, 0xDDDDFD, 0xD5BCFD, 0x858585, 0xBCBCBC, 0xF7FFFF,
        // 78-7F
        0xDA0000, 0x750000, 0x36F000, 0x008500, 0xEDE500, 0x846800, 0xE59E00, 0x9C2400
    };
    // @formatter:on

    private static final double [][] LAB = new double [NUM_COLORS][];
    static
    {
        for (int index = 0; index < NUM_COLORS; index++)
            LAB[index] = toLab (getColor (index));
    }


    /**
     * Utility class.
     */
    private LaunchkeyPalette ()
    {
        // Intentionally empty
    }


    /**
     * Get the RGB colour of a palette entry.
     *
     * @param index The palette index, 0-127
     * @return The colour
     */
    public static ColorEx getColor (final int index)
    {
        final int rgb = RGB[index];
        return ColorEx.fromRGB (rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF);
    }


    /**
     * Get the ID under which a palette entry is registered in the colour manager.
     *
     * @param index The palette index, 0-127
     * @return The colour ID
     */
    public static String getColorId (final int index)
    {
        return COLOR_ID_PREFIX + index;
    }


    /**
     * Get the palette entry which looks closest to a colour. The pad is never turned off, so black
     * gives the darkest colour.
     *
     * @param color The colour
     * @return The palette index, 1-127
     */
    public static int getNearestIndex (final ColorEx color)
    {
        final double [] lab = toLab (color);
        int nearest = INDEX_OFF + 1;
        double minDistance = Double.MAX_VALUE;
        for (int index = INDEX_OFF + 1; index < NUM_COLORS; index++)
        {
            final double distance = distance (lab, LAB[index]);
            if (distance < minDistance)
            {
                minDistance = distance;
                nearest = index;
            }
        }
        return nearest;
    }


    /**
     * Get the ID of the palette entry which looks closest to a colour.
     *
     * @param color The colour
     * @return The colour ID
     */
    public static String getNearestColorId (final ColorEx color)
    {
        return getColorId (getNearestIndex (color));
    }


    /**
     * Convert an sRGB colour to CIELAB, with the D65 white point.
     *
     * @param color The colour
     * @return The L*, a* and b* values
     */
    private static double [] toLab (final ColorEx color)
    {
        final double [] rgb = color.toDoubleRGB ();
        final double red = toLinear (rgb[0]);
        final double green = toLinear (rgb[1]);
        final double blue = toLinear (rgb[2]);

        final double x = labF ((0.4124 * red + 0.3576 * green + 0.1805 * blue) / 0.95047);
        final double y = labF (0.2126 * red + 0.7152 * green + 0.0722 * blue);
        final double z = labF ((0.0193 * red + 0.1192 * green + 0.9505 * blue) / 1.08883);
        return new double []
        {
            116 * y - 16,
            500 * (x - y),
            200 * (y - z)
        };
    }


    private static double toLinear (final double value)
    {
        return value <= 0.04045 ? value / 12.92 : Math.pow ((value + 0.055) / 1.055, 2.4);
    }


    private static double labF (final double t)
    {
        return t > 216.0 / 24389.0 ? Math.cbrt (t) : (24389.0 / 27.0 * t + 16) / 116;
    }


    private static double distance (final double [] lab1, final double [] lab2)
    {
        final double dl = lab1[0] - lab2[0];
        final double da = lab1[1] - lab2[1];
        final double db = lab1[2] - lab2[2];
        return Math.sqrt (dl * dl + da * da + db * db);
    }
}
