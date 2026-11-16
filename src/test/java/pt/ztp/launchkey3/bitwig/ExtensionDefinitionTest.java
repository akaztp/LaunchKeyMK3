// Written by akaztp (2026) for the LaunchKeyMK3 project - see README.md
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package pt.ztp.launchkey3.bitwig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.bitwig.extension.ExtensionDefinition;
import com.bitwig.extension.controller.ControllerExtensionDefinition;


class ExtensionDefinitionTest
{
    @Test
    void theExtensionIsRegisteredWithItsOwnIdAndName ()
    {
        final List<ControllerExtensionDefinition> definitions = new ArrayList<> ();
        for (final ExtensionDefinition extension: ServiceLoader.load (ExtensionDefinition.class))
            definitions.add ((ControllerExtensionDefinition) extension);
        assertEquals (1, definitions.size ());

        final ControllerExtensionDefinition definition = definitions.get (0);
        assertEquals ("Launchkey Mk3 (LK3)", definition.getName ());
        assertEquals ("Novation", definition.getHardwareVendor ());
        assertTrue (definition.getAuthor ().contains ("Moßgraber"), "credits DrivenByMoss");

        // Must differ from the DrivenByMoss Launchkey Mk3 extension ID
        assertFalse (UUID.fromString ("10CB5692-541C-4A5D-9EB4-07D80F34A02C").equals (definition.getId ()));
    }
}
