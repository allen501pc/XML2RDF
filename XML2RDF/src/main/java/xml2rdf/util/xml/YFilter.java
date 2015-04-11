package xml2rdf.util.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import junit.framework.Assert;


/*
 *  Here, we use symbols "_" to replace "ε".
 */
public class YFilter {
	public ArrayList<IndexNode> indexTable = new ArrayList<IndexNode>();
	 
	public enum IndexNodeType { accept, double_slash_child, normal};
	public char epsilon = 'ε'; 
	
	public Stack<Set<Integer>> stack = new Stack<Set<Integer>>();
		
	protected class IndexNode {
		public long stateID = -1;
		public IndexNodeType currentType = IndexNodeType.normal;
		public HashMap<String,Integer> currentHashTable = new HashMap<String, Integer>();
		public ArrayList<Integer> IDList = new ArrayList<Integer>();		
	}		
	
	protected class XPathNode {
		IndexNodeType currentType = IndexNodeType.normal;
		String name = "";
	}
	
	public YFilter() {
		indexTable.add(new IndexNode());
		Set<Integer> mySet = new HashSet<Integer>();
		mySet.add(0);
		stack.push(mySet);		
	}		
	
	protected boolean CreateXPathIndex(char[] xpath) {
		String tmpSymbol = "";
		short countOfSlashes = 0;	
		ArrayList<XPathNode> tmpIndexNodeTable = new ArrayList<XPathNode>();
		char tempChar;
		ArrayList<XPathNode> xPathList = new ArrayList<XPathNode>();
		// Start check loop.
		for( int i = 0; i < xpath.length; ++i ) {
			tempChar = xpath[i];
			// If it doesn't start from '/', return false. 
			if(i == 0 && tempChar != '/') {
				return false;
			}
			
			// count the Slashes. 
			if(tempChar == '/') {
				++countOfSlashes;
				if(countOfSlashes == 1) {
					continue;
				} else {
					return false;
				}
			}  						
						
			if(countOfSlashes >= 1 && i >= 1 &&  (i + 1) <= xpath.length ) {				
				if(tempChar == '*' ||  tempChar == epsilon) { 
					if(xpath[i-1] == '/')
						tmpSymbol = Character.toString((tempChar));
					else 
						return false; 					
				} else {
					tmpSymbol += tempChar;
				}								
				
				if(((i+1) < xpath.length && xpath[i+1] == '/') || (i+1) == xpath.length ) {					
					XPathNode myNode = new XPathNode();
					myNode.name = tmpSymbol;
					if(tmpSymbol.equals(String.valueOf(epsilon))) {
						myNode.currentType = IndexNodeType.double_slash_child;
					}					
					
					xPathList.add(myNode);
					
					if((i+1) == xpath.length) {
						XPathNode myNode2 = new XPathNode();
						myNode2.currentType = IndexNodeType.accept;
						myNode2.name = "";
						xPathList.add(myNode2);
					}
					
					countOfSlashes = 0;					
					tmpSymbol = "";
				} 
			}									
		}
		// End of check loop.
		IndexNode currentIndexNode = indexTable.get(0);
		Integer stateValue = 0;
		
		ArrayList<Integer> tmpIDList = new ArrayList<Integer>();		
		/* Start to add index */
		for(XPathNode currentNode: xPathList) {
			if(currentNode.name.isEmpty() && currentNode.currentType == IndexNodeType.accept) {
				currentIndexNode.currentType = IndexNodeType.accept;
			} else {
				stateValue = currentIndexNode.currentHashTable.get(currentNode.name);
				if(stateValue == null) {
					IndexNode myNode = new IndexNode();
					myNode.currentType = currentNode.currentType;					
					indexTable.add(myNode);
					stateValue = indexTable.size()-1;
					myNode.stateID = stateValue;
					currentIndexNode.currentHashTable.put(currentNode.name, stateValue);
					currentIndexNode = indexTable.get(stateValue);
					tmpIDList.add(stateValue);
					currentIndexNode.IDList.addAll(tmpIDList);
				} else {
					/* add existing list */
					currentIndexNode = indexTable.get(stateValue);
					if(currentIndexNode.currentType != IndexNodeType.accept)
						currentIndexNode.currentType = currentNode.currentType;
				}				
			}
		}				
		
		return true;
	}
	
