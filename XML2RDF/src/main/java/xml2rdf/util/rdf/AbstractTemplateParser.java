package xml2rdf.util.rdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang.mutable.MutableObject;

public abstract class AbstractTemplateParser implements ITemplateValidator, ITemplateParser {
	protected MutableObject defaultNameSpaceURI = new MutableObject(""); 
	protected Set<NameSpaceMapping> acceptedPrefixURImapping = new HashSet<NameSpaceMapping>();
	public List<ArrayList<Object>> constructionPatternList = new ArrayList<ArrayList<Object>>();
	
	AbstractTemplateParser() {
		acceptedPrefixURImapping.add(new NameSpaceMapping("_","_:"));
	}
	@Override
	public boolean IsSettingDefaultNameSpace(String str) {
		String regex = "\\s*(defaultnamespace)\\s*[=]\\s*(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		return str.matches(regex);
	}
	
	/* (non-Javadoc)
	 * @see xml2rdf.util.rdf.ITemplateParser#SetDefaultNameSpace(java.lang.String)
	 */
	@Override
	public void SetDefaultNameSpace(String namespaceExpression) {
		if(IsSettingDefaultNameSpace(namespaceExpression)) {
			Pattern namespacePattern = Pattern.compile("[=]\\s*((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");
			Matcher myMatcher = namespacePattern.matcher(namespaceExpression.substring(namespaceExpression.indexOf("=")));
			while(myMatcher.find()) {
				defaultNameSpaceURI.setValue(myMatcher.group(1).toString());
				break;
			}
		}
	}
	
	public NameSpaceMapping GetNameSpaceObjectByPrefix(String prefixName) {
		Iterator<NameSpaceMapping> it = acceptedPrefixURImapping.iterator();
		while(it.hasNext()) {
			NameSpaceMapping obj = it.next();
			if(obj.find(prefixName)) {
				return obj;
			}
		}
		return null;
	}
	
	public boolean IsAcceptedNameSpacePrefix(String prefixName) {
		return GetNameSpaceObjectByPrefix(prefixName) != null;
	}
	
	@Override
	public String GetDefaultNameSpaceURI() {
		return defaultNameSpaceURI.getValue().toString();
	}
	
	@Override
	public boolean IsSettingNameSpace(String str) {
		String regex = "\\s*(namespace)\\s+[a-zA-Z]+[0-9]*\\s*[=]\\s*(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		return str.matches(regex);
	}
	
	/* (non-Javadoc)
	 * @see xml2rdf.util.rdf.ITemplateParser#SetNameSpace(java.lang.String)
	 */
	@Override
	public void SetNameSpace(String namespaceExpression) {
		if(IsSettingNameSpace(namespaceExpression)) {
			Pattern namespacePattern = Pattern.compile("\\s*([a-zA-Z]+[0-9]*)\\s*[=]\\s*((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");
			Matcher myMatcher = namespacePattern.matcher(namespaceExpression.substring(namespaceExpression.indexOf("namespace") + 10));
			while(myMatcher.find()) {				
				acceptedPrefixURImapping.add(new NameSpaceMapping(myMatcher.group(1),myMatcher.group(2)));
				break;
			}
		}
	}
	
