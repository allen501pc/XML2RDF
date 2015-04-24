package xml2rdf.util.rdf;

import java.util.ArrayList;

import org.apache.commons.lang.mutable.MutableObject;

public class SPOComponentData {
	public ArrayList<MutableObject> subjectList = new ArrayList<MutableObject>();
	public MutableObject predicateName = new MutableObject();
	public ArrayList<MutableObject> objectList = new ArrayList<MutableObject>();
}
