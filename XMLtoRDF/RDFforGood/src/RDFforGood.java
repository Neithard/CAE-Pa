import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
import com.hp.hpl.jena.vocabulary.RDFS;


public class RDFforGood {
	
	String namepsace = "http://tu-dresden.de/ifa/cae/";
	
	private Document doc;
	private Model model;
	private ArrayList<Geraet> geraete = new ArrayList<Geraet>();
	
	public RDFforGood() {
			
				// Java Dokument einlesen
				doc = openXML(new File("./res/Export.xml"));
				
				// Jena Model erstellen
				model = ModelFactory.createDefaultModel();
				
				//************  Data Properties   ************
				//Dokument
				Property typ = model.createProperty(namepsace, "typ");
				Property project_number = model.createProperty(namepsace, "project_number");
				Property job_number = model.createProperty(namepsace, "job_number");
				
				//Revision
				Property revision_number = model.createProperty(namepsace, "revision_number");
				Property revision_comment = model.createProperty(namepsace, "revision_comment");
				Property PDF = model.createProperty(namepsace, "PDF");
				
				//blank Node --- Revision
				Property date = model.createProperty(namepsace, "date");
				Property name = model.createProperty(namepsace, "name");
				
				//Firma
				Property company_name = model.createProperty(namepsace, "company_name");
				Property email_adress = model.createProperty(namepsace, "email_adress");
				Property telephone_number = model.createProperty(namepsace, "telephone_number");
				Property firm_location = model.createProperty(namepsace, "firm_location");
				Property firm_zip = model.createProperty(namepsace, "firm_zip");
				Property firm_street = model.createProperty(namepsace, "firm_street");
				
				//UID
				Property uid = model.createProperty(namepsace, "uid");
				
				//************  Object Property   ************
				//SubProperty Of "has"
				Property has_dokument = model.createProperty(namepsace, "has_dokument");
				Property has_firm = model.createProperty(namepsace, "has_firm");
				Property has_revision = model.createProperty(namepsace, "has_revision");
				Property has_address = model.createProperty(namepsace, "has_address");
				
				//SubProperty Of "creation"
				Property checked = model.createProperty(namepsace, "checked");
				Property created = model.createProperty(namepsace, "created");
				Property released = model.createProperty(namepsace, "released");
				
				
				
				// Konsollenüberschieft erstellt
				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				System.out.println("----------------------------");
				
				//aus dem XML File alle Knoten mit dem Namen "RuI" auslesen
				NodeList RuIsList = doc.getElementsByTagName("RuI");
				
				//alle RuI-Knoten abarbeiten
				for(int i = 0; i<RuIsList.getLength(); i++) {
					Node temp = RuIsList.item(i);
				//	if(temp.hasChildNodes()){  
											{
							
							//Element zu diesem Konten erzeugen
							Element RuI = (Element) temp;
									
							/*
							 * siehe Dokumentation
							 */
								
							//Jena Resource erstellen
							//Property dokument_property = model.createProperty(namepsace, RuI.getAttribute("UID"));
							Resource Dokument = model.createResource(namepsace + RuI.getAttribute("UID"));
							
							// UID hinzufügen
							Dokument.addLiteral(uid, RuI.getAttribute("UID"));
								
							//Dokument Literal -- hinzufügen
							Dokument.addLiteral(typ, RuI.getAttribute("Beschreibung"));
							
								//Auftragsnummer und Projektnummer hinzufügen
								NodeList AufUndPro = RuI.getElementsByTagName("Auftrags_und_Projektnummer");
								
								//wenn es eine Auftragsnummer und Projektnummer wirklich gibt, dann Auswerten
								if(AufUndPro.getLength() > 0) {
									
									// kann mehrmal vorkommen, ist aber immer gleich
									Element eAuP = (Element) AufUndPro.item(0);
									
									// Dokument Auftragsnummer hinzufügen
									String Auftragsnummer = eAuP.getAttribute("Auftragsnr");
									
									//wenn nicht leer, dann Schneide den ersten Teil ab
									if(Auftragsnummer.contains("Auftrag Nr.:"))
										Auftragsnummer = (String) Auftragsnummer.subSequence(13, Auftragsnummer.length());
									
									//Literal erstellen
									Dokument.addLiteral(job_number, Auftragsnummer);
									
									// Dokument Projektnummer hinzufügen
									String Projektnummer = eAuP.getAttribute("Projektnr");
									
									//wenn nicht leer, dann Schneide den ersten Teil ab
									if(Projektnummer.contains("Projekt-Nr.:"))
										Projektnummer = (String) Projektnummer.subSequence(13, Projektnummer.length());
									
									//Literal erstellen
									Dokument.addLiteral(project_number, Projektnummer);				
									
								}
							
								//Revisionsinformationen holen
								NodeList RevInfo = RuI.getElementsByTagName("RevInfo");
								
									//nur Auswerten wenn es sie wirklich gibt
									if(RevInfo.getLength()>0){
										
										//Schlaifenstart
										int k_start = 0;
										
										//Element anlegen um Revisionskonten zu erstellen
										Element eRevision = (Element) RevInfo.item(0);
										
										//Revisionsknoten erstellen --- die UID des ersten Revisionskonten wird als Revisions-UID genommen
										Resource revision = model.createResource(namepsace + eRevision.getAttribute("UID"));
										
										// UID hinzufügen
										revision.addLiteral(uid, eRevision.getAttribute("UID"));
										
										//Revision an Dokument anhängen
										Dokument.addProperty(has_revision, revision);
										
										//wenn der erste Revisionsknoten einen Wert enthält, wird dieser als Revisionsnummer genommen. Wenn nicht, dann der Wert vom Zweiten.
										Integer Revisionsnummer;
										if(eRevision.getAttribute("Wert").length() != 0)
											Revisionsnummer = Integer.parseInt(eRevision.getAttribute("Wert"));
										else {
											eRevision = (Element) RevInfo.item(1);
											Revisionsnummer = Integer.parseInt(eRevision.getAttribute("Wert"));
											k_start = 1;
										}
										
										//Revisionsnummer hinzufügen
										revision.addLiteral(revision_number, Revisionsnummer);
										
										//Revisionspdf Pfad hinzufügen
										revision.addLiteral(PDF, RuI.getAttribute("Speicherort"));
																		
										//alle RevInfo Konten durchgehen
										for(int k = k_start; k < RevInfo.getLength(); k++) {
											
											eRevision = (Element) RevInfo.item(k);
											
											if(eRevision.getAttribute("Beschreibung").contains("Erstellt von")){
												Element eRevElementNextElement = (Element) RevInfo.item(k+1);
												
												// "Erstellt von" hinzufügen, was über einen leeren Knoten auf den Namen und das Datum zeigt
												revision.addProperty(created, model.createResource().addLiteral(name, eRevision.getAttribute("Wert"))
																									.addLiteral(date, eRevElementNextElement.getAttribute("Wert")));
												
												//Schleife das nächste Element überspringen lassen, da schon Ausgewertet
												k++;
											}
											
											if(eRevision.getAttribute("Beschreibung").contains("Geprüft von")){
												Element eRevElementNextElement = (Element) RevInfo.item(k+1);
												
												// "Geprüft von" hinzufügen, was über einen leeren Knoten auf den Namen und das Datum zeigt
												revision.addProperty(checked, model.createResource().addLiteral(name, eRevision.getAttribute("Wert"))
																									.addLiteral(date, eRevElementNextElement.getAttribute("Wert")));
												
												//Schleife das nächste Element überspringen lassen, da schon Ausgewertet
												k++;
											}
											
											if(eRevision.getAttribute("Beschreibung").contains("Freigegeben von")){
												Element eRevElementNextElement = (Element) RevInfo.item(k+1);
												
												// "Freigegeben von" hinzufügen, was über einen leeren Knoten auf den Namen und das Datum zeigt
												revision.addProperty(released, model.createResource().addLiteral(name, eRevision.getAttribute("Wert"))
																									.addLiteral(date, eRevElementNextElement.getAttribute("Wert")));
												
												//Schleife das nächste Element überspringen lassen, da schon Ausgewertet
												k++;
											}
											
											if(eRevision.getAttribute("Beschreibung").contains("Revisionsbeschreibung")){
												Element eRevElementNextElement = (Element) RevInfo.item(k+1);
												
												// Revisionskommentar hinzufügen
												revision.addLiteral(revision_comment, eRevision.getAttribute("Wert"));
											}
											
										}
										
										
									}
									
								//Firmeninformation holen
								NodeList FirmInfo = RuI.getElementsByTagName("Firma");
									
									//nur Auswerten wenn es sie wirklich gibt
									if(FirmInfo.getLength()>0){
										
										//Firm Element erstellen zum auslesen der UID
										Element eAtt = (Element) FirmInfo.item(0);
										
										//Firm Resource erstellen
										Resource rFirm = model.createResource(namepsace + eAtt.getAttribute("UID"));
										
										// UID hinzufügen
										rFirm.addLiteral(uid, eAtt.getAttribute("UID"));
										
										//Firma an Dokument Anhängen
										Dokument.addProperty(has_firm, rFirm);
										
										//Attribute auslesen --- es gibt immer nur eine Firma und alle Untergeordneten Knoten halten die Informationen
										NodeList AttInfo = eAtt.getElementsByTagName("Attribut");
										
										for(int y = 0; y<AttInfo.getLength(); y++) {
											
											//Element erzeugen
											eAtt = (Element) AttInfo.item(y);
											
											if(eAtt.getAttribute("Name").contains("Mail"))
												rFirm.addLiteral(email_adress, eAtt.getAttribute("Wert"));
											
											if(eAtt.getAttribute("Name").contains("Name"))
												rFirm.addLiteral(company_name,eAtt.getAttribute("Wert"));
											
											//leeren Knoten erzeugen, der Adresse, ZIP und Ort enthält
											if(eAtt.getAttribute("Name").contains("Ort")){
												Element eAttNext = (Element) AttInfo.item(y+1);
												Element eAttAfterNext = (Element) AttInfo.item(y+2);
												
												rFirm.addProperty(has_address, model.createResource().addLiteral(firm_location, eAtt.getAttribute("Wert"))
																									 .addLiteral(firm_zip, eAttNext.getAttribute("Wert"))
																									 .addLiteral(firm_street, eAttAfterNext.getAttribute("Wert")));
											}
											
											if(eAtt.getAttribute("Name").contains("Telefon"))
												rFirm.addLiteral(telephone_number,eAtt.getAttribute("Wert"));
												
										}
										
									}
								
							
							//"RuI-Element" raussuchen 
							NodeList RuIElement = RuI.getElementsByTagName("RuI-Element");
								
								//nur Auswerten wenn es sie wirklich gibt -> das Bedeutet es handelt sich um ein RuI Bild, da sind die zugehörigen Geräte untergeordnet 
								if(RuIElement.getLength() > 0) {
									
									for(int l = 0; l < RuIElement.getLength(); l++ ) {
										
										Element geraet = (Element) RuIElement.item(l);
										
										//Erzeuge Triple: Gerät -> has_dokument -> Dokument ... Resource Gerät wird erst später erstellt
										Resource geraet_triple = model.createResource(namepsace + geraet.getAttribute("UID"));
										geraet_triple.addProperty(has_dokument, Dokument);
										
										//Geräte zum späteren hinzufügen zwischenspeichern
										addGeraet(geraet.getAttribute("UID"), geraet.getAttribute("Name"), geraet.getAttribute("Beschreibung"));
								
									}
								}
								//sonst liegt die Information in den Attributen 
								else{
									
									//Erzeuge Triple: Gerät -> has_dokument -> Dokument ... Resource Gerät wird erst später erstellt
									Resource geraet_triple = model.createResource(namepsace + RuI.getAttribute("OwnerUID"));
									geraet_triple.addProperty(has_dokument, Dokument);
									
									addGeraet(RuI.getAttribute("OwnerUID"), RuI.getAttribute("OwnerName"), RuI.getAttribute("OwnerBeschreibung"));
								}
						}
																	
					}
				
				//Metainformationen von den Geräten hinzufügen
				for(Geraet tmp_Geraet : geraete) {
					
					//Geräte Resource erzeugen
					Resource rGeraetResource = model.createResource(namepsace + tmp_Geraet.getUid());
					
					// UID hinzufügen
					rGeraetResource.addLiteral(uid, tmp_Geraet.getUid());
					
					//Name hinzufügen
					rGeraetResource.addLiteral(RDFS.label, tmp_Geraet.getName());
					
					//Beschreibung hinzugefügt
					rGeraetResource.addLiteral(RDFS.comment, tmp_Geraet.getDescription());
					
				}
				
				String fileName = "RDF.rdf";
				FileWriter out;
		        try {
		        	out = new FileWriter( fileName );
		            model.write( out, "RDF/XML" );

		               out.close();
		               System.out.println("Datei Abgelegt");
		           }
		         catch (Exception closeException) {
		        	 System.out.println("Fehler");
		         }
		        
		        fileName = "Turtle.ttf";
		        try {
		        	out = new FileWriter( fileName );
		            model.write( out, "Turtle" );

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
	
	private void addGeraet(String UID, String name, String description){
		
		// Checken ob es schon einmal vorkommt
		for (Geraet tmp_geraet : geraete) 
			if (tmp_geraet.getUid().contains("UID"))
				return;
		
		// wenn nicht, dann Füge Geräte der Liste hinzu
		geraete.add(new Geraet(UID, name, description));
		
	}
	
}
