import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.w3c.dom.Node; => this is not imported for a reason => we also define our own class named Node
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.bcel.internal.classfile.Attribute;

public class XmlWriter {
	private Document doc;
	private static final String nodeIdChar="n";
	DocumentBuilder docBuilder;
	DocumentBuilderFactory docFactory;
	
	public XmlWriter() throws ParserConfigurationException
	{
		docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setValidating(true); 
		docFactory.setNamespaceAware(true); 
		docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.newDocument();
	}
	/*
	 * inspired by: http://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/
	 */
	public void makeOutput(String OutPutPath, RdfFileLoader rdf)
	{
		 try {		 
			 	/*
			 	 * Static Part
			 	 */
				// root element
				Element rootElement = doc.createElement("graphml");
				doc.appendChild(rootElement);
				rootElement.setAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
				rootElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
					    "xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd");
				rootElement.setAttribute("xmlns:y", "http://www.yworks.com/xml/graphml");
				rootElement.setAttribute("xmlns:yed", "http://www.yworks.com/xml/yed/3");
				
				//some keys
				rootElement.appendChild(makeKeyElement( 0, "resources", "graphml"));
				rootElement.appendChild(makeKeyElement( 1, "portgraphics" ,"port"));
				rootElement.appendChild(makeKeyElement( 2, "portgeometry", "port" ));
				rootElement.appendChild(makeKeyElement( 3, "portuserdata", "port"));
				ArrayList<Attr> attrList=new ArrayList<Attr>();
				attrList.add(doc.createAttribute("attr.name"));
				
				attrList.get(0).setNodeValue("url");
				rootElement.appendChild(makeKeyElement( 4, "string", "attr.type", "node", attrList));	
				
				attrList=new ArrayList<Attr>();
				attrList.add(doc.createAttribute("attr.name"));
				attrList.get(0).setNodeValue("description");
				rootElement.appendChild(makeKeyElement( 5, "string", "attr.type", "node", attrList));					
				
				rootElement.appendChild(makeKeyElement( 6, "nodegraphics", "node"));
				
				attrList=new ArrayList<Attr>();
				attrList.add(doc.createAttribute("attr.name"));
				attrList.get(0).setNodeValue("url");
				rootElement.appendChild(makeKeyElement( 7, "string", "attr.type", "edge", attrList));	
				
				attrList=new ArrayList<Attr>();
				attrList.add(doc.createAttribute("attr.name"));
				attrList.get(0).setNodeValue("description");
				rootElement.appendChild(makeKeyElement( 8, "string", "attr.type", "edge", attrList));	
				
				rootElement.appendChild(makeKeyElement( 9, "edgegraphics", "edge"));
				
				
				//the graph element
				Element graph=doc.createElement("graph");
				graph.setAttribute("edgedefault", "directed");
				graph.setAttribute("id", "G");
				rootElement.appendChild(graph);
				
				/*
				 * generated Part
				 */
				//append the document elements
				for(Node n : rdf.getDocuments())
				{
					graph.appendChild(makeDocumentElement(n));
				}
				
				//append the company elements
				for(Node n : rdf.getCompanies())
				{
					graph.appendChild(makeCompanyElement(n));
				}

				//append the equipment elements
				for(Node n : rdf.getEquipment())
				{
					graph.appendChild(makeEquipmentPieceElement(n));
				}
				
				
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
		 
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
	}
	
	private  Element makeKeyElement(int idNum, String type,  String forStr)
	{
		return makeKeyElement(idNum, type, "yfiles.type", forStr, new ArrayList<Attr>());
	}

	private  Element makeKeyElement(int idNum, String type, String typeTString, String forStr, List<Attr> otherAttrs)
	{
		Element keyElement=doc.createElement("key");
		keyElement.setAttribute("for", forStr);
		keyElement.setAttribute("id", "d" + idNum);
		keyElement.setAttribute(typeTString, type);
		for(Attr a : otherAttrs)
		{
			keyElement.setAttributeNode(a);
		}
		return keyElement;
	}
	
	private Element makeCompanyElement(Node compNode)
	{
		Element compElement=makeGenericElement(compNode);
		
		return compElement;
	}
	
	private Element makeEquipmentPieceElement(Node equiNode)
	{
		Element equElement=makeGenericElement(equiNode);
		
		return equElement;
	}
	
	private Element makeDocumentElement(Node docNode)
	{
		Element docElement=makeGenericElement(docNode);
		
		return docElement;
	}
	
	private Element makeNodeLabel(String labelText)
	{
		Element label=doc.createElement("y:NodeLabel");
		label.setAttribute("alignment", "center");
		label.setAttribute("autoSizePolicy", "content");
		label.setAttribute("fontFamily", "Dialog");
		label.setAttribute("fontSize", "12");
		label.setAttribute("fontStyle", "plain");
		label.setAttribute("hasBackgroundColor", "false");
		label.setAttribute("hasLineColor", "false");
		label.setAttribute("modelName", "custom");
		label.setAttribute("textColor", "#000000");
		label.setAttribute("visible", "true");
		label.setTextContent(labelText);
		
		return label;
	}
	  
	private Element makeGenericElement(Node node)
	{
		Element e=doc.createElement("node");
		e.setAttribute("id", nodeIdChar + node.getId());
		
		Element dataElement=doc.createElement("data");
		dataElement.setAttribute("key", "d6");
		e.appendChild(dataElement);
		
		Element genNodeElement=doc.createElement("y:GenericNode");
		genNodeElement.setAttribute("configuration", "BevelNodeWithShadow");
		dataElement.appendChild(genNodeElement);
		
		Element fillElement=doc.createElement("y:Fill");
		fillElement.setAttribute("color", "#FF9900");
		fillElement.setAttribute("transparent", "false");
		genNodeElement.appendChild(fillElement);
		
		Element borderStyleElement=doc.createElement("y:BorderStyle");
		borderStyleElement.setAttribute("hasColor", "false");
		borderStyleElement.setAttribute("type", "line");
		borderStyleElement.setAttribute("width", "1.0");
		genNodeElement.appendChild(borderStyleElement);
		
		genNodeElement.appendChild(makeNodeLabel(node.getName()));
		
		return e;
	}
}
