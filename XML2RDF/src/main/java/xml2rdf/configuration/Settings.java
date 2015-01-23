package xml2rdf.configuration;

import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * The object of settings which can load settings from indicated configuration files (default file name: default.conf)
 * @author Jyun-Yao Huang (Allen; allen501pc@gmail.com)
 *
 */
public class Settings {
	protected static PropertiesConfiguration conf = new PropertiesConfiguration();
	protected static ArrayList<String> configFiles = new ArrayList<String>();
	private static boolean isLoaded = false;
	private static boolean isWordNetUsed = false;
	
	/**
	 * Set the configuration files. 
	 * @param targetConfigFiles array of target configuration files. 
	 */
	public static void SetConfigurationFilesName(ArrayList<String> targetConfigFiles ) {
		configFiles = targetConfigFiles;
	}
	
	/**
	 * Load indicated configuration files.
	 * @param targetConfigFiles array of target config. files. 
	 */
	public static void Load(ArrayList<String> targetConfigFiles) {
		SetConfigurationFilesName(targetConfigFiles);
		Load();				
	}
	
	/**
	 * If the configuration files are not set, load default configuration file : deafult.conf.
	 * Otherwise, loaded the files that users indicate.
	 */
	public static void Load() {
		if(configFiles.isEmpty()) {			
			ArrayList<String> tempConfigFiles = new ArrayList<String>();
			tempConfigFiles.add("default.conf");
			SetConfigurationFilesName(tempConfigFiles);
		} 
		
		try {							
			for(String fileURI: configFiles) {
				conf.load(fileURI);
			}			
		} catch (ConfigurationException e) {
			System.err.println("Cannot load configuration files:" + e.getMessage());
			System.exit(1);
		} 		
		isLoaded = true;
	}
	
	/**
	 * To reload the configuration files. 
	 */
	public static void ReLoad() {
		isLoaded = false;
		Load();
	}
	
	/**
	 * Check the configuration files are loaded or not. 
	 * @return true for success, otherwise false.
	 */
	public static boolean IsLoaded() {
		return isLoaded;
	}
	
	/**
	 * Show the configuration files.
	 * @return the fileURIs, which are separated by space.
	 */
	public static String ShowConfigurationFiles() { 
		return configFiles.toString();
	}
	
	/**
	 * Get the XSLT File URI from the configuration file 
	 * @return the file URI of XSLT
	 */
	public static String GetXSLTFileURI() {
		if( !isLoaded ) {
			Load();
		}
		return conf.getString("transformation.file");
	}
	
	public static void SetWordNetUsed(boolean used) {
		isWordNetUsed = used;
	}
	
	public static boolean IsWordNetUsed() {
		if( !isLoaded ) {
			Load();
		}
		return conf.getBoolean("wordnet.use");
	}
	
	/**
	 * Get WordNet database's directory path
	 * @return path.
	 */
	public static String GetWordNetDBPath() {
		if( !isLoaded ) {
			Load();
		}
		return conf.getString("wordnet.dir_path");
	}
	
	/**
	 * Get WordNet IC file path.
	 * @return path
	 */
	public static String GetWordNetICFilePath() {
		if( !isLoaded ) {
			Load();
		}
		return conf.getString("wordnet.ic_path");
	}
}
