package net.sourceforge.plantuml.servlet;

import net.sourceforge.plantuml.FileFormat;

/* 
 * SVG servlet of the webapp.
 * TODO.
 */
@SuppressWarnings("serial")
public class SvgServlet extends UmlDiagramService {

    @Override
    public String getSource( String uri) {
        String[] result = uri.split("/svg/", 2);
        if (result.length != 2) {
            return "";
        } else {
          return result[1];
        }
    }

    @Override
    FileFormat getOutputFormat() {
        return FileFormat.SVG;
    }

}
