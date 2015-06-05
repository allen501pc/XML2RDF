package DataCrawler.OpenAIRE;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;

public class ProcessSequenceFile {
	
	public static void readSequenceFile(String sequenceFileName) throws IOException, URISyntaxException {
		Configuration conf = new Configuration();
		String directoryPath = "file:///";
		directoryPath = URLEncoder.encode(directoryPath, "UTF-8");
		FileSystem fs = FileSystem.get(new URI(directoryPath),conf);
		Path file = new Path(fs.getUri().toString() +  sequenceFileName);
		@SuppressWarnings("deprecation")
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, file, conf);
		Text key = (Text) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
		Text value = (Text) ReflectionUtils.newInstance(reader.getValueClass(), conf);
		while(reader.next(key,value)) {
			System.out.println("Key:" + key);
			System.out.println("=================");			
			System.out.println(value);
			
		}
		
	}
	
	public static void main(String[] args) {
		try {
			// If args[0] is the SequenceFile we need to read.
			readSequenceFile(args[0]);
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
