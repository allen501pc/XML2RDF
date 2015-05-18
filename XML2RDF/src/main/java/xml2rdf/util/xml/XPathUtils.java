package xml2rdf.util.xml;

public class XPathUtils {
	// Find LCA's XPath.
	public static String FindLCA(String xpath1, String xpath2) {
		String[] list1 = xpath1.split("/");
		String[] list2 = xpath2.split("/");
		int size = (list1.length < list2.length)?list1.length:list2.length;
		String result = "";
		for( int i = 1 ; i < size ; ++i ) {
			if(!list1[i].isEmpty() && !list2[i].isEmpty() && list1[i].equals(list2[i])) {
				result += "/" + list1[i];
			} else {
				break;
			}
		}
		
		return result.isEmpty() ? null : result;		
	}
	
	public static boolean isXPath(String xpath) {
		return xpath.indexOf("/") == 0;
	}
	
	public static String[] getLastConditionAttributeRules(String xpath) {
		String[] result = {};
		int startIndex = xpath.lastIndexOf("[@");
		int endIndex = xpath.lastIndexOf("]");
		if(startIndex < 0 || endIndex < 0 ) {
			return result;
		}
		String tempStr = xpath.substring(startIndex + 2, endIndex);
		// TODO: Need to use regular expression.
		if(tempStr.contains("/")) {
			return result;
		} else {
			String[] split = tempStr.split("=");
			if(split.length != 2) {				
				return result;
			} else {
				if(split[0].isEmpty() || split[1].isEmpty()) {
					return result;
				} else {
					String[] result2 = new String[2];
					result2[0] = split[0];
					result2[1] = split[1].replace("'", "");
					return result2;
				}
			}
		}
	}
	
	public static boolean isXPathAttribute(String xpath) {
		int index = xpath.lastIndexOf("/@");
		if(index != -1) {
			String xmlNode = xpath.substring(index + 2);
			if(!xmlNode.isEmpty()) 
				return true;
		}			
		return false;
	}
	
	public static boolean isQueryingTextNodeFromElements(String xpath) {
		if(!isXPathAttribute(xpath)) {
			int index = xpath.lastIndexOf("/text()");
			if(index != -1 && index + 7 == xpath.length() ) {
				return true;
			}
		}
		return false;
	}
	
	protected static String getSkippedBracketsElements(String item) {
		int index = item.indexOf("[");
		if(index != -1) {
			return item.substring(0,index);
		}
		return item;
	}
	
	public static String getXPathWithOutTextNode(String xpath) {
		String[] splitItems = xpath.split("/");
		String result = "";
		for( int i = 0; i < splitItems.length; ++i ) {
			String item = getSkippedBracketsElements(splitItems[i]);
			if(!item.isEmpty()) {
				if(item.indexOf("text()") == -1) {
					result += "/" + item;
				} else {
					break;
				}
			}
		}
		
		return result;
	}	
	
	public static String getXPathWithOutAttributeAndTextNode(String xpath) {
		String[] splitItems = xpath.split("/");
		String result = "";
		for( int i = 0; i < splitItems.length; ++i ) {
			String item = getSkippedBracketsElements(splitItems[i]);
			if(!item.isEmpty()) {
				if(item.indexOf("@") == -1 && item.indexOf("text()") == -1) {
					result += "/" + item;
				} else {
					break;
				}
			}
		}
		
		return result;
	}
	
	public static String getXPathWithOutAttribute(String xpath) {
		String[] splitItems = xpath.split("/");
		String result = "";
		for( int i = 0; i < splitItems.length; ++i ) {
			if(!splitItems[i].isEmpty()) {
				if(splitItems[i].indexOf("@") == -1) {
					result += "/" + splitItems[i];
				} else {
					break;
				}
			}
		}
		
		return result;
	}
	
	public static String getAttributeNameFromXPath(String xpath) {
		String[] splitItems = xpath.split("/");
		if( splitItems.length > 0 ) {
			if( splitItems[splitItems.length-1].indexOf("@") == 0) {
				return splitItems[splitItems.length-1].substring(1);
			}
		}
		return null;
	}
}
