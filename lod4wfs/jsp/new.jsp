<%@page import="de.ifgi.lod4wfs.facade.Facade"%>
<%@page import="com.hp.hpl.jena.query.QueryFactory"%>
<%@page import="com.hp.hpl.jena.query.Query"%>
<%@page import="de.ifgi.lod4wfs.core.WFSFeature"%>
<%@page import="java.io.*"%>
<%@page import="de.ifgi.lod4wfs.core.GlobalSettings"%>
<!DOCTYPE html>
<html lang="en">




<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<script src="http://cdn.leafletjs.com/leaflet-0.7.2/leaflet.js"></script>
<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.2/leaflet.css" />

<script src='https://api.tiles.mapbox.com/mapbox.js/v2.1.4/mapbox.js'></script>
<link href='https://api.tiles.mapbox.com/mapbox.js/v2.1.4/mapbox.css' rel='stylesheet' />
<script src='https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-omnivore/v0.2.0/leaflet-omnivore.min.js'></script>

<script src='https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-label/v0.2.1/leaflet.label.js'></script>
<link href='https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-label/v0.2.1/leaflet.label.css' rel='stylesheet' />
<script src='https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-minimap/v1.0.0/Control.MiniMap.js'></script>
<link href='https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-minimap/v1.0.0/Control.MiniMap.css' rel='stylesheet' />

<script src="js/map.js"></script>

<head>
<title>LOD4WFS Administration Interface</title>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="assets/css/bootstrap.min.css">
</head>
<body  onLoad="javascript:init();">
<div class="bs-docs-featurette">
  <div class="container">
    <h2 class="bs-docs-featurette-title">LOD4WFS Administration Interface </h2>
    <hr />
    <p> <a href="index.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-home"></span> Home</a> <a href="list.jsp" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-list"></span> Manage Layers</a> </p>
    <hr />
    <div class="panel panel-primary">
      <div class="panel-heading"> New SPARQL-Based WFS Layer </div>
      <div class="panel-body">
        <form name="form_feature" method="POST" action="preview.jsp" class="form-horizontal">
          <div class="form-group">
            <label for="endpoint" class="col-sm-2 control-label">SPARQL Endpoint</label>
            <div class="col-sm-10">
              <input type="text" id="endpoint" class="form-control" name="endpoint" value="<%out.println(GlobalSettings.getDefaultSPARQLEndpoint());%>" />
            </div>
          </div>
          <div class="form-group">
            <label for="feature" class="col-sm-2 control-label">Feature Name</label>
            <div class="col-sm-10">
              <input type="text" id="feature" name="feature" class="form-control" value="feature_name" />
            </div>
          </div>
          <div class="form-group">
            <label for="title" class="col-sm-2 control-label">Title</label>
            <div class="col-sm-10">
              <input type="text" id="title" name="title" class="form-control" value="" />
            </div>
          </div>
          <div class="form-group">
            <label for="abstract" class="col-sm-2 control-label">Abstract</label>
            <div class="col-sm-10">
              <input type="text" id="abstract" name="abstract" class="form-control" value="" />
            </div>
          </div>
          <div class="form-group">
            <label for="keywords" class="col-sm-2 control-label">Key-words</label>
            <div class="col-sm-10">
              <input type="text" id="keywords" name="keywords" class="form-control" value="" />
            </div>
          </div>
          <div class="form-group">
            <label for="variable" class="col-sm-2 control-label">Geometry Variable
            <img title="Variable in the SPARQL Query containing the WKT/GML literal." width="25" height="25" src="img/info.png" /></label>
            <div class="col-sm-10">
              <input type="text" id="variable" name="variable" class="form-control" value="?wkt" />
            </div>
          </div>
          
          <div class="form-group">
            
            <div class="col-sm-10">
            
            	<input type="checkbox" id="enable" name="enable" checked="checked" style="display:none;" />
              
          </div></div>
                    
          <div class="form-group">
            <label for="query" class="col-sm-2 control-label">SPARQL Query</label>
            <div class="col-sm-10">
              <textarea id="query" name="query" class="form-control" rows="9"></textarea>
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
              <input type="submit" value="Validate" class="btn btn-success"/>
            </div>
          </div>
          
          
        </form>
      </div>
    </div>
    
     <hr />
    
</div>

</body>
</html>