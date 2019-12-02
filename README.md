# Search
 Two search demos. 
 
 lucenefirst is a project which uses lucene, and a third party analyzer IKAnalyzer to analyze the files in the "searchsource" directory, build the index and do some queries. All of the CRUD operations have been tested in the project. 
 
 solrDemo1 is a project which uses solrj to do CRUD operations on the solr server. 
 
 solrDemo2 implements highlighting and faceting features in the query. Also in this project, solr service is deployed into Tomcat at port 8081, and more than 3800 goods information in MySQL are imported to solr server through Dataimport interface. Queries are done regarding to those goods. 
