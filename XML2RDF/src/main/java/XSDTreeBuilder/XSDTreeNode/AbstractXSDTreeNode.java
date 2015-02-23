package XSDTreeBuilder.XSDTreeNode;

import java.util.ArrayList;
import java.util.Collections;

import XSDTreeBuilder.XSDTreeNode.XSDTreeNodeType;

public abstract class AbstractXSDTreeNode implements IXSDTreeNode, Cloneable {

	protected ArrayList<AbstractXSDTreeNode> children = new ArrayList<AbstractXSDTreeNode>();
	protected AbstractXSDTreeNode parent = null;
	protected String name = "", context = "";
	protected XSDTreeNodeType.TypeName typeName = null;
	protected String referenceName = "";
	protected int depth = 0;
	protected boolean isCustom = false;
	
	public AbstractXSDTreeNode() {
		super();
	}
	
	public void SetDepth(int depth) {
		this.depth = depth;
	}
	
	public int GetDepth() {
		return this.depth;
	}

	public boolean IsRoot() { 
		return parent == null;
	}

	public String GetName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see nchu.cs.asm.xsdtreenode.IXSDTreeNode#SetName(java.lang.String)
	 */
	public void SetName(String name) {
		this.name = name;
	}
	
	public void SetType(XSDTreeNodeType.TypeName type) {
		this.typeName = type;
	}
	
	public String GetTypeName() {
		return XSDTreeNodeType.TypeNameString(this.typeName);		
	}

	/* (non-Javadoc)
	 * @see nchu.cs.asm.xsdtreenode.IXSDTreeNode#GetChildren()
	 */
	public ArrayList<AbstractXSDTreeNode> GetChildren() { 
		return children;
	}

	public AbstractXSDTreeNode GetParent() {
		return parent;
	}
	
	public AbstractXSDTreeNode(String name) {
		SetName(name);
	}	
	
	public AbstractXSDTreeNode AddChildren(AbstractXSDTreeNode node){		
		try {			
			AbstractXSDTreeNode cloneNode = (AbstractXSDTreeNode) node.clone();
			cloneNode.parent = this;
			children.add(cloneNode);
			return cloneNode;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean RemoveChild(AbstractXSDTreeNode child) {
		return this.children.remove(child);
	}
	
	public void MergeChildrenFrom(AbstractXSDTreeNode sourceNode) {
		ArrayList<AbstractXSDTreeNode> sourceChildrenList = sourceNode.GetChildren();
		ArrayList<AbstractXSDTreeNode> tempChildrenList = new ArrayList<AbstractXSDTreeNode>(); 
		for(AbstractXSDTreeNode sourceChild: sourceChildrenList) {			
			boolean isFoundSameChild = false;
			for(AbstractXSDTreeNode targetChild: this.children) {
				if(targetChild.GetName().equals(sourceChild.GetName())) {
					isFoundSameChild = true;
					break;
				}
			}
			if(!isFoundSameChild) {
				tempChildrenList.add(sourceChild);
			}
		}
		for(AbstractXSDTreeNode tempChild: tempChildrenList) {
			this.AddChildren(tempChild);
		}
	}
	
	public String GetContext() {
		return context;
	}
	
	public void SetAsReference(String ref) {		
		this.referenceName = ref;
	}
	
	public String GetReferenceName() {
		return this.referenceName;
	}
	
	public boolean IsReference() {
		return !this.referenceName.isEmpty();
	}
	
	public boolean IsCustom() {
		return this.isCustom;
	}
	
	public void SetCustom(boolean value) {
		this.isCustom = value;
	}
	
	public String GetXpath() { 
		ArrayList<AbstractXSDTreeNode> list = new ArrayList<AbstractXSDTreeNode>();
		AbstractXSDTreeNode element = this;
		String result = "";
		list.add(element);
		while(!element.IsRoot()) {
			element = element.GetParent();	
			list.add(element);
		}
		
		if(!list.isEmpty()) {
			Collections.reverse(list);		
			for(AbstractXSDTreeNode node: list) {
				if(!node.IsAttribute()) {
					result += "/" + node.GetName();
				} else {
					result += "[@" + node.GetName() + "]";
				}
			}
		}
		
		return result;
	}

	public void SetParent(AbstractXSDTreeNode myParent) {
		this.parent = myParent;
		
	}

}