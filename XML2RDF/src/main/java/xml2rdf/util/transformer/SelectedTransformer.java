package xml2rdf.util.transformer;

import java.io.InputStream;
import java.util.ArrayList;

import xml2rdf.util.rdf.GenericResourceValidator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

public class SelectedTransformer {
	
	Model model = ModelFactory.createDefaultModel();
	GenericResourceValidator validator = new GenericResourceValidator();
	
	public SelectedTransformer(String filePath) {
		  // use the FileManager to find the input file
        InputStream in = FileManager.get().open(filePath);
        if (in == null) {
            throw new IllegalArgumentException( "File: " + filePath + " not found");
        }        
        // read the RDF/XML file
        model.read( in, "" );
	}
	
	public ArrayList<String> DoSelect(String subjectXPath, String attribute) {
		ArrayList<String> result = new ArrayList<String>(); 
		StmtIterator iter = model.listStatements(
			new SimpleSelector(null, null, (RDFNode) null ) {
				public boolean selects(Statement s) {
					if( validator.InSubject(subjectXPath, s.getSubject().getURI())) {
						if(	validator.IsAttribute(attribute) && validator.IsAttributeInGivenStatement(attribute,s.getPredicate().getLocalName())) {
							return true;
						} else if (!validator.IsAttribute(attribute) && attribute.equals(s.getPredicate()) ) {
							return true;
						}
						return false;
					}
					return false;
				}					
		});
        while (iter.hasNext()) {
            result.add(iter.nextStatement().getString());
        }
        return result;
	}
	
	public static void main(String[] args) {
		SelectedTransformer myTransformer = new SelectedTransformer("Sahrrah.rdf");
		ArrayList<String> myObject=myTransformer.DoSelect("/response/results/result/metadata/oaf:entity/oaf:result/children/instance/hostedby", "@id");
		System.out.println("End");
		
	}
}
