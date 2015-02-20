package XSDTreeBuilder.XSDTreeNode;

import java.util.ArrayList;
import java.util.List;

import XSDTreeBuilder.XSDTreeNode.XSDTreeNodeType;


public class XSDTreeNodeElement extends AbstractXSDTreeNode {		
	// protected List<XSDTreeNodeAttribute> attrList = new ArrayList<XSDTreeNodeAttribute>();   
	protected String Annotation = "";
	
	public void SetAttribute(String attr, String attrVal, XSDTreeNodeType.TypeName type) {
		XSDTreeNodeAttribute node = new XSDTreeNodeAttribute(attr, attrVal);	
		node.SetType(type);
		this.children.add(node);
	}

	
	public List<XSDTreeNodeAttribute> GetAttributeList() {
		ArrayList<XSDTreeNodeAttribute> attrList = new ArrayList<XSDTreeNodeAttribute>();
		for(AbstractXSDTreeNode node: children) {
			if(node.IsAttribute()) {
				attrList.add((XSDTreeNodeAttribute) node);
			}
		}
		return attrList;
	}
	
	public void SetAnnotation(String anno) {
		Annotation = anno;
	}
	
	public String GetAnnotation() {
		return this.Annotation;
	}
	
	public XSDTreeNodeElement() {
		SetType(XSDTreeNodeType.TypeName.SimpleElementType);
	}
	
	public XSDTreeNodeElement(String name, XSDTreeNodeType.TypeName typeName ) {
		SetName(name);
		SetType(typeName);
	}				

	public boolean IsAttribute() {		
		return false;
	}
}
