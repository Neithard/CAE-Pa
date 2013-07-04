import java.io.IOException;

public class Rdf2yed {

	
	public static void main(String[] args)  {
			try {
			RdfFileLoader rdfLoader=new RdfFileLoader("RDFXML.rdf");
		} catch  (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}
}
