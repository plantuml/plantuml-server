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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to extract the index and diagram source from an URL, e.g., returned by `request.getRequestURI()`.
 */
public abstract class UrlDataExtractor {

    /**
     * URL regex pattern to easily extract index and encoded diagram.
     */
    private static final Pattern URL_PATTERN = Pattern.compile(
        "/\\w+(?:/(?<idx>\\d+))?(?:/(?<encoded>[^/]+))?/?$"
    );

    /**
     * Get diagram index from URL.
     *
     * @param url URL to analyse, e.g., returned by `request.getRequestURI()`
     *
     * @return if exists diagram index; otherwise -1
     */
    public static int getIndex(final String url) {
        return getIndex(url, -1);
    }

    /**
     * Get diagram index from URL.
     *
     * @param url URL to analyse, e.g., returned by `request.getRequestURI()`
     * @param fallback fallback index if no index exists in {@code url}
     *
     * @return if exists diagram index; otherwise {@code fallback}
     */
    public static int getIndex(final String url, final int fallback) {
        final Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.find()) {
            return fallback;
        }
        String idx = matcher.group("idx");
        if (idx == null) {
            return fallback;
        }
        return Integer.parseInt(idx);
    }

    /**
     * Get encoded diagram source from URL.
     *
     * @param url URL to analyse, e.g., returned by `request.getRequestURI()`
     *
     * @return if exists diagram index; otherwise `null`
     */
    public static String getEncodedDiagram(final String url) {
        return getEncodedDiagram(url, null);
    }

    /**
     * Get encoded diagram source from URL.
     *
     * @param url URL to analyse, e.g., returned by `request.getRequestURI()`
     * @param fallback fallback if no encoded diagram source exists in {@code url}
     *
     * @return if exists diagram index; otherwise {@code fallback}
     */
    public static String getEncodedDiagram(final String url, final String fallback) {
        final Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.find()) {
            return fallback;
        }
        String encoded = matcher.group("encoded");
        if (encoded == null) {
            return fallback;
        }
        return encoded;
    }
}
