package de.ifgi.lod4wfs.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class ServletGUI extends HttpServlet
{
    /**
	 * Automatically generated serial version UID
	 */
	private static final long serialVersionUID = -8413677464439331672L;
	private String greeting="Hello World";
    public ServletGUI(){}
    public ServletGUI(String greeting)
    {
        this.greeting=greeting;
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>"+greeting+"</h1>");
        response.getWriter().println("session=" + request.getSession(true).getId());
    }
    
}