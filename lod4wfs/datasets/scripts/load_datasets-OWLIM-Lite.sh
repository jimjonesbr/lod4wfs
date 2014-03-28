#!/bin/bash


#Loads the test datasets brazil_municipalities.nt, vegetation_legal_amazon.nt 
#and hydrology_amazon.nt to a local instance of OWLIM-Lite.

curl -X POST -T brazil_municipalities.nt -H "Content-Type: application/x-turtle" http://localhost:8080/openrdf-sesame/repositories/agile-lod4wfs/rdf-graphs/service?graph=http://ifgi.lod4wfs.de/layer/brazil_$

curl -X POST -T vegetation_legal_amazon.nt  -H "Content-Type: application/x-turtle" http://localhost:8080/openrdf-sesame/repositories/agile-lod4wfs/rdf-graphs/service?graph=http://ifgi.lod4wfs.de/layer/amaz$

curl -X POST -T hydrology_amazon.nt  -H "Content-Type: application/x-turtle" http://localhost:8080/openrdf-sesame/repositories/agile-lod4wfs/rdf-graphs/service?graph=http://ifgi.lod4wfs.de/layer/amazon_hydr$

