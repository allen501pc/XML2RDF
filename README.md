# XML2RDF
Description: A  transformation tool for XML to RDF with semantic ways.

#####Author: Jyun-Yao Huang (Allen)
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

You can import *xml2rdf.util.transformer.GenericTransformer* class to transform XML into generic RDF file.

	// Create the transformation object. 
	GenericTransformer trans = new GenericTransformer();
	// Make a transformation procedure. The source file's name: "STELL-I_3.rtml" ; the generated file name: "STELL-I_3.rdf" 
	trans.transform("STELL-I_3.rtml","STELL-I_3.rdf"); 

***Advanced usage*** 

**Generic RDF.**
You can use *xml2rdf.util.transformer.SelectedTransformer* to convert the **Generic RDF** file to another RDF base on what XPath you specify.

The following code was appeared at xml2rdf.example.SelectedTransformationExample class. Assume that *Example.rdf* is the **generic RDF** file. 
    
	SelectedTransformer myTransformer = new SelectedTransformer("Example.rdf");
		
	/**
	 * For creating user-defined RDF resources, add templates which should define "Subject" and "Predicate". Note that the remaining part we don't indicate is "Object". 
	 */
		
	// Find XML tags which should be formulated as XPath '//size' elements for Subject and use the elements' contexts as Predicate.
	myTransformer.addTemplate("//size", "value()");
	// Find XML tags which should be formulated as XPath elements for Subject and use the elements' attribute name as Predicate.
	// In this case, subject is "/response/results/result" and "@classname" indicates the attribute name:"classname" 
	myTransformer.addTemplate("/response/results/result", "@classname");
	myTransformer.addTemplate("/response/header/size", "value()");
	// Use parallel mode. 
	myTransformer.SetParallelMode(true);
	Model model = myTransformer.DoSelect();
	System.out.println("-----------Print out TURTLE format.-----------------");
	model.write(System.out,"TURTLE","http://www.example.com");	
	System.out.println("-----------End of printing TURTLE format--------------------");
	/**
	 * Or output the selected triples
	 */
	System.out.println("-----------Print out self-defined format.-----------------");
	ArrayList<Statement> retrievedStatements=myTransformer.DoSelect("/response/header/size", "value()");
	for(Statement myStatement: retrievedStatements) {
		System.out.println(myStatement.getSubject() + "," + myStatement.getPredicate().getLocalName() + "," + myStatement.getObject());
	}
	System.out.println("----------End of printing self-defined format.-----------------");

The output should be like:

	-----------Print out TURTLE format.-----------------
	@prefix :        <http://www.openaire.org/#> .
	@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
	@prefix oaf:     <http://namespace.openaire.eu/oaf#> .
	@prefix dri:     <http://www.driver-repository.eu/namespace/dri#> .
	@prefix xs:      <http://www.w3.org/TR/2008/REC-xml-20081126#> .
	@prefix xsi:     <http://www.w3.org/2001/XMLSchema-instance#> .
	
	<#response/results/result_5/metadata/oaf:entity/oaf:result/children/instance_2/licence>
	      :classname "Open Access" .
	
	<#response/results/result_4/metadata/oaf:entity/oaf:result/resulttype>
	      :classname "publication" .
	
	<#response/results/result_9/metadata/oaf:entity/oaf:result/datainfo/provenanceaction>
	      :classname "sysimport:crosswalk:repository" .
	
	<#response/results/result_7/metadata/oaf:entity/oaf:result/subject_4>
	      :classname "keyword" .
	
	<#response/results/result_3/metadata/oaf:entity/oaf:result/subject_4>
	      :classname "keyword" .
	
	<#response/results/result_9/metadata/oaf:entity/oaf:result/children/result/resulttype>
	      :classname "publication" .
	

	-----------End of printing TURTLE format--------------------
	-----------Print out self-defined format.-----------------
	#response/header/size,value,10
	----------End of printing self-defined format.-----------------
**Template based approach**
We also provide a language template to let users construct their RDF.
You can refer to [Example4](https://github.com/allen501pc/XML2RDF/blob/master/XML2RDF/src/main/java/xml2rdf/example/CustomizedSaxYFilterExample4.java "Example4") to see how to develop. (Notice: the available template can be found in the example templates: [template2](https://github.com/allen501pc/XML2RDF/tree/master/XML2RDF/templates2) and [template3](https://github.com/allen501pc/XML2RDF/tree/master/XML2RDF/templates2).

##FAQ#
* How do I create our own template parser? 

**Ans:** Like our provided [defaultTemplateParser](https://github.com/allen501pc/XML2RDF/blob/master/XML2RDF/src/main/java/xml2rdf/util/rdf/DefaultTemplateParser.java), you have to inherit [AbstractTemplateParser](https://github.com/allen501pc/XML2RDF/blob/master/XML2RDF/src/main/java/xml2rdf/util/rdf/AbstractTemplateParser.java) to create new one parser.

* How do I create my own LCA selector? 

**Ans:** Our XML handler needs XMLUtils' [FindLCA](https://github.com/allen501pc/XML2RDF/blob/master/XML2RDF/src/main/java/xml2rdf/util/xml/XPathUtils.java) method. You have to modify the method directly. Then the handler will successfully execute.

* How can I use Jena's RDF output format? 

**Ans:** Please refer to [Example3](https://github.com/allen501pc/XML2RDF/blob/master/XML2RDF/src/main/java/xml2rdf/example/CustomizedSaxYFilterExample3.java) to directly our provided APIs.

##Get source cdoe
Use Git tool to make following command:
``git clone https://github.com/allen501pc/XML2RDF.git``

##Known bugs
1. While using *xml2rdf.util.transformer.SelectedTransformer*:
Because the **Generic RDF** sometimes generated RDF which resources' URIs are specified as "#resources....", the [Jena](https://jena.apache.org)  library we used can not write file in "RDF/XML" format. It seems like a bug for Jena in  this *Generic RDF*. We recommend you use "N-Triple" or "TURTLE" as basic output format for RDF. 

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