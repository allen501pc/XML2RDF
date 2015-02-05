package xml2rdf.util.transformer;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public class SelectedTransformerThread extends SelectedTransformer implements Runnable {
	private Thread runningThread;
	private String subject="", predicate="";
	private int runningTaskNumber = -1;
	private boolean isSuccessful = false;
	private ArrayList<Statement> statement = null;
	
	public SelectedTransformerThread(Model model, Model outputModel) {
		super(model,outputModel);
	}
	
	public SelectedTransformerThread(String filePath) {
		super(filePath);
	}

	@Override
	/**
	 * Run DoSelect method.
	 */
	public void run() {		
		statement = DoSelect(subject,predicate);	
		isSuccessful = true;
	}
	
	public boolean IsSuccessful() {
		return isSuccessful;
	}
	
	public ArrayList<Statement> GetResult() {
		return statement;
	}
	
	public boolean IsTerminated() {
		if(runningThread != null) {
			return runningThread.getState() == Thread.State.TERMINATED;			
		}
		return true;
	}
	
	public void start(int taskNumber, ArrayList<Statement> tempStatement, String subject, String predicate) { 			
		if(runningThread == null) {
			this.runningTaskNumber = taskNumber;
			statement = tempStatement;
			this.subject = subject;
			this.predicate = predicate;
			runningThread = new Thread(this,Integer.toString(taskNumber));
			runningThread.start();
		}
	}

}
