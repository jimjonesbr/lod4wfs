<%@ page import="java.io.*"%>
<%@ page import="de.ifgi.lod4wfs.core.*"%>
<%@ page import="de.ifgi.lod4wfs.facade.*"%>
<%@ page import="de.ifgi.lod4wfs.factory.*"%>
<%@ page import="java.util.ArrayList"%>

<HTML>
<HEAD>
<TITLE>LOD4WFS Administration Interface</TITLE>
</HEAD>

<BODY>
	<a href="index.jsp">Home</a>
	<a href="new.jsp">Create New Query</a>
	
	<%
    ArrayList<WFSFeature> dynamicFeatures = new ArrayList<WFSFeature>();
	
	dynamicFeatures = Facade.getInstance().listDynamicFeatures();
	out.println("<h1>SPARQL Queries available (" + dynamicFeatures.size() +  ")</h1>");
	%>

<FORM NAME="form1" >
	<table border="1">
		<tr>
			<th>Name</th>
			<th>Title</th>
			<th>Abstract</th>
			<th>Keywords</th>
			<th>Endpoint</th>
			<th>Query</th>
		</tr>
		<%
			
			for (int i = 0; i < dynamicFeatures.size(); i++) {
				out.println("<tr>");
				out.println("<td>"+dynamicFeatures.get(i).getName()+"</td>");
				out.println("<td>"+dynamicFeatures.get(i).getTitle()+"</td>");
				out.println("<td>"+dynamicFeatures.get(i).getFeatureAbstract()+"</td>");
				out.println("<td>"+dynamicFeatures.get(i).getKeywords()+"</td>");
				out.println("<td>"+dynamicFeatures.get(i).getEndpoint()+"</td>");
				out.println("<td>"+ Facade.getInstance().forHTML(dynamicFeatures.get(i).getQuery().toString())+"</td>"); 								
				out.println("<td><a href=\"list.jsp?delete="+ dynamicFeatures.get(i).getFileName()+ "\"> Delete</a></td>");
				out.println("<td><a href=\"edit.jsp?edit="+ dynamicFeatures.get(i).getFileName()+ "\"> Edit</a></td>");
				out.println("</tr>");
				
			}

		%>
	
	</table>
		
</FORM>

		<script type="text/javascript">
		   
		function deleteFeature(file) {
			
			if (confirm('Are you sure you want to delete ' + file + '?')) {
			    							
				alert("<%=GlobalSettings.getSparqlDirectory()%>" + file + " deleted.");
						    
							    		
			} else {

			}
		}
		
		</script>
		
 		<%
		
 		if(request.getParameter("delete")!= null){
	 		String path = GlobalSettings.getSparqlDirectory()+request.getParameter("delete");
	 		
			WFSFeature feature = new WFSFeature();
	 		feature.setFileName(path);
	 		
			Facade.getInstance().deleteFeature(feature);	
		%>
			<script type="text/javascript">
			window.open("list.jsp","_self")
			</script>
		<%	
		out.println("SPARQL " + request.getParameter("delete") + " deleted.");
 		}
 		%> 
		
		
</BODY>

</HTML>
