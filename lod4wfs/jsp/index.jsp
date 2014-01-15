<%@ page import="java.io.*"%>
<%@ page import="de.ifgi.lod4wfs.core.GlobalSettings"%>

<HTML>
<HEAD>
<TITLE>LOD4WFS Administration Interface</TITLE>
</HEAD>

<BODY>
	<br />
	<br />
	<br />
	<h1 style="text-align: center;">LOD4WFS Administration Interface</h1>
	<h2 style="text-align: center;">(Under Construction)</h2>
	
	<a href="list.jsp">Manage Queries</a><br>
	<a href="new.jsp">Create New Query</a>

	<h3>System Information</h3>
	Application version: <b>BETA 0.4.1</b></br>
	<%
	
	out.println("Default SPARQL Endpoint: <b> "+ GlobalSettings.default_SPARQLEndpoint + "</b><br>");
	out.println("Application Started on: <b> "+ GlobalSettings.startupTime + "</b><br>");
	out.println("Java Runtime: <b> "+ System.getProperty("java.version") + "</b><br>");
	out.println("Operating System: <b>" + System.getProperty("os.name") + " " + System.getProperty("os.version")+ " (" + System.getProperty("os.arch") + ")</b><br>");
	out.println("Memory usage: <b>" + Runtime.getRuntime().freeMemory() / 1024/1024 + "MB </b>");
	
	%>
	
	<br><br><a href="paper/index_jones.html">Making the Web of Data Available via Web Feature Services (Draft)</a><br />
</BODY>

</HTML>
