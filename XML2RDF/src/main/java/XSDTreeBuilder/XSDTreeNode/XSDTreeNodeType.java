package XSDTreeBuilder.XSDTreeNode;

public class XSDTreeNodeType {
	public enum TypeName { ComplexElementType, SimpleElementType, StringType, NumericType, DateType, MiscType, IntegerType, None, ReferenceType };
	
	public static String DefaultNameSpace = "xsd";
	
	public static String TypeNameString(TypeName type) {
		String result = null;
		if(type == null) {
			return null;
		}
		switch(type) {
			case StringType:
				result = "string";
				break;
			case NumericType:
				result = "numeric";
				break;
			case IntegerType:
				result = "integer";
				break;
			case DateType:
				result = "date";
				break;
			case MiscType:
				result = "misc";
				break;
			case ReferenceType:
				result = "reference";
				break;
			default:
				result = null;
				break;
		}
		return result;
	}
	
	public static TypeName GetTypeName(String str) {
		String lowerCaseStr = str.toLowerCase();
		if(lowerCaseStr.equals(DefaultNameSpace + ":string")) {
			return TypeName.StringType;
		} else if (lowerCaseStr.equals(DefaultNameSpace + ":numeric")) {
			return TypeName.StringType;
		} else if (lowerCaseStr.equals(DefaultNameSpace + ":integer")) {
			return TypeName.IntegerType;
		} else if (lowerCaseStr.equals(DefaultNameSpace + ":date" )) {
			return TypeName.DateType;
		} else if (lowerCaseStr.equals(DefaultNameSpace + ":misc")) {
			return TypeName.MiscType;
		} 
		return TypeName.ReferenceType;		
	}
}