<%@ page import="java.io.*"%>
<%@ page import="de.ifgi.lod4wfs.core.*"%>
<%@ page import="de.ifgi.lod4wfs.facade.*"%>
<%@ page import="de.ifgi.lod4wfs.factory.*"%>
<%@ page import="com.hp.hpl.jena.query.*"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.hp.hpl.jena.query.ARQ"%>
<%@ page import="java.net.URLEncoder"%>
<!DOCTYPE html>


<html lang="en">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script src="js/jquery.bpopup.min.js"></script>
<head>
<title>LOD4WFS Administration Interface</title>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="assets/css/bootstrap.min.css">
<link rel="stylesheet" href="assets/css/popup.css">
</head>
<body>
<div class="bs-docs-featurette">
  <div class="container">
    <h2 class="bs-docs-featurette-title">LOD4WFS Administration Interface </h2>
    <hr />
    <p> <a href="index.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-home"></span> Home</a> <a href="list.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-list"></span> Manage Layers</a> </p>
    <hr />
    <div class="panel panel-primary">
      <div class="panel-heading"> Feature Preview </div>
      <div class="panel-body">
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
	
 		if(request.getParameterValues("enable")!=null){
			feature.setEnabled(true);
		} else {
			feature.setEnabled(false);	
		} 
		
		feature.setGeometryVariable(request.getParameter("variable"));
		feature.setCRS(request.getParameter("crs"));
		
		if(!Facade.getInstance().isVariableValid(feature)){
			isValidEntry = false;
			out.println("Invalid Geometry Variable. The geometry variable provided cannot be found in the given SPARQL query.  <br>");
			
		}
	
		if(isValidEntry){
			
		%>
        <FORM NAME="form1" method="POST" class="form-horizontal">
          <div class="form-group">
            <label for="endpoint" class="col-sm-2 control-label">SPARQL Endpoint</label>
            <div class="col-sm-10">
              <input type="text" id="endpoint" class="form-control" name="endpoint" value="<%=feature.getEndpoint()%>" readonly/> 
              <%--<code ><%=feature.getEndpoint()%></code> --%>
            </div>
          </div>
          <div class="form-group">
            <label for="feature" class="col-sm-2 control-label">Feature Name</label>
            <div class="col-sm-10">
              <input type="text" id="feature" name="feature" class="form-control" value="<%=feature.getName().toLowerCase()%>" readonly/>
              <%-- <code ><%=feature.getName().toLowerCase()%></code> --%>
            </div>
          </div>
          <div class="form-group">
            <label for="title" class="col-sm-2 control-label">Title</label>
            <div class="col-sm-10">
              <input type="text" id="title" name="title" class="form-control" value="<%=feature.getTitle()%>" readonly/>
              <%-- <code ><%=feature.getTitle()%></code> --%>
            </div>
          </div>
          <div class="form-group">
            <label for="abstract" class="col-sm-2 control-label">Abstract</label>
            <div class="col-sm-10">
              <input type="text" id="abstract" name="abstract" class="form-control" value="<%=feature.getFeatureAbstract()%>" readonly/> 
              <%-- <code ><%=feature.getFeatureAbstract()%></code>--%>
            </div>
          </div>
          <div class="form-group">
            <label for="keywords" class="col-sm-2 control-label">Key-words</label>
            <div class="col-sm-10">
               <input type="text" id="keywords" name="keywords" class="form-control" value="<%=feature.getKeywords()%>" readonly/> 
              <%-- <code "><%=feature.getKeywords()%></code>--%>
            </div>
          </div>
          <div class="form-group">
            <label for="variable" class="col-sm-2 control-label">Geometry Variable</label>
            <div class="col-sm-10">
               <input type="text" id="variable" name="variable" class="form-control" value="<%=feature.getGeometryVariable()%>" readonly/> 
              <%--<code "><%=feature.getGeometryVariable()%></code>--%>
            </div>
          </div>
          
          <div class="form-group">
            <label for="crs" class="col-sm-2 control-label">CRS
            <img onclick="$('#popup').bPopup();" title="Automatically generated based on the WKT/GML literal. In case no CRS is found, WGS84 is assumed." width="25" height="25" src="img/info.png" /></label>
                        
            <div class="col-sm-10">
               <input type="text" id="crs" name="crs" class="form-control" value="<%request.getParameter("crs");%>" readonly/> 
              
            </div>
          </div>
          
          <div class="form-group">
            <label for="query" class="col-sm-2 control-label">SPARQL Query</label>
            <div class="col-sm-10">
              <textarea id="query" name="query" class="form-control" rows="13" readonly><%=feature.getQuery()%></textarea>
              <%-- <code><%=feature.getQuery()%></code> --%>
            </div>
          </div>
          
          <div class="form-group">
            <label for="variable" class="col-sm-2 control-label">Feature Enabled</label>
            <div class="col-sm-10">
            
            	<%
                        		if(feature.isEnabled()){
                        		            		
                        		            		out.println("<td><img src=\"img/ok.png\"/ width=\"20\" title=\"Feature enabled.\"></td>");
                        		            		out.println("<input type=\"checkbox\" id=\"enable\" name=\"enable\" checked=\"checked\" style=\"display:none;\" />");
                        		            		
                        		            	} else {
                        		            		
                        		            		out.println("<td><img src=\"img/nok.png\"/ width=\"20\" title=\"Feature disabled. This feature will not appear in the Capabilities Document.\"></td>");
                        		            		out.println("<input type=\"checkbox\" id=\"enable\" name=\"enable\" style=\"display:none;\" />");
                        		            	}
                        	%>
              
            </div>
          </div>
          
          <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
              <input type="submit" id="btnCreate" name="store" value="Save" class="btn btn-success"/>
            </div>
          </div>
        </FORM>
      </div>
    </div>
   <div class="row"> <div class="col-md-offset-2 col-md-8"><%
 	if(request.getParameter("store")!=null){
 			
 			out.println(feature.isEnabled()+" <- enabled");
 			Facade.getInstance().addFeature(feature);
 		    
 			out.println("<script>alert(\"Feature '" + feature.getName() + "' successfully stored. \")</script>");
 			response.sendRedirect("list.jsp"); 
 			
 		} else {
 							
 		 	        Query query = QueryFactory.create(request.getParameter("query"));
 		 	        ARQ.getContext().setTrue(ARQ.useSAX); 	       	        
 		 	                     
 		 	        int previewLimit = GlobalSettings.getPreviewLimit();
 		 	        
 		 	        if(!query.hasLimit()){
 		 	        	
 		 	        	query.setLimit(previewLimit);
 		 	        			 	        	
 		 	        } else if (query.getLimit()>previewLimit){
 		 	        	
 		 	        	query.setLimit(previewLimit);
 		 	        	
 		 	        }
 		 	        
 		 	        out.println("<p>* Limited to the first " + previewLimit + " records.</p>");
 	 	        	 	       
 		 	        ResultSet results = Facade.getInstance().executeQuery(query.toString(), request.getParameter("endpoint"));
 		 			
 		 			out.println("<table border=\"1\" class='table table-condensed table-hover table-striped table-bordered'>");
 		 			out.println("<thead><tr>");
 		 			
 		 			for (int i = 0; i < query.getResultVars().size(); i++) {	
 	
 		 				out.println("<th>"+query.getResultVars().get(i).toString()+"</th>");
 	 			
 		 			}
 		 			
 		 			out.println("</tr></thead><tbody>");
 		 			
 		 			while (results.hasNext()) {
 		 	            
 		 	        	QuerySolution soln = results.nextSolution();

 		 	 			out.println("<tr>");

 		 	 			for (int i = 0; i < query.getResultVars().size(); i++) {	
 						
 		 	    			
 		 	    			if(("?"+query.getResultVars().get(i).toString()).equals(feature.getGeometryVariable())){
 		 	    				
 		 	    				String crs = Facade.getInstance().getCoordinateReferenceSystem(soln.get("?" + query.getResultVars().get(i)).toString()); 
 		 	    				String geometryType = Facade.getInstance().getGeomeryType(soln.get("?" + query.getResultVars().get(i)).toString());
 						
 		 	    				if(geometryType.equals("gml:MultiPointPropertyType")){
 		 	    				
 		 	    					geometryType = "POINT";

 		 	    				}
 		 	    				
 		 	    				if(geometryType.equals("gml:MultiPolygonPropertyType")){
 		 	    				
 		 	    					geometryType = "POLYGON";
 		 	    					
 		 	    				}
 		 	    				
 		 	    				if(geometryType.equals("gml:MultiLineStringPropertyType")){
 	 	    				
 		 	    					geometryType = "LINESTRING";
 		 	    					
 		 	    				}

 		 	    						 	    				
 		 	    				out.println("<td><img src = \"img/" + geometryType.toLowerCase() + 
 		 	    						      ".png\" alt = \"" + geometryType + 
 		 	    						        "\" title = \"" + geometryType + "\"" +
 		 	    						        "  height = 51 width = 51> </td>");	
 		 	    				
 		 	    				feature.setCRS(crs);
 		 	    						 	    				
 		 	    				out.println("<script type='text/javascript'>$('#crs').val('"+ crs +"');</script>");
 		 	    				
 		 	    			} else {
 		 	    				
 		 	    				out.println("<td>"+soln.get("?" + query.getResultVars().get(i).toString())+"</td>");
 		 	    				
 		 	    			}
 		 	    			
 		 	    		}
 		 	 			out.println("</tr>");
     
 		 	        }
 		 			out.println("</tbody></table>");

 		 		}
 	
 	}
 %></div></div>
         <hr />
    
  </div>
</div>
</BODY>
</HTML>