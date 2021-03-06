package xml2rdf.util.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.lang.mutable.MutableObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import xml2rdf.util.rdf.GenericResourceValidator;
import xml2rdf.util.rdf.SPOComponent;
import xml2rdf.util.rdf.TemplateRDF;

public class CustomizedSAXYFilterHandler extends DefaultHandler {
		
	public Model model = ModelFactory.createDefaultModel();
	public Resource rootResource = null;
	protected TemplateRDF mainTemplateRDF = null;  
	
	protected LinkedList<MutableObject> delayAddingDataList = new LinkedList<MutableObject>();
	// public Stack<Resource> resourceStack = new Stack<Resource>();
	
	public String relationshipUri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public String xmlns = "";
	public String rootResourceNamespace = "http://example.com/";
	protected boolean isFirstElement = true;
	// Use automatic namespace mapping.
	public boolean useAutomaticNamespaceMapping = false;
	
	protected boolean useRDFStore = true;
	protected PrintWriter output = null;
	
	public void useRDFMemoryStore(boolean turnOn) {
		useRDFStore = turnOn;
	}
	
	public void setOutputStream(PrintWriter printer) {
		output = printer;
	}
	
	protected String outputSubject = "", outputPredicate = "", outputObject = ""; 
	/**
	 * Xpath List. Record current xpaht elements.
	 */
	protected List<String> currentXPathList = new ArrayList<String>() {

		private static final long serialVersionUID = 1189586247780969224L;

		@Override 
		public String toString() {
			String result = "";
			for( int i = 0; i < this.size(); ++i ) {
				result += "/" + this.get(i);			
			}
			return result;
		}
	};	
	
	
	public CustomizedSAXYFilterHandler() { 
		
	}
	
	public void setTemplateRDF(TemplateRDF template) {
		mainTemplateRDF = template; 
	}
	
	public String exportCurrentXPath() {
		return this.currentXPathList.toString();
	}
		
	public void exportCurrentResult(List<String> result) {
		Iterator<String> it = result.iterator();
		while(it.hasNext()) {
			output.println(it.next());			
		}
	}	
	
