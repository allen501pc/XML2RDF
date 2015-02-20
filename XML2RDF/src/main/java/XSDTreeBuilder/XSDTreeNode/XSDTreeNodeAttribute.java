package XSDTreeBuilder.XSDTreeNode;

public class XSDTreeNodeAttribute extends AbstractXSDTreeNode{
	
	public XSDTreeNodeAttribute(String attr, String attrVal) {
		name = attr; 
		context = attrVal;
	}

	public boolean IsAttribute() {
		return true;
	}	
	
	public String GetValue() {
		return GetContext();
	}
	@Override
	public boolean equals(Object node) {
		if( node instanceof XSDTreeNodeAttribute) {
			return ((XSDTreeNodeAttribute) node).name.equals(this.name);
		} else if ( node instanceof String) {
			return ((String) node).equals(this.name);
		}
		return false;
	}
}
