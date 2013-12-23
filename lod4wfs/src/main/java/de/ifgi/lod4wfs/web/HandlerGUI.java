package de.ifgi.lod4wfs.web;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.plexus.util.IOUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
 
public class HandlerGUI extends AbstractHandler {
 
    final String _greeting;
 
    final String _body;
 
    public HandlerGUI() {
        _greeting = "Welcome!";
        _body = null;
    }
 
    public HandlerGUI(String greeting) {
        _greeting = greeting;
        _body = null;
    }
 
    public HandlerGUI(String greeting, String body) {
        _greeting = greeting;
        _body = body;
    }
 
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        
//        FileReader fileReader = new FileReader("html/index_jones.html");
//        BufferedReader buffer = new BufferedReader(fileReader);         
//        String tmp = IOUtil.toString(buffer);        
//		  response.getWriter().println(tmp);
        
        response.getWriter().println("<br/><br/><br/><h1 style=\"text-align:center;\">LOD4WFS Administration Interface</h1><br/>" +
        							 "<h2 style=\"text-align:center;\">(Under Construction)</h2>");
        
        if (_body != null) response.getWriter().println(_body);
    }
}