	public void startDocument() throws SAXException {	
		try {				
			isFirstElement = true;
			// Create Empty Resource.		
			if(useRDFStore) {
				rootResource = model.createResource(rootResourceNamespace);		
			}
			// resourceStack.push(rootResource);
			// Prepare to store static pattern.
			if(mainTemplateRDF != null) {
				for(SPOComponent comp: mainTemplateRDF.spoCompList) {
					if(!comp.metaData.subjectPattern.isXPath &&
					   !comp.metaData.objectPattern.isXPath
					) {
						if(useRDFStore) {
							mainTemplateRDF.GenerateModelFromSPOComponent(model, comp);
						} else {
							exportCurrentResult(mainTemplateRDF.getTriples(comp));
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("ERROR:" + e.getMessage());
		}
	}
	
	public void endDocument() throws SAXException {	
		try {
			/*
			if(this.mainTemplateRDF != null) {
				this.mainTemplateRDF.clearData();
			}
			*/
		} catch (Exception e) {
			System.out.println("ERROR:" + e.getMessage());
		}
	}
	
	/**
	 * Input string: abc_1/de_3
	 * Output string: abc/de
	 * @param relativePath
	 */
	/*
	public static String cleanXPath(String relativePath) {
		String[] items = relativePath.split("/");
		//Pattern pattern = Pattern.compile("([^_][_][0-9]+$)");	
		String result = "";
		boolean isFirst = true;
		for(String item: items) {
			String[] array = item.split("_\\d+");
			String item2 = "";
			if(array.length == 1) {
				item2 = array[0];
			} else {
				for(int i = 0; i < (array.length -1) ; ++i ) {
					item2 += array[i];
				}					
			}	
			if(isFirst) {
				result = item2;
				isFirst = false;
			} else {
				result += "/" + item2;
			}			
		}
		
		return result;
	}
	*/
	
	protected void checkInSLCASection() {
		if(this.mainTemplateRDF.spoCompList.size() > 0) {
			for(SPOComponent comp: this.mainTemplateRDF.spoCompList) {
				if(comp.metaData.SLCAPath.equals(exportCurrentXPath())) {
					this.mainTemplateRDF.currentMaintainedList.add(comp);
				}
			}
		}
	}
	
	protected void generateTriples(String uri, String localName,
            String qName, Attributes attributes) {
		if(this.mainTemplateRDF.currentMaintainedList.size() > 0 ) {
			for(SPOComponent comp: this.mainTemplateRDF.currentMaintainedList) {
				// subject pattern.
				if(comp.metaData.subjectPattern.isXPath) {
					// Check the condition matched or not.  
					boolean hasConditionAttributes = !comp.metaData.subjectPattern.conditionAttributeName.isEmpty();
					boolean isMatched = false;
					if(hasConditionAttributes) {						
						for(int i = 0; i < attributes.getLength(); ++i) {
							if(attributes.getQName(i).equals(comp.metaData.subjectPattern.conditionAttributeName) &&
									attributes.getValue(i).equals(comp.metaData.subjectPattern.conditionAttributeValue)) {
								isMatched = true;
								break;
							}
						}
					}
					if( isMatched || !hasConditionAttributes ) {
						if(comp.metaData.subjectPattern.attributeName.isEmpty()) {
							if(comp.metaData.subjectPattern.xpath.equals(exportCurrentXPath())) {
								// Delay adding  because the value should be retrieved from its' content.						
								MutableObject dalayAddingData = new MutableObject();
								comp.data.subjectList.add(dalayAddingData);
								this.delayAddingDataList.add(dalayAddingData);
							}
						} else {
							// Get the attribute value
							if(comp.metaData.subjectPattern.xpath.equals(exportCurrentXPath())) {
								for(int i = 0; i < attributes.getLength(); ++i) {
									String myQName = attributes.getQName(i);
									//String myAttributeValue = attributes.getValue(i);
									if(myQName.equals(comp.metaData.subjectPattern.attributeName)) {
										comp.data.subjectList.add(new MutableObject(attributes.getValue(i)));
									}
								}
							}
						}
					}
				}
				// Object pattern
				if(comp.metaData.objectPattern.isXPath) {
					
					boolean hasConditionAttributes = !comp.metaData.objectPattern.conditionAttributeName.isEmpty();
					boolean isMatched = false;
					if(hasConditionAttributes) {						
						for(int i = 0; i < attributes.getLength(); ++i) {
							if(attributes.getQName(i).equals(comp.metaData.objectPattern.conditionAttributeName) &&
									attributes.getValue(i).equals(comp.metaData.objectPattern.conditionAttributeValue)) {
								isMatched = true;
								break;
							}
						}
					}
					if(isMatched || !hasConditionAttributes ) {
						if(comp.metaData.objectPattern.attributeName.isEmpty()) {						
							// Delay adding  because the value should be retrieved from its' content.
							if(comp.metaData.objectPattern.xpath.equals(exportCurrentXPath())) {
								MutableObject dalayAddingData = new MutableObject();
								comp.data.objectList.add(dalayAddingData);
								this.delayAddingDataList.add(dalayAddingData);
							}
						} else {
							// Get the attribute value
							if(comp.metaData.objectPattern.xpath.equals(exportCurrentXPath())) {
								for(int i = 0; i < attributes.getLength(); ++i) {
									String myQName = attributes.getQName(i);
									//String myAttributeValue = attributes.getValue(i);
									if(myQName.equals(comp.metaData.objectPattern.attributeName)) {
										comp.data.objectList.add(new MutableObject(attributes.getValue(i)));
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	protected void checkOutSLCASection() {
		
		if(this.mainTemplateRDF.currentMaintainedList.size() > 0 ) {
			ListIterator<SPOComponent> iter = this.mainTemplateRDF.currentMaintainedList.listIterator();
			while(iter.hasNext()) {
				SPOComponent comp = iter.next();
				if(comp.metaData.SLCAPath.equals(exportCurrentXPath())) {
					
					if(useRDFStore) {
						this.mainTemplateRDF.GenerateModelFromSPOComponent(model,comp);
					} else {
						exportCurrentResult(this.mainTemplateRDF.getTriples(comp));
					}
					comp.resetData();
					iter.remove();
				}
			}
		}
	}
	
	public void startElement(String uri, String localName,
            String qName, Attributes attributes) {
		
		this.currentXPathList.add(qName);
		if(this.mainTemplateRDF.filter != null ) {
			this.mainTemplateRDF.filter.ReadElement(qName);
			if(this.mainTemplateRDF.filter.IsAccept()) {
				checkInSLCASection();
				generateTriples(uri,localName,qName,attributes);
			}
		}
				
	}
	
	@Override
	public void endElement(String uri, String localName,
			 String qName) {
		/*outputStream += "</rdf:Description>" + System.lineSeparator();
		outputStream += "</" + qName + ">" + System.lineSeparator();			
		*/
		checkOutSLCASection();
		this.delayAddingDataList.clear();
		// Pop out the last one.
		if(currentXPathList.size() > 0) {
			currentXPathList.remove(currentXPathList.size() -1);
		}
		
		if(this.mainTemplateRDF.filter != null) {
			this.mainTemplateRDF.filter.PopElement();						
		}

		/*
		if(outputXPathList.size() > 0) {
			outputXPathList.remove(outputXPathList.size() -1);
		}
		*/
	}
	
    public void characters(char ch[], int start, int length) throws SAXException {
    	String value = StringEscapeUtils.escapeXml(new String(ch, start, length).trim());
    	try {
    		// if(value.length()>0) {
    			// outputStream += "<rdf:value>" + value + "</rdf:value>" + System.lineSeparator();
    			ListIterator<MutableObject> listIterator = this.delayAddingDataList.listIterator();
    			while(listIterator.hasNext()) {
    				listIterator.next().setValue(value);
    			}    			    			
    		// } 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
    	
    }
}
