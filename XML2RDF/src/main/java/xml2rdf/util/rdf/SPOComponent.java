package xml2rdf.util.rdf;

import org.apache.commons.lang.mutable.MutableObject;

public class SPOComponent{
	/**
	 * Meta data, which consists of patterns of subject, predicate and object.
	 */
	public SPOComponentMetaData metaData = new SPOComponentMetaData();
	public SPOComponentData data = new SPOComponentData();
	
	public void resetData() {
		data.subjectList.clear();
		data.objectList.clear();	
		// That means, we should add default name of metaData.
		if(!metaData.subjectPattern.isXPath) {
			data.subjectList.add(new MutableObject(metaData.subjectPattern.outputIdentifier));
		}
		if(!metaData.objectPattern.isXPath) {
			data.objectList.add(new MutableObject(metaData.objectPattern.outputIdentifier));
		}
		data.predicateName.setValue(metaData.predicatePattern.outputIdentifier);
			
	}
}
