import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


public class RDFforGood {
	
	String namepsace = "http://tu-dresden.de/ifa/cae/";
	
	private Document doc;
	private Model model;
	
	private int lastComosID = 10000;
	
	public RDFforGood() {
		
				// Java Dokument einlesen
				doc = openXML(new File("./res/Export.xml"));
				
				// Jena Model erstellen
				model = ModelFactory.createDefaultModel();
				
				//Property
				Property typ = model.createProperty(namepsace, "typ");
				Property has_dokument = model.createProperty(namepsace, "has_dokument");
				
				// Konsollenüberschieft erstellt
				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				System.out.println("----------------------------");
				
				//aus dem XML File alle Knoten mit dem Namen "RuI" auslesen
				NodeList RuIsList = doc.getElementsByTagName("RuI");
				
				//alle RuI-Knoten abarbeiten
				for(int i = 0; i<RuIsList.getLength(); i++) {
					Node temp = RuIsList.item(i);
					if(temp.hasChildNodes()){
							
							//Element zu diesem Konten erzeugen
							Element RuI = (Element) temp;
							
							//alle Kinderknoten mit dem Namen "RuI-Element" raussuchen
							NodeList RuIElement = RuI.getElementsByTagName("RuI-Element");
									
							/*
							 * siehe Dokumentation
							 */
							if(RuIElement.getLength() > 0) {
								
								//Jena Resource erstellen
								Resource Dokument = model.createResource(namepsace + String.valueOf(lastComosID));
								lastComosID++;
								
								Dokument.addLiteral(typ, RuI.getAttribute("Beschreibung"));
								
								System.out.println("UID: " + RuI.getAttribute("UID"));
								System.out.println("Beschreibung: " + RuI.getAttribute("Beschreibung"));
								
								for(int l = 0; l < RuIElement.getLength(); l++ ) {
									
									Element geraet = (Element) RuIElement.item(l);
									
									Resource Element = model.createResource(namepsace + String.valueOf(lastComosID));
									lastComosID++;
									
									Element.addProperty(has_dokument, Dokument);
									
									
										
									System.out.println(	"	Name: " + geraet.getAttribute("Name"));
									System.out.println(	"	UID: " + geraet.getAttribute("UID"));
							
								}
							}
						}
					}
				
				String fileName = "TURTLE.ttf";
				FileWriter out;
		        try {
		        	out = new FileWriter( fileName );
		            model.write( out, "TURTLE" );

		               out.close();
		               System.out.println("Datei Abgelegt");
		           }
		         catch (Exception closeException) {
		        	 System.out.println("Fehler");
		         }
		        
				
	}
		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		new RDFforGood();
		
	}
	
	private Document openXML(File fXmlFile){
		
		Document doc_temp = null;
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc_temp = dBuilder.parse(fXmlFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		doc_temp.getDocumentElement().normalize();
		
		return doc_temp;
		
	}
	
}
