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

<BODY>
	
	<%
 			
		WFSFeature feature = new WFSFeature();
		boolean isValidEntry = true;
		
		if(Facade.getInstance().isFeatureNameValid(request.getParameter("feature"))){
							
			if(Facade.getInstance().existsFeature(request.getParameter("feature")) && 
					request.getParameter("store")==null &&
					request.getParameter("operation")==null){
					
				isValidEntry = false;
				out.println("Invalid Feature Name. The feature '" + request.getParameter("feature") + "' already exists. <br>" );						
				
			}  else {
								
				feature.setName(request.getParameter("feature").toLowerCase());
									
			}
		
		
	
		} else {
		
			isValidEntry = false;			
			out.println("Invalid Feature Name. It must contain either alphanumeric characters or '_'. <br>");			
		}
		
		
		if(Facade.getInstance().isEndpointValid(request.getParameter("endpoint"))){
			feature.setEndpoint(request.getParameter("endpoint"));	
		} else {
			isValidEntry = false;
			out.println("Invalid SPARQL Endpoint. The URL is either malformed or cannot be resolved. <br>");

		}
					
		if(Facade.getInstance().isQueryValid(request.getParameter("query"))){ 
			feature.setQuery(request.getParameter("query"));
		} else {
			isValidEntry = false;	
			out.println("Invalid SPARQL Query.  <br>");
			
		}
		
		if(request.getParameter("abstract").equals("")){
			feature.setFeatureAbstract("no abstract");
		} else {
			feature.setFeatureAbstract(request.getParameter("abstract"));	
		}
		
		if(request.getParameter("title").equals("")){
			feature.setTitle("no title");
		} else {
			feature.setTitle(request.getParameter("title"));	
		}
	
		if(request.getParameter("keywords").equals("")){
			feature.setKeywords("no keywords");
		} else {
			feature.setKeywords(request.getParameter("keywords"));	
		}
	
		feature.setGeometryVariable(request.getParameter("variable"));
		
		if(!Facade.getInstance().isVariableValid(feature)){
			isValidEntry = false;
			out.println("Invalid Geometry Variable. The geometry variable provided cannot be found in the given SPARQL query.  <br>");
			
		}
	
		if(isValidEntry){
			
		%>
			<a href="index.jsp">Home</a>
			<h1>Feature Preview</h1>
			
			<FORM NAME="form1" method ="POST">
	
				<table border="1">
					<tr>				
						<td>SPARQL Endpoint </td>
						<td><INPUT style="width: 800px; TYPE="text" NAME="endpoint" value="<%=feature.getEndpoint()%>" readonly/></td>
					</tr>				
					<tr>
						<td>Feature Name </td>
						<td><INPUT style="width: 800px; TYPE="text" NAME="feature" value="<%=feature.getName().toLowerCase()%>" readonly/></td>
					</tr>
					<tr>
	 					<td>Title </td> 					
	 					<td><INPUT style="width: 800px; TYPE="text" NAME="title" value="<%=feature.getTitle()%>" readonly/></td>
				 	</tr>
				 	<tr>
				 		<td>Abstract </td>
				 		<td><INPUT style="width: 800px; TYPE="text" NAME="abstract" value="<%=feature.getFeatureAbstract()%>" readonly/></td>
				 	</tr>
				 	<tr>
				 		<td>Key-words </td>
				 		<td><INPUT style="width: 800px; TYPE="text" NAME="keywords" value="<%=feature.getKeywords()%>" readonly/></td>
				 	</tr>
					<tr>
						<td>Geometry Variable </td>
						<td><INPUT style="width: 800px; TYPE="text" NAME="variable" value="<%=feature.getGeometryVariable()%>" readonly/></td>
					</tr>
					<tr>
						<td>SPARQL Query </td>
						<td><textarea style="width: 800px; height: 400px; TYPE="text" NAME="query" value="<%=Facade.getInstance().forHTML(feature.getQuery())%>" readonly><%=feature.getQuery()%></textarea></td>
					</tr>  
								
				</table>

		
			<input type="submit" id="btnCreate" name="store" value="Save" />
			
			</FORM>
				
				<% 
				
				if(request.getParameter("store")!=null){
					
					Facade.getInstance().addFeature(feature);
					//response.sendRedirect("list.jsp");
					out.println("Feature '" + feature.getName() + "' successfully stored. ");

				} else {
									
		 	        Query query = QueryFactory.create(request.getParameter("query"));
		 	        ARQ.getContext().setTrue(ARQ.useSAX); 	       	        
		 	                     
		 	        if(!query.hasLimit()){
		 	        	query.setLimit(10);
		 	        			 	        	
		 	        } else if (query.getLimit()>10){
		 	        	query.setLimit(10);
		 	        }
		 	        
		 	       out.println("* Limited to the first 10 records.");

	 	        	 	       
		 	        ResultSet results = Facade.getInstance().executeQuery(query.toString(), request.getParameter("endpoint"));
	 				        
		 			
		 			out.println("<table border=\"1\">");
		 			out.println("<tr>");
		 			
		 			for (int i = 0; i < query.getResultVars().size(); i++) {	
	
		 				out.println("<td><b>"+query.getResultVars().get(i).toString()+"</b></td>");
			 			
		 			}
		 			
		 			out.println("</tr>");
		 			
		 			while (results.hasNext()) {
		 	            
		 	        	QuerySolution soln = results.nextSolution();
		 	        	
		 	 			
		 	 			out.println("<tr>");

		 	 			for (int i = 0; i < query.getResultVars().size(); i++) {	
	
		 	    			out.println("<td>"+soln.get("?" + query.getResultVars().get(i).toString())+"</td>"); 	    			
		 	    		}
		 	 			out.println("</tr>");
    
		 	        }
		 			out.println("</table>");
		 				 	         	        
	
		 		}
	
			}	
 		%>
 		
 		
 
		
		
</BODY>

</HTML>
