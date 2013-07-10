import java.io.IOException;
import java.util.TimeZone;

public class Rdf2yed {

	
	public static void main(String[] args)  {
			try {
			RdfFileLoader rdfLoader=new RdfFileLoader("RDFXML.rdf");
		} catch  (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
			
			OleAutomationDateUtil a=new OleAutomationDateUtil();
			System.out.println(a.fromOADate(new Double(new String("41464,6291319445").replace(",",".")), TimeZone.getDefault()).toString());
		
	}
	
}
