package transformation;
import java.util.ArrayList;
import java.util.Collection;
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
	private HashMap<String, Node> persons;
	private HashMap<String, UniqueNode> companies;
	private HashMap<String, UniqueNode> equipmentPieces;
	private HashMap<String, UniqueNode> documents;
	private List<Node> mailNodes;
	private List<Node> otherNodes;
	private List<Node> emptyNodes;
	private List<Node> phoneNodes;	
	private List<Node> pdfNodes;	
	private List<Node> revisions;	
		
	public Collection<UniqueNode> getCompanies()
	{
		return companies.values();
	}
	
	public Collection<Node> getEmpty()
	{
		return emptyNodes;
	}
	
	public Collection<Node> getMail()
	{
		return mailNodes;
	}
	
	public Collection<Node> getOther()
	{
		return otherNodes;
	}
	
	public Collection<Node> getPersons()
	{
		return persons.values();
	}
	
	public Collection<Node> getPhones()
	{
		return phoneNodes;
	}
	
	public Collection<Node> getPdfs()
	{
		return pdfNodes;
	}
	
	public Collection<Node> getRevisions()
	{
		return revisions;
	}
	
	public Collection<UniqueNode> getEquipment()
	{
		return equipmentPieces.values();
	}
	
	public Collection<UniqueNode> getDocuments()
	{
		return documents.values();
	}

	public RdfFileLoader(String sourceFile, String uidRegEx) throws IllegalArgumentException
	{
		queryString="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>    " + 
				"	PREFIX cae: <http://tu-dresden.de/ifa/cae/>   " + 
				"	    " + 
				"	SELECT  ?label ?typ ?project_number ?comment ?job_number ?revision_number    " + 
				"	        ?revision_comment ?PDF ?ch_name ?ch_date ?cr_name ?cr_date  " + 
				"	        ?re_name ?re_date  ?company_name  ?email_adress  ?telephone_number   " + 
				"	        ?firm_location  ?firm_zip  ?firm_street  ?ger_uid ?doc_uid  ?comp_uid" + 
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
	"                                          cae:uid          ?comp_uid;       "	+
	"                                          cae:email_adress ?email_adress;   " + 
	"					                       cae:telephone_number ?telephone_number. }   " + 
	"   " + 
	"					OPTIONAL {  ?firm       cae:has_address ?adress.   " + 
	"					            ?adress     cae:firm_location ?firm_location;    " + 
	"					                        cae:firm_zip ?firm_zip;   " + 
	"					                        cae:firm_street ?firm_street.  }   " + 
	"   " + 
	"					        FILTER regex(?ger_uid,\"" +  uidRegEx + "\")  " + 
	"					    }";
		
		HashMap<String, List<QuerySolution>> equipment= new HashMap<String, List<QuerySolution>>();
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
					String uid=sol.getLiteral("ger_uid").getValue().toString();
					if(!equipment.containsKey(uid))
					{
						equipment.put(uid, new ArrayList<QuerySolution>());
					}	
					equipment.get(uid).add(sol);
				}
	
				persons=new HashMap<String, Node>();
				companies=new HashMap<String, UniqueNode>();
				equipmentPieces= new HashMap<String, UniqueNode>();
				documents= new HashMap<String, UniqueNode>();
				mailNodes=new ArrayList<Node>();
				otherNodes=new ArrayList<Node>();
				emptyNodes=new ArrayList<Node>();
				phoneNodes=new ArrayList<Node>();
				pdfNodes=new ArrayList<Node>();
				revisions=new ArrayList<Node>();		
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
		String label=rawPieceList.get(0).getLiteral("label").getValue().toString() + " (" + uid + ")"; //label is present in every solution
		UniqueNode equipmentNode=new UniqueNode(label,  uid);
		equipmentPieces.put(uid, equipmentNode);
		
		//find comment
		for(QuerySolution sol : rawPieceList)
		{
			String comment=sol.getLiteral("comment").getValue().toString();
			if(!comment.isEmpty())
			{
				Node commentNode= new Node(comment);
				otherNodes.add(commentNode);
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
			String docUid=sol.getLiteral("doc_uid").getValue().toString();
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
		QuerySolution sol=rawDocList.get(0);//arbitrary solution from List
		
		//build node
		String name=sol.getLiteral("typ").getValue().toString() + " (" + docUid + ")"; //type should be the same for every entry
		UniqueNode docNode=new UniqueNode(name, docUid);
		
		/*
		 * build company node
		 * 
		 * this should be trivial because we find this information in all of the provided
		 * QuerySolutions if it exists
		 */
		if(sol.contains("comp_uid"))
		{
			String comp_uid=sol.getLiteral("comp_uid").getValue().toString();
			if(!companies.containsKey(comp_uid))
			{
				//Company Node itself
				UniqueNode newCompanyNode= new UniqueNode(sol.getLiteral("company_name").getValue().toString() + " (" + comp_uid + ")",
						comp_uid);
				//EMail - mandatory
				Node compEmail=new Node(sol.getLiteral("email_adress").getValue().toString());
				mailNodes.add(compEmail);
				newCompanyNode.addEdge(new Edge("eMail", compEmail));
				//Telephone - mandatory
				Node compPhone= new Node(sol.getLiteral("telephone_number").getValue().toString());
				phoneNodes.add(compPhone);
				newCompanyNode.addEdge(new Edge("phone", compPhone));
				//Street Address - optional
				if(sol.contains("firm_zip"))
				{
					//Address
					String comp_address=sol.getLiteral("firm_street").getValue().toString() + "\n" +
										sol.getLiteral("firm_zip").getValue().toString() + " " +
										sol.getLiteral("firm_location").getValue().toString();
					Node addrNode=new Node(comp_address);
					otherNodes.add(addrNode);
					newCompanyNode.addEdge(new Edge("address",
							               addrNode));
				}
				
				companies.put(comp_uid, newCompanyNode);
			}
			docNode.addEdge(new Edge("company", companies.get(comp_uid)));
		}
		//project number is the same for every solution too
		Node projNode=new Node(sol.getLiteral("project_number").getValue().toString());
		otherNodes.add(projNode);
		docNode.addEdge(new Edge("projectNumber",
						projNode));
		//also job_number
		Node jobNode=new Node(sol.getLiteral("job_number").getValue().toString());
		otherNodes.add(jobNode);
		docNode.addEdge(new Edge("jobNumber",
				jobNode));
		
		/*
		 * Now we create the Revision nodes
		 * 
		 * revisions for a document can be uniquely identified by their revision_number
		 * There might be several results for one revision, but their information (for this context) is the same.
		 */
		HashMap<String, Node> revNumbers= new HashMap<String, Node>();
		for(QuerySolution revSol : rawDocList)
		{
			String revisionNr=revSol.getLiteral("revision_number").getValue().toString();
			if(!revNumbers.containsKey(revisionNr))
			{
				Node revNode=makeRevisionNode(revSol);
				revNumbers.put(revisionNr, revNode);
				docNode.addEdge(new Edge("hasRev", revNode));
				
				Node pdfNode= new Node(revSol.getLiteral("PDF").getValue().toString());
				revNode.addEdge(new Edge("pdf", pdfNode));
				pdfNodes.add(pdfNode);
				
			}
		}
		
		//build comment nodes
		for(QuerySolution revSol : rawDocList)
		{
			if(revSol.contains("revision_comment"))
			{
				String revisionNr=revSol.getLiteral("revision_number").getValue().toString();
				if(revNumbers.containsKey(revisionNr))
				{
					Node comment=new Node(revSol.getLiteral("revision_comment").getValue().toString());
					otherNodes.add(comment);
					revNumbers.get(revisionNr).addEdge(new Edge("comment", comment));
					revNumbers.remove(revisionNr);
				}
			}
		}
		return docNode;
	}
	
	/*
	 * to compare the Revision's dates we return the Value of the Date String,
	 * if the String is empty no date could be found;
	 */
	private Node makeRevisionNode(QuerySolution sol)
	{
		Node revNode=new Node("Revision " + sol.getLiteral("revision_number").getValue().toString());
		revisions.add(revNode);
		//created - optional
		maybeMakeRevisionStepNode(revNode, sol, "cr", "created");
		//checked - optional
		maybeMakeRevisionStepNode(revNode, sol, "ch", "checked");	
		//released - optional
		maybeMakeRevisionStepNode(revNode, sol, "re", "released");
		
		return  revNode;
	}
	
	private String maybeMakeRevisionStepNode(Node parentNode, QuerySolution sol, String revPrefix, String edgeName)
	{
		if(sol.contains(revPrefix + "_date"))
		{
			String date = sol.getLiteral(revPrefix + "_date").getValue().toString();
			Node blankNode = new Node("");
			emptyNodes.add(blankNode);
			
			Node dateNode=new Node(date);
			otherNodes.add(dateNode);
			blankNode.addEdge(new Edge("date",
							  dateNode));
			
			//check if Person exists
			String personName=sol.getLiteral(revPrefix + "_name").getValue().toString();
			if(!persons.containsKey(personName))
			{
				persons.put(personName, new Node(personName));
			}
			blankNode.addEdge(new Edge("by", persons.get(personName)));
			
			//finally add it all to the parentNode
			parentNode.addEdge(new Edge(edgeName, blankNode));
			return date;
		} else return "";
	}
	
	private final String queryString;
}
