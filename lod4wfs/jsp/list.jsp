<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.io.*"%>
<%@ page import="de.ifgi.lod4wfs.core.*"%>
<%@ page import="de.ifgi.lod4wfs.facade.*"%>
<%@ page import="de.ifgi.lod4wfs.factory.*"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.text.DecimalFormat"%>

<html lang="en">
<head>
<title>LOD4WFS Administration Interface</title>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.4.4.js"></script>  
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="assets/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">

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
               		out.println("<td><span title=\"Edit feature\"><a title=\"Edit feature\" href=\"edit.jsp?edit="
               				+ fdaFeatures.get(i).getFileName()
               				+ "\"> <i class='fa fa-pencil-square-o fa-lg'></i></a></span></td>");

               		if(fdaFeatures.get(i).getGeometryType()!=null){
               			              		               			
               			if(fdaFeatures.get(i).getGeometryType().toUpperCase().equals("POLYGON")){

               				out.println("<td><span title=\"POLYGON\"><i class=\"fa fa-object-ungroup fa-lg\"></i></span></td>");
               			
               			} else if(fdaFeatures.get(i).getGeometryType().toUpperCase().equals("POINT")){

               				out.println("<td><span title=\"POINT\"><i class=\"fa fa-map-marker fa-lg\"></i></span></td>");

               			} else if(fdaFeatures.get(i).getGeometryType().toUpperCase().equals("MULTIPOLYGON")){

               				out.println("<td><span title=\"MULTIPOLYGON\"><i class=\"fa fa-object-group fa-lg\"></span></i></td>");
               			
						} else if(fdaFeatures.get(i).getGeometryType().toUpperCase().equals("LINESTRING")){

               				out.println("<td><span title=\"LINESTRING\"><i class=\"fa fa-code-fork fa-lg\"></span></i></td>");
               			}             			
               			

               			//out.println("<td><i class=\"fa fa-object-group fa-lg\"><img src=\"img/"+fdaFeatures.get(i).getGeometryType().toLowerCase() +".png\"/ width=\"22\" title=\""+ fdaFeatures.get(i).getGeometryType() +" \"></i></td>");
               		} else {
               			
               			out.println("<td><span title =\"Unknown Geometry Type\"><i class=\"fa fa-question-circle fa-lg\"></i></span></td>");
               			
               		}
               		
               		out.println("<td><span title =\"Size: "+ new DecimalFormat("#.##").format(fdaFeatures.get(i).getSize()/1024.0/1024.0) + " MB" +
								  "\nGeometries: " + fdaFeatures.get(i).getGeometries() + 
								  "\nLast Downloaded: " + fdaFeatures.get(i).getLastAccess() +"\"><i class=\"fa fa-info-circle fa-lg\"></i></span></td>");
               		
               		if (fdaFeatures.get(i).isEnabled()) {

               			out.println("<td><span title=\"Feature enabled\"><i class=\"fa fa-check fa-lg\"></i></span></td>");

               		} else {

               			out.println("<td><span title=\"Feature disabled. This feature will not appear in the Capabilities Document.\"><i class=\"fa fa-ban fa-lg\"></i></span></td>");
               		}
               		
               		out.println("<td><span title=\"Delete feature\"><a title=\"Delete feature\" href='#' onclick='deleteFeature(\""
               				+ fdaFeatures.get(i).getFileName()
               				+ "\");'><i class='fa fa-trash-o fa-lg'></a></i></span></td>");

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