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
	<FORM NAME="form1">
	
			<table border="1">
				<tr>				
					<td>SPARQL Endpoint </td>
					<td><INPUT style="width: 500px; TYPE="TEXT" value="<%out.println(GlobalSettings.default_SPARQLEndpoint);%>" name="txtEndpoint"></td>
				</tr>				
				<tr>
					<td>Feature Name </td>
					<td><input style="width: 500px; type="TEXT" value="Name" name=txtName><br></td>
				</tr>
				<tr>
 					<td>Title </td> 					
 					<td><input style="width: 500px; type="TEXT" value="Title" name=txtTitle><br></td>
			 	</tr>
			 	<tr>
			 		<td>Abstract </td>
			 		<td><input style="width: 500px; type="TEXT" value="Abstract" name=txtAbstract><br></td>
			 	</tr>
			 	<tr>
			 		<td>Key-words </td>
			 		<td><input style="width: 500px; type="TEXT" value="Key-words" name=txtKeywords><br></td>
			 	</tr>
				<tr>
					<td>Geometry Variable </td>
					<td><input  type="TEXT" value="?wkt" name=txtVariable><br></td>
				</tr>
				<tr>
					<td>SPARQL Query </td>
					<td><TEXTAREA name="txtQuery" style="width: 500px; height: 400px;  ROWS="5" value="select ?wkt where {} limit 1"></TEXTAREA><br></td>
				</tr>  
			
			
			</table>
	<input type="submit" id="btnCreate" name="btnCreate" value="Create"/>
		
		
	</FORM>

	<%if(request.getParameter("btnCreate")!=null) //btnCreate is the name of your button, not id of that button.
	{
		
		WFSFeature feature = new WFSFeature();
		boolean isValidEntry = true;
		
		if(Facade.getInstance().isFeatureNameValid(request.getParameter("txtName"))){
			
			if(Facade.getInstance().existsFeature(request.getParameter("txtName"))){
				isValidEntry = false;
				out.println("Invalid Feature Name. The feature '" + request.getParameter("txtName") + "' already exists." );
				%><br><%			
				
			} else {
				feature.setName(request.getParameter("txtName"));				
			}
			
		} else {
			isValidEntry = false;
			out.println("Invalid Feature Name. It must contain either alphanumeric characters or '_'.");
			%><br><%			
		}
		
		if(Facade.getInstance().isEndpointValid(request.getParameter("txtEndpoint"))){
			feature.setEndpoint(request.getParameter("txtEndpoint"));	
		} else {
			isValidEntry = false;
			out.println("Invalid SPARQL Endpoint.");
			%><br><%
		}
					
		if(Facade.getInstance().isQueryValid(request.getParameter("txtQuery"))){ //
			feature.setQuery(request.getParameter("txtQuery"));
		} else {
			isValidEntry = false;	
			out.println("Invalid SPARQL Query.");
			%><br><%
		}
		
		if(request.getParameter("txtAbstract").equals("")){
			feature.setFeatureAbstract("no abstract given");
		} else {
			feature.setFeatureAbstract(request.getParameter("txtAbstract"));	
		}
		
		if(request.getParameter("txtTitle").equals("")){
			feature.setTitle("no title given");
		} else {
			feature.setTitle(request.getParameter("txtTitle"));	
		}

		if(request.getParameter("txtKeywords").equals("")){
			feature.setKeywords("no keywords given");
		} else {
			feature.setKeywords(request.getParameter("txtKeywords"));	
		}

		feature.setGeometryVariable(request.getParameter("txtVariable"));
		
		if(!Facade.getInstance().isVariableValid(feature)){
			isValidEntry = false;
			out.println("Invalid Geometry Variable. The geometry variable provided cannot be found in the given SPARQL query.");
			%><br><%
		}
		
		if(isValidEntry){
			Facade.getInstance().addFeature(feature);
			out.println("Feature '" + feature.getName() + "' successfully stored. ");
		}
		

	} %>



</BODY>

</HTML>

