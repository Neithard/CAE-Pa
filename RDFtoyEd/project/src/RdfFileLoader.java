import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map.Entry;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
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
					//System.out.println(sol.toString());
					String uid=sol.getLiteral("ger_uid").toString();
					if(!equipment.containsKey(uid))
					{
						equipment.put(uid, new ArrayList<QuerySolution>());
					}	
					equipment.get(uid).add(sol);
				}
	
				persons=new ArrayList<Node>();
				companies=new ArrayList<Node>();
		}	catch (Exception e) {
			System.out.println(e.toString());
			throw new IllegalArgumentException("File: \"" + sourceFile + "\" not valid.");
		}
		
		Iterator<Entry<String, List<QuerySolution>>> it=equipment.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, List<QuerySolution>> sol=it.next();
			queryEquipmentPiece(sol.getKey(), sol.getValue());
		}
	}
	
	private void queryEquipmentPiece(String uid, List<QuerySolution> rawPieceList)
	{	
		//create node
		String label=rawPieceList.get(0).getLiteral("label").toString() + " (" + uid + ")"; //label is present in every solution
		UniqueNode equipmentNode=new UniqueNode(label, NodeType.GERAET, uid);
		equipmentPieces.put(uid, equipmentNode);
		
		//find comment
		for(QuerySolution sol : rawPieceList)
		{
			String comment=sol.getLiteral("comment").toString();
			if(!comment.isEmpty())
			{
				Node commentNode= new Node(comment,NodeType.OTHER);
				Edge commentEdge=new Edge("comment", commentNode);
				equipmentNode.addEdge(commentEdge);
				break;
			}
		}
		
		//List of new documents
		HashMap<String, List<QuerySolution>> documentLists=new HashMap<String, List<QuerySolution>>();
		//List of docNodes for this particular piece of equipment
		HashMap<String, UniqueNode> nodeDocs=new HashMap<String, UniqueNode>();
		
		//Sort documents by doc_uid
		for(QuerySolution sol : rawPieceList)
		{
			String docUid=sol.getLiteral("doc_uid").toString();
			//check if document exists
			if(!documentLists.containsKey(docUid))
			{
				documentLists.put(docUid, new ArrayList<QuerySolution>());
			}
			documentLists.get(docUid).add(sol);
		}
		
		//Associate doc nodes
		Iterator<Entry<String, List<QuerySolution>>> it=documentLists.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, List<QuerySolution>> sol=it.next();
			String docUid=sol.getKey();
			if(!documents.containsKey(docUid))
			{
				//create doc node
				UniqueNode document=createDocumentNode(docUid, sol.getValue());
				documents.put(docUid, document);
			}
			//check if document was assciated with node
			//doc node is definitely present at this point
			if(!nodeDocs.containsKey(docUid))
			{
				UniqueNode docNode=documents.get(docUid);
				Edge  docEdge=new Edge("hasDocument", docNode);
				nodeDocs.put(docNode.getUid(), docNode);
				equipmentNode.addEdge(docEdge);
			}
		}
	}
	
	private UniqueNode createDocumentNode(String docUid, List<QuerySolution> rawDocList)
	{
		String name=rawDocList.get(0).getLiteral("typ").toString() + " (" + docUid + ")"; //type should be the same for every entry
		
		return new UniqueNode(name, NodeType.DOCUMENT, docUid);
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
