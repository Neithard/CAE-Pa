package RDFtillDoomsday;

import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

public class RDFtillDoomsday {

/**
* @param args
* @throws IOException
*/
public static void main(String[] args) throws IOException {

//Namespace
String ns = "http://www.tu-dresden.de/ifa/LinkData/";
String nspr = "http://www.tu-dresden.de/ifa/LinkData/link/";

        //Model erstellen
        Model model = ModelFactory.createDefaultModel();
        
        //Resourcen erstellen == Konten erzeugen
        
        //Knoten: Nutzer
        Resource tobias
         = model.createResource(ns+ "tobias") // erstellt Knoten
         .addLiteral(VCARD.FN, model.createTypedLiteral("Tobias Steege"))
         .addProperty(VCARD.N, model.createResource()
         .addLiteral(VCARD.NAME, model.createTypedLiteral("Tobias"))	
         .addLiteral(VCARD.Family, model.createTypedLiteral("Steege")))	
         .addLiteral(VCARD.PHOTO, model.createTypedLiteral("C:/istderschön.png"));

        Resource sascha
     = model.createResource(ns + "sascha")
     .addLiteral(VCARD.FN, model.createTypedLiteral("Sascha Steffen"))
     .addProperty(VCARD.N, model.createResource()
         .addLiteral(VCARD.NAME, model.createTypedLiteral("Sascha"))	
         .addLiteral(VCARD.Family, model.createTypedLiteral("Steffen")))	
     .addLiteral(VCARD.PHOTO, model.createTypedLiteral("C:/istderhübsch.png"));
        
        //Konten: Revision
        Resource revision1
         = model.createResource(ns + "revision1")
         .addLiteral(DCTerms.identifier, model.createTypedLiteral(1))
         .addLiteral(RDFS.comment, model.createTypedLiteral("Erste Änderung bzg.."))
         .addProperty(model.createProperty(nspr,"erstellt"), model.createResource()
         .addLiteral(DCTerms.created, model.createTypedLiteral(new GregorianCalendar(2013, 05, 17)))
         .addProperty(DCTerms.creator , tobias))
         .addProperty(model.createProperty(nspr,"geprueft"), model.createResource()
         .addLiteral(DCTerms.created, model.createTypedLiteral(new GregorianCalendar(2013, 05, 18)))
         .addProperty(DCTerms.creator , sascha))	
         .addProperty(model.createProperty(nspr,"freigegeben"), model.createResource()
         .addLiteral(DCTerms.created, model.createTypedLiteral(new GregorianCalendar(2013, 05, 19)))
         .addProperty(DCTerms.creator , tobias));
        //Knoten: Revision
        Resource revision2
         = model.createResource(ns + "revision2")
         .addLiteral(DCTerms.identifier, model.createTypedLiteral(2))
         .addLiteral(RDFS.comment, model.createTypedLiteral("Zweite Änderung bzg.."))
         .addProperty(model.createProperty(nspr,"erstellt"), model.createResource()
         .addLiteral(DCTerms.created, model.createTypedLiteral(new GregorianCalendar(2013, 05, 17)))
         .addProperty(DCTerms.creator , tobias))
         .addProperty(model.createProperty(nspr,"geprueft"), model.createResource()
         .addLiteral(DCTerms.created, model.createTypedLiteral(new GregorianCalendar(2013, 05, 18)))
         .addProperty(DCTerms.creator , sascha))	
         .addProperty(model.createProperty(nspr,"freigegeben"), model.createResource()
         .addLiteral(DCTerms.created, model.createTypedLiteral(new GregorianCalendar(2013, 05, 19)))
         .addProperty(DCTerms.creator , tobias));
        
        //Knoten: Firma
        Resource siemens
         = model.createResource(ns + "siemens")
         .addLiteral(VCARD.N, model.createTypedLiteral("Siemens"))
         .addLiteral(VCARD.PHOTO, model.createTypedLiteral(12345678))
         .addLiteral(VCARD.MAILER, model.createTypedLiteral("siemens@fake.server"));
        
        //Knoten: Dokument
        Resource RuI123
     = model.createResource(ns + "RuI123")
     .addLiteral(model.createProperty(nspr, "PDF"), model.createTypedLiteral("c:/hier.pdf"))
     .addLiteral(model.createProperty(nspr, "Auftragsnummer"), model.createTypedLiteral(1))
     .addLiteral(model.createProperty(nspr, "Projektnummer"), model.createTypedLiteral(1))
     .addLiteral(model.createProperty(nspr, "Type"), model.createTypedLiteral("R&I Fließbild"))
     .addLiteral(model.createProperty(nspr, "Kontakt"), siemens)
     .addProperty(model.createProperty(nspr, "besitzt_Revision"), revision1)
     .addProperty(model.createProperty(nspr, "besitzt_Revision"), revision2);
        
        //Knoten: Gerät
        Resource Teilanlage1
     = model.createResource(ns + "teilanlage1")
     .addProperty(model.createProperty(nspr, "beseitzt_Dokument"), RuI123);
      
        StmtIterator iter = model.listStatements();
            
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();
            
            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }
            System.out.println(" .");
        }
        
        //Dokument Ausgeben
        String fileName = "TURTLE.ttf";
        FileWriter out = new FileWriter( fileName );
        try {
            model.write( out, "TURTLE" );
        }
        finally {
           try {
               out.close();
               System.out.println("Datei Abgelegt");
           }
           catch (IOException closeException) {
         System.out.println("Fehler");
           }
        }
        
        fileName = "RDFXML.rdf";
        out = new FileWriter( fileName );
        try {
            model.write( out, "RDF/XML" );
        }
        finally {
           try {
               out.close();
               System.out.println("Datei Abgelegt");
           }
           catch (IOException closeException) {
         System.out.println("Fehler");
           }
        }

}

}