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
    <h2 class="bs-docs-featurette-title">LOD4WFS Administration Interface <small>(Beta 0.4.2)</small></h2>
    <h4 class="bs-docs-featurette-title"><small>Linked Open Data for Web Feature Services</small></h4>
    <hr />
    <p><a href="list.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-wrench"></span> Manage Layers</a> <a href="new.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-plus"></span> Create New Layer</a></p>
    <hr />
    <div class="panel panel-primary">
      <div class="panel-heading">System Information</div>
      <div class="panel-body">
        <ul>
          <li>Application version: <b>BETA 0.4.2</b></li>
          <%
            out.println("<li>Default SPARQL Endpoint: <b> "+ GlobalSettings.default_SPARQLEndpoint + "</b></li>");
            out.println("<li>Application Started on: <b> "+ GlobalSettings.startupTime + "</b></li>");
            out.println("<li>Java Runtime: <b> "+ System.getProperty("java.version") + "</b></li>");
            out.println("<li>Operating System: <b>" + System.getProperty("os.name") + " " + System.getProperty("os.version")+ " (" + System.getProperty("os.arch") + ")</b></li>");
            out.println("<li>Memory usage: <b>" + Runtime.getRuntime().freeMemory() / 1024/1024 + "MB </b></li>");
            //out.println("<li>AGILE 2014 (Publication):  <a href=\"http://ifgi.uni-muenster.de/~j_jone02/publications/AGILE2014-LOD4WFS.pdf\">Making the Web of Data Available via Web Feature Services</a></li>");
            out.println("<li>AGILE 2014 (Publication):  <a href=\"http://www.researchgate.net/publication/260286637_Making_the_Web_of_Data_Available_via_Web_Feature_Services\">Making the Web of Data Available via Web Feature Services</a></li>");            
            %>
        </ul>
      </div>
    </div>
     <hr />
     A project of: <br><br>
     <table>
     	<tr>
     		<td width="400" align="center"><a href="http://lodum.de/life" target="_blank"><img width="145" height="90" src="img/life_logo.png" /></a></td>
     		<td width="400" align="center"><a href="http://lodum.de" target="_blank"><img width="220" height="90" src="img/lodum_logo.png" /></a></td>
     		<td width="400" align="center"><a href="http://ifgi.uni-muenster.de" target="_blank"><img width="240" height="90" src="img/ifgi_logo.jpg" /></a></td>
     		<td width="400" align="center"><a href="http://www.uni-muenster.de/de/" target="_blank"><img width="340" height="85" src="img/wwu_logo.jpg" /></a></td>
     	</tr>
     </table>
    
    <hr/>     Copyright © Institut f&#252;r Geoinformatik 2014
</div>
	
</body>
</html>