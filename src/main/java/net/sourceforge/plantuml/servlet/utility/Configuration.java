/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * Project Info:  https://plantuml.com
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */
package net.sourceforge.plantuml.servlet.utility;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

/**
 * Shared PlantUML Server configuration.
 */
public final class Configuration {

    /**
     * Singleton configuration instance.
     */
    private static Configuration instance;
    /**
     * Configuration properties.
     */
    private Properties config;

    /**
     * Singleton constructor.
     */
    private Configuration() {
        config = new Properties();

        // Default values
        config.setProperty("SHOW_SOCIAL_BUTTONS", "off");
        config.setProperty("SHOW_GITHUB_RIBBON", "off");
        // End of default values

        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
            if (is != null) {
                config.load(is);
                is.close();
            }
        } catch (IOException e) {
            // Just log a warning
            e.printStackTrace();
        }
    }

    /**
     * Get the configuration.
     *
     * @return the complete configuration
     */
    public static Properties get() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance.config;
    }

    /**
     * Get a boolean configuration value.
     *
     * @param key config property key
     *
     * @return true if the value is "on"
     */
    public static boolean get(final String key) {
        if (get().getProperty(key) == null) {
            return false;
        }
        return get().getProperty(key).startsWith("on");
    }

}
