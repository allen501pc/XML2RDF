package xml2rdf.util.rdf;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

public class TemplateResource implements Cloneable{
	public boolean isXPath = false;
 
	public String attributeName = "";
	public String xpath = "";
	public String outputIdentifier = "";
	public NameSpaceMapping mapping = new NameSpaceMapping();
	public boolean isLiteral = false;
	public String conditionAttributeName = "";
	public String conditionAttributeValue = "";

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
		this.mapping.copy(res.mapping);
		this.conditionAttributeName = res.conditionAttributeName;
		this.conditionAttributeValue = res.conditionAttributeValue;
	}
	
	public Object clone() { 
		TemplateResource res = TemplateResource.newInstance();
		res.isXPath = this.isXPath;
		// res.identifier = this.identifier;
		res.attributeName = this.attributeName;
		res.xpath = this.xpath;
		res.outputIdentifier = this.outputIdentifier;
		res.mapping.copy(this.mapping);
		res.conditionAttributeName = this.conditionAttributeName;
		res.conditionAttributeValue = this.conditionAttributeValue;
		return res;
	}
	
	
	@Override
	public int hashCode() { 
		return outputIdentifier.hashCode();
	}
}
