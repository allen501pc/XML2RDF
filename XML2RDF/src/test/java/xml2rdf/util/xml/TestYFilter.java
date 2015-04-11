package xml2rdf.util.xml;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestYFilter extends TestCase {
	 /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestYFilter( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TestYFilter.class);
    }
    
    public void TestChnageXPathExpression() {
    	YFilter myFilter = new YFilter();		
		Assert.assertEquals("/a/c", String.valueOf(myFilter.ChangeXPathExpression("/a/c")));
		Assert.assertEquals("/a/" + myFilter.epsilon + "/c", 
				String.valueOf(myFilter.ChangeXPathExpression("/a//c")));
		Assert.assertEquals("/" + myFilter.epsilon + "/a/c", 
				String.valueOf(myFilter.ChangeXPathExpression("//a/c")));
		Assert.assertEquals("/a/" + myFilter.epsilon + "/c/d", 
				String.valueOf(myFilter.ChangeXPathExpression("/a//c/d")));		
    }
	
    /**
     * Rigourous Test :-)
     */		
    public void testApp()
    {
    	TestChnageXPathExpression();
    }
}
