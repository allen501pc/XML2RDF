package DataCrawler.OpenAIRE;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.xsoup.Xsoup;

public class Graber {
	public String openAIREAPIURL = "http://api.openaire.eu/search/publications";
	public String datasetDirectory = "DataSet";
	public String searchDirectory = datasetDirectory + File.separator + "Search";
	public String relsDirectory = datasetDirectory + File.separator + "Rels";
	
	public void SearchTitle(String title, boolean searchRelated) throws IOException {
		File destinationFile = new File(searchDirectory + File.separator + title + ".xml");
		
		if(destinationFile.exists()) {
			return;
		}
		
		Document doc = Jsoup.connect(openAIREAPIURL + "?title=" + title).get();
		if(searchRelated) {
			// Elements questions = Xsoup.compile("/response/results/result/metadata/oaf:entity/oaf:result/rels/rel/to").evaluate(doc).getElements();
			Elements metadata = Xsoup.compile("//response/results/result/metadata").evaluate(doc).getElements();
			for(Element metadatum: metadata) {
				Elements children = metadatum.children();
				for(Element child : children) {
					if(child.tagName().equals("oaf:entity")) {
						Elements childrenOfEntity = child.children();
						for(Element childOfEntity : childrenOfEntity) {
							if(childOfEntity.tagName().equals("oaf:result")) {
								Elements toElements = Xsoup.compile("//rels/rel/to").evaluate(childOfEntity).getElements();
								for(Element toElement : toElements) {
									RelsObjectId(title,toElement.text());
								}
							}
						}
					}
				}
			}

		}
		
		PrintWriter writer = new PrintWriter(searchDirectory + File.separator + title + ".xml", "UTF-8");
		writer.write(doc.getElementsByTag("response").first().toString());
		writer.close();
		
	}
	
	public void RelsObjectId(String titleDirectory, String objectIdentifier) throws IOException, SecurityException {
		String dirPath = relsDirectory + File.separator + titleDirectory;
		File dir = new File(dirPath);
		
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		Document doc = Jsoup.connect(openAIREAPIURL + "?openairePublicationID=" + objectIdentifier).get();
		PrintWriter writer = new PrintWriter(dirPath + File.separator + objectIdentifier.replace(":", "#") + ".xml", "UTF-8");
		writer.write(doc.getElementsByTag("response").first().toString());
		writer.close();
	}
		
	public static void main(String[] args) {
		String[] searchTitles = {"semantic", "data", "rdf", "web", "cloud", "mining", "owl", "linked", "search", "grid", "IoT","internet", "open data"};
		try {
			Graber myGraber = new Graber();
			for(int idx = 0; idx < searchTitles.length; ++idx) {
				myGraber.SearchTitle(searchTitles[idx],true);
				if(searchTitles.length != 1) {
					Thread.sleep(2000);   
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}		
	}

}
