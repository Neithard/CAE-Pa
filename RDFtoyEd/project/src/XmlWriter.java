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
				
				//append the person elements
				for(Node n : rdf.getPersons())
				{
					graph.appendChild(makePersonElement(n));
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
	
	private Element makePersonElement(Node personNode)
	{
		Element personElement=doc.createElement("node");
		Element k1=doc.createElement("data");
		k1.setAttribute("key", "d6");
		personElement.appendChild(k1);
		
		Element k2= doc.createElement("data");
		k2.setAttribute("key", "d6");
		
		Element svg=doc.createElement("y:SVGNode");
		k2.appendChild(svg);
		Element geom=doc.createElement("y:Geometry");
		geom.setAttribute("x", "0.0");
		geom.setAttribute("y", "0.0");
		geom.setAttribute("width", "0.0");
		geom.setAttribute("height", "0.0");		
		
		return personElement;
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
	
	private String personNodeSvg="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> " +
			"<!-- Created with Inkscape (http://www.inkscape.org/) --> <svg xmlns:dc=\"http://purl.org/dc/elements/1.1/\"" +
			" xmlns:cc=\"http://creativecommons.org/ns#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" +
			" xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:sodipodi=\"http://" +
			"sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\" xmlns:inkscape=\"http://www.inkscape.org/namespaces/inksc" +
			"ape\" width=\"19.78125\" height=\"49.865517\" id=\"svg2\" version=\"1.1\" inkscape:version=\"0.48.4 r99" +
			"39\" sodipodi:docname=\"drawing.svg,.svg\"> <defs id=\"defs4\" /> <sodipodi:namedview id=\"base\" pagec" +
			"olor=\"#ffffff\" bordercolor=\"#666666\" borderopacity=\"1.0\" inkscape:pageopacity=\"0.0\" inkscape:pa" +
			"geshadow=\"2\" inkscape:zoom=\"1.4\" inkscape:cx=\"-53.278929\" inkscape:cy=\"170.01595\" inkscape:docu" +
			"ment-units=\"px\" inkscape:current-layer=\"layer1\" showgrid=\"false\" fit-margin-top=\"0\" fit-margin-" +
			"left=\"0\" fit-margin-right=\"0\" fit-margin-bottom=\"0\" inkscape:window-width=\"1920\" inkscape:window-" +
			"height=\"1025\" inkscape:window-x=\"-2\" inkscape:window-y=\"-3\" inkscape:window-maximized=\"1\" /> <met" +
			"adata id=\"metadata7\"> <rdf:RDF> <cc:Work rdf:about=\"\"> <dc:format>image/svg+xml</dc:format> <dc:type " +
			"rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" /> <dc:title></dc:title> </cc:Work> </rdf:RDF> </" +
			"metadata> <g inkscape:label=\"Layer 1\" inkscape:groupmode=\"layer\" id=\"layer1\" transform=\"translate(" +
			"-141.15625,-457.54073)\"> <path sodipodi:type=\"arc\" style=\"fill:#000000;fill-opacity:1;stroke:none\" i" +
			"d=\"path2989\" sodipodi:cx=\"247.14285\" sodipodi:cy=\"213.43361\" sodipodi:rx=\"70\" sodipodi:ry=\"69.64" +
			"286\" d=\"m 317.14285,213.43361 c 0,38.46269 -31.34006,69.64286 -70,69.64286 -38.65993,0 -70,-31.18017 -7" +
			"0,-69.64286 0,-38.46269 31.34007,-69.64286 70,-69.64286 38.65994,0 70,31.18017 70,69.64286 z\" transform=" +
			"\"matrix(0.13745706,0,0,0.13745706,117.28401,437.77568)\" /> <path style=\"fill:none;stroke:#000000;strok" +
			"e-width:2.06185627;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-" +
			"dasharray:none\" d=\"m 151.25555,474.13376 -0.14734,26.70594\" id=\"path3783\" inkscape:connector-curvatu" +
			"re=\"0\" /> <path style=\"fill:none;stroke:#000000;stroke-width:2.06185627;stroke-linecap:butt;stroke-lin" +
			"ejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" d=\"m 151.20643,500.49606 -9.523" +
			"8,5.74375\" id=\"path3785\" inkscape:connector-curvature=\"0\" /> <path style=\"fill:none;stroke:#000000;" +
			"stroke-width:2.06185627;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;st" +
			"roke-dasharray:none\" d=\"m 150.86279,500.76606 9.52381,5.74375\" id=\"path3785-6\" inkscape:connector-cu" +
			"rvature=\"0\" inkscape:transform-center-x=\"-1.7182004\" /> <path style=\"fill:none;stroke:#000000;stroke" +
			"-width:2.06185627;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-" +
			"dasharray:none\" d=\"m 160.92662,485.42488 -19.53854,0\" id=\"path3805\" inkscape:connector-curvature=\"" +
			"0\" /> </g> </svg>";
}
