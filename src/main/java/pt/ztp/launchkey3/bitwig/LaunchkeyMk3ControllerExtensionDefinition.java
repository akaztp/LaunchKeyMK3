// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
// Modified by akaztp (2026) for the LaunchKeyMK3 project - see README.md

package pt.ztp.launchkey3.bitwig;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import pt.ztp.launchkey3.LaunchkeyMk3Configuration;
import pt.ztp.launchkey3.LaunchkeyMk3ControllerDefinition;
import pt.ztp.launchkey3.LaunchkeyMk3ControllerSetup;
import pt.ztp.launchkey3.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Launchkey Mk3 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchkeyMk3ControllerExtensionDefinition extends AbstractControllerExtensionDefinition<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    private static final LaunchkeyMk3ControllerDefinition DEFINITION = new LaunchkeyMk3ControllerDefinition ();


    /**
     * Constructor.
     */
    public LaunchkeyMk3ControllerExtensionDefinition ()
    {
        super (DEFINITION);
    }


    /** {@inheritDoc} */
    @Override
    public String getHelpFilePath ()
    {
        return "https://github.com/akaztp/LaunchKeyMK3#readme";
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration> getControllerSetup (final ControllerHost host)
    {
        return new LaunchkeyMk3ControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
