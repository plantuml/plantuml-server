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


class DiagramResponse {
    private HttpServletResponse response;
    private FileFormat format;
    private static final Map<FileFormat, String> contentType;
    static {
        Map<FileFormat, String> map = new HashMap<FileFormat, String>();
        map.put(FileFormat.PNG, "image/png");
        map.put(FileFormat.SVG, "image/svg+xml");
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
