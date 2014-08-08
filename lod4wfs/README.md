LOD4WFS - Linked Open Data for Web Feature Services Adapter (Beta)
==============

The LOD4WFS Adapter (Linked Open Data for Web Feature Services) is a service to provide access to Linked Geographic Data from Geographical Information Systems (GIS). It implements a service which listens to WFS requests and converts these requests into the SPARQL Query Language for RDF. After the SPARQL Query is processed, the LOD4WFS Adapter receives the RDF result set from the Triple Store, encodes it as a WFS XML document, and returns it to the client, e.g. a GIS (see Figure 1). 
This approach enables current GIS to transparently have access to geographic LOD data sets, using their implementation of WFS, without any adaptation whatsoever being necessary. In order to reach a higher number of GIS, the currently most common implementation of WFS has been adopted for the LOD4WFS Adapter, namely the OGC Web Feature Service Implementation Specification 1.0.0. It provides the basic functions offered by the WFS reference implementation, GeoServer. 

For more information go to: [LOD4WFS Documentation](http://ifgi.uni-muenster.de/~j_jone02/lod4wfs/LOD4WFS_documentation.pdf)
