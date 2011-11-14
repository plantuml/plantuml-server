/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * Project Info:  http://plantuml.sourceforge.net
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
package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.StringUtils;

/** 
 * Delegates the diagram generation from the UML source and
 * the filling of the HTTP response with the diagram in the right format.
 * Its own responsibility is to produce the right HTTP headers.
 */
class DiagramResponse {
    private HttpServletResponse response;
    private FileFormat format;
    private static final Map<FileFormat, String> contentType;
    static {
        Map<FileFormat, String> map = new HashMap<FileFormat, String>();
        map.put(FileFormat.PNG, "image/png");
        map.put(FileFormat.SVG, "image/svg+xml");
        map.put(FileFormat.ATXT, "text/plain");
        contentType = Collections.unmodifiableMap(map);
    }

    DiagramResponse( HttpServletResponse r, FileFormat f) {
        response = r;
        format = f;
    }
    
  
    void sendDiagram( String uml) throws IOException {
        long today = System.currentTimeMillis();
        if ( StringUtils.isDiagramCacheable( uml)) {
            // Add http headers to force the browser to cache the image
            response.addDateHeader( "Expires", today + 31536000000L);
            // today + 1 year
            response.addDateHeader( "Last-Modified", 1261440000000L);
            // 2009 dec 22 constant date in the past
            response.addHeader( "Cache-Control", "public");
        }
        response.setContentType( getContentType());
        SourceStringReader reader = new SourceStringReader( uml);
        reader.generateImage( response.getOutputStream(), new FileFormatOption(format));
        response.flushBuffer();
    }
    
    private String getContentType() {
        return contentType.get( format);
    }
    

}
