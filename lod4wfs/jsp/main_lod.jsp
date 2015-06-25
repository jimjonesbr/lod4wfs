<%@ page import="de.ifgi.lod4wfs.core.Utils"%>
<%@ page import="java.io.*"%>
<%@ page import="de.ifgi.lod4wfs.core.GlobalSettings"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>LOD4WFS Administration Interface</title>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="assets/css/bootstrap.min.css">
</head>
<body>
<div class="bs-docs-featurette"> 
  <div class="container">
    <h2 class="bs-docs-featurette-title">LOD4WFS Administration Interface </h2>
    <h4 class="bs-docs-featurette-title">Linked Open Data for Web Feature Services</h4>
    <hr />
    <p><a href="list.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-wrench"></span> Manage Layers</a> 
    <a href="new.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-plus"></span> Create New Layer</a> 
    <a href="http://<% out.println(Utils.getCanonicalHostName().toString()); %>:<%out.println(Integer.toString(GlobalSettings.getDefaultPort()));%>/<%out.println(GlobalSettings.getDefaultServiceName());%>/wfs/?service=wfs&version=1.0.0&request=GetCapabilities" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-list"></span> Service Capabilities</a></p>
    <hr />
    <div class="panel panel-primary">
      <div class="panel-heading">System Information</div>
      <div class="panel-body">
        <ul>
          <li>Application version: <b><%out.println(GlobalSettings.getAppVersion());%></b></li>
          <%
          	out.println("<li>Default SPARQL Endpoint: <b> "+ GlobalSettings.getDefaultSPARQLEndpoint() + "</b></li>");
                      out.println("<li>Application Started on: <b> "+ GlobalSettings.getStartupTime() + "</b></li>");
                      out.println("<li>Java Runtime: <b> "+ System.getProperty("java.version") + "</b></li>");
                      out.println("<li>Operating System: <b>" + System.getProperty("os.name") + " " + System.getProperty("os.version")+ " (" + System.getProperty("os.arch") + ")</b></li>");
                      out.println("<li>Current memory usage: <b>" + Runtime.getRuntime().freeMemory() / 1024/1024 + "MB </b></li>");                       
                      out.println("<li>LOD4WFS Documentation:  <a href=\"http://ifgi.uni-muenster.de/%7Ej_jone02/lod4wfs/LOD4WFS_documentation.pdf\">User and Developer Manual</a></li>");
                      out.println("<li>Source Code:  <a href=\"https://github.com/jimjonesbr/lod4wfs\">GitHub Repository</a></li>");
                      out.println("<li>Publication (AGILE 2014):  <a href=\"http://www.researchgate.net/publication/260286637_Making_the_Web_of_Data_Available_via_Web_Feature_Services\">Making the Web of Data Available via Web Feature Services</a></li>");
                      //<a href="documentation/LOD4WFS_documentation.pdf" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-wrench"></span> LOD4WFS Documentation </a>
          %>
        </ul>
      </div>
    </div>
     <hr />
     A project from: <br><br>
     <table>
     	<tr>
     		<td width="400" align="center"><a href="http://www.ulb.uni-muenster.de/" target="_blank"><img src="img/ulb_logo.gif" /></a></td>
     		<td width="400" align="center"><a href="http://lodum.de/life" target="_blank"><img width="125" height="80" src="img/life_logo.png" /></a></td>
     		<td width="400" align="center"><a href="http://lodum.de" target="_blank"><img width="200" height="80" src="img/lodum_logo.png" /></a></td>
     		<td width="400" align="center"><a href="http://ifgi.uni-muenster.de" target="_blank"><img width="220" height="80" src="img/ifgi_logo.jpg" /></a></td>
     		<td width="400" align="center"><a href="http://www.uni-muenster.de/de/" target="_blank"><img width="280" height="65" src="img/wwu_logo.jpg" /></a></td>
     	</tr>
     </table>
    
    <hr/>     Copyright © Institut f&#252;r Geoinformatik 2013
</div>
	
</body>
</html>