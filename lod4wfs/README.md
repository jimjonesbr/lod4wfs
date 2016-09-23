# LOD4WFS - Linked Open Data for Web Feature Services Adapter (Beta)


The LOD4WFS Adapter (Linked Open Data for Web Feature Services) is a service to provide access to Linked Geographic Data from Geographical Information Systems (GIS). It implements a service which listens to WFS requests and converts these requests into the SPARQL Query Language for RDF. After the SPARQL Query is processed, the LOD4WFS Adapter receives the RDF result set from the Triple Store, encodes it as a WFS XML document, and returns it to the client, e.g. a GIS.
This approach enables current GIS to transparently have access to geographic LOD data sets, using their implementation of WFS, without any adaptation whatsoever being necessary. In order to reach a higher number of GIS, the currently most common implementation of WFS has been adopted for the LOD4WFS Adapter, namely the OGC Web Feature Service Implementation Specification 1.0.0. It provides the basic functions offered by the WFS reference implementation, GeoServer.

[LOD4WFS Documentation](http://ifgi.uni-muenster.de/~j_jone02/lod4wfs/LOD4WFS_documentation.pdf)

The LOD4WFS Adapter enables access to geographic LOD data sets in two different ways: *Standard Data Access (SDA)* and *Federated Data Access (FDA)*. To cut a long story short: SDA very easy to use but, but not very flexibel. FDA a bit more complex to use (you have to write you own SPARQL Queries), but very flexibel. 

Click [here](http://linkeddata.uni-muenster.de/lod4wfs/download.html) to download the latest stable releases.

## Standard Data Access (SDA) 

The Standard Data Access feature was designed in order to enable access to all geographic LOD data sets contained in a triple store. This feature basically works as an explorer, looking for geographic LOD data sets from a certain Triple
Store and making them available via WFS. Due to the possibility of describing different types of geometries (polygons, lines, points) and many different coordinate reference systems, which are characteristic requirements of a WFS, we chose by default the GeoSPARQL Vocabulary as an input requirement for the Standard Data Access feature. The example bellow shows how geometries and their related attributes are expected to be structured. The geometries are encoded as WKT literals and the attributes of Features are linked to the instance of the *geo:Geometry* class via RDF Schema and Dublin Core Metadata Element Set vocabularies. However, there are no constraints on which vocabularies or
properties may be used for describing attributes. For a full version of the data set click [here](lod4wfs/lod4wfs/datasets/brazil_municipalities.nt.zip).

```
@prefix geo: <http://www.opengis.net/ont/geosparql/1.0#>.
@prefix my: <http://ifgi.lod4wfs.de/resource/>.
@prefix sf: <http://www.opengis.net/ont/geosparql#>.
@prefix dc: <http://purl.org/dc/elements/1.1/>.
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.
@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.

my:FEATURE_RECIFE a geo:Feature ;
	rdf:ID "2611606"^^xsd:integer ;
	dc:description "Recife"^^xsd:string ;
	geo:hasGeometry my:GEOMETRY_RECIFE .
my:GEOMETRY_RECIFE a geo:Geometry ;
	geo:asWKT "POLYGON ((-35.0148559599999984 -8.0564907399999992,-34.9939074400000010 -8.0493884799999993, ...
-35.0148559599999984 -8.0564907399999992))"^^sf:wktLiteral .
```

In order to make the data sets discoverable via the Standard Data Access feature, additional metadata must be added to the data sets. We make use of Named Graphs for this purpose. Every Named Graph in the LOD data source must contain only objects of the same feature type. This approach facilitates the discovery of Features, speeding up queries that list the Features available in the triple store. In case a Named Graph contains multiple Feature types, the features can be split into different layers using the Federated Data Access (see next section). Finally, each Named Graph needs to be described by certain RDF properties, namely abstract, title and subject from the Dublin Core Terms Vocabulary. These properties help the adapter to classify all Features available in a Triple Store, so that they can be further on discovered by the WFS client through the WFS Capabilities Document. 

```
@prefix dct: <http://purl.org/dc/terms/>.
@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.

<http://ifgi.lod4wfs.de/graph/municipalities> dct:abstract "Municipalities of the Brazilian Federal States."^^xsd:string .
<http://ifgi.lod4wfs.de/graph/municipalities> dct:title "Brazilian Municipalities"^^xsd:string .
<http://ifgi.lod4wfs.de/graph/municipalities> dct:subject "municipalities boundaries"^^xsd:string .
```


## Federated Data Access (FDA) 

The Federated Data Access feature offers the possibility of accessing geographic LOD data sets based on predefined SPARQL Queries. Differently than the Standard Data Access, the Federated Data Access feature is able to execute SPARQL Queries to multiple SPARQL Endpoints, thus enabling WFS Features to be composed of data from different sources. As a proof of concept of what can be achieved, the SPARQL Query bellow shows an example of a federated query, combining data from DBpedia and Ordnance Survey of Great Britain. The SPARQL Query is executed against the [Ordnance Survey’s SPARQL Endpoint](http://data.ordnancesurvey.co.uk/datasets/os-linked-data/explorer/sparql), retrieving the GSS Code and geographic coordinates from districts of Great Britain – the coordinates are provided by the Ordnance Survey using the WGS84 lat/long Vocabulary, but this example converts them to WKT literals using the function CONCAT. Afterwards, the retrieved entries are filtered by matching the districts’ names with DBpedia entries from the east of England, which are written in English language. The [result of this SPARQL Query](https://goo.gl/s8F17E) can be further on listed as a single WFS Feature via the LOD4WFS Adapter, thereby providing a level of interoperability between data sets that is currently unachievable by any implementation of WFS, whether using Shapefiles or geographic databases.

```sparql
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX dbpo: <http://dbpedia.org/ontology/>
PREFIX dbp: <http://dbpedia.org/resource/>
PREFIX wgs84: <http://www.w3.org/2003/01/geo/wgs84_pos#>
PREFIX os: <http://data.ordnancesurvey.co.uk/ontology/admingeo/>

SELECT ?abstract ?name ?gss(CONCAT("POINT(", xsd:string(?long),"", xsd:string(?lat), ")") AS ?wkt)
WHERE { ?subject rdfs:label ?name .
		?subject wgs84:lat ?lat .
		?subject wgs84:long ?long .
		?subject os:gssCode ?gss .
		?subject a os:District
		SERVICE <http://dbpedia.org/sparql/> {
				?entry rdfs:label ?place .
				?entry dbpo:abstract ?abstract .
				?entry dbpo:isPartOf dbp:East_of_England
				FILTER langMatches(lang(?place), "EN")
				FILTER langMatches(lang(?abstract), "EN")
				FILTER ( STR(?place) = ?name )
		}
}
```



## Setting up your environment

To setup your WFS Adapter environment properly, first you have to start playing with the settings file, which is - surprise, surprise - located at `settings` folder. The settings file is divided in the following sections.

#### [GetCapabilities]

Properties used for displaying the SDA Features metadata in the Capabilities Document.

`title` : RDF property for the Features’ title. Default value: `<http://purl.org/dc/terms/title>`

`abstract` : RDF property for the Features’ abstract. Default value: `<http://purl.org/dc/terms/abstract>`

`keywords` : RDF property for the Features’ subject. Default value: `<http://purl.org/dc/terms/subject>`

#### [Geometry]

Predicates and classes used for identifying geometries (SDA compliant) in SPARQL Endpoints. 

`geometryPredicate` : `<http://www.opengis.net/ont/geosparql/1.0#asWKT>`  

`geometryClass` : `<http://www.opengis.net/ont/geosparql/1.0#Geometry>`  

`predicatesContainer` : `<http://www.opengis.net/ont/geosparql/1.0#Feature>`  

`featureConnector` : `<http://www.opengis.net/ont/geosparql/1.0#hasGeometry>`  

`wktLiteral` : `<http://www.opengis.net/ont/geosparql#wktLiteral>`  

These properties are based in the following schema:

```rdf
@PREFIX geo:  <http://www.opengis.net/ont/geosparql/1.0#> . 
@PREFIX sf:   <http://www.opengis.net/ont/geosparql#> . 

_:feature a geo:Feature .
_:geometry a geo:Geometry .
_:feature geo:hasGeometry _:geometry .
_:geometry geo:asWKT "POINT(15.60 50.72))"^^sf:wktLiteral .  
```


#### [SystemDefaults] 

Most of the properties in this section, if not carefully configured, can possibily damage the service. 

Let's start with the easy ones...

`defaultCRS` : Default Coordinate Reference System for the encoded geometries. You'll probably never need to touch this property, since geometries without an explicit CRS are to be interpreted as `EPSG:4326` (WGS84). However, if your data set does not stick to the standards, here you have an option to change it.

`sdaPrefix` : Prefix used for SDA Features, displayed in the WFS documents. Default value: `sda`.

`fdaPrefix` : Prefix used for FDA Features, displayed in the WFS documents. Default value: `fda`.

`solrPrefix` : Prefix used for SOLR Features, displayed in the WFS documents. Default value: `solr`.

`sdaEnabled` : Property to enable or disable the usage of SDA Features. Since the SDA Features extrictly rely on a certain data schema, it's not applicable in most cases. Therefore, it is disabled by default. Default value: `false`.

`fdaEnabled` : Property to enable or disable the usage of FDA Features. Default value: `true`.

`solrEnabled` : Property to enable or disable the usage of SOLR Features. Default value: `false`.

Here there are the critical properties, so unless you really know what you're doing, just don't touch them. These properties are related to the default RDF literal types and namespaces used by the application, which are compliant to the current standards. 

`defaultLiteral` : In case no literal type is provided, the type defined here is going to be assumed. Default value: `<http://www.w3.org/2001/XMLSchema#string>`

`stringLiteral` : String literal type. Default value: `<http://www.w3.org/2001/XMLSchema#string>`

`integerLiteral` : Integer literal type. Default value: `<http://www.w3.org/2001/XMLSchema#int>`

`decimalLiteral` : Decimal literal type. Default value: `<http://www.w3.org/2001/XMLSchema#decimal>`

`longLiteral` : Long literal type. Default value: `<http://www.w3.org/2001/XMLSchema#long>`

`dateLiteral` : Date literal type. Default value: `<http://www.w3.org/2001/XMLSchema#date>`

`floatLiteral` : Float literal type. Default value: `<http://www.w3.org/2001/XMLSchema#float>`

`serviceName` : Service used at the HTTP requests, e.g. `http://localhost:8088/lod4wfs/wfs/?service=wfs&version=1.0.0&request=GetCapabilities`. Default value: `lod4wfs`

#### [Server] 

Application's settings.

`defaultPort` : Port in which the service will listen to the requests. Default value: `8088`.

`SPARQLEndpointURL` : SPARQL Endpoint address where the SDA Features are located. 

`SPARQLDirectory` : Directory where the FDA Features’ queries will be stored. Default value: `features/` 

`connectionTimeOut` : Connection time-out (in milliseconds) for the given SPARQL Endpoint. Setting a time out will prevent that the requests to wait forever, in case of a server malfunction or network problems. I case you're more like *"no risk, no fun"*, just set the property to `0`. Default value: `1000`.

#### [WebInterface]

`[PreviewLimit]` : Maximum number of records displayed at the web interface preview. Default value: `10`

## Starting the WFS Adapter

After the settings file is properly configured, you’re ready to start the LOD4WFS adapter. The application is compressed in a single jar file [lod4wfs_releasex.x.jar](http://linkeddata.uni-muenster.de/lod4wfs/download.html) located in the root directory and can be started in the following command:

```shell
$ java -Xmx1g -jar lod4wfs_release-x.x.jar
```

**Note**: The parameter -Xmx reserves the amount of heap space necessary for the application. So, if you’re dealing with large geometries, it might be necessary to increase the heap space, e.g. -Xmx5g. Since it is totally dependent on the geometries size, there is no optimum heap space size. For better maintainability, it is recommended to create a service in your operating system to manage the process created with command above mentioned.

If you're a developer and want to start the service directly from your IDE, just execute the `main()` method of the class **de.ifgi.lod4wfs.web.Start**. If you didn't miss anything, you should be seeing by now the following greetings in your console:

```
LOD4WFS Adapter (Linked Open Data for Web Feature Service) 

Institut für Geoinformatik | Universitäts- und Landesbibliothek 
Westfälische Wilhelms-Universität Münster
http://www.uni-muenster.de/

Application Version: BETA-0.4.4.16091509
Startup time: 15-09-2016 17:04:43
Java Runtime: 1.8.0_91
Operating System: Linux 4.2.0-42-generic (amd64)
Port: 8088
Default SPARQL Endpoint (SDA Features): http://any.endpoint.de:8081/parliament/sparql
Connection Time-out in ms: 1000
FDA Features Enabled: true
SDA Features Enabled: false
SOLR Features Enabled: false
FDA Features Directory: features/
Web Interface Preview Limit: 10
```