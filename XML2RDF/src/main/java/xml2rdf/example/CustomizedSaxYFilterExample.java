package xml2rdf.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
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

public class CustomizedSaxYFilterExample {
	
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
			
			long SumTime = 0;			
			int runTimes = 1;
			for(int i = 0; i < runTimes; ++i) {
				long startTime = System.currentTimeMillis();
				SAXParser      saxParser = factory.newSAXParser();
			    CustomizedSAXYFilterHandler handler   = new CustomizedSAXYFilterHandler();
			    TemplateRDF template = new TemplateRDF();
			    // For STELL-I.rtml
			    
//			    template.addTriplePattern("RTMLData", null, "type", null, "/RTML/Telescope/Camera/FilterWheel/Filter/@type", null);
//			    template.addTriplePattern("/RTML/Telescope/Camera/FilterWheel/Filter/@type", null, "FocalLength", null, "/RTML/Telescope/FocalLength/text()",XSDDatatype.XSDinteger);
//			    template.addTriplePattern("/RTML/Telescope/Camera/FilterWheel/Filter/@type", null, "units", null, "meters",XSDDatatype.XSDstring);
//			    template.addTriplePattern("Author", null, "a", null, "person",XSDDatatype.XSDstring);
			    template.addTriplePattern("/RTML/Telescope/Camera/FilterWheel/Filter/@name", null,"type", null,"CameraType",XSDDatatype.XSDstring);		   
			    template.addTriplePattern("/RTML/Telescope/Camera/FilterWheel/Filter/@name", null,"modelType", null,"/RTML/Telescope/Camera/FilterWheel/Filter/@type",XSDDatatype.XSDstring);
			    // For publicatios.xml. It's subject is realted to :OpenAIREDataModel.
//			    template.addTriplePattern(":OpenAIREDataModel", null, "a", null, "bibo:Publication", null);
//			    template.addTriplePattern(":OpenAIREDataModel", null, "swpo:hasTitle", null, "/response/results/result/metadata/oaf:entity/oaf:result/title/text()", XSDDatatype.XSDstring);			    
//			    template.addTriplePattern(":OpenAIREDataModel", null, ":objectIdentifier", null, "/response/results/result/header/dri:objIdentifier/text()",null);
//			    template.addTriplePattern(":OpenAIREDataModel", null, ":dateofacceptance", null, "/response/results/result/metadata/oaf:entity/oaf:result/dateofacceptance/text()",XSDDatatype.XSDdate);
//			    template.addTriplePattern(":OpenAIREDataModel", null, ":language", null, "/response/results/result/metadata/oaf:entity/oaf:result/language/@classname",XSDDatatype.XSDstring);
//			    template.addTriplePattern(":OpenAIREDataModel", null, ":hasPublisher", null, "/response/results/result/metadata/oaf:entity/oaf:result/publisher/text()",XSDDatatype.XSDstring);
//			    template.addTriplePattern(":OpenAIREDataModel", null, ":description", null, "/response/results/result/metadata/oaf:entity/oaf:result/description/text()",XSDDatatype.XSDstring);
//			    template.addTriplePattern(":OpenAIREDataModel", null, ":license", null, "/response/results/result/metadata/oaf:entity/oaf:result/bestlicense/@classid",XSDDatatype.XSDstring);
//			    template.addTriplePattern(":OpenAIREDataModel", null, ":licenseType", null, "/response/results/result/metadata/oaf:entity/oaf:result/bestlicense/@classname",XSDDatatype.XSDstring);
			    
			    // Not support
			    // template.addTriplePattern(":OpenAIREDataModel", null, ":hasAuthor", null, "/response/results/result/metadata/oaf:entity/oaf:result/rels/rel/to[class="hasAuthor"]/text()",XSDDatatype.XSDstring);			    			    
			    // handler.setTemplateRDF(template);
			    
			    // End of publications.xml
			 // For publicatios.xml. It's subject is realted to :OpenAIREDataModel.
			    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, "type", null, "bibo:Publication", null);
			    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, "swpo:hasTitle", null, "/response/results/result/metadata/oaf:entity/oaf:result/title/text()", XSDDatatype.XSDstring);			    

			    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":dateofacceptance", null, "/response/results/result/metadata/oaf:entity/oaf:result/dateofacceptance/text()",XSDDatatype.XSDdate);
			    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":language", null, "/response/results/result/metadata/oaf:entity/oaf:result/language/@classname",XSDDatatype.XSDstring);
			    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":hasPublisher", null, "/response/results/result/metadata/oaf:entity/oaf:result/publisher/text()",XSDDatatype.XSDstring);
			    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":description", null, "/response/results/result/metadata/oaf:entity/oaf:result/description/text()",XSDDatatype.XSDstring);
			    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":license", null, "/response/results/result/metadata/oaf:entity/oaf:result/bestlicense/@classid",XSDDatatype.XSDstring);
			    template.addTriplePattern("/response/results/result/header/dri:objIdentifier/text()", null, ":licenseType", null, "/response/results/result/metadata/oaf:entity/oaf:result/bestlicense/@classname",XSDDatatype.XSDstring);
			    
			    // Not support
			    // template.addTriplePattern(":OpenAIREDataModel", null, ":hasAuthor", null, "/response/results/result/metadata/oaf:entity/oaf:result/rels/rel/to[class="hasAuthor"]/text()",XSDDatatype.XSDstring);
			    
			    // End of publications.xml. It's subject is related to :OpenAIREDataModel.
			    handler.setTemplateRDF(template);
			    
			    InputStream    xmlInput  = new FileInputStream("publications.xml");
			    //InputStream    xmlInput  = new FileInputStream("STELL-I_3.rtml");
			    //InputStream    xmlInput  = new FileInputStream("publications.xml");
				//InputStream xmlInput = new FileInputStream("dblp_small.xml");			    
			    //myFilter.AppendXPath("/RTML/Telescope/Camera//*");
			    //myFilter.AppendXPath("/RTML/Telescope/Camera/");
			    // myFilter.AppendXPath("/response/header/page");
			    
			    // For STELL-I_3.rtml.
			    // myFilter.AppendXPath("//Camera/FilterWheel");
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
			    
			    
			    //handler.setFilter(myFilter);
			    //long startTime = System.currentTimeMillis();
			    saxParser.parse(xmlInput, handler);
			    long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				SumTime += elapsedTime;
			    //handler.model.write(new FileWriter("dblp_small_customized.rdf"), "RDF/XML-ABBREV");
				//handler.model.write(System.out, "N-TRIPLES");
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
