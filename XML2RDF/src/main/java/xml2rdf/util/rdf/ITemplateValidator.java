package xml2rdf.util.rdf;

interface ITemplateValidator {
	
	public boolean IsSettingDefaultNameSpace(String str);
	
	public String GetDefaultNameSpaceURI();
	
	public boolean IsSettingNameSpace(String str);	
	
	public boolean IsURI(String str);
	
	public boolean IsConstructingPattern(String str);
	
	public boolean IsAcceptedDataType(String str);
	
	public boolean ParseByLine(String line);
	
	public boolean IsAcceptedNameSpacePrefix(String str);
	
	public boolean IsNull(String str);
	
	public Object GetDataType(String str);
	
	public boolean IsAcceptedSubjectPattern(String ns, String ds, String ts);
	
	public boolean IsAcceptedPredicatePattern(String ns, String ds, String ts);
	
	public boolean IsAcceptedObjectPattern(String ns, String ds, String ts);
		
}
