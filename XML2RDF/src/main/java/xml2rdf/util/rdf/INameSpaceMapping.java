package xml2rdf.util.rdf;

import org.apache.commons.lang.mutable.MutableObject;

interface INameSpaceMapping {
	public MutableObject getPrefixName();
	
	public MutableObject getURI();
}
