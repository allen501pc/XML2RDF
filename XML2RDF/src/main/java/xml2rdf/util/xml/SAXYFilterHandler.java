package xml2rdf.util.xml;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.mutable.MutableInt;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import xml2rdf.util.rdf.GenericResourceValidator;

public class SAXYFilterHandler extends DefaultHandler {
	
	public YFilter currentFilter = null;
	static GenericResourceValidator validator = new GenericResourceValidator();
	public String outputStream = new String();
	protected Map<String, MutableInt> elementCountsTable = new HashMap<String, MutableInt>();
	public Model model = ModelFactory.createDefaultModel();
	public Resource rootResource = null;
	public Stack<Resource> resourceStack = new Stack<Resource>();
	
	public String relationshipUri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public String xmlns = "";
	public String rootResourceNamespace = "http://example.com/";
	protected boolean isFirstElement = true;
	// Use automatic namespace mapping.
	public boolean useAutomaticNamespaceMapping = false;
	
	/**
	 * Xpath List. Record current xpaht elements.
	 */
	protected List<String> currentXPathList = new ArrayList<String>() {
			
		/**
		 * 
		 */
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
	
	/**
	 * Xpath List. Record current xpaht elements.
	 */
	protected List<String> outputXPathList = new ArrayList<String>() {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * 
		 */
		

		@Override 
		public String toString() {
			String result = "";
			for( int i = 0; i < this.size(); ++i ) {
				result += "/" + this.get(i);			
			}
			return result;
		}
	};
	
	
	/**
	 * 
	 */		
	
	
	public SAXYFilterHandler() { 
		
	}
	
	public void setHandler(YFilter srcFilter) {
		setFilter(srcFilter);
	}
	
	public void setWriter(StringWriter writer) {
		// this.outputStream = writer;
	}

	
	public void setFilter(YFilter srcFilter) {
		currentFilter = srcFilter; 
	}
	
	public String exportCurrentXPath() {
		return this.currentXPathList.toString();
	}
	
	public String exportOutputXPath() {
		return this.outputXPathList.toString();
	}
	
	public void startDocument() throws SAXException {	
		try {
			/*
			outputStream += "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" + System.lineSeparator();
			outputStream += "<rdf:Description xmlns:xs=\"http://www.w3.org/TR/2008/REC-xml-20081126#\""
					+ "rdf:about=\"\">" + System.lineSeparator();	
			*/	
			if(currentFilter == null ) {
				isFirstElement = true;
				// Create Empty Resource.
				rootResource = model.createResource(rootResourceNamespace);				
				resourceStack.push(rootResource);
			}
		} catch (Exception e) {
			System.out.println("ERROR:" + e.getMessage());
		}
	}
	
	public void endDocument() throws SAXException {	
		try {
			//outputStream += "</rdf:Description>" + System.lineSeparator();
			//outputStream += "</rdf:RDF>" + System.lineSeparator() + System.lineSeparator();			
		} catch (Exception e) {
			System.out.println("ERROR:" + e.getMessage());
		}
		
	}
	
	/**
	 * Input string: abc_1/de_3
	 * Output string: abc/de
	 * @param relativePath
	 */
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
	
	public void startElement(String uri, String localName,
            String qName, Attributes attributes) {
		// Check Namespace. TODO: It's based on what's the needs we have to do. For exampe, XSD may not be very good to use because of less semantic mapping.
		/*
		if(isFirstElement) {
			isFirstElement = false;
			for(int i = 0; i < attributes.getLength(); ++i) {
				String myQName = attributes.getQName(i);
				String myAttributeValue = attributes.getValue(i);
				
			}
		}
		*/
		
		this.currentXPathList.add(qName);
		String myCurrentXPath = exportCurrentXPath();
		String outputQName = "";
		MutableInt currentCount = elementCountsTable.get(myCurrentXPath);
		if(currentCount == null) {
			currentCount = new MutableInt(1);
			outputQName = qName;
			elementCountsTable.put(myCurrentXPath, currentCount);
			outputXPathList.add(outputQName);
			
		} else {
			currentCount.add(1);
			outputQName = qName + "_" + currentCount;
			outputXPathList.add(outputQName);
		}
		
		if( currentFilter != null ) {
			currentFilter.ReadElement(qName);
			if(currentFilter.IsAccept()) {				
				// output the stream.
				Resource myResource = model.createResource(rootResourceNamespace + "#" + exportOutputXPath().substring(1));
				// TODO: Create RDF Resource type.
				myResource.addProperty(model.createProperty("type"), model.createResource("http://RTML.com/#myResource"));
				if(resourceStack.size() != 0) {
					String propertyLabelName = "";
					
					if(myResource.getURI().indexOf(resourceStack.peek().getURI()) == 0) {
						propertyLabelName = myResource.getURI().substring(resourceStack.peek().getURI().length()+1);
					} else {
						propertyLabelName = qName;
					}
										
					resourceStack.peek().addProperty(model.createProperty(rootResourceNamespace + "#",propertyLabelName), myResource);
				}
				
				try {
					// outputStream += "<" + qName + ">" + System.lineSeparator();				
					// outputStream += "<rdf:Description rdf:about=\"" + exportOutputXPath() + "\" >" + System.lineSeparator();
					
					// If it's in accept status, check the attributes in it or not.
					for(int i = 0; i < attributes.getLength(); ++i) {
						String myQName = attributes.getQName(i);
						String myAttributeValue = attributes.getValue(i);
						if( true/* TODO: User defined Attirbutes */ /* myQName.equals(anObject ) */) {
							// outputStream += "<" + myQName + ">" + myAttributeValue + "</" + myQName + ">" + System.lineSeparator();
							
							 myResource.addProperty(model.createProperty(rootResourceNamespace + "#",myQName), model.createLiteral(myAttributeValue));
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								
				resourceStack.push(myResource);
					
				/*
				if(	validator.IsAttribute(attribute) && validator.IsAttributeInGivenStatement(attribute,s.getPredicate().getLocalName())) {
					return true;
				} else if (!validator.IsAttribute(attribute) && validator.IsAcquireValue(attribute, s.getPredicate().getLocalName())) {
					return true;
				}
				return false;
				*/
			}
		} else {
			try {
				Resource myResource = model.createResource(rootResourceNamespace + "#" + exportOutputXPath().substring(1));
				
				// TODO: Create RDF Resource type.
				myResource.addProperty(model.createProperty("type"), model.createResource("http://RTML.com/#myResource"));
				if(resourceStack.size() != 0) {
					resourceStack.peek().addProperty(model.createProperty(rootResourceNamespace + "#",qName), myResource);
				}
				/*
				outputStream += "<" + qName + ">" + System.lineSeparator();
				outputStream += "<rdf:Description rdf:about=\"" + exportOutputXPath() + "\" >" + System.lineSeparator();
				*/
				// If it's in accept status, check the attributes in it or not.
				for(int i = 0; i < attributes.getLength(); ++i) {
					String myQName = attributes.getQName(i);
					String myAttributeValue = attributes.getValue(i);
					if( true/* TODO: User defined Attirbutes */ /* myQName.equals(anObject ) */) {
						/* outputStream += "<" + myQName + ">" + myAttributeValue + "</" + myQName + ">" + System.lineSeparator();
						 */
						 myResource.addProperty(model.createProperty(rootResourceNamespace + "#",myQName), model.createLiteral(myAttributeValue));
					}
				}
				resourceStack.push(myResource);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
			
		}
	}
	
	@Override
	public void endElement(String uri, String localName,
			 String qName) {
		/*outputStream += "</rdf:Description>" + System.lineSeparator();
		outputStream += "</" + qName + ">" + System.lineSeparator();			
		*/
		
		
		// Pop out the last one.
		if(currentXPathList.size() > 0) {
			currentXPathList.remove(currentXPathList.size() -1);
		}
		if(outputXPathList.size() > 0) {
			outputXPathList.remove(outputXPathList.size() -1);
		}
		
		if(currentFilter != null) {
			if(currentFilter.IsAccept()) {
				resourceStack.pop();
			}
			currentFilter.PopElement();						
		} else {
			resourceStack.pop();
		}
	}
	
    public void characters(char ch[], int start, int length) throws SAXException {
    	String value = new String(ch, start, length).trim();
    	try {
    		if(value.length()>0) {
    			// outputStream += "<rdf:value>" + value + "</rdf:value>" + System.lineSeparator();
    			if(currentFilter != null && currentFilter.IsAccept())
    				resourceStack.peek().addProperty(model.createProperty(relationshipUri,"value"), model.createLiteral(value));
    			else if(currentFilter == null) {
    				resourceStack.peek().addProperty(model.createProperty(relationshipUri,"value"), model.createLiteral(value));
    			}
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
    	
    }
}
