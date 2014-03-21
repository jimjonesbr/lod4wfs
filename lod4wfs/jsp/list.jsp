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
    <h2 class="bs-docs-featurette-title">LOD4WFS Administration Interface <small>(Beta 0.4.2)</small></h2>
    <hr />
    <p> <a href="index.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-home"></span> Home</a> <a href="new.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-plus"></span> Create New Layer</a> </p>
    <hr />
    <div class="panel panel-primary">
      <div class="panel-heading">
        <%
    ArrayList<WFSFeature> dynamicFeatures = new ArrayList<WFSFeature>();
	
	dynamicFeatures = Facade.getInstance().listDynamicFeatures();
	out.println("Layers available (" + dynamicFeatures.size() +  ")");
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
              </tr>
            </thead>
            <tbody>
              <%
			
			for (int i = 0; i < dynamicFeatures.size(); i++) {
				out.println("<tr>");
				//out.println("<td>"+dynamicFeatures.get(i).getName()+"</td>");
				out.println("<td>"+dynamicFeatures.get(i).getTitle()+"</td>");
				out.println("<td>"+dynamicFeatures.get(i).getFeatureAbstract()+"</td>");
				out.println("<td>"+dynamicFeatures.get(i).getKeywords()+"</td>");
				out.println("<td>"+dynamicFeatures.get(i).getEndpoint()+"</td>");								
				out.println("<td><a href=\"edit.jsp?edit="+ dynamicFeatures.get(i).getFileName()+ "\"> <span class='glyphicon glyphicon-pencil'></span></a></td>");
				//out.println("<td><a href=\"list.jsp?delete=" + dynamicFeatures.get(i).getFileName() + "\">  <span class='glyphicon glyphicon-trash text-danger'></span></a></td>");
				out.println("<td><a href='#' onclick='deleteFeature(\"" + dynamicFeatures.get(i).getFileName() + "\");'><span class='glyphicon glyphicon-trash text-danger'></span></a></td>");
				
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
		
 		if(request.getParameter("delete")!= null){
	 		String path = GlobalSettings.getSparqlDirectory()+request.getParameter("delete");
	 		
			WFSFeature feature = new WFSFeature();
	 		feature.setFileName(path);
	 		
			Facade.getInstance().deleteFeature(feature);	
		%>
		
<!-- <script type="text/javascript">
			window.open("list.jsp","_self")
			</script>
 -->
 <%	
		//out.println("SPARQL " + request.getParameter("delete") + " deleted.");
 		}
 		%>
</BODY>
</HTML>