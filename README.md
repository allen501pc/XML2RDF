# XML2RDF
Description: A  transformation tool for XML to RDF with semantic ways.

#####Author: Jayun-Yao Huang (Allen)
#####Email: allen501pc@gmail.com

##Usage

#### 1. Edit configuration
1.1 copy *xml2rdf3.xsl* 

1.2 copy *default.conf* file and edit it as below:

	# Set a transformation file's path.
	transformation.file = xml2rdf3.xsl
  
#### 2. Usage
#####2.1 Command
**2.1.1.** If you have installed JRE(Java Runtime Envrionment), you can easily use our provided jar file (be usually named  *XML2RDF-xx-yy-zz-with-dependencies.jar*) with following command:

	# java -jar XML2RDF-xx-yy-zz-with-dependencies.jar -s STELL-I_3.rtml -o output.rdf
    # The transformation is completed. Please check the output file. 
Note that the *default.conf* and *xml2rdf3.xsl should be in the same directory location of *XML2RDF-xx-yy-zz-with-dependencies.jar*.  

#####2.2 Libraries
**2.2.1.** Go to *target* directory and then download the JAR file named *XML2RDF-xx.yy.zz-with-dependencies.jar*, which included all dependent libraries. 
You can use the donwloaded jar file to develop the program whath you want. 

**2.2.2. Examples**

***Basic Example***
	
	// Create the transformation object. 
	GenericTransformer trans = new GenericTransformer();
	// Make a transformation procedure. The source file's name: "STELL-I_3.rtml" ; the generated file name: "STELL-I_3.rdf" 
	trans.transform("STELL-I_3.rtml","STELL-I_3.rdf"); 

***Advanced usage*** 

It is under construction now. 

##Retrive source cdoe
Use Git tool to make following command:
``git clone https://github.com/allen501pc/XML2RDF.git``

##Dependency:
If you want to develop it 
Please use [Maven](http://maven.apache.org/users/index.html) tool and manipulate the following dependent libraries. 

- commons-cli 1.2
- commons-configuration: 1.10
- commons-lang 2.6commons-logging: 1.1.1
- junit: 3.8.1
- serializer: 2.7.2
- xalan: 2.7.2
- xml-apis: 1.3.04