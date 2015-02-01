package xml2rdf.example;

import java.util.ArrayList;

import xml2rdf.util.transformer.SelectedTransformer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public class SelectedTransformationExample {

	public static void main(String[] args) {
		SelectedTransformer myTransformer = new SelectedTransformer("Example.rdf");
		
		/**
		 * For creating user-defined RDF resources, add templates which should define "Subject" and "Predicate". Note that the remaining part we don't indicate is "Object". 
		 */
		
		// Find XML tags which should be formulated as XPath '//size' elements for Subject and use the elements' contexts as Predicate.
		myTransformer.addTemplate("//size", "value()");
		// Find XML tags which should be formulated as XPath elements for Subject and use the elements' attribute name as Predicate.
		// In this case, subject is "/response/results/result" and "@classname" indicates the attribute name:"classname" 
		myTransformer.addTemplate("/response/results/result", "@classname");
		myTransformer.addTemplate("/response/header/size", "value()");
		
		Model model = myTransformer.DoSelect();
		System.out.println("-----------Print out TURTLE format.-----------------");
		model.write(System.out,"TURTLE","http://www.example.com");	
		System.out.println("-----------End of printing TURTLE format--------------------");
		/**
		 * Or output the selected triples
		 */
		System.out.println("-----------Print out self-defined format.-----------------");
		ArrayList<Statement> retrievedStatements=myTransformer.DoSelect("/response/header/size", "value()");
		for(Statement myStatement: retrievedStatements) {
			System.out.println(myStatement.getSubject() + "," + myStatement.getPredicate().getLocalName() + "," + myStatement.getObject());
		}
		System.out.println("----------End of printing self-defined format.-----------------");
		
	}

}
