package xml2rdf.util.transformer;

import xml2rdf.configuration.Settings;

/**
 * Take a generic transformation from XML to RDF which is described in ReDefer's project.
 * @see "http://rhizomik.net/html/redefer/"
 * @author Jyun-Yao Huang (Allen; allen501pc@gmail.com)
 *
 */
public class GenericTransformer {

	/**
	 *  The transformation object from XSLTtransformer.
	 *  @see xml2rdf.util.transformer.XSLTtransformer
	 */ 
	protected XSLTtransformer trans = new XSLTtransformer();
	
	/**
	 * Make a transformation from source xml file to target file with specific xslt file.
	 * The xslt file is specified by configuration. 
	 * @see xml2rdf.configuration.Settings
	 * @param sourceURI source file's URI.
	 * @param targetURI target file's URI.
	 * @return true for success, otherwise false. 
	 */
	public boolean transform(String sourceURI, String targetURI) { 
		trans.LoadSouceFileFromURI(sourceURI);
		trans.SetOutputFileURI(targetURI);
		trans.LoadXSLTFileFromURI(Settings.GetXSLTFileURI());
		return trans.DoTransformation();
	}
}
