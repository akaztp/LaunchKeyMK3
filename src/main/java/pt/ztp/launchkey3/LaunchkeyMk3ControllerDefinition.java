// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
// Modified by akaztp (2026) for the LaunchKeyMK3 project - see README.md

package pt.ztp.launchkey3;

import java.util.List;
import java.util.UUID;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;


/**
 * Definition class for the Novation Launchkey Mk3 controller extension.
 *
 * @author Jürgen Moßgraber
 * @author akaztp
 */
public class LaunchkeyMk3ControllerDefinition extends DefaultControllerDefinition
{
    /** The name shown in Bitwig, which tells this extension apart from DrivenByMoss's. */
    public static final String  NAME         = "LaunchKey Mk3 (ztp)";

    /** The author shown in Bitwig. */
    public static final String  AUTHOR       = "akaztp, based on DrivenByMoss by J. Moßgraber";

    // A new ID, so that this extension can be installed next to DrivenByMoss
    private static final UUID   EXTENSION_ID = UUID.fromString ("4fa22f36-43b5-476c-9bf5-2fb572fd086d");

    private static final int [] KEY_SIZES    =
    {
        25,
        37,
        49,
        61,
        88
    };


    /**
     * Constructor.
     */
    public LaunchkeyMk3ControllerDefinition ()
    {
        super (EXTENSION_ID, NAME, "Novation", 2, 1);
    }


    /** {@inheritDoc} */
    @Override
    public String getAuthor ()
    {
        return AUTHOR;
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion (final Package pckg)
    {
        // Read the version from this project's JAR manifest
        final String version = LaunchkeyMk3ControllerDefinition.class.getPackage ().getImplementationVersion ();
        return version == null ? "1.0" : version;
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            default:
            case WINDOWS:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "MIDIIN2 (LKMK3 MIDI)",
                    "LKMK3 MIDI"
                }, new String []
                {
                    "MIDIOUT2 (LKMK3 MIDI)"
                }));
                break;

            case MAC, MAC_ARM:
                for (final int element: KEY_SIZES)
                {
                    final String deviceName = "Launchkey MK3 " + element;
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                    {
                        deviceName + " LKMK3 DAW Out",
                        deviceName + " LKMK3 MIDI Out"
                    }, new String []
                    {
                        deviceName + " LKMK3 DAW In"
                    }));
                }
                break;

            case LINUX:
                for (final int element: KEY_SIZES)
                {
                    final String deviceName = "Launchkey MK3 " + element;

                    // Kernel 5.13+
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                    {
                        deviceName + " LKMK3 DAW Out",
                        deviceName + " LKMK3 MIDI Out"
                    }, new String []
                    {
                        deviceName + " LKMK3 DAW In"
                    }));

                    // Kernel prior 5.13
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                    {
                        deviceName + " MIDI 2",
                        deviceName + " MIDI 1"
                    }, new String []
                    {
                        deviceName + " MIDI 2"
                    }));

                    // Reaper specific
                    final String deviceID = "L" + element;
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
                    {
                        deviceID + " [hw:1,0,1]",
                        deviceID + " [hw:1,0,0]"
                    }, new String []
                    {
                        deviceID + " [hw:1,0,1]"
                    }));
                }
                break;
        }
        return midiDiscoveryPairs;
    }
}
