package xml2rdf.example;
import xml2rdf.util.transformer.*;

public class BasicExample {

	public static void main(String[] args) {
		GenericTransformer trans = new GenericTransformer();
		//trans.transform("E:/Downloads/dblp_small.xml","dblp_small.rdf");
		trans.transform("publications.xml","publications.rdf");
	}

}
