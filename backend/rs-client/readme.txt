To generate entities & property file

1) go to utility/dev/foros-api-sandbox-generator
2) build the project mvn clean install. 
   Note: make sure that ejb-jar and it's dependencies are already installed in maven repo.
3) run generate.sh 
   Note: To override property use -Dproperty=value. 
   Properties and their default values are in sandbox.properties

To install needed PHP libraries, do
1) apt-get install php-pear
2) pear install PEAR-1.9.1
3) pear install Log

To run rs-client test replace exiting property file with generated one.
