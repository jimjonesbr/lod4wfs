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

	<h3>Server Information</h3>

	Default SPARQL Endpoint: <b><% out.println(GlobalSettings.default_SPARQLEndpoint); %></b><br>
	Application Started on: <b><% out.println(GlobalSettings.startupTime); %></b>
	<br><br><a href="paper/index_jones.html">Making the Web of Data Available via Web Feature Services</a><br />
</BODY>

</HTML>
