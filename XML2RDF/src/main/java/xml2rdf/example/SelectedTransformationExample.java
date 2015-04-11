package xml2rdf.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import xml2rdf.util.transformer.GenericTransformer;
import xml2rdf.util.transformer.SelectedTransformer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;

public class SelectedTransformationExample {

	public static void main(String[] args) throws IOException, InterruptedException {
		long SumTime = 0;
		Model model = null;
		int runTimes = 10;
		for(int i = 0; i < runTimes; ++i) {
			long startTime = System.currentTimeMillis();
			
			GenericTransformer trans = new GenericTransformer();
			trans.transform("publications.xml","publications.rdf"); 
			//long startTime = System.currentTimeMillis();
			SelectedTransformer myTransformer = new SelectedTransformer("publications.rdf");
			
			/**
			 * For creating user-defined RDF resources, add templates which should define "Subject" and "Predicate". Note that the remaining part we don't indicate is "Object". 
			 */
	//		 Model model = ModelFactory.createDefaultModel();
	//		 InputStream in = FileManager.get().open( "publications.rdf");
	//		 if (in == null) {
	//		     throw new IllegalArgumentException(
	//		                                  "File: " + "publications.rdf" + " not found");
	//		 }
	//
	//		 // read the RDF/XML file
	//		 model.read(in, null);
	//
	//		 // write it to standard out
	//		 model.write(System.out,"N-TRIPLES");
			// Find XML tags which should be formulated as XPath '//size' elements for Subject and use the elements' contexts as Predicate.
			//myTransformer.AddTemplate("response/results/result/metadata/oaf:entity/extraInfo/citations/citation/rawText", "value()");
			// Find XML tags which should be formulated as XPath elements for Subject and use the elements' attribute name as Predicate.
			// In this case, subject is "/response/results/result" and "@classname" indicates the attribute name:"classname" 
			//myTransformer.AddTemplate("/response/results/result", "@classname");
			
			myTransformer.AddTemplate("//publisher", "value()");
			myTransformer.AddTemplate("//journal", "value()");
			myTransformer.AddTemplate("//title", "value()");
			myTransformer.AddTemplate("//originalId", "value()");
			myTransformer.AddTemplate("//dateofacceptance", "value()");
			//myTransformer.AddTemplate("//citations/citation/rawText", "value()");
			//myTransformer.AddTemplate("//result/header/dri:objIdentifier", "value()");
			//myTransformer.AddTemplate("/response/header/page", "value()");
		
		/**
		 * Use Parallel mode.
		 */
			myTransformer.SetParallelMode(true);
			//long startTime = System.currentTimeMillis();
			model = myTransformer.DoSelect();			
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			SumTime += elapsedTime;
			TimeUnit.MILLISECONDS.sleep(2000);
		}
//		// System.in.read();
//		
		//System.out.println("-----------Print out TURTLE format.-----------------");
		model.write(System.out,"TURTLE");	
		//System.out.println("-----------End of printing TURTLE format--------------------");
		System.out.println("Time elapsed: " + SumTime/runTimes);
		// System.out.println("Used memory is bytes: " + memory);
//		/**
//		 * Or output the selected triples
//		 */
		/*
		System.out.println("-----------Print out self-defined format.-----------------");
		ArrayList<Statement> retrievedStatements=myTransformer.DoSelect("/response/header/size", "value()");
		for(Statement myStatement: retrievedStatements) {
			System.out.println(myStatement.getSubject() + "," + myStatement.getPredicate().getLocalName() + "," + myStatement.getObject());
		}
		System.out.println("----------End of printing self-defined format.-----------------");
		*/
		
	}

}
