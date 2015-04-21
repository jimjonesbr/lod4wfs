var tmpBBOX = 0;

function init() {


    L.mapbox.accessToken = 'pk.eyJ1Ijoiamltam9uZXMiLCJhIjoib2R0ZUVmTSJ9.9fXpF8LWx9bm2WSW6hg4PQ';	

	LMap = L.mapbox.map('map', 'examples.map-i86nkdio').setView([52, 5], 3);
	markerGroup = new L.featureGroup();
	markerGroup.addTo(LMap);
	//markerGroup.addLayer(omnivore.wkt.parse('POINT(-90 52)'));
	//LMap.fitBounds(markerGroup.getBounds());


    var info = L.control();

	info.onAdd = function (map) {
	    this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
	  //  this.update();
	    return this._div;
	};


	info.addTo(LMap);

	function updateBBox() {
		info.update();
	}

	LMap.on('drag', updateBBox);
	LMap.on('zoomend', updateBBox)


}

function plotGeometry(wkt) {


	markerGroup.clearLayers();
	markerGroup.addLayer(omnivore.wkt.parse(unescape(wkt)));

	if ($('#chkZoom').is(":checked"))
	{
	  LMap.fitBounds(markerGroup.getBounds());
	}
	
}

function setSpatiatConstraint(){
	
	//Disabling Set Spatial Constraint Button
	$('#btnSpatialConstraint').prop('disabled', true);
	$('#btnRemoveSpatialConstraint').prop('disabled', false);

	//Creates a rectangle based on the current BBOX
	var bounds = [[LMap.getBounds().getNorth(), LMap.getBounds().getEast()], [LMap.getBounds().getSouth(), LMap.getBounds().getWest()]];
	tmpBBOX = L.rectangle(bounds, {color: "#ff7800", weight: 1});

	tmpBBOX.addTo(LMap);
	
	//** Setting spatial constraint and executing query to filter out records outside the given BBOX.
	wktBBOX=toWKT(tmpBBOX);
	executeQuery(0);
}

function removeSpatiatConstraint(){
	
	//Enabling Set Spatial Constraint Button
	$('#btnSpatialConstraint').prop('disabled', false);
	$('#btnRemoveSpatialConstraint').prop('disabled', true);

	//** Removing spatial constraint.
	LMap.removeLayer(tmpBBOX);
	wktBBOX="";
}



function toWKT(layer) {
	var lng, lat, coords = [];
	if (layer instanceof L.Polygon || layer instanceof L.Polyline) {

		var latlngs = layer.getLatLngs();

		for (var i = 0; i < latlngs.length; i++) {
			latlngs[i]
			coords.push(latlngs[i].lat + " " + latlngs[i].lng);

			if (i === 0) {
				lng = latlngs[i].lng;
				lat = latlngs[i].lat;
			}
		};

	if (layer instanceof L.Polygon) {
		return "POLYGON((" + coords.join(",") + "," + lat + " " + lng + "))";
	} else if (layer instanceof L.Polyline) {
	return "LINESTRING(" + coords.join(",") + ")";
	}
	} else if (layer instanceof L.Marker) {
	return "POINT(" + layer.getLatLng().lat + " " + layer.getLatLng().lng + ")";
	}
} 
