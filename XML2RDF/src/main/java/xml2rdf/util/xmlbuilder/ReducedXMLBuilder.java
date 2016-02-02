package xml2rdf.util.xmlbuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class ReducedXMLBuilder implements XMLBuilderIO {
	XPath xpath = null;	
	ArrayList<String> xpathPatternList = new ArrayList<String>();
	InputSource inputSource = null;
	org.w3c.dom.Document doc = null;
	DocumentBuilder docBuilder = null;
	
	public ReducedXMLBuilder() {
		XPathFactory factory =  javax.xml.xpath.XPathFactory.newInstance();
		xpath = factory.newXPath();
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		}
	}
	
	public void SetXPath(String path) {
		xpathPatternList.add(path);
	}
	
	@Override
	public void Read(String path) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(new FileInputStream(path));
		}catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}		
	}
	
	public void Parse() throws XPathExpressionException {
		for(String xpathPattern: xpathPatternList) {
			NodeList nodes = (NodeList) xpath.evaluate(xpathPattern,
							  doc.getDocumentElement(), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); ++i ) {
				Element e = (Element) nodes.item(i);				
			}
		}
	}

	@Override
	public boolean Write(String path) {
		
		return false;
	}
	
}
