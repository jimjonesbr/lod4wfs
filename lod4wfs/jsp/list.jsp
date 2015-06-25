<%@ page import="java.io.*"%>
<%@ page import="de.ifgi.lod4wfs.core.*"%>
<%@ page import="de.ifgi.lod4wfs.facade.*"%>
<%@ page import="de.ifgi.lod4wfs.factory.*"%>
<%@ page import="java.util.ArrayList"%>
<html lang="en">
<head>
<title>LOD4WFS Administration Interface</title>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.4.4.js"></script>  
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="assets/css/bootstrap.min.css">
</head>
<body>
<div class="bs-docs-featurette">
  <div class="container">
    <h2 class="bs-docs-featurette-title">LOD4WFS Administration Interface </h2>
    <hr />
    <p> <a href="index.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-home"></span> Home</a> <a href="new.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-plus"></span> Create New Layer</a> </p>
    <hr />
    <div class="panel panel-primary">
      <div class="panel-heading">
        <%
        	ArrayList<WFSFeature> fdaFeatures = new ArrayList<WFSFeature>();

        	fdaFeatures = Facade.getInstance().listFDAFeatures();
        	out.println("Layers available (" + fdaFeatures.size() + ")");
        %>
      </div>
      <div class="panel-body">
      
        <FORM NAME="form1" >
          <table class="table table-condensed table-hover table-striped table-bordered">
            <thead>
              <tr>                
                <th>Title</th>
                <th>Abstract</th>
                <th>Keywords</th>
                <th>Endpoint</th>
                <th></th>
                <th></th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <%
              	for (int i = 0; i < fdaFeatures.size(); i++) {
                            		
                            		out.println("<tr>");
                            		out.println("<td>" + fdaFeatures.get(i).getTitle() + "</td>");
                            		out.println("<td>" + fdaFeatures.get(i).getFeatureAbstract()
                            				+ "</td>");
                            		out.println("<td>" + fdaFeatures.get(i).getKeywords() + "</td>");
                            		out.println("<td>" + fdaFeatures.get(i).getEndpoint() + "</td>");
                            		out.println("<td><a title=\"Edit feature\" href=\"edit.jsp?edit="
                            				+ fdaFeatures.get(i).getFileName()
                            				+ "\"> <span class='glyphicon glyphicon-pencil'></span></a></td>");

                            		if (fdaFeatures.get(i).isEnabled()) {

                            			out.println("<td><img src=\"img/ok.png\"/ width=\"20\" title=\"Feature enabled\"></td>");

                            		} else {

                            			out.println("<td><img src=\"img/nok.png\"/ width=\"21\" title=\"Feature disabled. This feature will not appear in the Capabilities Document.\"></td>");
                            		}
                            		
                            		out.println("<td><a title=\"Delete feature\" href='#' onclick='deleteFeature(\""
                            				+ fdaFeatures.get(i).getFileName()
                            				+ "\");'><span class='glyphicon glyphicon-trash text-danger'></span></a></td>");

                            		out.println("</tr>");

                            	}
              %>
            </tbody>
          </table>
        </FORM>
        
      </div>
    </div>
     <hr />
    </div>
</div>



<script type="text/javascript">
		
		function deleteFeature(file) {
			
			if (confirm('Are you sure you want to delete the layer \"' + file + '\"?')) {
			    											
			    $.ajax({  
			        type:"POST",      
			        url: "list.jsp",  
			        data:"delete="+file,           
			          success: function(success) {  
			        	  window.location.reload(true);        
			          }  
			        }); 
					
				
		
			} else {
		
			}
		}

</script>

<%
	if (request.getParameter("delete") != null) {
		String path = GlobalSettings.getFeatureDirectory() + request.getParameter("delete");

		WFSFeature feature = new WFSFeature();
		feature.setFileName(path);

		Facade.getInstance().deleteFeature(feature);
	}

	
%>
	
</BODY>
</HTML>