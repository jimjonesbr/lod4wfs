<%@ page import="java.io.*"%>
<%@ page import="de.ifgi.lod4wfs.core.*"%>
<%@ page import="de.ifgi.lod4wfs.facade.*"%>
<%@ page import="de.ifgi.lod4wfs.factory.*"%>
<%@ page import="com.hp.hpl.jena.query.*"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.hp.hpl.jena.query.ARQ"%>
<%@ page import="java.net.URLEncoder"%>

<HTML>
<HEAD>
<TITLE>LOD4WFS Administration Interface</TITLE>
</HEAD>

	<%
		//Create function getSPARQLFeature(String fileName);

	WFSFeature feature = new WFSFeature();
	
	if(request.getParameter("edit")!=null){
		
		feature = Facade.getInstance().getSPARQLFeature(request.getParameter("edit"));

	}
	
	%>
	
<BODY>
	
		<a href="index.jsp">Home</a>
		<h1>Feature Update</h1>
		
		<FORM action="preview.jsp" NAME="form1" method ="POST" >

				<table border="1">
					<tr>				
						<td>SPARQL Endpoint </td>
						<td><INPUT style="width: 800px; TYPE="text" NAME="endpoint" value="<%=feature.getEndpoint()%>" /></td>
					</tr>				
					<tr>
						<td>Feature Name </td>
						<td><INPUT style="width: 800px; TYPE="text" NAME="feature" value="<%=feature.getName().replace(GlobalSettings.getDynamicFeaturesNameSpace(), "")%>" readonly/></td>
					</tr>
					<tr>
	 					<td>Title </td> 					
	 					<td><INPUT style="width: 800px; TYPE="text" NAME="title" value="<%=feature.getTitle()%>" /></td>
				 	</tr>
				 	<tr>
				 		<td>Abstract </td>
				 		<td><INPUT style="width: 800px; TYPE="text" NAME="abstract" value="<%=feature.getFeatureAbstract()%>" /></td>
				 	</tr>
				 	<tr>
				 		<td>Key-words </td>
				 		<td><INPUT style="width: 800px; TYPE="text" NAME="keywords" value="<%=feature.getKeywords()%>" /></td>
				 	</tr>
					<tr>
						<td>Geometry Variable </td>
						<td><INPUT style="width: 800px; TYPE="text" NAME="variable" value="<%="?"+feature.getGeometryVariable()%>" /></td>
					</tr>
					<tr>
						<td>SPARQL Query </td>
						<td><textarea style="width: 800px; height: 400px; TYPE="text" NAME="query" value='<%=feature.getQuery()%>'><%=feature.getQuery()%></textarea></td>
					</tr>  
								
				</table>
				
				* Feature name cannot be modified.<br/>
		<input type="hidden" id="hiddenId" name="operation" value ="edit" >
		
		<input type="submit" id="btnSave" name="update" value="Preview" />
		
		</FORM>
		
		<%
		
/* 		if(request.getParameter("update")!=null){
			
			
			Facade.getInstance().addFeature
			out.println("Changes saved.");	
			
			
		} */
		
		%>
				
<%-- 				<% 
				
				if(request.getParameter("update")!=null){
					
					
					out.println("Changes at '" + feature.getName() + "' saved. ");

				} else {
									
		 	        Query query = QueryFactory.create(request.getParameter("query"));
		 	        ARQ.getContext().setTrue(ARQ.useSAX); 	       	        
		 	                     
		 	        if(!query.hasLimit()){
		 	        	query.setLimit(10);
		 	        			 	        	
		 	        } else if (query.getLimit()>10){
		 	        	query.setLimit(10);
		 	        }
		 	        
		 	       out.println("* Limited to the first 10 records.");
		 	        
		 	        
		 	        //QueryExecution qexec = QueryExecutionFactory.sparqlService(request.getParameter("endpoint"), query);
		 	        //ResultSet results = qexec.execSelect();
	 	        	 	       
		 	        ResultSet results = Facade.getInstance().executeQuery(query.toString(), request.getParameter("endpoint"));
	 				        
		 			%> 
		 			<table border="1">
		 				<tr>
		 			<% 
		 			
		 			for (int i = 0; i < query.getResultVars().size(); i++) {	
	
		 				out.println("<td><b>"+query.getResultVars().get(i).toString()+"</b></td>");
			 			
		 			}
		 			%> 
			 			</tr>
			 		<%
		 	        while (results.hasNext()) {
		 	            
		 	        	QuerySolution soln = results.nextSolution();
		 	        	
		 	 			%> 
		 	 			<tr>
		 	 			<% 
		 	    		for (int i = 0; i < query.getResultVars().size(); i++) {	
	
		 	    			out.println("<td>"+soln.get("?" + query.getResultVars().get(i).toString())+"</td>"); 	    			
		 	    		}
		 	 			%> 
		 	 			</tr>
		 	 			<%  	           
		 	        }
		 			%> 
		 			</table>
		 			<%		 				 	         	        
	
		 		}
	
		
 		%>
 		
 	 --%>	
 
		
		
</BODY>

</HTML>

