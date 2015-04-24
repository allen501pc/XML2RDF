package xml2rdf.util.rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.mutable.MutableObject;

import xml2rdf.util.xml.XPathUtils;
import xml2rdf.util.xml.YFilter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/*
 * Template RDF. It's a data model which stored Components of both Pattern and Matadata of subject, predicate and object.
 * It add an additional YFilter in order to find out needed pattern.  
 */
public class TemplateRDF {
	
	/**
	 * The List of SPO (i.e. subject, predicate and object) component.
	 * @See SPOComponent
	 */
	public ArrayList<SPOComponent> spoCompList = new ArrayList<SPOComponent>(); 	
	/**
	 * Current SPO Component list, which is dynamically operated by Handler. 
	 */
	public List<SPOComponent> currentMaintainedList = new LinkedList<SPOComponent>();
	
	public YFilter filter = new YFilter();
	
	protected void assignMetaResource(TemplateResource res, String pattern, XSDDatatype type) {
		if(XPathUtils.isXPath(pattern)) {
			res.isXPath = true;
			if(XPathUtils.isXPathAttribute(pattern)) {
				res.isLiteral = true;
				res.attributeName = XPathUtils.getAttributeNameFromXPath(pattern);
				res.xpath = XPathUtils.getXPathWithOutAttributeAndTextNode(pattern);
			} else if(XPathUtils.isQueryingTextNodeFromElements(pattern)) {
				res.isLiteral = false;
				res.xpath = XPathUtils.getXPathWithOutAttributeAndTextNode(pattern);
			}
			
		} else {
			res.isXPath = false;
			res.isLiteral = true;
			res.outputIdentifier = pattern;
		}
		if(type != null) {
			res.literalType = type;	
			res.isLiteral = true;
		}
	}		
	
	/**
	 * Add triple Pattern. Notice that we assume that users' subject and object "can be" in XPath way. 
	 * But predicateString "cannot be" formated in XPath.
	 * @param subject
	 * @param predicateString
	 * @param object
	 */
	public void addTriplePattern(String subject,XSDDatatype subjectLiteral, String predicateString,XSDDatatype predicateLiteral, String object, XSDDatatype objectLiteral) {		
		SPOComponent spoComp = new SPOComponent();
		assignMetaResource(spoComp.metaData.subjectPattern, subject, subjectLiteral);
		assignMetaResource(spoComp.metaData.predicatePattern, predicateString, predicateLiteral);
		assignMetaResource(spoComp.metaData.objectPattern, object, objectLiteral);
		// assume that predicate is not written in XPath way. 
		// Set SLCA Path
		if(spoComp.metaData.subjectPattern.isXPath) {
			if(spoComp.metaData.objectPattern.isXPath) {
				// Both subject and object use xpath as pattern.
				spoComp.metaData.SLCAPath = XPathUtils.FindLCA(spoComp.metaData.subjectPattern.xpath, spoComp.metaData.objectPattern.xpath);
			}
			else {
				// Only subject use xpath pattern but object use user-defined text.
				spoComp.metaData.SLCAPath = spoComp.metaData.subjectPattern.xpath;
			}
		} else if(spoComp.metaData.objectPattern.isXPath) {
			// Only object use xpath.
			spoComp.metaData.SLCAPath = spoComp.metaData.objectPattern.xpath;
		} else {
			// If the added triple patterns don't contain any xpath pattern. 
			// , it is static declaration.			
		}
 
		spoComp.resetData();
		
		// Add the watch list.
		this.spoCompList.add(spoComp);
		// Add to YFilter list.
		if(!spoComp.metaData.SLCAPath.isEmpty()) {
			this.filter.AppendXPath(spoComp.metaData.SLCAPath);
			if(spoComp.metaData.subjectPattern.isXPath) {
				this.filter.AppendXPath(spoComp.metaData.subjectPattern.xpath);
			}
			if(spoComp.metaData.objectPattern.isXPath) {
				this.filter.AppendXPath(spoComp.metaData.objectPattern.xpath);
			}
		}		
	}		
	
	public void GenerateModelFromSPOComponent(Model model,SPOComponent tempComp) {
		//for(: currentMaintainedList) {
		if(tempComp.data.subjectList.size() > 0) {
			for(MutableObject subjectName : tempComp.data.subjectList) {
				if(subjectName.getValue() == null || ((String) subjectName.getValue()).isEmpty())
					continue;
				if(tempComp.data.objectList.size() > 0 ) {
					for(MutableObject objectName : tempComp.data.objectList) {
						if(objectName.getValue() == null || ((String) objectName.getValue()).isEmpty())
							continue;
						Resource resOfSubject = model.createResource((String) subjectName.getValue());
						// TODO : Add namespace of property.
						Property prop = model.createProperty((String) tempComp.data.predicateName.getValue());
						if(tempComp.metaData.objectPattern.isLiteral) {
							if(tempComp.metaData.objectPattern.literalType != null) {
								resOfSubject.addProperty(prop, model.createTypedLiteral((String) objectName.getValue(),tempComp.metaData.objectPattern.literalType));
							} else {
								resOfSubject.addProperty(prop, model.createResource((String) objectName.getValue()));
							}
						} else {
							resOfSubject.addProperty(prop, model.createResource((String) objectName.getValue()));
						}				
					}
				}
			}
		}
			
		//}
	}
}
