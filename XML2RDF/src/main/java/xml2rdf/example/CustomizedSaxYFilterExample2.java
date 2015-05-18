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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;

import xml2rdf.util.rdf.TemplateRDF;
import xml2rdf.util.xml.CustomizedSAXYFilterHandler;
import xml2rdf.util.xml.SAXYFilterHandler;
import xml2rdf.util.xml.YFilter;

public class CustomizedSaxYFilterExample2 {
	public static void fixIncompleteTag(String filename) {
		String outputData = "";
		BufferedReader reader;
		String text = "";
		boolean shouldRewrite = false;
		try {
			reader = new BufferedReader(new FileReader(filename));
			int lines = 0;
			while((text = reader.readLine()) != null) {
				++lines;
				if(lines==1 && !text.contains("<!DOCTYPE") && !text.contains("nbsp")) {
					shouldRewrite = true;
					text = " <!DOCTYPE xml [ <!ENTITY nbsp \"&#160;\"> ]>";
				} 
				/*
				if(text.contains("<device>")) {
					if(!text.contains("</device>")) {
						shouldRewrite = true;
						text += "</device>";
					}
				}
				if(text.contains("<source>")) {
					if(!text.contains("</source>")) {
						shouldRewrite = true;
						text += "</source>";
					}
				}
				*/
				outputData += text + System.lineSeparator();
			}
			reader.close();
			if(shouldRewrite) {
				PrintWriter writer = new PrintWriter(new FileOutputStream(filename,false));
				writer.write(outputData);
				writer.close();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
	
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
			String[] sourceFolderNames = {};
			String sourceFolderName = "";
			if(args.length < 4) {
				System.out.println("command folder_names output_RDF_file log_file num_of_records");
				System.out.println("folder_names: serveral folder names which are seperated by \",\"");
				return;
			}			
			// sourceFolderName = "I:/OpenAIRE/Generated_Data/publication/";
			sourceFolderNames = args[0].split(",");
			// PrintWriter logWriter = new PrintWriter(new FileOutputStream(sourceFolderName + "../customizedSaxYFilterExample2_2rdRound.log",false));
			PrintWriter logWriter = new PrintWriter(new FileOutputStream(args[2],false));
			float sumOfMappingTime = 0, sumOfLoadingTime = 0, sumOfOutputRDFTime = 0; 		
			int runTimes = 1;

				for(int i = 0; i < runTimes; ++i) {	
					int numberOfFiles = 0;
					SAXParser      saxParser = factory.newSAXParser();
					
				    CustomizedSAXYFilterHandler handler   = new CustomizedSAXYFilterHandler();
				    TemplateRDF template = new TemplateRDF();
				    // For generated XML data from publications.
				    /*
				    0. dri:objIdentifier context
					9. title context
					12. publisher context
					18. dateofacceptance
					19. bestlicense @classname
					21. resulttype  @classname
					26. originalId context  
					*/
				    /*
				    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, "type", null, "bibo:Publication", null);
				    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, "dcterms:title", null, "/response/results/result/metadata/oaf:entity/oaf:result/title/text()", XSDDatatype.XSDstring);			    
	
				    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":dateofacceptance", null, "/response/results/result/metadata/oaf:entity/oaf:result/dateofacceptance/text()",XSDDatatype.XSDdate);
				    // template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":language", null, "/response/results/result/metadata/oaf:entity/oaf:result/language/@classname",XSDDatatype.XSDstring);
				    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, "dcterms:Publisher", null, "/response/results/result/metadata/oaf:entity/oaf:result/publisher/text()",XSDDatatype.XSDstring);
				    // template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":description", null, "/response/results/result/metadata/oaf:entity/oaf:result/description/text()",XSDDatatype.XSDstring);
				    // template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":license", null, "/response/results/result/metadata/oaf:entity/oaf:result/bestlicense/@classid",XSDDatatype.XSDstring);
				    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":licenseType", null, "/response/results/result/metadata/oaf:entity/oaf:result/bestlicense/@classname",XSDDatatype.XSDstring);
				    */
				    template.addTriplePattern("/response/results/result/header/dri:objidentifier/text()", null, "type", null, "bibo:Publication", null);
				    template.addTriplePattern("/response/results/result/header/dri:objidentifier/text()", null, "swpo:hasTitle", null, "/response/results/result/metadata/oaf:entity/oaf:result/title/text()", XSDDatatype.XSDstring);			    
				    template.addTriplePattern("/response/results/result/header/dri:objidentifier/text()", null, ":dateofacceptance", null, "/response/results/result/metadata/oaf:entity/oaf:result/dateofacceptance/text()",XSDDatatype.XSDdate);
				    //template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":language", null, "/response/results/result/metadata/oaf:entity/oaf:result/language/@classname",XSDDatatype.XSDstring);
				    template.addTriplePattern("/response/results/result/header/dri:objidentifier/text()", null, "dcterms:Publisher", null, "/response/results/result/metadata/oaf:entity/oaf:result/publisher/text()",XSDDatatype.XSDstring);
				    //template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":description", null, "/response/results/result/metadata/oaf:entity/oaf:result/description/text()",XSDDatatype.XSDstring);
				    //template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":license", null, "/response/results/result/metadata/oaf:entity/oaf:result/bestlicense/@classid",XSDDatatype.XSDstring);
				    template.addTriplePattern("/response/results/result/header/dri:objidentifier/text()", null, ":licenseType", null, "/response/results/result/metadata/oaf:entity/oaf:result/bestlicense/@classname",XSDDatatype.XSDstring);
				    
				    // End of publications.xml. It's subject is related to :OpenAIREDataModel.
				    handler.setTemplateRDF(template);	
					for(int fidx = 0; fidx < sourceFolderNames.length; ++fidx) {
						sourceFolderName = sourceFolderNames[fidx];
						 		    
					    File sourceFolder = new File(sourceFolderName); 
					    File[] listOfFiles = sourceFolder.listFiles(); 
					    
					    for(int index = 0; index < listOfFiles.length; ++index ) {
					    	if(listOfFiles[index].isFile()) {
					    		String filename = sourceFolderName + listOfFiles[index].getName();
					    		// Do NOT use it because current files have been repaired. 
					    	    fixIncompleteTag(filename);
					    		logWriter.write(filename + " > start" + System.lineSeparator());
					    		long startTime = System.currentTimeMillis();
					    		
							    InputStream  xmlInput  = new FileInputStream(filename);
							   
							    saxParser.parse(xmlInput, handler);
							    long stopTime = System.currentTimeMillis();
							    logWriter.write(filename + " > OK" + System.lineSeparator());
							    logWriter.flush();
								long elapsedTime = stopTime - startTime;
								sumOfMappingTime += (elapsedTime/1000.0f);
								++numberOfFiles;
								if(numberOfFiles%1000 == 0) {
									System.out.println( numberOfFiles + " files. Time (in seconds) = " + sumOfMappingTime);
									logWriter.write(numberOfFiles + " files. Time (in seconds) = " + sumOfMappingTime + System.lineSeparator());
									logWriter.flush();
									if(!args[3].equals("0")) {
										if(numberOfFiles == Integer.valueOf(args[3])) {
											break;
										}
									}
								}
								xmlInput.close();
					    	}
					    }
				   
					    //String filename = "I:/OpenAIRE/Generated_Data/publication/dedup_wf_001##00693436d87991865c806519efcad0f6.xml";
					    
					    //handler.model.write(new FileWriter("dblp_small_customized.rdf"), "RDF/XML-ABBREV");
					    long startTime = System.currentTimeMillis();
						// handler.model.write(new FileWriter(sourceFolderName + "../publications.rdf",false), "N-TRIPLES");
					    handler.model.write(new FileWriter(args[1],false), "N-TRIPLES");
						long endTime = System.currentTimeMillis();
						sumOfOutputRDFTime = (endTime - startTime)/1000.0f;
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
