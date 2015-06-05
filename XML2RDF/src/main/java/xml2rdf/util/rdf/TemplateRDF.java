package xml2rdf.util.rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	public HashSet<NameSpaceMapping> prefixSet = new HashSet<NameSpaceMapping>();
	/**
	 * Current SPO Component list, which is dynamically operated by Handler. 
	 */
	public List<SPOComponent> currentMaintainedList = new LinkedList<SPOComponent>();
	protected String defaultNameSpace = "http://www.example.com/#";
	
	public YFilter filter = new YFilter();
	
	public void setDefaultNameSpace(String url) {
		defaultNameSpace = new String(url);
	}
	
	public String getDefaultNameSpace() {
		return defaultNameSpace;
	}
	
	public void clearData() {
		if(this.currentMaintainedList.size() > 0) {
			this.currentMaintainedList.clear();
		}
		if(this.spoCompList.size() > 0 ) {
			Iterator<SPOComponent> it = this.spoCompList.iterator();
			while(it.hasNext()) {
				it.next().resetData();
			}
		}
	}
	
	public boolean setNameSpaceMapping(MutableObject prefix, MutableObject URI) {
		MutableObject prefixObject = new MutableObject(prefix);
		if(prefixSet.contains(prefix)) {
			Iterator<NameSpaceMapping> iterator = prefixSet.iterator();
			while(iterator.hasNext()) {
				NameSpaceMapping obj = iterator.next();
				if(obj.hashCode() == prefixObject.hashCode()) {
					obj.setPrefix((String) prefix.getValue(), (String) URI.getValue());
					return true;
				}
			}
		}
		NameSpaceMapping obj = new NameSpaceMapping((String) prefix.getValue(),(String) URI.getValue());
		return prefixSet.add(obj);		
	}
	
	protected void assignMetaResource(TemplateResource res, NameSpaceMapping mapping, String pattern, XSDDatatype type) {
		if(mapping != null && !mapping.isEmpty()) {
			setNameSpaceMapping(mapping.prefixName,  mapping.URI);
			res.mapping.copy(mapping);
		} else if (mapping == null) {
			mapping = new NameSpaceMapping("defaultNS",getDefaultNameSpace());
			setNameSpaceMapping(mapping.prefixName,  mapping.URI);
			res.mapping.copy(mapping);
		}
		
		if(XPathUtils.isXPath(pattern)) {
			res.isXPath = true;
			if(XPathUtils.isXPathAttribute(pattern)) {
				res.isLiteral = true;
				res.attributeName = XPathUtils.getAttributeNameFromXPath(pattern);
				String[] conditions = XPathUtils.getLastConditionAttributeRules(pattern);
				if(conditions.length != 0) {
					res.conditionAttributeName = conditions[0];
					res.conditionAttributeValue = conditions[1];
				}
				res.xpath = XPathUtils.getXPathWithOutAttributeAndTextNode(pattern);
			} else if(XPathUtils.isQueryingTextNodeFromElements(pattern)) {
				res.isLiteral = false;
				String[] conditions = XPathUtils.getLastConditionAttributeRules(pattern);
				if(conditions.length != 0) {
					res.conditionAttributeName = conditions[0];
					res.conditionAttributeValue = conditions[1];
				}
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
	
	public void adddTriplePattern(List<ArrayList<Object>> list) {
		Iterator<ArrayList<Object>> it = list.iterator();
		while(it.hasNext()) {
			addTriplePattern(it.next());
		}		
	}
	
	public void addTriplePattern(List<Object> list) {
		if(list.size() != 9) {
			return;
		}
		addTriplePattern((NameSpaceMapping) list.get(0), 
						 (String) list.get(1),
						 (XSDDatatype) list.get(2),
						 (NameSpaceMapping) list.get(3), 
						 (String) list.get(4),
						 (XSDDatatype) list.get(5),
						 (NameSpaceMapping) list.get(6), 
						 (String) list.get(7),
						 (XSDDatatype) list.get(8)
				);
	}
	
	/**
	 * Add triple Pattern. Notice that we assume that users' subject and object "can be" in XPath way. 
	 * But predicateString "cannot be" formated in XPath.
	 * @param subject
	 * @param predicateString
	 * @param object
	 */
	public void addTriplePattern(String subject,XSDDatatype subjectLiteral, String predicateString,XSDDatatype predicateLiteral, String object, XSDDatatype objectLiteral) {		
		addTriplePattern(null, subject,subjectLiteral, null, predicateString,predicateLiteral, null, object,  objectLiteral);				
	}	
	
	public void addTriplePattern(NameSpaceMapping prefixOfSubject,String subject,XSDDatatype subjectLiteral, NameSpaceMapping prefixOfPredicate, String predicateString,XSDDatatype predicateLiteral, NameSpaceMapping prefixOfObject, String object, XSDDatatype objectLiteral) {		
		SPOComponent spoComp = new SPOComponent();
		assignMetaResource(spoComp.metaData.subjectPattern, prefixOfSubject, subject, subjectLiteral);
		assignMetaResource(spoComp.metaData.predicatePattern, prefixOfPredicate, predicateString, predicateLiteral);
		assignMetaResource(spoComp.metaData.objectPattern, prefixOfObject, object, objectLiteral);
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
	}
	
	protected String setOneOfNTriples(TemplateResource pattern, String outputObj) {
		String tempResult = "";		
		if(!pattern.isLiteral && !pattern.mapping.isEmpty()) {
			if(pattern.mapping.URI.getValue().equals("_:")) {
				tempResult += "_:" + outputObj;
			} else if(((String) pattern.mapping.URI.getValue()).endsWith("/")) {
				tempResult += "<" + pattern.mapping.URI + outputObj + "> ";
			} else {
				tempResult += "<" + pattern.mapping.URI + "/" + outputObj + "> ";
			}
		} else if(!pattern.isLiteral) {
			if(defaultNameSpace.endsWith("/")) {
				tempResult += "<" + defaultNameSpace + outputObj + ">";
			} else {
				tempResult += "<" + defaultNameSpace + "/" + outputObj + ">";
			}
		} else {
			tempResult += "\"" + outputObj + "\" ";
		}
		return tempResult;
	}
	
	public List<String> getTriples(SPOComponent tempComp) {
		List<String> result = new LinkedList<String>();
		String subject = "", predicate = "", object = "";
		if(tempComp.data.subjectList.size() > 0) {
			for(MutableObject subjectName : tempComp.data.subjectList) {
				if(subjectName.getValue() == null || ((String) subjectName.getValue()).isEmpty())
					continue;
				if(tempComp.data.objectList.size() > 0 ) {
					for(MutableObject objectName : tempComp.data.objectList) {
						//if(objectName.getValue() == null)
						//	continue;
						subject = ((String) subjectName.getValue());						
						predicate = (String) tempComp.data.predicateName.getValue();
						object = (String) objectName.getValue();
						String tempResult = "";
						tempResult += setOneOfNTriples(tempComp.metaData.subjectPattern, subject);
						tempResult += setOneOfNTriples(tempComp.metaData.predicatePattern, predicate);
						tempResult += setOneOfNTriples(tempComp.metaData.objectPattern, object);
						tempResult += " .";
						
						result.add(tempResult);			
					}
				}
			}
		}
		return result;
	}
}
