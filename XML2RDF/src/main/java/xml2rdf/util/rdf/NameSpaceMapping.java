package xml2rdf.util.rdf;

import org.apache.commons.lang.mutable.MutableObject;

public class NameSpaceMapping implements INameSpaceMapping {
	protected MutableObject prefixName = new MutableObject(""); 
	protected MutableObject URI = new MutableObject("");
	
	public NameSpaceMapping() {};
	public NameSpaceMapping(String prefixName, String URI) {
		setPrefix(prefixName,URI);
	}	
	
	
	public void setPrefix(String prefixName, String URI) {
		if(prefixName!= null && !prefixName.isEmpty() &&
			URI != null && !URI.isEmpty()) {
			this.prefixName.setValue(prefixName);
			this.URI.setValue(URI);
		}
	}
	public void copy(NameSpaceMapping mapping) {
		this.prefixName = mapping.prefixName;
		this.URI = mapping.URI;
	}
	
	public boolean isEmpty() {
		return (prefixName == null || URI == null) ? true:
			(( ((String) prefixName.getValue()).isEmpty() ||
			  ((String) URI.getValue()).isEmpty() )?true:false);
	}
	
	public boolean find(String prefixName) {
		return this.prefixName.getValue().equals(prefixName);
	}
	
	public int hashCode() { 
		return prefixName.hashCode();
	}
	
	public boolean equals(Object mapping) {
		return equals((NameSpaceMapping) mapping);
	}
	
	public boolean equals(NameSpaceMapping mapping) {
		return  this.prefixName.equals(mapping.prefixName) && this.URI.equals(mapping.URI);
	}
	@Override
	public MutableObject getPrefixName() {
		return this.prefixName;
	}
	@Override
	public MutableObject getURI() {
		return this.URI;
	}
		
	
}
