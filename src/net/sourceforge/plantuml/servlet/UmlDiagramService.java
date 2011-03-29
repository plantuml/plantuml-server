package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;

@SuppressWarnings("serial")
public abstract class UmlDiagramService extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // build the uml source from the compressed request parameter
        String text = URLDecoder.decode( getSource( request.getRequestURI()), "UTF-8");
        Transcoder transcoder = getTranscoder();
        text = transcoder.decode(text);
        StringBuilder plantUmlSource = new StringBuilder();
        plantUmlSource.append("@startuml\n");
        plantUmlSource.append( text);
        if (text.endsWith("\n") == false) {
            plantUmlSource.append("\n");
        }
        plantUmlSource.append("@enduml");
        final String uml = plantUmlSource.toString();

        // generate the response
        DiagramResponse dr = new DiagramResponse( response, getOutputFormat());
        dr.sendDiagram( uml);
    }
    
    abstract public String getSource( String uri);
    abstract FileFormat getOutputFormat();
    
    private Transcoder getTranscoder() {
        return TranscoderUtil.getDefaultTranscoder();
    }
}
