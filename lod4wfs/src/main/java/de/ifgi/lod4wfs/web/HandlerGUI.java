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
        
        FileReader fileReader = new FileReader("admin/index.jsp");
        BufferedReader buffer = new BufferedReader(fileReader);         
        String tmp = IOUtil.toString(buffer);        
        
		response.getWriter().print(tmp);
              
        if (_body != null) response.getWriter().println(_body);
    }
}