package xml2rdf.util.rdf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GenericResourceValidator {
	
	public enum ResourceType { ElementType, AttributeType, TextType, NoneType}
	
	public boolean IsAttribute(String attribute) {
		return attribute.indexOf('@') == 0;
	}
	
	public ResourceType PredicateTypeFromXPath(String predicateXPath) {
		
		String[] stringElements = predicateXPath.split("/");
		if(stringElements.length > 0) {
			return (stringElements[stringElements.length -1 ].indexOf("@") >= 0)? ResourceType.AttributeType:ResourceType.ElementType;
		}
		
		return ResourceType.NoneType;
	}
	
	/**
	 * Check "@attributePara" equals to "attributePara";
	 * @param referencePredicateAttribute
	 * @param targetAttribute
	 * @return
	 */
	public boolean IsAttributeInGivenStatement(String referencePredicateAttribute, String targetAttribute) {		
		return referencePredicateAttribute.substring(1).equals(targetAttribute);
	}
	
	/**
	 * For example, given reference path : "response/results/result/header" and 
	 * target path: "response/results/result_2/header", this method should return true.  
	 * @param referenceXPath
	 * @param targetXPath
	 * @return
	 */
	public boolean InSubject(String referenceXPath, String targetXPath) {
		if(targetXPath.isEmpty()) {
			return false;
		}
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Document doc = docBuilder.newDocument();
		Element currentElement = null;

		String[] stringElements = targetXPath.split("/");

		String patternStr = "[a-zA-Z]+[_]+\\d+$";
		String patternStr2 = "[_]+\\d+";
		Pattern pattern = Pattern.compile(patternStr);
		Pattern pattern2 = Pattern.compile(patternStr2);
		
		for(int i = 0; i < stringElements.length; ++i) {
			 if(stringElements[i].isEmpty()) 
				 continue;
			 Matcher matcher = pattern.matcher(stringElements[i]);
			 if(matcher.find()) {
				 Matcher matcher2 = pattern2.matcher(stringElements[i]);
				 if(matcher2.find()){
					 String tempStr = stringElements[i].substring(0, matcher2.start());
					 stringElements[i] = tempStr;
				 }
			 }
			 if(stringElements[i].contains("#")) {
				 String tempStr =  stringElements[i].substring(1);
				 stringElements[i] = tempStr;
			 }
			 if(!stringElements[i].isEmpty()) {
				 Element tempElement = doc.createElement(stringElements[i]);
				 if(currentElement == null) {
					 currentElement = tempElement;		
					 doc.appendChild(currentElement);
				 } else {
					 currentElement.appendChild(tempElement);
					 currentElement = tempElement;
				 }
			 }
			
		}
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = null;
		try {
			nodes = (NodeList) xPath.evaluate(referenceXPath,
			        doc.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		}
		if(nodes == null) {
			return false;
		}
		
		return (nodes.getLength() > 0);		
	}
}
