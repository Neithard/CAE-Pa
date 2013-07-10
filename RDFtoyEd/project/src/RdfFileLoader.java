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

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
/*
 * https://jena.apache.org/tutorials/rdf_api.html
 */
public class RdfFileLoader {
	private ResultSet results;
	private List<Node> persons;
	private List<Node> companies;

	public RdfFileLoader(String sourceFile) throws IllegalArgumentException
	{
		//create new Query
		try{
			Model model=FileManager.get().loadModel(sourceFile);
			
			Query query= QueryFactory.create(queryString);
			QueryExecution qExec=QueryExecutionFactory.create(query,model);
			
			results=qExec.execSelect();
			System.out.print(results.toString());
			
			persons=new ArrayList<Node>();
			companies=new ArrayList<Node>();
		} catch (Exception e) {
			throw new IllegalArgumentException("File: \"" + sourceFile + "\" not valid.");
		}
		


		
		
		//queryPersons();
	}
	
	/*
	 * Create Person Nodes
	 */
//	private void queryPersons()
//	{
//		//ResIterator iter = model.listSubjectsWithProperty(VCARD.FN);
//		while(iter.hasNext()) {
//			Resource r= iter.nextResource();
//			if(r.hasProperty(VCARD.N))
//			{
//				r=r.getProperty(VCARD.N).getResource();
//
//				
//				String name=r.getProperty(VCARD.NAME).getObject().asLiteral().getValue().toString();
//				name=name + " " +  r.getProperty(VCARD.Family).getObject().asLiteral().getValue().toString();
//				persons.add(new Node(name, NodeType.PERSON));
//			}
//		}
//	}
	
	private static final String queryString="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>    " + 
			"	PREFIX cae: <http://tu-dresden.de/ifa/cae/>   " + 
			"	    " + 
			"	SELECT  ?label ?typ ?project_number ?job_number ?revision_number    " + 
			"	        ?revision_comment ?PDF ?ch_name ?ch_date ?cr_name ?cr_date  " + 
			"	        ?re_name ?re_date  ?company_name  ?email_adress  ?telephone_number   " + 
			"	        ?firm_location  ?firm_zip  ?firm_street   " + 
			"   " + 
			"					    WHERE {   " + 
			"					           ?gereat    cae:has_dokument  ?dokument;   " + 
			"					                      rdfs:label        ?label.   " + 
			"   " + 
			"					           ?dokument   cae:typ            ?typ;   " + 
			"					                       cae:project_number ?project_number;   " + 
			"					                       cae:job_number     ?job_number.   " + 
			"					           ?dokument   cae:has_revision   ?revision.    " + 
			"   " + 
			"					           ?revision  cae:revision_number ?revision_number;   " + 
			"					                      cae:PDF ?PDF .   " + 
			"					OPTIONAL { ?revision  cae:revision_comment ?revision_comment. }   " + 
			"   " + 
			"					OPTIONAL { ?revision  cae:checked ?checked.   " + 
			"					           ?checked   cae:name ?ch_name;   " + 
			"					                      cae:date ?ch_date. }   " + 
			"   " + 
"					OPTIONAL { ?revision  cae:created ?created.   " + 
"					           ?created   cae:name ?cr_name;   " + 
"					                      cae:date ?cr_date. }   " + 
"   " + 
"					OPTIONAL { ?revision  cae:released ?released.   " + 
"					           ?released  cae:name ?re_name;   " + 
"					                      cae:date ?re_date. }   " + 
"   " + 
"					OPTIONAL { ?dokument   cae:has_firm ?firm.   " + 
"					           ?firm       cae:company_name ?company_name;    " + 
"                                          cae:email_adress ?email_adress;   " + 
"					                       cae:telephone_number ?telephone_number. }   " + 
"   " + 
"					OPTIONAL {  ?firm       cae:has_address ?adress.   " + 
"					            ?adress     cae:firm_location ?firm_location;    " + 
"					                        cae:firm_zip ?firm_zip;   " + 
"					                        cae:firm_street ?firm_street.  }   " + 
"   " + 
"					    FILTER regex(?label,\"P001\")   " + 
"					    }";
}
