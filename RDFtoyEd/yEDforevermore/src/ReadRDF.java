import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;


public class ReadRDF {
	
	public ReadRDF(){
		
		Model model = null;
		
		model = FileManager.get().loadModel( "./res/RDF.rdf" );

		// Create a new query
		String queryString =
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>    " + 
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
		

		Query query = QueryFactory.create(queryString);
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
		
		// while(results.hasNext()) {
		QuerySolution rb = results.nextSolution();
		
		RDFNode neu;
		
		neu = rb.getLiteral("label");
		System.out.println(neu.toString());
		
		neu = rb.get("typ");
		System.out.println(neu.toString());
			
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ReadRDF();

	}

}
