public class Rdf2yed {

	
	public static void main(String[] args)  {
			try {
			RdfFileLoader rdfLoader=new RdfFileLoader("res/raus.rdf", "A3AOUEPQWH"); //A3AOUEE6WH
			
			XmlWriter writer= new XmlWriter();
			writer.makeOutput("res/out.graphml", rdfLoader);

		} catch  (Exception e) {
			
			System.out.println(e.getMessage());
		}
	}
	
}
