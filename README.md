# nuxeo-mule-connector

This is a Nuxeo Connector for Mule that is based on http (Automation) API.

Using this connector, you can build Mule Flows that will use services exposed by Nuxeo Platform.

This connector exposes :

 - a predefined set of Operations 
    - getDocument, createDocument, updateDocument, query ...
 - a generic runOperation 
    - allows to call any Operation or Chain defined on the Nuxeo server
 - some converters from Nuxeo objects (Document, Documents, Blob) to raw types (Maps, List of Maps, File ...)

## Using the connector

To install the connector in Mule Studio you can use the [update site](http://community.nuxeo.com/static/mule/stable/update-site/).

You can find a [quick user guide here](doc/sample.md).

## About connector design

### Nuxeo Automation Client

The connector is build on top of nuxeo Java Automation Client.

The automation client handles authentication and network marshaling.

### Static connector and Nuxeo dynamic API

Nuxeo API is highly dynamic since :

 - addons can provide additionnal Operations
 - new Operations or chains can be added via Nuxeo Studio, Java or scripting.

The Mule Connector being statically build from annotations, we need some tricks to be able to expose all Nuxeo API.

This connector exposes :

 - a predefined set of Operations
     - createDocument
     - getDocument
     - update
     - ...
 - a runOperation that allows to call any Operation or Chain defined on the Nuxeo server
     - allows to leverage custom Operations or Chains contributed by addons
 - some converters from Nuxeo objects (Document, Documents, Blob) to raw types (Maps, List of Maps, File ...)
 - an event listener (@Source) that allows to make Mule listen to Nuxeo events
     - it uses long polling http to fetch events from Audit log

### Nuxeo Documents, Converters and DataSense

A lot of Nuxeo API expose objects like : Document or Documents (List<Document\>).

    Get document : will return a Document object
    Query        : will return a Documents object (List<Document>)

The associated Java object can be directly manipulated using for example Groovy script :

    doc.getId();
    doc.getType();
    doc.getProperties().get("dc:description");

However, in some cases, it may be useful to be able to manipulate Document and Document as Map and List of Map.

For that you can use the provider Converter.
The generated map will be DataSense aware so that you can easily use the Mule DataMapper.

### DataSense

Nuxeo Document Types are exposed as DataSense types (mapped to `MetaDataKey`).

Nuxeo dynamic properties are not exposed with their native name to avoid some naming issues encountered (at least with certains versions of Mule).

When converted to a Map, Nuxeo Document properties are exposed with a `'__'` to replace the `':'`

     doc.getName()                                       
         => map.get("ecm:name")                 
                => map["ecm__name"]

     doc.getProperties().get("dc:description")           
         => map.get("dc:description")           
                => map["dc__description"]

     doc.getProperties().get("file:content").get("name") 
         => map.get("file:content").get("name") 
                => map["file:content"]["name"]

see [datasense section](doc/datasense.md) for more details.

## Nuxeo Connector vs CMIS Connector

Compared to a CMIS connector, Automation based Nuxeo connector allows full access to Nuxeo API :

 - CRUD on document repository
 - relations, tags, queries
 - workflows and tasks management
 - converters

but also

 - all the features that are not available via CMIS (complex properties, multi-blobs ...)
 - additional Custom Automation Operation (contributed via Nuxexo IDE)
 - additional Custom Automation Chains (contributed via Nuxeo Studio)

## Build and install

### Target Mule version

The connector uses the lastest Mule API, 3.5, since we use @Source.

### Build requirements

 - JDK 1.7
 - Maven 3+
 - Internet connection (to fetch required maven artifact from Nuxeo Nexus repositories)

### To build the plugin

Quick build

> mvn -Ddevkit.studio.package.skip=false -Ddevkit.javadoc.check.skip -DskipTests=true -Dmaven.test.skip=true clean package 

Build with tests

> mvn -Ddevkit.studio.package.skip=false clean package

Then use the update site generated in target/update-site to load the plugin from MuleStudio

NB: You have to use Maven 3 and Java 6 (you can 'source' env.sh for that)
but the tests require Java 7 so you must also provide the path to the Java 7 if different than the default value (/usr/lib/jvm/jdk1.7.0_10).

### Update sites

Latest release: http://community.nuxeo.com/static/mule/stable/update-site/

Older releases: http://community.nuxeo.com/static/mule/releases/

Development: http://community.nuxeo.com/static/mule/dev/update-site/


## Next steps

 - integrate OAuth support
 - extend list of build-in functions (code gen ?)
 - fix remaining DataSense binding issues


