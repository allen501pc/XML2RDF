package DataCrawler.OpenAIRE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;


public class XMLGenerator {	
		
	public static void main(String[] args) {
		String text = "";
		
		try {
			if(args.length < 4) {
				System.out.println("<command> template_file csv_file output_dir log_file [start_id]");
			}
			
			// InputStream fis = new FileInputStream("E:/Downloads/result-r-00000");
			InputStream fis = new FileInputStream(args[1]);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
									
			// String content = new String(Files.readAllBytes(Paths.get("publications_template.xml")));
			String content = new String(Files.readAllBytes(Paths.get(args[0])));
			Document doc = Jsoup.parse(content, "UTF-8", Parser.xmlParser());
			// String outputDirectory = "G:/";
			String outputDirectory = args[2];
			// PrintWriter logWriter = new PrintWriter(new FileOutputStream("publication.log",false));
			PrintWriter logWriter = new PrintWriter(new FileOutputStream(args[3],false));
			Element objectId = null, title = null, publisher = null
					,dateofacceptance = null,bestlicense = null, resulttype = null
					, originalId = null, originalId2 = null;
			boolean start = true;
		    // String startID = "dedup_wf_001::207a098867b64f3b5af505fa3aeecd24";
			String startID = "";
			if(args.length >=5) {
				start = false;
				startID = args[4];
			}
			String previousText = "";
			while((text = br.readLine()) != null) {
				/*  For publications:
				    0. dri:objIdentifier context
					9. title context
					12. publisher context
					18. dateofacceptance
					19. bestlicense @classname
					21. resulttype  @classname
					26. originalId context  
					(Notice that the prefix is null and will use space to separate two different "originalId")
				*/
				
				if(!previousText.isEmpty()) {
					text = previousText + text;
					start = true;
					previousText = "";
				}
				
				String[] items = text.split("!");
				for(int i = 0; i < items.length; ++i) {
					items[i] = StringUtils.strip(items[i], "#");
				}
				if(objectId==null)
					objectId = doc.getElementsByTag("dri:objIdentifier").first();
				objectId.text(items[0]);
				
				if(!start && items[0].equals(startID)) {					
					start = true;							
				}
				
				
				
				if(title == null)
					title = doc.getElementsByTag("title").first();
				title.text(items[9]);
				
				if(publisher == null)
					publisher = doc.getElementsByTag("publisher").first();
				
				if(items.length < 12) {
					previousText = text;
					continue;
				}
				publisher.text(items[12]);
				
				if(dateofacceptance == null)
					dateofacceptance = doc.getElementsByTag("dateofacceptance").first();
				dateofacceptance.text(items[18]);
				
				if(bestlicense == null)
					bestlicense =  doc.getElementsByTag("bestlicense").first();
				bestlicense.attr("classname",items[19]);
				
				if(resulttype == null)
					resulttype =  doc.getElementsByTag("resulttype").first();
				resulttype.attr("classname",items[21]);
				
				if(originalId == null || originalId2 == null) {
					Elements elements = doc.getElementsByTag("originalId");
					String[] context = items[26].split(" ");
					if(elements.size() > 0) {
						if(elements.size() >= 1) {
							originalId = elements.get(0);	
							if(context.length >= 1) {
								int indexOfnull = context[0].trim().indexOf("null");
								String value = "";
								if(indexOfnull != -1) {
									if(context[0].trim().length() >= (indexOfnull + 5))
										value = context[0].trim().substring(indexOfnull + 5);
											
								} else {
									value = context[0].trim();
								}						
								originalId.text(value);
							}
						} 
						if(elements.size() >= 2) {							
							originalId2 = elements.get(1);
							if(context.length >= 2) {
								int indexOfnull = context[1].trim().indexOf("null");
								String value = "";
								if(indexOfnull != -1) {
									if(context[1].trim().length() >= (indexOfnull + 5))
										value = context[1].trim().substring(indexOfnull + 5);
											
								} else {
									value = context[1].trim();
								}						
								originalId2.text(value);	
							}
						}
					}
				} else {
					String[] context = items[26].split(" ");
					if(context.length >= 1) {
						int indexOfnull = context[0].trim().indexOf("null");
						String value = "";
						if(indexOfnull != -1) {
							if(context[0].trim().length() >= (indexOfnull + 5))
								value = context[0].trim().substring(indexOfnull + 5);
									
						} else {
							value = context[0].trim();
						}						
						originalId.text(value);
					}
					if(context.length >= 2) {
						int indexOfnull = context[1].trim().indexOf("null");
						String value = "";
						if(indexOfnull != -1) {
							if(context[1].trim().length() >= (indexOfnull + 5))
								value = context[1].trim().substring(indexOfnull + 5);
									
						} else {
							value = context[1].trim();
						}	
						originalId2.text(value);												
					}					
				}	
				if(start) {
					String filePath = outputDirectory + items[0].replace(":", "#") + ".xml";
					PrintWriter writer = new PrintWriter(new FileOutputStream(filePath, false));
					logWriter.write(filePath + " > Start" + System.lineSeparator());
					writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());
					writer.write(doc.getElementsByTag("response").first().toString());
					writer.close();		
					logWriter.write(filePath + " > OK" + System.lineSeparator());
					logWriter.flush();
				}
				
			}
			logWriter.close();
		} catch (Exception e) {			
			e.printStackTrace();
		} 		
	}

}
