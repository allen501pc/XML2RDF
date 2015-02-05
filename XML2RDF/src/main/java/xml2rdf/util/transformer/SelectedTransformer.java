package xml2rdf.util.transformer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.xpath.XPathExpressionException;

import xml2rdf.util.rdf.GenericResourceValidator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;
import com.hp.hpl.jena.util.FileManager;
//import com.hp.hpl.jena.query.*;

public class SelectedTransformer{
	protected Model model = ModelFactory.createDefaultModel();
	protected Model outputModel = ModelFactory.createDefaultModel();
	protected boolean modelsHaveBeenSet = false;
	private boolean useParallelMode = false;
	protected String filePath = ""; 
	// template for selecting desire subject's and predicate's conditions. 
	protected HashMap<String, ArrayList<String>> template = new HashMap<String, ArrayList<String>>();
	GenericResourceValidator validator = new GenericResourceValidator();
	
	public SelectedTransformer() {
		
	}	
	
	public SelectedTransformer(String filePath) {
		  // use the FileManager to find the input file
		if(!modelsHaveBeenSet) {
			this.filePath = filePath;
	        InputStream in = FileManager.get().open(filePath);
	        if (in == null) {
	            throw new IllegalArgumentException( "File: " + filePath + " not found");
	        } 
	        // Do not show Warnning Message here.
	        RDFDefaultErrorHandler.silent =true;
	        // read the RDF/XML file        
	        model.read( in, "" );
	        outputModel.setNsPrefixes(model);
	        modelsHaveBeenSet = true;
		}        
	}
	
	public SelectedTransformer(Model sourceModel, Model outputModel) {
		if(!modelsHaveBeenSet) {
			this.model.add(sourceModel);
			this.outputModel.add(outputModel);
			modelsHaveBeenSet = true;
		}
	}
	
	public final boolean IsParallelMode() {
		return useParallelMode;
	}
	
	public final void SetParallelMode(boolean IsSet) {
		useParallelMode = IsSet;
	}
	
	public Model GetSelectedOutputModel() {
		return outputModel;
	}
	
	public void PrintOut() {
		model.write(System.out, "N-TRIPLES");
		
	}	
	
	public void AddTemplate(String subjectXPath, String attribute) {
		ArrayList<String> object = null;
		if( (object = template.get(subjectXPath)) != null) {
			object.add(attribute);
		} else {
			object = new ArrayList<String>();
			object.add(attribute);
			template.put(subjectXPath, object);
		}
	}
	
	public final Model DoSelect() {
		if(IsParallelMode()) {
			ArrayList<SelectedTransformerThread> threadPool = new ArrayList<SelectedTransformerThread>();
			int taskNumber = 1;
			for(Entry<String,ArrayList<String>> object: template.entrySet()) {
				for(String attribute: object.getValue()) {					
					SelectedTransformerThread tempThread = new SelectedTransformerThread(this.model, this.outputModel);
					threadPool.add(tempThread);
					tempThread.start(taskNumber++, new ArrayList<Statement>(),object.getKey(), attribute);
					// outputModel.add(DoSelect(object.getKey(), attribute));
				}
			}
			boolean allTerminated = false;
			while(!allTerminated) {
				for(int i = 0; i < threadPool.size(); ++i) {
					if(threadPool.get(i).IsTerminated() && threadPool.get(i).IsSuccessful()) {
						outputModel.add(threadPool.get(i).GetResult());
					}
					if(i == 0) {
						allTerminated = threadPool.get(i).IsTerminated();
					} else {
						allTerminated = allTerminated && threadPool.get(i).IsTerminated();
					}
				}				
			}
		} else {
			for(Entry<String,ArrayList<String>> object: template.entrySet()) {
				for(String attribute: object.getValue())
					outputModel.add(DoSelect(object.getKey(), attribute));
			}
		}
		return GetSelectedOutputModel();
	}
	
	public ArrayList<Statement> DoSelect(String subjectXPath, String attribute) {
		ArrayList<Statement> result = new ArrayList<Statement>(); 
		StmtIterator iter = null;
		iter = model.listStatements(
			new SimpleSelector(null, null, (RDFNode) null ) {
				public boolean selects(Statement s) {

					try {

						if( validator.InSubject(subjectXPath, s.getSubject().getURI())) {
							/**
							 * If the subject is in it, check the following properties, 
							 * "<subject schemename="dnet:result_subject" classname="keyword"
							 *  schemeid="dnet:result_subject" classid="keyword">LCC:Internal medicine</subject>"
							 *  It should be like in N-Triples:
							 *  #subject classname 'keyword'
							 *  #subject schemeid  'dnet:result_subject'
							 */
							if(	validator.IsAttribute(attribute) && validator.IsAttributeInGivenStatement(attribute,s.getPredicate().getLocalName())) {
								return true;
							} else if (!validator.IsAttribute(attribute) && validator.IsAcquireValue(attribute, s.getPredicate().getLocalName())) {
								return true;
							}
							return false;
						}
					} catch (XPathExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}					
		});

        while (iter.hasNext()) {        	
            result.add(iter.nextStatement());
        }
        return result;
	}
}
