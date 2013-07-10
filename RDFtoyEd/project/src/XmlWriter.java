import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlWriter {
	private String OutPutPath;
	private Document xmlDoc;
	private static final String nodeIdChar="n";
	private static final String portIdChar="d";
	
	/*
	 * inspired by: http://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/
	 */
	public static void makeOutput(String OutPutPath, RdfFileLoader rdf)
	{
		 try {
			 
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root element
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("graphml");
				doc.appendChild(rootElement);
		 
				// staff elements
				Element staff = doc.createElement("Staff");
				rootElement.appendChild(staff);
		 
				// set attribute to staff element
				Attr attr = doc.createAttribute("id");
				attr.setValue("1");
				staff.setAttributeNode(attr);
		 
				// shorten way
				// staff.setAttribute("id", "1");
		 
				// firstname elements
				Element firstname = doc.createElement("firstname");
				firstname.appendChild(doc.createTextNode("yong"));
				staff.appendChild(firstname);
		 
				// lastname elements
				Element lastname = doc.createElement("lastname");
				lastname.appendChild(doc.createTextNode("mook kim"));
				staff.appendChild(lastname);
		 
				// nickname elements
				Element nickname = doc.createElement("nickname");
				nickname.appendChild(doc.createTextNode("mkyong"));
				staff.appendChild(nickname);
		 
				// salary elements
				Element salary = doc.createElement("salary");
				salary.appendChild(doc.createTextNode("100000"));
				staff.appendChild(salary);
		 
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				
				/*
				 * Start copy
				 * source: http://stackoverflow.com/questions/1384802/java-how-to-indent-xml-generated-by-transformer
				 */
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				//end copy
				
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(OutPutPath));
		 
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
		 
				System.out.println("File saved!");
		 
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
	}
	 

}
