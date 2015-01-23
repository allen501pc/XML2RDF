package xml2rdf.util.semantic.similarity;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.NGramDistance;

/**
 * Implement name similarity including LevenshteinDistance and NGramDistance. 
 * @author Jyun-Yao Huang (Allen; allen501pc@gmail.com)
 *
 */
public class NameSimilarity {
	
	/**
	 * Give two strings, s0 and s1 and calculate the LevenshteinDistance.
	 * @param s0 target string
	 * @param s1 another string
	 * @return value  
	 */
	public float getLevenshteinDistance(String s0, String s1) {
		LuceneLevenshteinDistance distanceObject = new LuceneLevenshteinDistance();
		return distanceObject.getDistance(s0, s1);
	}
	
	/**
	 * Give two strings, s0 and s1, one value size and calculate the NGrams 
	 * @param s0 target string
	 * @param s1 the other string
	 * @param size of grams
	 * @return value
	 */
	public float getNGramDistance(String s0, String s1, int size) {
		NGramDistance distanceObject = new NGramDistance(size);
		return distanceObject.getDistance(s0,s1);
	}
}
