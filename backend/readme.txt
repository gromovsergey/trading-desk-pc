How to build the OIX UI project
-------------------------------

1) To build OIX UI EAR module (ready to be deployed to an app server), use

mvn clean install


2) To skip the unit tests' execution, use

mvn -Dmaven.test.skip.exec=true clean install


3) To build OIX UI and create RS-Client JAR file, use

mvn -P RsClient -Dmaven.test.skip.exec=true clean install


4) To test Rs-Client, there are three possibilities:

 - make sure there is an OIX UI instance ready to get requests (see
   rs-client/java/src/test/resources/test.properties file)

cd rs-client
mvn clean test

 - using maven args to define an OIX UI instance

cd rs-client
mvn -DforkMode=never -DoixBase="http://localhost:8080/" -DoixLogin="test@ocslab.com" -DoixPassword="pwtest" clean test
