import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/*
 * https://jena.apache.org/tutorials/rdf_api.html
 */
public class RdfFileLoader {
	private List<Node> persons;
	private List<Node> companies;
	private HashMap<String, UniqueNode> equipmentPieces;
	private HashMap<String, UniqueNode> documents;
	
	
	private HashMap<String, List<QuerySolution>> equipment;

	public RdfFileLoader(String sourceFile) throws IllegalArgumentException
	{
		equipment= new HashMap<String, List<QuerySolution>>();
		//create new Query
		try{
			Model model=FileManager.get().loadModel(sourceFile);
			//try {
				Query query= QueryFactory.create(queryString);
				QueryExecution qExec=QueryExecutionFactory.create(query,model);
				
				ResultSet results=qExec.execSelect();
				//sort equipment
				while(results.hasNext())
				{
					QuerySolution sol=results.nextSolution();
					System.out.println(sol.toString());
					String label=sol.getLiteral("label").toString();
	//				if(!equipment.containsKey(label))
	//				{
	//					equipment.put(label, new ArrayList<QuerySolution>());
	//					
	//					//create node
	//					String uid="pups";
	//					Node piece=new UniqueNode(label, NodeType.GERAET);
	//					equipmentPieces.add(piece);
	//				}	
	//				equipment.get(label).add(sol);
				}
	
				persons=new ArrayList<Node>();
				companies=new ArrayList<Node>();
		}	catch (Exception e) {
			System.out.println(e.toString());
			throw new IllegalArgumentException("File: \"" + sourceFile + "\" not valid.");
		}
		
		for(List<QuerySolution> sol : equipment.values())
		{
			queryEquipmentPiece(sol);
		}
		
		
		


		
		
		//queryPersons();
	}
	
	private void queryEquipmentPiece(List<QuerySolution> rawPieceList)
	{	
		HashMap<String, List<QuerySolution>> documents=new HashMap<String, List<QuerySolution>>();
		//Sort documents by type
		for(QuerySolution rawDocument : rawPieceList)
		{
			String name=rawDocument.getLiteral("typ").toString();
			if(!documents.containsKey(name))
			{
				documents.put(name, new ArrayList<QuerySolution>());
			}
		}
	}
	
	private static final String queryString="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>    " + 
			"	PREFIX cae: <http://tu-dresden.de/ifa/cae/>   " + 
			"	    " + 
			"	SELECT  ?label ?typ ?project_number ?comment ?job_number ?revision_number    " + 
			"	        ?revision_comment ?PDF ?ch_name ?ch_date ?cr_name ?cr_date  " + 
			"	        ?re_name ?re_date  ?company_name  ?email_adress  ?telephone_number   " + 
			"	        ?firm_location  ?firm_zip  ?firm_street  ?ger_uid ?doc_uid " + 
			"   " + 
			"					    WHERE {   " + 
			"					           ?gereat    cae:has_dokument  ?dokument;   " + 
			"										 rdfs:comment      ?comment;  " +
            "										  cae:uid           ?ger_uid; " +
			"					                      rdfs:label        ?label.   " + 
			"   " + 
			"					           ?dokument   cae:typ            ?typ;   " + 
			"					                       cae:project_number ?project_number;   " + 
			"					                       cae:job_number     ?job_number;   " + 
			"                                          cae:uid            ?doc_uid."  +
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
"					        FILTER regex(?ger_uid,\"A3AOUEE6WH\")  " + 
"					    }";
}
