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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.ImageData;

/**
 * Utility class to extract the UML source from the compressed UML source contained in the end part
 * of the requested URI.
 */
public abstract class UmlExtractor {

    /**
     * Build the complete UML source from the compressed source extracted from the
     * HTTP URI.
     *
     * @param source the last part of the URI containing the compressed UML
     *
     * @return the textual UML source
     */
    static public String getUmlSource(String source) {
        // build the UML source from the compressed part of the URL
        String text;
        try {
            text = URLDecoder.decode(source, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            text = "' invalid encoded string";
        }
        Transcoder transcoder = TranscoderUtil.getDefaultTranscoder();
        try {
            text = transcoder.decode(text);
        } catch (IOException ioe) {
            text = "' unable to decode string";
        }

        // encapsulate the UML syntax if necessary
        String uml;
        if (text.startsWith("@start")) {
            uml = text;
        } else {
            StringBuilder plantUmlSource = new StringBuilder();
            plantUmlSource.append("@startuml\n");
            plantUmlSource.append(text);
            if (text.endsWith("\n") == false) {
                plantUmlSource.append("\n");
            }
            plantUmlSource.append("@enduml");
            uml = plantUmlSource.toString();
        }
        return uml;
    }

    /**
     * Get image map from uml.
     *
     * @param uml textual diagram source
     *
     * @return image map of the diagram in HTML format if the image has some position information; otherwise `null`
     *
     * @throws IOException if an input or output exception occurred
     */
    public static String extractMap(final String uml) throws IOException {
        return extractMap(uml, FileFormat.PNG);
    }

    /**
     * Get image map from uml.
     *
     * @param uml textual diagram source
     * @param fileFormat underlying file format of uml image
     *
     * @return image map of the diagram in HTML format if the image has some position information; otherwise `null`
     *
     * @throws IOException if an input or output exception occurred
     */
    public static String extractMap(final String uml, final FileFormat fileFormat) throws IOException {
        Diagram diagram = new SourceStringReader(uml).getBlocks().get(0).getDiagram();
        ImageData map = diagram.exportDiagram(new NullOutputStream(), 0, new FileFormatOption(fileFormat, false));
        if (map.containsCMapData()) {
            return map.getCMapData("plantuml");
        }
        return null;
    }

}
