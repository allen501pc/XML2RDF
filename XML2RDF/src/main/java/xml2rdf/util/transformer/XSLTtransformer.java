package xml2rdf.util.transformer; 

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

/**
 * Usage: 
 * 	XSLTtransformer trans = new XSLTtransformer(); <BR/>
 *  // Load XML file <BR/>
 *  trans.LoadSourceFileFromURI("source.xml"); <BR/>
 *  // Load XSLT file.  <BR/>
 *  trans.LoadXSLTFileFromURI("transform.xsl"); <BR/>
 *  // prepare output file. <BR/>
 *  trans.SetOutputFileURI("output.rdf"); <BR/>
 *  // take transformation procedure.  <BR/>
 *  if(trans.DoTransformation()) { <BR/>
 *  	System.out.println("The transformation is successful."); <BR/>
 *  } <BR/>
 *  
 * @author Jyun-Yao Huang (Allen; allen501pc@gmail.com)
 *
 */
public class XSLTtransformer {
	
	protected String sourceFileURI = "", xsltFileURI = "", outputFileURI = "";
	
	protected javax.xml.transform.TransformerFactory tFactory = null;
	protected javax.xml.transform.sax.SAXTransformerFactory saxTFactory = null;
	protected javax.xml.transform.sax.TransformerHandler transformerHandler = null ;
	protected javax.xml.transform.Transformer transformer = null;
	protected org.xml.sax.XMLReader saxReader = null; 
	
	protected javax.xml.transform.stream.StreamSource sourceFileStream = null;
	
	protected java.io.FileOutputStream outputFileStream = null;
	
	/**
	 *  Constructor for creating a new instance.
	 */
	public XSLTtransformer() { 
		tFactory = javax.xml.transform.TransformerFactory.newInstance();
		if (tFactory.getFeature(javax.xml.transform.sax.SAXSource.FEATURE) && 
			    tFactory.getFeature(javax.xml.transform.sax.SAXResult.FEATURE)) {
			 saxTFactory = 
	                   ((javax.xml.transform.sax.SAXTransformerFactory) tFactory);			 
		}
	}
	
	/**
	 * Load XSLT file from URI
	 * @param fileURI the XSLT file's URI.
	 * @throws SAXException 
	 * @throws  
	 */
	public void LoadXSLTFileFromURI(String fileURI) {
		xsltFileURI = fileURI;
		try {
			javax.xml.transform.sax.TemplatesHandler templatesHandler = 
                    saxTFactory.newTemplatesHandler();
			 saxReader = 
	                   org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
			 saxReader.setContentHandler(templatesHandler);
			 saxReader.parse(xsltFileURI);
			 javax.xml.transform.Templates templates = 
                     templatesHandler.getTemplates();
			 transformerHandler = saxTFactory.newTransformerHandler(templates);
			 saxReader.setContentHandler(transformerHandler);
			 saxReader.setProperty("http://xml.org/sax/properties/lexical-handler", transformerHandler);
			 
			/* transformer = tFactory.newTransformer
	                (new javax.xml.transform.stream.StreamSource(xsltFileURI)); */
	                
		} catch(TransformerConfigurationException e) {
			System.err.println("Cannot read the XSLT file.");
			System.exit(1);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Load XSLT from input stream.
	 * @param inputString the XSLT language with string type. 
	 */
	public void LoadXSLTFileFromString(String inputString) {		
		try {			
			transformer = tFactory.newTransformer(
					new javax.xml.transform.stream.StreamSource(
							new StringReader(inputString)));
		} catch(TransformerConfigurationException e) {
			System.err.println("Error: the input string is empty.");
			System.exit(1);
		}				
	}
	
	/**
	 * Get XSLTFileURI. 
	 * @return XSLT file URI.
	 */
	public String GetXSLTFileURI() { 
		return xsltFileURI;
	}
	
	/**
	 * Set outputFileURI and prepare the output file stream.  
	 * @param outputURI the output file's URI.
	 */
	public void SetOutputFileURI(String outputURI) {
		this.outputFileURI = outputURI;
		try {
			java.util.Properties xmlProps = org.apache.xml.serializer.OutputPropertiesFactory.getDefaultMethodProperties("xml");
			xmlProps.setProperty("indent", "yes");
		    xmlProps.setProperty("standalone", "no");      
			org.apache.xml.serializer.Serializer serializer = 
					org.apache.xml.serializer.SerializerFactory.getSerializer(xmlProps);
			serializer.setOutputStream(new java.io.FileOutputStream(outputFileURI));
		    javax.xml.transform.Result result =
		    	      new javax.xml.transform.sax.SAXResult(serializer.asContentHandler());
		    transformerHandler.setResult(result);
			/*
			outputFileStream = new java.io.FileOutputStream(this.outputFileURI);
			*/
			
		} catch (FileNotFoundException e) {			
			System.err.println("File is not existed: " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get output file URI. 
	 * @return outputFileURI the output file's URI.
	 */
	public String GetOutputFileURI() { 
		return this.outputFileURI;
	}
	
	/**
	 * load source file. 
	 * @param fileURI the XML language with string type.
	 */
	public void LoadSouceFileFromURI(String fileURI) {
		sourceFileURI = fileURI;		
		/* sourceFileStream = new javax.xml.transform.stream.StreamSource(fileURI); */
	}
	
	/**
	 * Make a transformation from XML via XSLT.
	 * @return true for success or false. 
	 */
	public boolean DoTransformation() { 
		try {
			saxReader.parse(sourceFileURI);
			/*
			transformer.transform
			(sourceFileStream, 
			 new javax.xml.transform.stream.StreamResult(outputFileStream));
			 */
			/*
		} catch (TransformerException e) {	
			System.err.println(e.getMessage());	
			return false; */
		} catch (IOException e) {
			System.err.println(e.getMessage());				
			return false;
		} catch (SAXException e) {
			System.err.println(e.getMessage());				
			return false;
		}
		return true;
	}
}
