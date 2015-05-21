package xml2rdf.util.rdf;

import java.io.Reader;

public interface ITemplateParser {

	public abstract void SetDefaultNameSpace(String namespaceExpression);

	public abstract void SetNameSpace(String namespaceExpression);

	public abstract void Parse(Reader reader);

	public abstract boolean ParseByLine(String line);

}