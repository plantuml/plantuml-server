package net.sourceforge.plantuml.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* 
 * Welcome servlet of the webapp.
 * Displays the sample Bob and Alice sequence diagram.
 */
public class Welcome extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

	    // set the sample
        request.setAttribute("net.sourceforge.plantuml.servlet.decoded", "Bob -> Alice : hello");
        request.setAttribute("net.sourceforge.plantuml.servlet.encoded", "SyfFKj2rKt3CoKnELR1Io4ZDoSa70000");
        
        // forward to index.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
	}

}
