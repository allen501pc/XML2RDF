package xml2rdf.example;

import java.io.*;

public class SplitFiles {
    public static void main(String[] args) {
        if(args.length == 0 ) {
                return ;
        }
        String fileName = args[0];
        int lineNumbers = Integer.parseInt(args[1]);
        String directory = args[2];

        PrintWriter writer;
		try {
	        BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(fileName), "UTF-8"));
	        int fileCount = 1;
			writer = new PrintWriter(new FileOutputStream(directory + "/" + fileName + "_" + fileCount, true));
			
	        int count = 0;
	        String text = "";

			while((text = br.readLine()) != null) {
			        ++count;
			        writer.println(text);
			        if(count%lineNumbers == 0) {
			                count = 0;
			                writer.close();
			                fileCount++;
			                writer = new PrintWriter(new FileOutputStream(directory + "/" + fileName + "_" + fileCount, true));
			        }
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
        
    }

}
