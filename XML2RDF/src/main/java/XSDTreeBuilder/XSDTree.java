package XSDTreeBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import XSDTreeBuilder.XSDTreeNode.AbstractXSDTreeNode;
import XSDTreeBuilder.XSDTreeNode.XSDTreeNodeAttribute;
import XSDTreeBuilder.XSDTreeNode.XSDTreeNodeElement;
import XSDTreeBuilder.XSDTreeNode.XSDTreeNodeType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XSDTree {
	public XSDTreeNodeElement rootNode = new XSDTreeNodeElement(); 
	public ArrayList<XSDTreeNodeElement> otherElementsList = new ArrayList<XSDTreeNodeElement>(); 
	protected ArrayList<XSDTreeNodeElement> referenceElementList = new ArrayList<XSDTreeNodeElement>();
	protected ArrayList<XSDTreeNodeAttribute> referenceAttributeList = new ArrayList<XSDTreeNodeAttribute>();
	protected HashMap<String, AbstractXSDTreeNode> allElementAndAttributeMap = new HashMap<String, AbstractXSDTreeNode>(); 
	private NamedNodeMap currentAttributeNodes = null;
	
	protected AbstractXSDTreeNode DoElementOperation(Element tempElement,  AbstractXSDTreeNode currentNode, int depth) { 
		if(tempElement instanceof Element) {
			boolean isReference = false;
			if(tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":element")) {
				XSDTreeNodeElement myElement = new XSDTreeNodeElement();
				/**
				 * Add attributes.
				 */
				NamedNodeMap attributeNodes = tempElement.getAttributes();			
				for(int j=0; j < attributeNodes.getLength(); ++j) {
					// If this element is a reference node, add it into referenceElement list.					
					if(attributeNodes.item(j).getNodeName().toLowerCase().equals("ref")) {							 
						myElement.SetAsReference(attributeNodes.item(j).getNodeValue());
						isReference = true;
						//referenceElementList.add(myElement);
					} else if (attributeNodes.item(j).getNodeName().toLowerCase().equals("name")) {
						myElement.SetName(attributeNodes.item(j).getNodeValue());
					} else if (attributeNodes.item(j).getNodeName().toLowerCase().equals("type")) {
						XSDTreeNodeType.TypeName theElementType = XSDTreeNodeType.GetTypeName(attributeNodes.item(j).getNodeValue());
						if(theElementType != XSDTreeNodeType.TypeName.ReferenceType) {
							myElement.SetType(theElementType);
						} else {
							myElement.SetAsReference(attributeNodes.item(j).getNodeValue());
							isReference = true;
						}
														
					} else {
						// Add attributes which declared in the elements.
						XSDTreeNodeAttribute foundAttribute = 
								new XSDTreeNodeAttribute(attributeNodes.item(j).getNodeName(),attributeNodes.item(j).getNodeValue());
						foundAttribute.SetDepth(depth);
						myElement.AddChildren(foundAttribute);	
						
					}						
				}
				
				if(currentAttributeNodes != null) {
					List<XSDTreeNodeAttribute> attList = myElement.GetAttributeList();
					for(int j=0; j < currentAttributeNodes.getLength(); ++j) {
						// TODO: Add choice attributes.
						if(attList.indexOf(currentAttributeNodes.item(j).getNodeName()) < 0) {
							XSDTreeNodeAttribute foundAttribute = 
									new XSDTreeNodeAttribute(currentAttributeNodes.item(j).getNodeName(),currentAttributeNodes.item(j).getNodeValue());
							foundAttribute.SetDepth(depth);
							myElement.AddChildren(foundAttribute);							
							
						}
					}
				}
				// Add this element into currentNode's children.
				myElement.SetDepth(depth);
				AbstractXSDTreeNode result = currentNode.AddChildren(myElement);
				if(result != null && isReference) {
					referenceElementList.add((XSDTreeNodeElement) result);
				}
				return result;
			} else if(tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":attribute")){
				/**
				 * If the element equals to xsd:attribute, it is a custom attribute values.
				 */
				XSDTreeNodeAttribute myAttributeElement = new XSDTreeNodeAttribute(tempElement.getAttribute("name"), "");
				myAttributeElement.SetCustom(true);
				NamedNodeMap attributeNodes = tempElement.getAttributes();
				
				for(int idx=0; idx < attributeNodes.getLength(); idx++) {
					// Check this attribute is of reference node or not.
					if(attributeNodes.item(idx).getNodeName().toLowerCase().equals("ref")) {
						// TODO: Should add a reference table here. 
						myAttributeElement.SetAsReference(attributeNodes.item(idx).getNodeValue());
						// referenceAttributeList.add(myAttributeElement);
						isReference = true;
					} else if (attributeNodes.item(idx).getNodeName().toLowerCase().equals("name")) {
						myAttributeElement.SetName(attributeNodes.item(idx).getNodeValue());
					} else if (attributeNodes.item(idx).getNodeName().toLowerCase().equals("type")) {
						XSDTreeNodeType.TypeName theElementType = XSDTreeNodeType.GetTypeName(attributeNodes.item(idx).getNodeValue());
						if(theElementType != XSDTreeNodeType.TypeName.ReferenceType) {
							myAttributeElement.SetType(theElementType);
						} else {
							myAttributeElement.SetAsReference(attributeNodes.item(idx).getNodeValue());
						}
														
					} else {
						XSDTreeNodeAttribute foundAttribute = 
								new XSDTreeNodeAttribute(attributeNodes.item(idx).getNodeName(),attributeNodes.item(idx).getNodeValue());
						foundAttribute.SetDepth(depth);
						myAttributeElement.AddChildren(foundAttribute);							
					}
				}
				myAttributeElement.SetDepth(depth);
				AbstractXSDTreeNode result = currentNode.AddChildren(myAttributeElement);
				if(isReference && result != null) {
					referenceAttributeList.add((XSDTreeNodeAttribute) result);
				}
				return result;
			}
		}
		return null;
	}
	
	
	protected void DFS(Element currentElement, AbstractXSDTreeNode currentNode, int depth) {
		NodeList tempChildren = currentElement.getChildNodes();
		AbstractXSDTreeNode newAddedNode = null;

		newAddedNode = DoElementOperation(currentElement,currentNode,depth);
		if(newAddedNode == null) {
			newAddedNode = currentNode;
		}
		// if(currentElement.getTagName().toLowerCase().equals("xsd:))
		for(int i = 0; i < tempChildren.getLength(); ++i) {
			// if the node is of "element", add the node as child. 
			if(tempChildren.item(i) instanceof Element) {
				Element tempElement = (Element) tempChildren.item(i);
				if(tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":complextype") || 
						tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":simpletype") || 
						tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":complexcontent") ||
						tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":restriction") || 
						tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":extension") ||
						tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":sequence") || 
						tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":element") ||
						tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":attribute")) {
					DFS(tempElement, newAddedNode, depth + 1);
				} else if (tempElement.getTagName().toLowerCase().equals(XSDTreeNodeType.DefaultNameSpace + ":choice")) {
					currentAttributeNodes = tempElement.getAttributes();
					DFS(tempElement, newAddedNode, depth + 1);
					currentAttributeNodes = null;
				}
			} 	
		}
	}
	
	private void Resolve() {
		/**
		 * Resolve the element reference.		 
		 */
		Collections.sort(referenceElementList, new Comparator<XSDTreeNodeElement>() {
			public int compare(XSDTreeNodeElement e1, XSDTreeNodeElement e2) {
				return e1.GetDepth() - e2.GetDepth();
			}
		});
		for(AbstractXSDTreeNode element: referenceElementList) {
			if(element.IsReference()) {
				for(AbstractXSDTreeNode otherElement: otherElementsList) {
					if(element.GetReferenceName().equals(otherElement.GetName())) {
						otherElement.MergeChildrenFrom(element);
						/**
						 * Release the element resource.
						 */
						AbstractXSDTreeNode parent = element.GetParent();						
						if(element.IsAttribute()) {												
							parent.AddChildren((XSDTreeNodeAttribute) otherElement);
							
						} else {																		
							parent.AddChildren((XSDTreeNodeElement) otherElement);							
						}						
						parent.RemoveChild(element);
					}
				}
			}
		}
		
		Collections.sort(referenceAttributeList, new Comparator<XSDTreeNodeAttribute>() {
			public int compare(XSDTreeNodeAttribute e1, XSDTreeNodeAttribute e2) {
				return e1.GetDepth() - e2.GetDepth();
			}
		});
		
		for(AbstractXSDTreeNode element: referenceAttributeList) {
			if(element.IsReference()) {
				for(AbstractXSDTreeNode otherElement: otherElementsList) {
					if(element.GetReferenceName().equals(otherElement.GetName())) {
						/**
						 * Release the element resource.
						 */
						AbstractXSDTreeNode parent = element.GetParent();
						if(element.IsAttribute()) {																			
							parent.AddChildren((XSDTreeNodeAttribute) otherElement);						
						} else {																		
							parent.AddChildren((XSDTreeNodeElement) otherElement);						
						}
						parent.RemoveChild(element);
					}
				}
			}
		}
	}
	
	private void ResursivePrint(AbstractXSDTreeNode currentNode, int rescursiveRunds) {
		for(int i = 0; i < rescursiveRunds; ++i) {
			System.out.print("+");
		}
		if(!currentNode.IsAttribute()) {
			System.out.println(currentNode.GetName());
		} else {
			XSDTreeNodeAttribute node = (XSDTreeNodeAttribute) currentNode;
			if(currentNode.IsCustom()) {
				System.out.println("#" + node.GetName() + " -[type]: " + node.GetTypeName());
			} else {
				System.out.println("@" + node.GetName() + "=" + node.GetContext());
			}
			
		}
		ArrayList<AbstractXSDTreeNode> childrenList = currentNode.GetChildren();
		for(AbstractXSDTreeNode myNode: childrenList) {
			ResursivePrint(myNode,rescursiveRunds+1);
		}
	}
	
	/**
	 * TODO: Add print trees.
	 */
	public void Print() {
		AbstractXSDTreeNode currentNode = rootNode;
		ResursivePrint(currentNode, 0);
	}
	
	public void Parse(String FilePath, String elementName) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse (new File(FilePath)); 

			NodeList list = doc.getElementsByTagName(XSDTreeNodeType.DefaultNameSpace + ":schema"); 
			
			/**
			 * For each schema, traverse its' children elements.
			 */
			for(int i = 0 ; i < list.getLength(); i++) {
				NodeList firstList = list.item(i).getChildNodes();
				
				for(int idx=0; idx < firstList.getLength(); ++idx) {
					if(firstList.item(idx) instanceof Element) {
						Element first = (Element) firstList.item(idx);
						// If the element is that we want. 
						if(first.getAttribute("name").toLowerCase().equals(elementName.toLowerCase())) {
							rootNode.SetName(first.getAttribute("name"));							
							// If there are attributes, add these attributes into the root node.
							NamedNodeMap attributeNodes = first.getAttributes();					
							for(int j=0; j < attributeNodes.getLength(); ++j) {		
								if(attributeNodes.item(j).getNodeName().toLowerCase().equals("name") || 
								   attributeNodes.item(j).getNodeName().toLowerCase().equals("type")) {
									continue;
								} 
								XSDTreeNodeAttribute newAttributeNode = new 
										XSDTreeNodeAttribute(attributeNodes.item(j).getNodeName(),attributeNodes.item(j).getNodeValue());
								rootNode.AddChildren(newAttributeNode);
							}
							NodeList secondList = first.getChildNodes();
							for(int idx2 = 0; idx2 < secondList.getLength(); ++ idx2) {
								if(secondList.item(idx2) instanceof Element) {
									Element second = (Element) secondList.item(idx2);
									DFS(second,rootNode, 0);
								}
							}							
							// If the root node belongs to complex type.							
						} else { // if the element is not what we want, add it into otherElementsList.							
							XSDTreeNodeElement myNode = new XSDTreeNodeElement();
							myNode.SetName(first.getAttribute("name"));
							NamedNodeMap attributeNodes = first.getAttributes();					
							for(int j=0; j < attributeNodes.getLength(); ++j) {	
								if(attributeNodes.item(j).getNodeName().toLowerCase().equals("name") || 
									attributeNodes.item(j).getNodeName().toLowerCase().equals("type")) {
									continue;
								} 
								XSDTreeNodeAttribute newAttributeNode = new 
										XSDTreeNodeAttribute(attributeNodes.item(j).getNodeName(),attributeNodes.item(j).getNodeValue());
								myNode.AddChildren(newAttributeNode);
							}
							
							NodeList secondList = first.getChildNodes();
							for(int idx2 = 0; idx2 < secondList.getLength(); ++ idx2) {
								if(secondList.item(idx2) instanceof Element) {
									Element second = (Element) secondList.item(idx2);
									DFS(second,myNode, 0);
								}
							}														
							otherElementsList.add(myNode);
						}
					}
				}								
			} // End of for loop.
			/**
			 * resolve the elements' name and attributes. 
			 */
			Resolve();
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
