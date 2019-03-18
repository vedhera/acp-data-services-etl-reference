# Data Access API Sample Usage

### Building
Java 8 is required to build this. This is a maven project. One can build it locally using
```
mvn clean install
```

### Execution
```
// Navigate to directory:
cd examples/

java -Xms1024m -Xmx2048m -Xdebug -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=9802,server=y,suspend=n \
-jar target/ecosystem-examples-0.0.1.jar \
-ims <ImsEndpoint in quotes> \
--dataAccessEndpoint  <Data Access Endpoint in quotes> \
-catalog  <Catalog Endpoint in quotes> \
--imsOrg <ImsOrg> \
--clientId <ClientId>\
--clientKey <ClientKey> \
--privateKeyPath "/path/to/local/file" \
--technicalAccount <technical account id> \
--outputPath "/path/to/output/directory"  \
--dataSetId <Dataset Id for which files needs to be egressed> \
--limit limit=1

// Limit above can be a max of 500.
```



