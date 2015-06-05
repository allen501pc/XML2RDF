package xml2rdf.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;

import xml2rdf.util.rdf.AbstractTemplateParser;
import xml2rdf.util.rdf.DefaultTemplateParser;
import xml2rdf.util.rdf.NameSpaceMapping;
import xml2rdf.util.rdf.TemplateRDF;
import xml2rdf.util.xml.CustomizedSAXYFilterHandler;
import xml2rdf.util.xml.SAXYFilterHandler;
import xml2rdf.util.xml.YFilter;

public class CustomizedSaxYFilterExample3 {
	
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
						
			if(args.length < 6) {
				System.out.println("command sourceFolder RDF_FILE log_file num_of_records useRDF_Store(1=true,0=false)");				
				return;
			}			
			
			String sourceFolderName = args[0];
			File sourceFolder = new File(sourceFolderName); 
			PrintWriter logWriter = new PrintWriter(new FileOutputStream(args[2],false));
			float sumOfMappingTime = 0, sumOfLoadingTime = 0, sumOfOutputRDFTime = 0; 		
			int runTimes = 1;

				for(int i = 0; i < runTimes; ++i) {	
					int numberOfFiles = 0;
					SAXParser      saxParser = factory.newSAXParser();
					
				    CustomizedSAXYFilterHandler handler   = new CustomizedSAXYFilterHandler();
				    TemplateRDF template = new TemplateRDF();
				    boolean useRDFStore = true;
				    if(args[4].equals("0")) {
				    	useRDFStore = false;
				    	handler.useRDFMemoryStore(useRDFStore);				    
					    handler.setOutputStream(new PrintWriter(args[1]));
				    }
				    
				    AbstractTemplateParser parser = new DefaultTemplateParser();
				    parser.Parse(new FileReader(args[5]));
				    template.setDefaultNameSpace(parser.GetDefaultNameSpaceURI());
				    template.adddTriplePattern(parser.constructionPatternList);
				    
//				    NameSpaceMapping spaceOA = new NameSpaceMapping("oa", "http://lod.openaire.eu/data/");
//				    NameSpaceMapping spaceSwpo = new NameSpaceMapping("swpo","http://sw-portal.deri.org/ontologies/swportal/#");
//				    NameSpaceMapping spaceDcterms = new NameSpaceMapping("dcterms","http://dublincore.org/documents/dcmi-terms/#");
//				    NameSpaceMapping spaceFoaf = new NameSpaceMapping("foaf","http://xmlns.com/foaf/0.1/#");
//				    NameSpaceMapping spaceBibo = new NameSpaceMapping("bibo","http://purl.org/ontology/bibo/");
//				    NameSpaceMapping spaceBibtex = new NameSpaceMapping("bibtex","http://purl.org/net/nknouf/ns/bibtex#");
//				    
//				    template.addTriplePattern(spaceOA,"/record/result/header/dri:objIdentifier/text()", null, null,"a", null, spaceOA,"Publication", null);
//				    template.addTriplePattern(spaceOA,"/record/result/header/dri:objIdentifier/text()", null, spaceSwpo,"title", null, null,"/record/result/metadata/oaf:entity/oaf:result/title/text()", XSDDatatype.XSDstring);			    
//				    template.addTriplePattern(spaceOA,"/record/result/header/dri:objIdentifier/text()", null, spaceDcterms, "dateAccepted", null, null, "/record/result/metadata/oaf:entity/oaf:result/dateofacceptance/text()",XSDDatatype.XSDdate);
//				    
//				    template.addTriplePattern(spaceOA,"/record/result/header/dri:objIdentifier/text()", null, spaceDcterms, "publisher", null, null,"/record/result/metadata/oaf:entity/oaf:result/publisher/text()",XSDDatatype.XSDstring);
//				    
//				    template.addTriplePattern(spaceOA,"/record/result/header/dri:objIdentifier/text()", null, spaceDcterms,"dcterms:licenseType", null, null, "/record/result/metadata/oaf:entity/oaf:result/bestlicense/@classname",XSDDatatype.XSDstring);
//				    template.addTriplePattern(spaceOA,"/record/result/header/dri:objIdentifier/text()", null, spaceOA,"author", null, null,"/record/result/metadata/oaf:entity/oaf:result/rels/rel/to[@type='person']/text()", null);
//				    template.addTriplePattern(null,"/record/result/metadata/oaf:entity/oaf:result/rels/rel/to[@type='person']/text()", null, spaceOA,"fullname", null, null,"/record/result/metadata/oaf:entity/oaf:result/rels/rel/fullname/text()", XSDDatatype.XSDstring);
				    // End of publications.xml. It's subject is related to :OpenAIREDataModel.
				    handler.setTemplateRDF(template);	
								    

		    		// String filename = sourceFolderName + listOfFiles[index].getName();
		    		// Do NOT use it because current files have been repaired. 
		    	    // fixIncompleteTag(filename);
				    File[] listOfFiles = sourceFolder.listFiles(); 
				    for ( int idx = 0; idx < listOfFiles.length; ++idx) {
				    	String filename = sourceFolderName + "/" + listOfFiles[idx].getName();
			    		BufferedReader reader = new BufferedReader(new FileReader(filename));
			    		String text = "";
					    String processingID = "";
					    String fieldName = "";
			    		while(true) {
			    			long startTime = System.currentTimeMillis();
			    			if((text = reader.readLine()) == null) {
			    				break;
			    			}
			    			if(text.isEmpty()) 
			    				continue;
						    JsonFactory jfactory = new JsonFactory();
						    JsonParser jParser = jfactory.createParser(text);
						    jParser.nextToken();
						    processingID = "";
						    fieldName = "";
						    while(jParser.nextToken()!= JsonToken.END_OBJECT ) {
						    	fieldName = jParser.getCurrentName();							    	
						    	
						    	if(fieldName.equals("_id")) {
						    		// Move to "{"
						    		jParser.nextToken();
						    		// Move to "$oid"
						    		jParser.nextToken();
						    		fieldName = jParser.getCurrentName();
						    		if(fieldName.equals("$oid")) {
						    			// Move to id which is like 5538bf46ecd280c77b28be0f
						    			jParser.nextToken();
						    			processingID = jParser.getText();
							    		logWriter.write(processingID + " > start" + System.lineSeparator());
							    		// Move to "}"
							    		jParser.nextToken();
						    		}							    		
						    	} else if(fieldName.equals("body")) {	
						    		jParser.nextToken();
						    		String xmlContent = jParser.getText().replace("&nbsp;", " ");
								    saxParser.parse(new InputSource(new StringReader(xmlContent)), handler);
								    long stopTime = System.currentTimeMillis();
								    logWriter.write(processingID + " > OK" + System.lineSeparator());
								    logWriter.flush();
									long elapsedTime = stopTime - startTime;
									sumOfMappingTime += (elapsedTime/1000.0f);
									++numberOfFiles;
									if(numberOfFiles%1000 == 0) {									
										logWriter.write(numberOfFiles + " files. Time (in seconds) = " + sumOfMappingTime + System.lineSeparator());
										logWriter.flush();
										if(!args[3].equals("0")) {
											if(numberOfFiles == Integer.valueOf(args[3])) {
												break;
											}
										}
									}
						    	} else {
						    		jParser.nextToken();
						    	}
						    }
			    		}
	
					   
					    
					    
					    //handler.model.write(new FileWriter("dblp_small_customized.rdf"), "RDF/XML-ABBREV");
					    long startTime1 = System.currentTimeMillis();
						if(useRDFStore) {
							handler.model.write(new FileWriter(args[1],false), "N-TRIPLES");
						}
					    
						long endTime1 = System.currentTimeMillis();
						sumOfOutputRDFTime = (endTime1 - startTime1)/1000.0f;
					    // System.out.println(SAXYFilterHandler.outputStream);
						//TimeUnit.MILLISECONDS.sleep(2000);	
				    }
				}
			System.out.println("============================================");
			logWriter.write("============================================" + System.lineSeparator());
			System.out.println("Mapping Time elapsed: " + sumOfMappingTime/runTimes);
			logWriter.write("Mapping Time elapsed: " + sumOfMappingTime/runTimes + System.lineSeparator());
			System.out.println("Output RDF Time elapsed: " + sumOfOutputRDFTime/runTimes);
			logWriter.write("Output RDF Time elapsed: " + sumOfOutputRDFTime/runTimes + System.lineSeparator());
			logWriter.close();
		} catch (SAXException e) {		
			System.out.println(e.getMessage());			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		    e.printStackTrace ();
		}
	}
}
