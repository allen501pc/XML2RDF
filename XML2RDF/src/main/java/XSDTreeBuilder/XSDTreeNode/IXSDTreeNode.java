package XSDTreeBuilder.XSDTreeNode;

import java.util.ArrayList;

public interface IXSDTreeNode {

	public abstract void SetName(String name);

	public abstract ArrayList<AbstractXSDTreeNode> GetChildren();

	public abstract String GetXpath();
	
	public abstract boolean IsAttribute();
	
	public abstract boolean IsCustom();
	
	public abstract void SetCustom(boolean value);

}