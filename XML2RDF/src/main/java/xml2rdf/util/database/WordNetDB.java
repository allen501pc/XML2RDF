package xml2rdf.util.database;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xml2rdf.configuration.Settings;
import edu.mit.jwi.Dictionary;

/**
 * Implement the Wordnet database which needs to set Settings
 * @author Jyun-Yao Huang (Allen; allen501pc@gmail.com)
 *
 */
public class WordNetDB {
	protected String wordNetDBPath = null;
	protected String icFilePath = null;
	
	private IDictionary dict = null;
	private ArrayList<ISynsetID> roots = null;
	
	public WordNetDB() {
			
	}
		
	public WordNetDB(String wordNetDBPath, String icFilePath) {
		SetWordNetDBPath(wordNetDBPath);
		SetICFilePath(icFilePath);
	}
	
	/**
	 * Load WordNet database.
	 * @return success
	 */
	public boolean Load() {
		if( Settings.IsWordNetUsed() ) {
			if( this.wordNetDBPath == null ) {
				SetWordNetDBPath(Settings.GetWordNetDBPath());
			}
			if( this.icFilePath == null ) {
				SetICFilePath(Settings.GetWordNetICFilePath());
			}
			URL url = null;
			try
			{
				url = new URL("file", null, this.wordNetDBPath);
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			if(url != null ) {
				return ( SetWordNetDB(url) && SetRootOfSynset() );
			}
		}
		return false;
	}
	
	/**
	 * Set WordNet DB Path.
	 * @param path
	 */
	public void SetWordNetDBPath(String path) {
		wordNetDBPath = path;
	}
	
	/**
	 * Set WordNet IC file's path
	 * @param path
	 */
	public void SetICFilePath(String path) {
		icFilePath = path;
	}	
	
	/**
	 * Get Wordnet's dictoinary.
	 * @return dict
	 */
	public IDictionary getDict() { 
		return this.dict;
	}
	
	/**
	 * Get roots of synset.
	 * @return ArrayList<ISynsetID>, get the roots of WordNetDB
	 */
	public ArrayList<ISynsetID> getSynsetRoots() {
		return this.roots;
	}
	
	/**
	 * Set WordNet database's path
	 * @param url
	 * @return success
	 */
	private boolean SetWordNetDB(URL url) {
		dict = new Dictionary(url);
		try {
			dict.open();
		} catch (IOException e) {				
			System.err.println("ERROR: Cannot open WordNet library. Please check the WordNet's Path.");
		}
		return true;
	}
	
	/**
	 * Set Wordnet's roots of synset.
	 * @return success.
	 */
	private boolean SetRootOfSynset() {		
		this.roots = new ArrayList<ISynsetID>();
		ISynset	synset = null;
		Iterator<ISynset> iterator = null;
		List<ISynsetID> hypernyms =	null;
		List<ISynsetID>	hypernym_instances = null;
		iterator = dict.getSynsetIterator(POS.NOUN);
		while(iterator.hasNext())
		{
			synset = iterator.next();
 			hypernyms =	synset.getRelatedSynsets(Pointer.HYPERNYM);					// !!! if any of these point back (up) to synset then we have an inf. loop !!!
 			hypernym_instances = synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE);
 			if(hypernyms.isEmpty() && hypernym_instances.isEmpty())
 			{
				this.roots.add(synset.getID());
			}
		}
		iterator = this.dict.getSynsetIterator(POS.VERB);
		while(iterator.hasNext())
		{
			synset = iterator.next();
 			hypernyms =	synset.getRelatedSynsets(Pointer.HYPERNYM);					// !!! if any of these point back (up) to synset then we have an inf. loop !!!
 			hypernym_instances = synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE);
 			if(hypernyms.isEmpty() && hypernym_instances.isEmpty())
 			{
				this.roots.add(synset.getID());
			}
		}
		return ( this.roots.size() > 0 );
	}
}
