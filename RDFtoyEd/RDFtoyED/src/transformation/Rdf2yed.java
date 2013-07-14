package transformation;
public class Rdf2yed {

	
	public static void main(String[] args)  {
//		rdf2yed("res/raus.rdf", "res/out.graphml", "A3AOUEE6WH");//A3AOUEE6WH
	}
	
	public boolean rdf2yed (String inFile, String outFile, String equipment_uid_Query)
	{
		try {
		RdfFileLoader rdfLoader=new RdfFileLoader(inFile, equipment_uid_Query); //
		
		XmlWriter writer= new XmlWriter();
		writer.makeOutput(outFile, rdfLoader);

	} catch  (Exception e) {
		
		System.out.println(e.getMessage());
		return false;
	}
		
		return true;
	}
	
}
