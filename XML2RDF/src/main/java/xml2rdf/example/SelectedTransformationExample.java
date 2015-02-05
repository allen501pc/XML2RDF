package xml2rdf.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

import xml2rdf.util.transformer.SelectedTransformer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;

public class SelectedTransformationExample {

	public static void main(String[] args) throws IOException {
		SelectedTransformer myTransformer = new SelectedTransformer("Example.rdf");
		
		/**
		 * For creating user-defined RDF resources, add templates which should define "Subject" and "Predicate". Note that the remaining part we don't indicate is "Object". 
		 */
		
		// Find XML tags which should be formulated as XPath '//size' elements for Subject and use the elements' contexts as Predicate.
		myTransformer.AddTemplate("//size", "value()");
		// Find XML tags which should be formulated as XPath elements for Subject and use the elements' attribute name as Predicate.
		// In this case, subject is "/response/results/result" and "@classname" indicates the attribute name:"classname" 
		myTransformer.AddTemplate("/response/results/result", "@classname");
		myTransformer.AddTemplate("/response/header/size", "value()");
		myTransformer.AddTemplate("/response/results/result/header/dri:objIdentifier", "value()");
		myTransformer.AddTemplate("/response/header/page", "value()");
		
		/**
		 * Use Parallel mode.
		 */
		myTransformer.SetParallelMode(true);
		long SumTime = 0;
		Model model = null;
		int runTimes = 10;
		for(int i = 0; i < runTimes; ++i) {
			long startTime = System.currentTimeMillis();
			model = myTransformer.DoSelect();
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			SumTime += elapsedTime;
		}
		System.in.read();
		
		System.out.println("-----------Print out TURTLE format.-----------------");
		model.write(System.out,"TURTLE","http://www.example.com");	
		System.out.println("-----------End of printing TURTLE format--------------------");
		System.out.println("Time elapsed: " + SumTime/runTimes);
		// System.out.println("Used memory is bytes: " + memory);
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
