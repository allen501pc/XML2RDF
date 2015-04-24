package xml2rdf.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import xml2rdf.util.rdf.GenericResourceValidator;
import xml2rdf.util.xml.SAXYFilterHandler;
import xml2rdf.util.xml.YFilter;

public class SaxYFilterExample {
	
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
			
			long SumTime = 0;			
			int runTimes = 1;
			for(int i = 0; i < runTimes; ++i) {
				long startTime = System.currentTimeMillis();
				SAXParser      saxParser = factory.newSAXParser();
			    SAXYFilterHandler handler   = new SAXYFilterHandler();
			    YFilter myFilter = new YFilter();
			    //InputStream    xmlInput  = new FileInputStream("publications.xml");
			    InputStream    xmlInput  = new FileInputStream("STELL-I_3.rtml");
				//InputStream xmlInput = new FileInputStream("dblp_small.xml");			    
			    //myFilter.AppendXPath("/RTML/Telescope/Camera//*");
			    //myFilter.AppendXPath("/RTML/Telescope/Camera/");
			    // myFilter.AppendXPath("/response/header/page");
			    
			    // For STELL-I_3.rtml.
			    // Get the subject name.
			    myFilter.AppendXPath("/RTML/Telescope/Camera/FilterWheel/Filter");
			    // myFilter.AppendXPath("//Filter");
			    // For publicaitons.xml	
			    /*
			    myFilter.AppendXPath("//metadata");
			    myFilter.AppendXPath("//publisher");
			    myFilter.AppendXPath("//journal");
			    myFilter.AppendXPath("//title");
			    myFilter.AppendXPath("//originalId");
			    myFilter.AppendXPath("//dateofacceptance");
			    
			    */
			    // For dblp_small.xml 
			    /*
			    myFilter.AppendXPath("//author");			    
			    myFilter.AppendXPath("//title");
			    myFilter.AppendXPath("//journal");
			    myFilter.AppendXPath("//booktitle");
			    myFilter.AppendXPath("//volume");
			    myFilter.AppendXPath("//year");
			    myFilter.AppendXPath("//month");
			    myFilter.AppendXPath("//pages");
			    */
			    
			    
			    handler.setFilter(myFilter);
			    //long startTime = System.currentTimeMillis();
			    saxParser.parse(xmlInput, handler);
			    
			    //handler.model.write(new FileWriter("dblp_small_customized.rdf"), "RDF/XML-ABBREV");
				//handler.model.write(System.out, "N-Triples");
				// Customize the output model.
				Model outputModel = ModelFactory.createDefaultModel();
				// Iterate with (subjectPattern, predicate_string, object_string);
				
				StmtIterator iter = handler.model.listStatements(
						  new
						      SimpleSelector(null, null, (RDFNode) null) {
							  	  GenericResourceValidator validator = new GenericResourceValidator(); 
						          public boolean selects(Statement s) {
						        	
						        	try {
										return (validator.InSubject("/RTML/Telescope/Camera/FilterWheel/Filter", "/" + s.getSubject().getURI().substring(s.getSubject().getURI().indexOf("#")+1)))
											      && (s.getPredicate().getLocalName().equals("name"));
									} catch (XPathExpressionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} 
									return false;

						          }
						     });
				while(iter.hasNext()) {
					outputModel.add(outputModel.createResource(iter.nextStatement().getObject().toString()), outputModel.createProperty("type"), "CameraModle");
				}
				
				// Iterate with (subjectPattern, predicate_string, object_string);
				iter = handler.model.listStatements(
						  new
						      SimpleSelector(null, null, (RDFNode) null) {
							  	  GenericResourceValidator validator = new GenericResourceValidator(); 
						          public boolean selects(Statement s) {
						        	
						        	try {
										return (validator.InSubject("/RTML/Telescope/Camera/FilterWheel/Filter", "/" + s.getSubject().getURI().substring(s.getSubject().getURI().indexOf("#")+1)))
											      && ((s.getPredicate().getLocalName().equals("name") || s.getPredicate().getLocalName().equals("type")));
									} catch (XPathExpressionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} 
									return false;
									/*
						        	  return (s.getSubject().getLocalName().equals("RTML/Telescope/Camera/FilterWheel/Filter"))
										      && (s.getPredicate().getLocalName().equals("name"));
						        	*/
						          }
						     });
				Map<String,ArrayList<String>> resMap = new HashMap<String,ArrayList<String>>(); 
				while(iter.hasNext()) {					
					Statement stat = iter.nextStatement();
					if(stat.getPredicate().getLocalName().equals("name")) {
						ArrayList<String> targetList = resMap.get(stat.getSubject().getLocalName());
						if(targetList== null) {
							ArrayList<String> list = new ArrayList<String>();
							list.add("s:" + stat.getObject().toString());
							resMap.put(stat.getSubject().getLocalName(),list);
						} else {
							targetList.add("s:" + stat.getObject().toString());
						}
					} else if(stat.getPredicate().getLocalName().equals("type")) {
						ArrayList<String> targetList = resMap.get(stat.getSubject().getLocalName());
						if(targetList== null) {
							ArrayList<String> list = new ArrayList<String>();
							list.add("o:" + stat.getObject().toString());
							resMap.put(stat.getSubject().getLocalName(),list);
						} else {
							targetList.add("o:" + stat.getObject().toString());
						}
					}					
				}
				for(Entry<String, ArrayList<String>> entry: resMap.entrySet()) {
					String s = null, o = null;	
					ArrayList<String> list = entry.getValue();
					for(String item: list) {
						if(item.indexOf("s:") == 0) {
							s = item.substring(2);
						}
						if(item.indexOf("o:") == 0) {
							o = item.substring(2);
						}
					}
					outputModel.add(outputModel.createResource(s), outputModel.createProperty("modelType"), outputModel.createLiteral(o));
				}
				//outputModel.write(System.out, "N-Triples");
				
				long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				SumTime += elapsedTime;
			    // System.out.println(SAXYFilterHandler.outputStream);
				//TimeUnit.MILLISECONDS.sleep(2000);
			}
			System.out.println("Time elapsed: " + SumTime/runTimes);
		} catch (SAXException e) {		
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		    e.printStackTrace ();
		}
	}
}
