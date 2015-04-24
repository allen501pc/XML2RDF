package xml2rdf.util.rdf;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

public class TemplateResource implements Cloneable{
	public boolean isXPath = false;
	// public String identifier = "";
	public String attributeName = "";
	public String xpath = "";
	public String outputIdentifier = "";
	public boolean isLiteral = false;
	// public enum TypeOfLiteral{ xsd_string, xsd_integer,xsd_date, xsd_float,none};
	public XSDDatatype literalType = null;
	public static TemplateResource newInstance() {
		return new TemplateResource();
	}
	
	public static TemplateResource newInstance(TemplateResource source) {
		TemplateResource myResource = new TemplateResource();
		myResource.copy(source);
		return myResource;
	}
	
	public void copy(TemplateResource res) {
		this.isXPath = res.isXPath;		
		this.attributeName = res.attributeName;
		this.xpath = res.xpath;
		this.outputIdentifier = res.outputIdentifier;
	}
	
	public Object clone() { 
		TemplateResource res = TemplateResource.newInstance();
		res.isXPath = this.isXPath;
		// res.identifier = this.identifier;
		res.attributeName = this.attributeName;
		res.xpath = this.xpath;
		res.outputIdentifier = this.outputIdentifier;
		return res;
	}
	
	
	@Override
	public int hashCode() { 
		return outputIdentifier.hashCode();
	}
}
