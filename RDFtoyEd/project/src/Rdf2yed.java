import java.io.IOException;
import java.util.TimeZone;

public class Rdf2yed {

	
	public static void main(String[] args)  {
			try {
			RdfFileLoader rdfLoader=new RdfFileLoader("res/RDF.rdf");
			XmlWriter.makeOutput("res/out.graphml", rdfLoader);

		} catch  (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}
	
}
