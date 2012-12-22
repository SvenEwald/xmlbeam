package org.xmlbeam.tutorial.e03_eclipse;

import java.util.List;

import org.xmlbeam.URI;
import org.xmlbeam.Xpath;

/**
 * We proceed with our examples to parameterized projections. Because
 * projections will be compiled and processed when used, there is no need to
 * keep them static. Instead give your getter method some parameters. They will
 * be applied as a {@lik MessageFormat} on the Xpath expression. (This is
 * possible on URI annotations, too).
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
@URI("resource://eclipsecodeformatprofile.xml")
public interface EclipseFormatterConfigFile {

	interface Setting {
		@Xpath("@id")
		String getName();

		@Xpath("@value")
		String getValue();
	}

	@Xpath(value = "//profile/@name", targetComponentType = String.class)
	List<String> getProfiles();

	@Xpath(value = "//profiles/profile[@name=\"{0}\"]/setting", targetComponentType = String.class)
	List<Setting> getAllSettingsForProfile(String profileName);
}