	protected void ReadElement(Set<Integer> currentSet, String elementName, Integer stateNumber) {
		IndexNode tmpIndexNode = indexTable.get(stateNumber);
		
		// Step 3. If the state itself is a "//-child" state, then its own stateID is added to the set
		if(tmpIndexNode.currentType == IndexNodeType.double_slash_child) {
			currentSet.add((int) tmpIndexNode.stateID);
		}
		
		for(Entry<String,Integer> entry : tmpIndexNode.currentHashTable.entrySet()) {
			// Step 1: Check element name existed or not.
			// Step 2: Check '*' existed or not
			if( elementName.equals(entry.getKey()) || entry.getKey().equals("*") ) {
				currentSet.add(entry.getValue());
			}			
		}
		
	}
	
	public void ReadElement(String elementName) {
		Set<Integer> currentSet =  new HashSet<Integer>();
		for(Integer currentID: stack.peek() ) {
			IndexNode tmpIndexNode = indexTable.get(currentID);
			// Step 3: If the state itself is a "//-child" state, 
			//         then its own stateID is added to the set
			if(tmpIndexNode.currentType == IndexNodeType.double_slash_child) {
				currentSet.add((int) tmpIndexNode.stateID);
			}
			
			for(Entry<String,Integer> entry : tmpIndexNode.currentHashTable.entrySet()) {
				// Step 1: Check element name existed or not.
				// Step 2: Check '*' existed or not
				if( elementName.equals(entry.getKey()) || entry.getKey().equals("*") ) {
					currentSet.add(entry.getValue());
				}
				// Step 4: Check 'epsilon' existed or not
				if(entry.getKey().equals(String.valueOf(epsilon))) {
					ReadElement(currentSet,elementName,entry.getValue());
				}
			}
		}
		
		stack.add(currentSet);
	}
	
	public Set<Integer> AcceptedStates() {
		Set<Integer> acceptedStates = new HashSet<Integer>();
		for(Integer currentID: stack.peek() ) {
			IndexNode myCurrentNode = indexTable.get(currentID);
			if(myCurrentNode.currentType == IndexNodeType.accept) {
				acceptedStates.add((int) myCurrentNode.stateID);
			}
		}
		return acceptedStates;
	}
	
	public boolean IsAccept() { 
		
		for(Integer currentID: stack.peek() ) {
			if(indexTable.get(currentID).currentType == IndexNodeType.accept) {
				return true;
			}
		}
		return false;
	}
	
	public void PopElement() {
		stack.pop();
	}
	
	public char[] ChangeXPathExpression(String xpath) { 
		String result = "";	
		String remainingSubStr = xpath;
		do {
			int pos = remainingSubStr.indexOf("//"); 
			if(pos > 0 ) {
				result += remainingSubStr.substring(0, pos) + '/' + epsilon + '/';
				remainingSubStr = remainingSubStr.substring(pos + 2);
			} else if(pos == 0) {
				result += "/" + epsilon + '/';
				remainingSubStr = remainingSubStr.substring(2);
			} else {
				result += remainingSubStr;
				remainingSubStr = "";
			}	
		} while(!remainingSubStr.isEmpty());
		if(result.charAt(result.length()-1) == '/') {
			result = result.substring(0, result.length()-1);
		}
		
		return result.toCharArray();
	}	
	
	public static void main(String[] args) {
		YFilter myFilter = new YFilter();
		
		myFilter.AppendXPath("/a/b");
		myFilter.AppendXPath("/a/c");
		myFilter.AppendXPath("/a/b/c");
		myFilter.AppendXPath("/a//b/c");
		myFilter.AppendXPath("/a/*/c");
		myFilter.AppendXPath("/a//c");
		myFilter.AppendXPath("/a/*/*/c");
		myFilter.AppendXPath("/a/b/c");
//		myFilter.AppendXPath("/a//*");
		
		myFilter.ReadElement("a");
		System.out.println("Read:a");
		AcceptThenPrint(myFilter);
		myFilter.ReadElement("b");
		System.out.println("Read:b");
		AcceptThenPrint(myFilter);
		System.out.println("Read:c");
		myFilter.ReadElement("c");
		AcceptThenPrint(myFilter);
		
		
		System.out.println("End of Test");
	}
	
	public static void AcceptThenPrint(YFilter myFilter) {
		if(myFilter.IsAccept()) {
			System.out.println("Accpeted States:");
			Set<Integer> mySet = myFilter.AcceptedStates();			
			for(Integer currentID: mySet) {
				System.out.println(currentID);								
			}
		}
	}
	
	public boolean AppendXPath(String xpath) {
		if(CreateXPathIndex(ChangeXPathExpression(xpath))) {
			return true;
		}
		return false;
	}
	
	public boolean AppendXPath(char[] xpath) {
		if(CreateXPathIndex(ChangeXPathExpression(new String(xpath)))) {
			return true;
		}
		return false;
	}
	

}
