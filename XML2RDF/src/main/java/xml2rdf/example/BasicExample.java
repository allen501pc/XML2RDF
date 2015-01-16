package xml2rdf.example;
import xml2rdf.util.transformer.*;

public class BasicExample {

	public static void main(String[] args) {
		GenericTransformer trans = new GenericTransformer();
		trans.transform("STELL-I_3.rtml","STELL-I_3.rdf");
	}

}