	@Override
	public void Parse(Reader reader) {
		try (BufferedReader br = new BufferedReader(reader)) {
			String sCurrentLine = null;
			 
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.length() != 0)
					ParseByLine(sCurrentLine);
			}
		} catch (IOException e) {			
			e.printStackTrace();
		} 
	}
	
	/* (non-Javadoc)
	 * @see xml2rdf.util.rdf.ITemplateParser#ParseByLine(java.lang.String)
	 */
	@Override
	public boolean ParseByLine(String line) {		
		if(this.IsSettingDefaultNameSpace(line)) {
			this.SetDefaultNameSpace(line);
			return true;
		} else if (this.IsSettingNameSpace(line)) {
			this.SetNameSpace(line);
			return true;
		} else if (this.IsConstructingPattern(line)) {
			boolean added  = constructionPatternList.add(this.GetConstructingPattern(line));
			return added;
		} else if (this.IsComment(line)) {
			return true;
		}
		return false;
	}
	
	public boolean IsURI(String str) {
		String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";		
		return str.matches(regex);
	}
	
	
	public boolean IsBlankNode(String ns) {
		if(ns.equals("_")) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean IsConstructingPattern(String str) {
		String[] triples = str.split(" ");
		String nsPart = "", descriptionPart = "", typePart = "";
		if(triples == null || (triples != null && triples.length != 3)) {
			return false;
		}
		
		
		for(int i = 0; i < triples.length; ++i) {
			
			if(triples[i] == null)
				return false;
			String item = triples[i].trim();

			if(item.isEmpty())
				return false;
			
			switch(i+1) {
				case 1: // For subject
					String[] components = item.split(",");
					if(components.length == 1) { // Only description
						nsPart = "";
						descriptionPart = components[0].trim();
						typePart = "";
						if (!IsAcceptedSubjectPattern(nsPart, descriptionPart, typePart)) {
							return false;
						}						
					} else if (components.length == 2) { // Check ns,des or des,type
						boolean finalResult = true;
						// Check ns,des 
						nsPart = components[0].trim();
						descriptionPart = components[1].trim();
						typePart = "";
						if (!IsAcceptedSubjectPattern(nsPart, descriptionPart, typePart)) {							
							finalResult = false;
						}
						
						if (finalResult == false) {	
							// Check des,type
							nsPart = "";
							descriptionPart = components[0].trim();
							typePart = components[1].trim();
							if (!IsAcceptedSubjectPattern(nsPart, descriptionPart, typePart)) {
								finalResult = false;
							}
						}
						if(finalResult == false) {
							return false;
						}
					} else if (components.length == 3) {
						nsPart = components[0].trim();
						descriptionPart = components[1].trim();
						typePart = components[2].trim();
						if (!IsAcceptedSubjectPattern(nsPart, descriptionPart, typePart)) {
							return false;
						}						
					}
					break;
				case 2: // For predicate
					boolean finalResult = true;
					String[] components2 = item.split(",");
					if(components2.length == 1) { // Only description
						nsPart = "";
						descriptionPart = components2[0].trim();
						typePart = "";
						if (!IsAcceptedPredicatePattern(nsPart, descriptionPart, typePart)) {
							finalResult =  false;
						}						
					} else if (components2.length == 2) {						
						// Check ns,des 
						nsPart = components2[0].trim();
						descriptionPart = components2[1].trim();
						typePart = "";
						if (!IsAcceptedPredicatePattern(nsPart, descriptionPart, typePart)) {							
							finalResult =  false;
						}																	
					}
					if(finalResult == false) {
						return false;
					}
					break;
				case 3: // For object.
					String[] components3 = item.split(",");
					if(components3.length == 1) { // Only description
						nsPart = "";
						descriptionPart = components3[0].trim();
						typePart = "";
						if (!IsAcceptedObjectPattern(nsPart, descriptionPart, typePart)) {
							return false;
						}						
					} else if (components3.length == 2) { // Check ns,des or des,type
						boolean finalResult3 = true;
						// Check ns,des 
						nsPart = components3[0].trim();
						descriptionPart = components3[1].trim();
						typePart = "";
						if (!IsAcceptedObjectPattern(nsPart, descriptionPart, typePart)) {							
							finalResult3 = false;
						}
						
						if (finalResult3 == false) {	
							// Check des,type
							nsPart = "";
							descriptionPart = components3[0].trim();
							typePart = components3[1].trim();
							if (!IsAcceptedObjectPattern(nsPart, descriptionPart, typePart)) {
								finalResult3 = false;
							}
						}
						if(finalResult3 == false) {
							return false;
						}						
					} else if (components3.length == 3) {
						nsPart = components3[0].trim();
						descriptionPart = components3[1].trim();
						typePart = components3[2].trim();
						if (!IsAcceptedObjectPattern(nsPart, descriptionPart, typePart)) {
							return false;
						}						
					}
					break;
			}									
			
//			switch(i%3) {
//				case 0 :
//					nsPart = item;
//					
//					break;
//				
//				case 1 :
//					descriptionPart = item;
//					// if( !IsAcceptedNameSpacePrefix(triples[i]) && !IsURI(triples[i]) && !IsNull(triples[i]) )
//					// 	return false;
//					break;
//					
//				case 2 : 
//					typePart = item;
//					switch (i) {
//						case 2:
//							if (!IsAcceptedSubjectPattern(nsPart, descriptionPart, typePart)) {
//								return false;
//							}
//							break;
//						case 5: 
//							if ( !IsAcceptedPredicatePattern(nsPart, descriptionPart, typePart)) {
//								return false;
//							}
//							break;
//						case 8:
//							if ( !IsAcceptedObjectPattern(nsPart, descriptionPart, typePart)) {
//								return false;
//							}
//							break;
//						default:
//							break;
//					}
//					break;	
//					
//				default:
//					break;
//			}
		}		
		return true;
	}
	
	public ArrayList<Object> GetConstructingPattern(String str) {
		String[] triples = str.split(" ");
		ArrayList<Object> list = new ArrayList<Object>();
		String nsPart = "", descriptionPart = "", typePart = "";
		if(triples == null || (triples != null && triples.length != 3)) {
			return list;
		}
		for(int i = 0; i < triples.length; ++i) {
			
			if(triples[i] == null)
				return list;
			String item = triples[i].trim();

			if(item.isEmpty())
				return list;
			
			switch(i+1) {
				case 1: // For subject
					String[] components = item.split(",");
					if(components.length == 1) { // Only description
						nsPart = "";
						descriptionPart = components[0].trim();
						typePart = "";
						
						if ( !IsAcceptedSubjectPattern(nsPart, descriptionPart, typePart)) {
							list.clear();
							return list;
						}
						list.add(GetNameSpaceObjectByPrefix(nsPart));
						list.add(descriptionPart);
						list.add(GetDataType(typePart));
					} else if (components.length == 2) { // Check ns,des or des,type
						boolean finalResult = true;
						// Check ns,des 
						nsPart = components[0].trim();
						descriptionPart = components[1].trim();
						typePart = "";
						if (!IsAcceptedSubjectPattern(nsPart, descriptionPart, typePart)) {							
							finalResult = false;
						}
						if (finalResult == false) {	
							// Check des,type
							nsPart = "";
							descriptionPart = components[0].trim();
							typePart = components[1].trim();
							if (!IsAcceptedSubjectPattern(nsPart, descriptionPart, typePart)) {
								finalResult = false;
							}
						}
						if(finalResult == false) {
							list.clear();
							return list;
						}
						list.add(GetNameSpaceObjectByPrefix(nsPart));
						list.add(descriptionPart);
						list.add(GetDataType(typePart));
					} else if (components.length == 3) {
						nsPart = components[0].trim();
						descriptionPart = components[1].trim();
						typePart = components[2].trim();
						if (!IsAcceptedSubjectPattern(nsPart, descriptionPart, typePart)) {
							list.clear();
							return list;
						}
						list.add(GetNameSpaceObjectByPrefix(nsPart));
						list.add(descriptionPart);
						list.add(GetDataType(typePart));
					}
					break;
				case 2: // For predicate
					boolean finalResult = true;
					String[] components2 = item.split(",");
					if(components2.length == 1) { // Only description
						nsPart = "";
						descriptionPart = components2[0].trim();
						typePart = "";
						if (!IsAcceptedPredicatePattern(nsPart, descriptionPart, typePart)) {
							finalResult =  false;
						}					
					} else if (components2.length == 2) {						
						// Check ns,des 
						nsPart = components2[0].trim();
						descriptionPart = components2[1].trim();
						typePart = "";
						if (!IsAcceptedPredicatePattern(nsPart, descriptionPart, typePart)) {							
							finalResult =  false;
						}																	
					}
					if(finalResult == false) {
						list.clear();
						return list;
					}
					list.add(GetNameSpaceObjectByPrefix(nsPart));
					list.add(descriptionPart);
					list.add(GetDataType(typePart));
					break;
				case 3: // For object.
					String[] components3 = item.split(",");
					if(components3.length == 1) { // Only description
						nsPart = "";
						descriptionPart = components3[0].trim();
						typePart = "";
						if (!IsAcceptedObjectPattern(nsPart, descriptionPart, typePart)) {
							list.clear();
							return list;
						}						
					} else if (components3.length == 2) { // Check ns,des or des,type
						boolean finalResult3 = true;
						// Check ns,des 
						nsPart = components3[0].trim();
						descriptionPart = components3[1].trim();
						typePart = "";
						if (!IsAcceptedObjectPattern(nsPart, descriptionPart, typePart)) {							
							finalResult3 = false;
						}
						
						if (finalResult3 == false) {	
							// Check des,type
							nsPart = "";
							descriptionPart = components3[0].trim();
							typePart = components3[1].trim();
							if (!IsAcceptedObjectPattern(nsPart, descriptionPart, typePart)) {
								finalResult3 = false;
							}
						}
						if(finalResult3 == false) {
							list.clear();
							return list;
						}						
					} else if (components3.length == 3) {
						nsPart = components3[0].trim();
						descriptionPart = components3[1].trim();
						typePart = components3[2].trim();
						if (!IsAcceptedObjectPattern(nsPart, descriptionPart, typePart)) {
							list.clear();
							return list;
						}						
					}
					list.add(GetNameSpaceObjectByPrefix(nsPart));
					list.add(descriptionPart);
					list.add(GetDataType(typePart));
					break;
			}	
		
//		for(int i = 0; i < triples.length; ++i) {
//			if(triples[i] == null)
//				return list;
//			String item = triples[i].trim();
//			if(item.isEmpty())
//				return list;			
//			switch(i%3) {
//				case 0 :
//					nsPart = item;					
//					break;
//				
//				case 1 :
//					descriptionPart = item;					
//					break;
//					
//				case 2 : 
//					typePart = item;					
//					
//					switch (i) {
//						case 2:
//							if ( !IsAcceptedSubjectPattern(nsPart, descriptionPart, typePart)) {
//								list.clear();
//								return list;
//							}
//							list.add(GetNameSpaceObjectByPrefix(nsPart));
//							list.add(descriptionPart);
//							list.add(GetDataType(typePart));
//							break;
//						case 5: 
//							if ( !IsAcceptedPredicatePattern(nsPart, descriptionPart, typePart)) {
//								list.clear();
//								return list;
//							}
//							list.add(GetNameSpaceObjectByPrefix(nsPart));
//							list.add(descriptionPart);
//							list.add(GetDataType(typePart));
//							break;
//						case 8:
//							if ( !IsAcceptedObjectPattern(nsPart, descriptionPart, typePart)) {
//								list.clear();
//								return list;
//							}
//							list.add(GetNameSpaceObjectByPrefix(nsPart));
//							list.add(descriptionPart);
//							list.add(GetDataType(typePart));
//							break;
//						default:
//							break;
//					}
//					break;	
//					
//				default:
//					break;
//			}
		}		
		return list;
	}
	
	@Override
	public boolean IsNull(String str) {
		return str.isEmpty();
	}
	
}
