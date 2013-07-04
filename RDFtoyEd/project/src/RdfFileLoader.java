import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import riotcmd.infer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.VCARD;

/*
 * https://jena.apache.org/tutorials/rdf_api.html
 */
public class RdfFileLoader {
	private Model model;
	private List<Node> persons;
	private List<Node> companies;

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
		
		persons=new ArrayList<Node>();
		companies=new ArrayList<Node>();
		
		queryPersons();
	}
	
	/*
	 * Create Person Nodes
	 */
	private void queryPersons()
	{
		ResIterator iter = model.listSubjectsWithProperty(VCARD.FN);
		while(iter.hasNext()) {
			Resource r= iter.nextResource();
			if(r.hasProperty(VCARD.N))
			{
				r=r.getProperty(VCARD.N).getResource();

				
				String name=r.getProperty(VCARD.NAME).getObject().asLiteral().getValue().toString();
				name=name + " " +  r.getProperty(VCARD.Family).getObject().asLiteral().getValue().toString();
				persons.add(new Node(name, NodeType.PERSON));
			}
		}
	}
	
	/*
	 * Create Company Nodes
	 */
	private void queryCompanies()
	{
		ResIterator iter = model.listSubjectsWithProperty(VCARD.FN);
		while(iter.hasNext()) {
			Resource r= iter.nextResource();
			if(r.hasProperty(VCARD.N))
			{
				r=r.getProperty(VCARD.N).getResource();

				
				String name=r.getProperty(VCARD.NAME).getObject().asLiteral().getValue().toString();
				name=name + " " +  r.getProperty(VCARD.Family).getObject().asLiteral().getValue().toString();
				persons.add(new Node(name, NodeType.COMPANY));
			}
		}
	}	
}
