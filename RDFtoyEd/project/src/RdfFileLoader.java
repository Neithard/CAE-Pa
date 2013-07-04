import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;


public class RdfFileLoader {
	private Model model;

	public RdfFileLoader(String sourceFile) throws IllegalArgumentException
	{
		 // create an empty model
		 model = ModelFactory.createDefaultModel();

		 // use the FileManager to find the input file
		 InputStream in = FileManager.get().open( sourceFile );
		if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + sourceFile + " not found");
		}

		// read the RDF/XML file
		model.read(in, null);

		// write it to standard out
		model.write(System.out);		
	}
}
