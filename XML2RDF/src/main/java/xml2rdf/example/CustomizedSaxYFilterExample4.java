package xml2rdf.example;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import DataCrawler.OpenAIRE.SequenceFileReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;

import xml2rdf.util.rdf.AbstractTemplateParser;
import xml2rdf.util.rdf.DefaultTemplateParser;
import xml2rdf.util.rdf.TemplateRDF;
import xml2rdf.util.xml.CustomizedSAXYFilterHandler;

public class CustomizedSaxYFilterExample4 {
	
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
			long trueStartTime = System.currentTimeMillis();
				for(int i = 0; i < runTimes; ++i) {	
					int numberOfFiles = 0;
					SAXParser      saxParser = factory.newSAXParser();
					
				    CustomizedSAXYFilterHandler handler   = new CustomizedSAXYFilterHandler();
				    
				    boolean useRDFStore = true;
				    if(args[4].equals("0")) {
				    	useRDFStore = false;
				    	handler.useRDFMemoryStore(useRDFStore);				    
					    handler.setOutputStream(new PrintWriter(new FileOutputStream(new File(args[1])),true));
				    }				    
				    AbstractTemplateParser resultParser = new DefaultTemplateParser();
				    resultParser.Parse(new FileReader(args[5] + "/" + "result.x2r"));
				    TemplateRDF resultTemplate = new TemplateRDF();
				    resultTemplate.setDefaultNameSpace(resultParser.GetDefaultNameSpaceURI());
				    resultTemplate.adddTriplePattern(resultParser.constructionPatternList);	
				    
				    AbstractTemplateParser projectParser = new DefaultTemplateParser();
				    projectParser.Parse(new FileReader(args[5] + "/" + "project.x2r"));
				    TemplateRDF projectTemplate = new TemplateRDF();
				    projectTemplate.setDefaultNameSpace(projectParser.GetDefaultNameSpaceURI());
				    projectTemplate.adddTriplePattern(projectParser.constructionPatternList);
				    
				    AbstractTemplateParser personParser = new DefaultTemplateParser();
				    personParser.Parse(new FileReader(args[5] + "/" + "person.x2r"));
				    TemplateRDF personTemplate = new TemplateRDF();
				    personTemplate.setDefaultNameSpace(personParser.GetDefaultNameSpaceURI());
				    personTemplate.adddTriplePattern(personParser.constructionPatternList);
				    
				    AbstractTemplateParser organizationParser = new DefaultTemplateParser();
				    organizationParser.Parse(new FileReader(args[5] + "/" + "organization.x2r"));
				    TemplateRDF organizationTemplate = new TemplateRDF();
				    organizationTemplate.setDefaultNameSpace(organizationParser.GetDefaultNameSpaceURI());
				    organizationTemplate.adddTriplePattern(organizationParser.constructionPatternList);
				    
				    AbstractTemplateParser datasourceParser = new DefaultTemplateParser();
				    datasourceParser.Parse(new FileReader(args[5] + "/" + "datasource.x2r"));
				    TemplateRDF datasourceTemplate = new TemplateRDF();
				    datasourceTemplate.setDefaultNameSpace(datasourceParser.GetDefaultNameSpaceURI());
				    datasourceTemplate.adddTriplePattern(datasourceParser.constructionPatternList);
				    /*
				    AbstractTemplateParser resultParser = new DefaultTemplateParser();
				    resultParser.Parse(new FileReader(args[5]));
				    TemplateRDF resultTemplate = new TemplateRDF();
				    resultTemplate.setDefaultNameSpace(resultParser.GetDefaultNameSpaceURI());
				    resultTemplate.adddTriplePattern(resultParser.constructionPatternList);							    
					handler.setTemplateRDF(resultTemplate);
					*/
					// saxParser.set
					
		    		// String filename = sourceFolderName + listOfFiles[index].getName();
		    		// Do NOT use it because current files have been repaired. 
		    	    // fixIncompleteTag(filename);
				    File[] listOfFiles = sourceFolder.listFiles(); 
				    for ( int idx = 0; idx < listOfFiles.length; ++idx) {
				    	String filename = sourceFolderName + "/" + listOfFiles[idx].getName();			    		
			    		SequenceFileReader reader = new SequenceFileReader();
			    		reader.readSequenceFile(filename);			    							   
					    						
						while(reader.hasNext()) {
							long startTime = System.currentTimeMillis();
							String xmlContent = reader.getValue().toString().replace("&nbsp;", " ");
							
							if(xmlContent.contains("<oaf:result>")) {								
						    
								handler.setTemplateRDF(resultTemplate);
								
							} else if (xmlContent.contains("<oaf:project>")) {
								// Set project template							    
								handler.setTemplateRDF(projectTemplate);
							} else if (xmlContent.contains("<oaf:person>")) {
								// Set person template							    
								handler.setTemplateRDF(personTemplate);
							} else if (xmlContent.contains("<oaf:organization>")) {
								// Set organization template							    
								handler.setTemplateRDF(organizationTemplate);
							} else if (xmlContent.contains("<oaf:datasource>")) {
								// Set datasource template							   
								handler.setTemplateRDF(datasourceTemplate);								
							}
							
						    saxParser.parse(new InputSource(new StringReader(xmlContent)), handler);
						    long stopTime = System.currentTimeMillis();
						    logWriter.write(reader.getKey() + " > OK" + System.lineSeparator());
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
						}						
					    
						long startTime1 = System.currentTimeMillis();
						if(useRDFStore) {
							
							handler.model.write(new FileWriter(args[1],false), "N-TRIPLES");
						}
					    
						long endTime1 = System.currentTimeMillis();
						sumOfOutputRDFTime = (endTime1 - startTime1)/1000.0f;					    
				    }
				}
				long trueEndTime = System.currentTimeMillis();
			System.out.println("============================================");
			logWriter.write("============================================" + System.lineSeparator());
			System.out.println("Mapping Time elapsed: " + ((trueEndTime - trueStartTime)/1000.0f)/runTimes);
			logWriter.write("Mapping Time elapsed: " + sumOfMappingTime/runTimes + System.lineSeparator());
			System.out.println("Output RDF Time elapsed: " + sumOfOutputRDFTime/runTimes);
			logWriter.write("Output RDF Time elapsed: " + sumOfOutputRDFTime/runTimes + System.lineSeparator());
			logWriter.flush();
			logWriter.close();
		} catch (SAXException e) {		
			System.out.println(e.getMessage());			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		    e.printStackTrace ();
		}
	}
}
