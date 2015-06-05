package xml2rdf.util.rdf;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import xml2rdf.util.xml.XPathUtils;

public class DefaultTemplateParser extends AbstractTemplateParser { 
	
	public static void main(String[] args) {
		String line = "defaultnamespace = http://example.com";
		AbstractTemplateParser parser = new DefaultTemplateParser();
		if(parser.ParseByLine(line)) {
			System.out.println(parser.GetDefaultNameSpaceURI());
		}
		String line2 = "namespace abc = http://abc.com";
		if(parser.ParseByLine(line2)) {
			System.out.println(parser.GetNameSpaceObjectByPrefix("abc").getURI());
		}
		
		String line3 = "abc, /abc/, string,null,title,null,abc,/abc/def,string";
		if(parser.ParseByLine(line3)) {
			System.out.println(parser.constructionPatternList.toString());
		}		
	}
	

	@Override
	public boolean IsAcceptedDataType(String str) {
		if(IsNull(str) || str.toLowerCase().equals("string"))
			return true;
		return false;
	}
	@Override
	public Object GetDataType(String str) {
		if(IsNull(str))
			return null;
		if(str.toLowerCase().equals("string"))
			return (Object) XSDDatatype.XSDstring;
		return null;
	}

	@Override
	public boolean IsAcceptedSubjectPattern(String ns, String ds, String ts) {
		if(IsBlankNode(ns) && IsAcceptedDataType(ts)) {
			return true;
		}
		if((IsAcceptedNameSpacePrefix(ns) || IsNull(ns)) && IsAcceptedDataType(ts)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean IsAcceptedPredicatePattern(String ns, String ds, String ts) {
		if((IsAcceptedNameSpacePrefix(ns) || IsNull(ns)) && IsAcceptedDataType(ts) && 
				!XPathUtils.isXPath(ds)) {
			return true;
		}
		return false;
	}

	
	public boolean IsAcceptedObjectPattern(String ns, String ds, String ts) {
		if(IsBlankNode(ns) && IsAcceptedDataType(ts)) {
			return true;
		}
		if((IsAcceptedNameSpacePrefix(ns) || IsNull(ns)) && IsAcceptedDataType(ts)) {
			return true;
		}
		return false;
	}


	@Override
	public boolean IsComment(String str) {
		String regex = "^(\\s*#).*$";
		return str.matches(regex);		
	}

}
