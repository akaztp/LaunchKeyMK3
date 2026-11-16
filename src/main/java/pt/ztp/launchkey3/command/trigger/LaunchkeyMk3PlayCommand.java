// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
// Modified by akaztp (2026) for the LaunchKeyMK3 project - see README.md

package pt.ztp.launchkey3.command.trigger;

import pt.ztp.launchkey3.LaunchkeyMk3Configuration;
import pt.ztp.launchkey3.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the play button.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchkeyMk3PlayCommand extends PlayCommand<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public LaunchkeyMk3PlayCommand (final IModel model, final LaunchkeyMk3ControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.transport.returnToArrangement ();
    }
}
