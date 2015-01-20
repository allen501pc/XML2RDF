package xml2rdf.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import xml2rdf.util.transformer.*;
import xml2rdf.configuration.*;

/**
 * Add command features
 * @author Jyun-Yao Huang (Allen; allen501pc@gmail.com)
 *
 */
public class Command {
	public static void main(String[] args) {
		Options options = new Options();
	    @SuppressWarnings("static-access")
		Option configOption = OptionBuilder.withArgName( "config_file" ).hasArgs().withDescription( "Use configuration file" ).create("config");
		options.addOption(configOption); 	
		
		@SuppressWarnings("static-access")
		Option sourceFileOption = OptionBuilder.withArgName( "source_file" ).hasArg().withDescription( "indicate the source XML file" ).create("s");
		options.addOption(sourceFileOption);
		
		@SuppressWarnings("static-access")
		Option outputFileOption = OptionBuilder.withArgName( "output_file" ).hasArg().withDescription( "indicate the output RDF file" ).create("o");
		options.addOption(outputFileOption);
		
		options.addOption("help",false,"print help messages");
		
		CommandLineParser parser = new GnuParser();
	      
		try {			
		    // parse the command line arguments
		    CommandLine line = parser.parse( options, args );
		    String[] configurationFileArray = null;
		    String srcFile = "";
		    String outputFile = "";
		    
		    if( line.hasOption("help")) {
		    	 HelpFormatter formatter = new HelpFormatter();
		         formatter.printHelp("XML2RDF", 
		        		 			 "The XML2RDF converter which can convert XML document into RDF document.", 
		        		 			 options, 
		        		 			 "Please report issues to author: Jyun-Yao Huang (Allen) " + formatter.getNewLine()  + "< allen501pc@gmail.com >",
		        		 			 true );	
		    } else {
		    	GenericTransformer trans = new GenericTransformer();
			    if( line.hasOption( "config" ) ) {			        
			        configurationFileArray = line.getOptionValues("config");			        
			    }
			    
			    if(line.hasOption("s")) {
			    	srcFile = line.getOptionValue("s");		    	
			    }
			    
			    if(line.hasOption("o")) {
			    	outputFile = line.getOptionValue("o");		    	
			    }
			    
			    if(configurationFileArray != null && configurationFileArray.length != 0 ) {			    	
			    	Settings.Load(new ArrayList<String>(Arrays.asList(configurationFileArray)));
			    }
			    
			    if(srcFile.isEmpty() || outputFile.isEmpty()) {
			    	System.out.println("Please check the parameters");
			    } else {			    	
			    	if(trans.transform( srcFile , outputFile )) {
			    		System.out.println("The transformation is completed. Please check the output file. Type XML2RDF -help for more information.");
			    	} else {
			    		System.out.println("The transformation is failed. Please check the source file's format. Type XML2RDF -help for more information.");
			    	}
			    }
		    }
		    		
		}
		catch( ParseException exp ) {
		    System.out.println( "Unexpected exception:" + exp.getMessage() );
		}
	}
}
