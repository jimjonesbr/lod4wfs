<%@page import="de.ifgi.lod4wfs.facade.Facade"%>
<%@page import="com.hp.hpl.jena.query.QueryFactory"%>
<%@page import="com.hp.hpl.jena.query.Query"%>
<%@page import="de.ifgi.lod4wfs.core.WFSFeature"%>
<%@page import="java.io.*"%>
<%@page import="de.ifgi.lod4wfs.core.GlobalSettings"%>

<HTML>
<HEAD>
<TITLE>LOD4WFS Administration Interface</TITLE>
</HEAD>

<BODY>
	<a href="index.jsp">Home</a>
	<a href="list.jsp">List Stored Queries</a>
	<H1>New SPARQL-Based WFS Layer </H1>

	<form name="form_feature" method="POST" action="preview.jsp">
	
				<table border="1">
				<tr>				
					<td>SPARQL Endpoint </td>
					<td><INPUT style="width: 500px; TYPE="TEXT" value="<%out.println(GlobalSettings.default_SPARQLEndpoint);%>" name="endpoint"></td>
				</tr>				
				<tr>
					<td>Feature Name </td>
					<td><input style="width: 500px; type="TEXT" value="FeatrureName" name="feature"><br></td>
				</tr>
				<tr>
 					<td>Title </td> 					
 					<td><input style="width: 500px; type="TEXT" value="" name="title"><br></td>
			 	</tr>
			 	<tr>
			 		<td>Abstract </td>
			 		<td><input style="width: 500px; type="TEXT" value="" name="abstract"><br></td>
			 	</tr>
			 	<tr>
			 		<td>Key-words </td>
			 		<td><input style="width: 500px; type="TEXT" value="" name="keywords"><br></td>
			 	</tr>
				<tr>
					<td>Geometry Variable </td>
					<td><input  type="TEXT" value="?wkt" name="variable"><br></td>
				</tr>
				<tr>
					<td>SPARQL Query </td>
					<td><TEXTAREA name="query" style="width: 500px; height: 400px;"></TEXTAREA><br></td>
				</tr>  
			
			
			</table>
		
			<input type="submit" value="Validate"/>
	</form>
	

</BODY>

</HTML>

