package transformation;
import java.io.File;
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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlWriter {
	private Document doc;
	private static final String nodeIdChar="n";
	private static final String edgeIdChar="e";
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
				
				//svg data
				Element data=doc.createElement("data");
				data.setAttribute("key", "d0");
				rootElement.appendChild(data);
				
				Element resources=doc.createElement("y:Resources");
				data.appendChild(resources);
				
				Element svgEl1=doc.createElement("y:Resource");
				svgEl1.setAttribute("id", "1");
				svgEl1.setTextContent(personNodeSvg);
				resources.appendChild(svgEl1);
				
				Element svgEl2=doc.createElement("y:Resource");
				svgEl2.setAttribute("id", "2");
				svgEl2.setTextContent(mailNodeSvg);
				resources.appendChild(svgEl2);
				
				Element svgEl3=doc.createElement("y:Resource");
				svgEl3.setAttribute("id", "3");
				svgEl3.setTextContent(phoneNodeSvg);
				resources.appendChild(svgEl3);
				
				Element svgEl4=doc.createElement("y:Resource");
				svgEl4.setAttribute("id", "4");
				svgEl4.setTextContent(pdfNodeSvg);
				resources.appendChild(svgEl4);
				
				/*
				 * generated Part
				 */
				//append the document elements
				for(Node n : rdf.getDocuments())
				{
					graph.appendChild(makeDocumentElement(n));
					makeEdges(graph, n);
				}
				
				//append the company elements
				for(Node n : rdf.getCompanies())
				{
					graph.appendChild(makeCompanyElement(n));
					makeEdges(graph, n);					
				}

				//append the equipment elements
				for(Node n : rdf.getEquipment())
				{
					graph.appendChild(makeEquipmentPieceElement(n));
					makeEdges(graph, n);					
				}
				
				//append the person elements
				for(Node n : rdf.getPersons())
				{
					graph.appendChild(makePersonElement(n));
					makeEdges(graph, n);					
				}
				
				//append the mail elements
				for(Node n : rdf.getMail())
				{
					graph.appendChild(makeMailelement(n));
					makeEdges(graph, n);					
				}
				
				//append the phone elements
				for(Node n : rdf.getPhones())
				{
					graph.appendChild(makePhoneElement(n));
					makeEdges(graph, n);					
				}
				//append empty nodes
				for(Node n : rdf.getEmpty())
				{
					graph.appendChild(makeEmptyElement(n));
					makeEdges(graph, n);					
				}
				//append rest
				for(Node n : rdf.getOther())
				{
					graph.appendChild(makeGenericElement(n, "#FFFF99"));
					makeEdges(graph, n);					
				}
				//append pdfs
				for(Node n : rdf.getPdfs())
				{
					graph.appendChild(makePdfElement(n));
					makeEdges(graph, n);
				}
				//append revs
				for(Node n : rdf.getRevisions())
				{
					graph.appendChild(makeRevElement(n));
					makeEdges(graph, n);
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
	
	private void makeEdges(Element element, Node n)
	{
		for(Edge e : n.getEdges())
		{
			element.appendChild(makeEdge(n, e));
		}
	}
	
	private Element makeEdge(Node start, Edge e)
	{
		Element edgeNode=doc.createElement("edge");
		edgeNode.setAttribute("id", edgeIdChar + e.getId());
		edgeNode.setAttribute("source", nodeIdChar + start.getId());
		edgeNode.setAttribute("target", nodeIdChar + e.getTarget().getId());
		
		Element key=doc.createElement("data");
		key.setAttribute("key", "d9");
		edgeNode.appendChild(key);
		
		Element label=doc.createElement("y:EdgeLabel");
		Element arc=doc.createElement("y:BezierEdge");
		key.appendChild(arc);
		arc.appendChild(label);
		label.setTextContent(e.getName());
		
		Element arrows=doc.createElement("y:Arrows");
		arrows.setAttribute("source", "none");
		arrows.setAttribute("target", "delta");
		arc.appendChild(arrows);
		
		
		return edgeNode;
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
		Element compElement=makeGenericElement(compNode, "#FF9900");
		
		return compElement;
	}
	
	private Element makeRevElement(Node compNode)
	{
		Element revElement=makeGenericElement(compNode, "#FF99cc");
		
		return revElement;
	}
	
	private Element makeEmptyElement(Node node)
	{
		Element empty=doc.createElement("node");
		empty.setAttribute("id", nodeIdChar + node.getId());
		
		Element data=doc.createElement("data");
		data.setAttribute("key", "d6");
		empty.appendChild(data);
		
		Element shN=doc.createElement("y:ShapeNode");
		data.appendChild(shN);
		
		Element fill=doc.createElement("y:Fill");
		fill.setAttribute("color", "#c0c0c0");
		fill.setAttribute("transparent", "false");
		shN.appendChild(fill);
		
		
		Element borderStyleElement=doc.createElement("y:BorderStyle");
		borderStyleElement.setAttribute("color", "#000000");
		borderStyleElement.setAttribute("type", "line");
		borderStyleElement.setAttribute("width", "1.0");
		shN.appendChild(borderStyleElement);
		
		Element label=doc.createElement("y:NodeLabel");
		shN.appendChild(label);
		
		Element shape=doc.createElement("y:Shape");
		shape.setAttribute("type", "ellipse");
		shN.appendChild(shape);
		
		
		return empty;
	}
	
	private Element makeSvgNode(double wdith, double height, Node node, int svgId)
	{
		Element svgNodeElement=doc.createElement("node");
		svgNodeElement.setAttribute("id", nodeIdChar + node.getId() );
		Element k1=doc.createElement("data");
		k1.setAttribute("key", "d6");
		svgNodeElement.appendChild(k1);
		
		Element svg=doc.createElement("y:SVGNode");
		k1.appendChild(svg);
		Element geom=doc.createElement("y:Geometry");
		geom.setAttribute("x", "0.0");
		geom.setAttribute("y", "0.0");
		geom.setAttribute("width", String.valueOf(wdith));
		geom.setAttribute("height", String.valueOf(height));
		svg.appendChild(geom);
		
		Element fill=doc.createElement("y:Fill");
		fill.setAttribute("color", "#CCCFF");
		fill.setAttribute("transparent", "false");
		svg.appendChild(fill);
		
		
		Element borderStyleElement=doc.createElement("y:BorderStyle");
		borderStyleElement.setAttribute("color", "#000000");
		borderStyleElement.setAttribute("type", "line");
		borderStyleElement.setAttribute("width", "1.0");
		svg.appendChild(borderStyleElement);
		
		svg.appendChild(makeNodeLabel(node.getName()));
		
		Element svgProp=doc.createElement("y:SVGNodeProperties");
		svgProp.setAttribute("usingVisualBounds", "true");
		svg.appendChild(svgProp);
		
		Element svgModel=doc.createElement("y:SVGModel");
		svgModel.setAttribute("svgBoundsPolicy", "0");
		svg.appendChild(svgModel);
		
		Element svgContent=doc.createElement("y:SVGContent");
		svgContent.setAttribute("refid", String.valueOf(svgId));
		svgModel.appendChild(svgContent);
		
		return svgNodeElement;
	}
	
	private Element makePersonElement(Node personNode)
	{
		Element personElement=makeSvgNode(19.0, 49.0, personNode, 1);
		
		return personElement;
	}
	
	private Element makeMailelement(Node mailNode)
	{
		Element mailElement=makeSvgNode(49.0, 28.0, mailNode, 2);
		
		return mailElement;
	}
	
	private Element makePdfElement(Node pdfNode)
	{
		String uri=pdfNode.getName();
		//pdfNode.setName(""); //because otherwise mkaSvgNode would show the file URI
		Element pdfElement=makeSvgNode(48.0, 48.0, pdfNode, 4);
		Element key1=doc.createElement("data");
		key1.setAttribute("key", "d4");
		key1.setTextContent(uri);
		pdfElement.appendChild(key1);
		
		return pdfElement;
	}
	
	private Element makeEquipmentPieceElement(Node equiNode)
	{
		Element equElement=makeGenericElement(equiNode,"#008000");
		
		return equElement;
	}
	
	private Element makePhoneElement(Node phoneNode)
	{
		Element phElement=makeSvgNode(50, 35.8, phoneNode, 3);
		
		return phElement;
	}
	
	private Element makeDocumentElement(Node docNode)
	{
		Element docElement=makeGenericElement(docNode, "#00ffff");
		
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
	  
	private Element makeGenericElement(Node node, String color)
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
		fillElement.setAttribute("color", color);
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
	
	private static final String pdfNodeSvg="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> <!-- Created wit" +
			"h Inkscape (http://www.inkscape.org/) --> <svg xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:cc=\"http:" +
			"//web.resource.org/cc/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:svg=\"http://www.w3." +
			"org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:sodipo" +
			"di=\"http://inkscape.sourceforge.net/DTD/sodipodi-0.dtd\" xmlns:inkscape=\"http://www.inkscape.org/namespaces" +
			"/inkscape\" inkscape:export-ydpi=\"240.00000\" inkscape:export-xdpi=\"240.00000\" inkscape:export-filename=\"/h" +
			"ome/jimmac/gfx/novell/pdes/trunk/docs/BIGmime-text.png\" sodipodi:docname=\"x-office-document.svg\" sodipodi:d" +
			"ocbase=\"/home/tigert/cvs/freedesktop.org/tango-icon-theme/scalable/mimetypes\" inkscape:version=\"0.43+devel\" sodi" +
			"podi:version=\"0.32\" id=\"svg249\" height=\"48.000000px\" width=\"48.000000px\" inkscape:output_extension=\"o" +
			"rg.inkscape.output.svg.inkscape\"> <defs id=\"defs3\"> <linearGradient id=\"linearGradient7648\" gradientUn" +
			"its=\"userSpaceOnUse\" x1=\"21.9326\" y1=\"24.6274\" x2=\"21.9326\" y2=\"7.1091\" style=\"stroke-dasharray:no" +
			"ne;stroke-miterlimit:4.0000000;stroke-width:1.2166667\"> <stop offset=\"0\" style=\"stop-color:#8595bc;stop-op" +
			"acity:1;\" id=\"stop7650\" /> <stop offset=\"1\" style=\"stop-color:#041a3b;stop-opacity:1;\" id=\"stop7652\" /> </" +
			"linearGradient> <linearGradient inkscape:collect=\"always\" xlink:href=\"#linearGradient7648\" id=\"linearGradient7671\" g" +
			"radientUnits=\"userSpaceOnUse\" gradientTransform=\"matrix(1.098989,0,0,-0.797757,-1.953865,37.324)\" x1=\"21.9326\" y1=\"2" +
			"4.627399\" x2=\"21.9326\" y2=\"7.1090999\" /> <linearGradient inkscape:collect=\"always\" id=\"linearGradient4542\"> <stop" +
			" style=\"stop-color:#000000;stop-opacity:1;\" offset=\"0\" id=\"stop4544\" /> <stop style=\"stop-color:#0000" +
			"00;stop-opacity:0;\" offset=\"1\" id=\"stop4546\" /> </linearGradient> <linearGradient id=\"linearGradient156" +
			"62\"> <stop id=\"stop15664\" offset=\"0.0000000\" style=\"stop-color:#ffffff;stop-opacity:1.0000000;\" /> <stop " +
			"id=\"stop15666\" offset=\"1.0000000\" style=\"stop-color:#f8f8f8;stop-opacity:1.0000000;\" /> </linearGradient" +
			"> <radialGradient id=\"aigrd3\" cx=\"20.8921\" cy=\"64.5679\" r=\"5.257\" fx=\"20.8921\" fy=\"64.5679\" gradien" +
			"tUnits=\"userSpaceOnUse\"> <stop offset=\"0\" style=\"stop-color:#F0F0F0\" id=\"stop15573\" /> <stop offset=\"1" +
			".0000000\" style=\"stop-color:#9a9a9a;stop-opacity:1.0000000;\" id=\"stop15575\" /> </radialGradient> <radialGr" +
			"adient id=\"aigrd2\" cx=\"20.8921\" cy=\"114.5684\" r=\"5.256\" fx=\"20.8921\" fy=\"114.5684\" gradientUnits=\"" +
			"userSpaceOnUse\"> <stop offset=\"0\" style=\"stop-color:#F0F0F0\" id=\"stop15566\" /> <stop offset=\"1.0000000\"" +
			" style=\"stop-color:#9a9a9a;stop-opacity:1.0000000;\" id=\"stop15568\" /> </radialGradient> <linearGradient" +
			" id=\"linearGradient269\"> <stop id=\"stop270\" offset=\"0.0000000\" style=\"stop-color:#a3a3a3;stop-opacity:1.00" +
			"00000;\" /> <stop id=\"stop271\" offset=\"1.0000000\" style=\"stop-color:#4c4c4c;stop-opacity:1.0000000;\" " +
			"/> </linearGradient> <linearGradient id=\"linearGradient259\"> <stop id=\"stop260\" offset=\"0.0000000\" styl" +
			"e=\"stop-color:#fafafa;stop-opacity:1.0000000;\" /> <stop id=\"stop261\" offset=\"1.0000000\" style=\"stop-co" +
			"lor:#bbbbbb;stop-opacity:1.0000000;\" /> </linearGradient> <radialGradient inkscape:collect=\"always\" xlink:" +
			"href=\"#linearGradient269\" id=\"radialGradient15656\" gradientUnits=\"userSpaceOnUse\" gradientTransform=\"m" +
			"atrix(0.968273,0.000000,0.000000,1.032767,3.353553,0.646447)\" cx=\"8.8244190\" cy=\"3.7561285\" fx=\"8.82441" +
			"90\" fy=\"3.7561285\" r=\"37.751713\" /> <radialGradient inkscape:collect=\"always\" xlink:href=\"#linearGrad" +
			"ient259\" id=\"radialGradient15658\" gradientUnits=\"userSpaceOnUse\" gradientTransform=\"scale(0.960493,1.04" +
			"1132)\" cx=\"33.966679\" cy=\"35.736916\" fx=\"33.966679\" fy=\"35.736916\" r=\"86.708450\" /> <radialGradien" +
			"t inkscape:collect=\"always\" xlink:href=\"#linearGradient15662\" id=\"radialGradient15668\" gradientUnits=\"" +
			"userSpaceOnUse\" gradientTransform=\"matrix(0.968273,0.000000,0.000000,1.032767,3.353553,0.646447)\" cx=\"8.1" +
			"435566\" cy=\"7.2678967\" fx=\"8.1435566\" fy=\"7.2678967\" r=\"38.158695\" /> <radialGradient r=\"5.256\" fy" +
			"=\"114.5684\" fx=\"20.8921\" cy=\"114.5684\" cx=\"20.8921\" gradientTransform=\"matrix(0.229703,0.000000,0.00" +
			"0000,0.229703,4.613529,3.979808)\" gradientUnits=\"userSpaceOnUse\" id=\"radialGradient2283\" xlink:href=\"#a" +
			"igrd2\" inkscape:collect=\"always\" /> <radialGradient r=\"5.257\" fy=\"64.5679\" fx=\"20.8921\" cy=\"64.5679" +
			"\" cx=\"20.8921\" gradientTransform=\"matrix(0.229703,0.000000,0.000000,0.229703,4.613529,3.979808)\" gradien" +
			"tUnits=\"userSpaceOnUse\" id=\"radialGradient2285\" xlink:href=\"#aigrd3\" inkscape:collect=\"always\" /> <radialG" +
			"radient inkscape:collect=\"always\" xlink:href=\"#linearGradient4542\" id=\"radialGradient4548\" cx=\"24.306795\" cy=" +
			"\"42.07798\" fx=\"24.306795\" fy=\"42.07798\" r=\"15.821514\" gradientTransform=\"matrix(1.000000,0.000000,0.000000,0.284" +
			"916,0.000000,30.08928)\" gradientUnits=\"userSpaceOnUse\" /> </defs> <sodipodi:namedview inkscape:window-y=\"0\" inkscape" +
			":window-x=\"70\" inkscape:window-height=\"791\" inkscape:window-width=\"1018\" inkscape:document-units=\"px\" inkscape:gr" +
			"id-bbox=\"true\" showgrid=\"false\" inkscape:current-layer=\"layer5\" inkscape:cy=\"20.221465\" inkscape:cx=\"21.627871\"" +
			" inkscape:zoom=\"1\" inkscape:pageshadow=\"2\" inkscape:pageopacity=\"0.0\" borderopacity=\"0.25490196\" bordercolor=\"#6" +
			"66666\" pagecolor=\"#ffffff\" id=\"base\" inkscape:showpageshadow=\"false\" fill=\"#729fcf\" /> <metadata id=\"metadata4\"" +
			"> <rdf:RDF> <cc:Work rdf:about=\"\"> <dc:format>image/svg+xml</dc:format> <dc:type rdf:resource=\"http://purl.org/dc/dcm" +
			"itype/StillImage\" /> <dc:title>Office Document</dc:title> <dc:subject> <rdf:Bag> <rdf:li>rich</rdf:li> <rdf:li>text</rd" +
			"f:li> <rdf:li>document</rdf:li> <rdf:li>pdf</rdf:li> <rdf:li>openoffice</rdf:li> <rdf:li>word</rdf:li> <rdf:li>rtf</rdf:" +
			"li> </rdf:Bag> </dc:subject> <cc:license rdf:resource=\"http://creativecommons.org/licenses/by-sa/2.0/\" /> <dc:creator>" +
			" <cc:Agent> <dc:title>Jakub Steiner</dc:title> </cc:Agent> </dc:creator> <dc:source>http://jimmac.musichall.cz</dc:sourc" +
			"e> </cc:Work> <cc:License rdf:about=\"http://creativecommons.org/licenses/by/2.0/\"> <cc:permits rdf:resource=\"http://w" +
			"eb.resource.org/cc/Reproduction\" /> <cc:permits rdf:resource=\"http://web.resource.org/cc/Distribution\" /> <cc:require" +
			"s rdf:resource=\"http://web.resource.org/cc/Notice\" /> <cc:requires rdf:resource=\"http://web.resource.org/cc/Attributi" +
			"on\" /> <cc:permits rdf:resource=\"http://web.resource.org/cc/DerivativeWorks\" /> <cc:requires rdf:resource=\"http://we" +
			"b.resource.org/cc/ShareAlike\" /> </cc:License> </rdf:RDF> </metadata> <g inkscape:groupmode=\"layer\" id=\"layer6\" ink" +
			"scape:label=\"Shadow\"> <path sodipodi:type=\"arc\" style=\"opacity:0.7836257;color:#000000;fill:url(#radialGradient4548" +
			");fill-opacity:1;fill-rule:evenodd;stroke:none;stroke-width:2;stroke-linecap:round;stroke-linejoin:round;marker:none;mar" +
			"ker-start:none;marker-mid:none;marker-end:none;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opa" +
			"city:1;visibility:visible;display:inline;overflow:visible\" id=\"path3667\" sodipodi:cx=\"24.306795\" sodipodi:cy=\"42." +
			"07798\" sodipodi:rx=\"15.821514\" sodipodi:ry=\"4.5078058\" d=\"M 40.128309 42.07798 A 15.821514 4.5078058 0 1 1  8.485" +
			"281,42.07798 A 15.821514 4.5078058 0 1 1  40.128309 42.07798 z\" transform=\"translate(0.000000,0.707108)\" /> </g> <g " +
			"style=\"display:inline\" inkscape:groupmode=\"layer\" inkscape:label=\"Base\" id=\"layer1\"> <rect style=\"color:#00000" +
			"0;fill:url(#radialGradient15658);fill-opacity:1.0000000;fill-rule:nonzero;stroke:url(#radialGradient15656);stroke-width" +
			":1.0000000;stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:4.0000000;stroke-dashoffset:0.0000000;stroke-op" +
			"acity:1.0000000;marker:none;marker-start:none;marker-mid:none;marker-end:none;visibility:visible;display:block;overflow" +
			":visible\" id=\"rect15391\" width=\"34.875000\" height=\"40.920494\" x=\"6.6035528\" y=\"3.6464462\" ry=\"1.1490486\" " +
			"/> <rect style=\"color:#000000;fill:none;fill-opacity:1.0000000;fill-rule:nonzero;stroke:url(#radialGradient15668);str" +
			"oke-width:1.0000000;stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:4.0000000;stroke-dashoffset:0.0000000" +
			";stroke-opacity:1.0000000;marker:none;marker-start:none;marker-mid:none;marker-end:none;visibility:visible;display:blo" +
			"ck;overflow:visible\" id=\"rect15660\" width=\"32.775887\" height=\"38.946384\" x=\"7.6660538\" y=\"4.5839462\" ry=\"0" +
			".14904857\" rx=\"0.14904857\" /> <g transform=\"translate(0.646447,-3.798933e-2)\" id=\"g2270\"> <g id=\"g1440\" style" +
			"=\"fill:#ffffff;fill-opacity:1.0000000;fill-rule:nonzero;stroke:#000000;stroke-miterlimit:4.0000000\" transform=\"matr" +
			"ix(0.229703,0.000000,0.000000,0.229703,4.967081,4.244972)\"> <radialGradient id=\"radialGradient1442\" cx=\"20.892" +
			"099\" cy=\"114.56840\" r=\"5.2560000\" fx=\"20.892099\" fy=\"114.56840\" gradientUnits=\"userSpaceOnUse\"> <stop offset=\"0" +
			"\" style=\"stop-color:#F0F0F0\" id=\"stop1444\" /> <stop offset=\"1\" style=\"stop-color:#474747\" id=\"stop1446\" /> </rad" +
			"ialGradient> <path style=\"stroke:none\" d=\"M 23.428000,113.07000 C 23.428000,115.04300 21.828000,116.64200 19.855000,11" +
			"6.64200 C 17.881000,116.64200 16.282000,115.04200 16.282000,113.07000 C 16.282000,111.09600 17.882000,109.49700 19.855000" +
			",109.49700 C 21.828000,109.49700 23.428000,111.09700 23.428000,113.07000 z \" id=\"path1448\" /> <radialGradient id=\"r" +
			"adialGradient1450\" cx=\"20.892099\" cy=\"64.567902\" r=\"5.2570000\" fx=\"20.892099\" fy=\"64.567902\" gradientUnits" +
			"=\"userSpaceOnUse\"> <stop offset=\"0\" style=\"stop-color:#F0F0F0\" id=\"stop1452\" /> <stop offset=\"1\" style=\"st" +
			"op-color:#474747\" id=\"stop1454\" /> </radialGradient> <path style=\"stroke:none\" d=\"M 23.428000,63.070000 C 23.42" +
			"8000,65.043000 21.828000,66.643000 19.855000,66.643000 C 17.881000,66.643000 16.282000,65.043000 16.282000,63.070000 " +
			"C 16.282000,61.096000 17.882000,59.497000 19.855000,59.497000 C 21.828000,59.497000 23.428000,61.097000 23.428000,63." +
			"070000 z \" id=\"path1456\" /> </g> <path style=\"fill:url(#radialGradient2283);fill-rule:nonzero;stroke:none;stroke-" +
			"miterlimit:4.0000000\" d=\"M 9.9950109,29.952326 C 9.9950109,30.405530 9.6274861,30.772825 9.1742821,30.772825 C 8.72" +
			"08483,30.772825 8.3535532,30.405301 8.3535532,29.952326 C 8.3535532,29.498892 8.7210780,29.131597 9.1742821,29.131597" +
			" C 9.6274861,29.131597 9.9950109,29.499122 9.9950109,29.952326 z \" id=\"path15570\" /> <path style=\"fill:url(#radia" +
			"lGradient2285);fill-rule:nonzero;stroke:none;stroke-miterlimit:4.0000000\" d=\"M 9.9950109,18.467176 C 9.9950109,18.9" +
			"20380 9.6274861,19.287905 9.1742821,19.287905 C 8.7208483,19.287905 8.3535532,18.920380 8.3535532,18.467176 C 8.3535532,18." +
			"013742 8.7210780,17.646447 9.1742821,17.646447 C 9.6274861,17.646447 9.9950109,18.013972 9.9950109,18.467176 z \" id=\"pa" +
			"th15577\" /> </g> <path style=\"fill:none;fill-opacity:0.75000000;fill-rule:evenodd;stroke:#000000;stroke-width:0.98855311;" +
			"stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4.0000000;stroke-opacity:0.017543854\" d=\"M 11.505723,5.494276" +
			"6 L 11.505723,43.400869\" id=\"path15672\" sodipodi:nodetypes=\"cc\" /> <path style=\"fill:none;fill-opacity:0.75000000;fil" +
			"l-rule:evenodd;stroke:#ffffff;stroke-width:1.0000000;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4.0000000;" +
			"stroke-opacity:0.20467831\" d=\"M 12.500000,5.0205154 L 12.500000,43.038228\" id=\"path15674\" sodipodi:nodetypes=\"cc\" />" +
			" </g> <g inkscape:groupmode=\"layer\" id=\"layer5\" inkscape:label=\"Text\" style=\"display:inline\"> <rect ry=\"0.06539087" +
			"7\" rx=\"0.13778631\" y=\"31\" x=\"15.999986\" height=\"1\" width=\"20.000006\" id=\"rect15738\" style=\"color:#000000;fill:#9b9" +
			"b9b;fill-opacity:0.54970757;fill-rule:nonzero;stroke:none;stroke-width:1;stroke-linecap:round;stroke-linejoin:round;marker:none;mark" +
			"er-start:none;marker-mid:none;marker-end:none;stroke-miterlimit:4;stroke-dashoffset:0;stroke-opacity:0.08187136;" +
			"visibility:visible;display:block;overflow:visible\" /> <rect ry=\"0.065390877\" rx=\"0.13778631\" y=\"33\" x=\"15.9999" +
			"86\" height=\"1\" width=\"20.000006\" id=\"rect15740\" style=\"color:#000000;fill:#9b9b9b;fill-opacity:0.54970757;fill-rul" +
			"e:nonzero;stroke:none;stroke-width:1;stroke-linecap:round;stroke-linejoin:round;marker:none;marker-start:none;marker-mid:" +
			"none;marker-end:none;stroke-miterlimit:4;stroke-dashoffset:0;stroke-opacity:0.08187136;visibility:visible;display:block" +
			";overflow:visible\" /> <rect ry=\"0.065390877\" rx=\"0.13778631\" y=\"35\" x=\"15.999986\" height=\"1\" width=\"20.000006\"" +
			" id=\"rect15742\" style=\"color:#000000;fill:#9b9b9b;fill-opacity:0.54970757;fill-rule:nonzero;stroke:none;stroke-width:1;s" +
			"troke-linecap:round;stroke-linejoin:round;marker:none;marker-start:none;marker-mid:none;marker-end:none;stroke-miterlimit:" +
			"4;stroke-dashoffset:0;stroke-opacity:0.08187136;visibility:visible;display:block;overflow:visible\" /> <rect ry=\"0.06539" +
			"0877\" rx=\"0.096450485\" y=\"37\" x=\"15.999986\" height=\"1\" width=\"14.000014\" id=\"rect15744\" style=\"color:#000000;" +
			"fill:#9b9b9b;fill-opacity:0.54970757;fill-rule:nonzero;stroke:none;stroke-width:1;stroke-linecap:round;stroke-linejoin:roun" +
			"d;marker:none;marker-start:none;marker-mid:none;marker-end:none;stroke-miterlimit:4;stroke-dashoffset:0;stroke-opacity:0.0" +
			"8187136;visibility:visible;display:block;overflow:visible\" /> <g id=\"g5376\" style=\"opacity:0.66477273\"> <g trans" +
			"form=\"matrix(0.608982,0,0,0.606219,12.8233,10.5572)\" id=\"g7654\"> <path style=\"fill:url(#linearGradient7671);fill" +
			"-opacity:1;fill-rule:nonzero;stroke:none;stroke-width:1.2166667;stroke-miterlimit:4\" d=\"M 5.512695,30 L 39.643234,30 L 39.643234,19.627375 L 5.512695,19" +
			".627375 L 5.512695,30 z \" id=\"path7644\" /> <path id=\"path16203\" d=\"M 5.512695,5.6791358 L 39.643234,5.679135" +
			"8 L 39.643234,19.627375 L 5.512695,19.627375 L 5.512695,5.6791358 z \" style=\"fill:#729fcf;fill-rule:nonzero;strok" +
			"e:none;stroke-width:1.2166667;stroke-miterlimit:4;fill-opacity:1\" /> <g transform=\"matrix(1.189217,0,0,1.189217,-" +
			"3.525355,-6.535408)\" style=\"fill-rule:nonzero;stroke:#000000;stroke-width:2.00241709;stroke-miterlimit:4\" id=\"g1" +
			"6205\"> <g id=\"g16207\"> <path id=\"path16209\" d=\"M 18.4,15.4 C 18.4,17.6 16.6,19.5 14.3,19.5 C 12.1,19.5 10.2,17" +
			".7 10.2,15.4 C 10.2,13.2 12,11.3 14.3,11.3 C 16.5,11.3 18.4,13.1 18.4,15.4 z \" style=\"opacity:0.04999994;fill:#e8f52f;st" +
			"roke:none\" /> <path id=\"path16211\" d=\"M 18,15.4 C 18,17.4 16.4,19.1 14.3,19.1 C 12.3,19.1 10.6,17.5 10.6,15.4 C 10.6,13" +
			".4 12.2,11.7 14.3,11.7 C 16.3,11.7 18,13.3 18,15.4 L 18,15.4 z \" style=\"opacity:0.20829994;fill:#ecf751;stroke:none\" /> " +
			"<path id=\"path16213\" d=\"M 17.6,15.4 C 17.6,17.2 16.1,18.7 14.3,18.7 C 12.5,18.7 11,17.2 11,15.4 C 11,13.6 12.5,12.1 14.3," +
			"12.1 C 16.1,12.1 17.6,13.6 17.6,15.4 L 17.6,15.4 z \" style=\"opacity:0.36669994;fill:#f0f972;stroke:none\" /> <path id=\"p" +
			"ath16215\" d=\"M 17.2,15.4 C 17.2,17 15.9,18.3 14.3,18.3 C 12.7,18.3 11.4,17 11.4,15.4 C 11.4,13.8 12.7,12.5 14.3,12.5 C 1" +
			"5.9,12.5 17.2,13.8 17.2,15.4 z \" style=\"opacity:0.525;fill:#f4fa95;stroke:none\" /> <path id=\"path16217\" d=\"M 16.8,15" +
			".4 C 16.8,16.8 15.7,17.9 14.3,17.9 C 12.9,17.9 11.8,16.8 11." +
			"8,15.4 C 11.8,14 12.9,12.9 14.3,12.9 C 15.7,12.9 16.8,14 16.8,15.4 L 16.8,15.4 z \" style=\"opacity:0.6833;fill:#f7fcb7;st" +
			"roke:none\" /> <path id=\"path16219\" d=\"M 16.4,15.4 C 16.4,16.6 15.4,17.5 14.3,17.5 C 13.2,17.5 12.2,16.5 12.2,15.4 C 12" +
			".2,14.3 13.2,13.3 14.3,13.3 C 15.4,13.3 16.4,14.3 16.4,15.4 z \" style=\"opacity:0.8417;fill:#fbfddb;stroke:none\" /> <path" +
			" id=\"path16221\" d=\"M 16,15.4 C 16,16.4 15.2,17.2 14.2,17.2 C 13.2,17.2 12.4,16.4 12.4,15.4 C 12.4,14.4 13" +
			".2,13.6 14.2,13.6 C 15.2,13.6 16,14.4 16,15.4 L 16,15.4 z \" style=\"fill:#ffffff;stroke:none\" /> </g> </g>" +
			" <path id=\"path16223\" d=\"M 25.015859,21.649044 L 33.697148,21.649044 L 35.362052,22.124732 L 32.507931,22." +
			"124732 C 32.507931,22.124732 35.362052,22.362574 36.789115,24.146401 C 38.216174,25.811305 35.12421,27.8329" +
			"76 35.12421,27.832976 C 35.12421,27.832976 35.12421,27.832976 35.12421,27.832976 C 35.005288,27.47621 34.29" +
			"1756,24.622087 32.864696,23.43287 C 31.794399,22.481496 30.605182,22.243652 30.605182,22.243652 L 25.015859" +
			",22.243652 L 25.015859,21.767966 L 25.015859,21.649044 z \" style=\"opacity:0.3;fill-rule:nonzero;stroke:non" +
			"e;stroke-width:1.2166667;stroke-miterlimit:4\" /> <path id=\"path16225\" d=\"M 30.724106,22.362574 L 25.729391,22.36" +
			"2574 L 35.005288,27.595131 L 30.724106,22.362574 L 30.724106,22.362574 z \" style=\"opacity:0.3;fill-rule:nonzero;stroke:no" +
			"ne;stroke-width:1.2166667;stroke-miterlimit:4\" /> <path id=\"path16227\" d=\"M 25.015859,21.767966 L 33.697148,21.767966 L" +
			" 35.005288,20.935513 L 32.151167,20.935513 C 32.151167,20.935513 34.767443,20.459827 35.12421,17.486782 C 35.480973,14.51373" +
			"9 31.080869,11.183931 31.080869,11.183931 C 31.080869,11.183931 31.080869,11.183931 31.080869,11.302853 C 31.19979,12.016383" +
			" 32.389007,17.011096 31.556557,18.913846 C 31.19979,20.578747 30.129495,20.935513 30.129495,20.935513 L 24.659094,20.93551" +
			"3 L 24.896938,21.767966 L 25.015859,21.767966 z \" style=\"fill:#515151;fill-rule:nonzero;stroke:none;stroke-width:1.2166667;stroke-miterlimit:4\" /> <path id=\"path16229\" d=\"M 30.248418,20.459827 L 25.253704,20.459827 L 31.19979,11.421773 L 30.248418,20.459827 z \" style=\"fill:#515151;fill-rule:nonzero;stroke:none;stroke-width:1.2166667;stroke-miterlimit:4\" /> </g> <rect y=\"14.485752\" x=\"16.508501\" height=\"13.997463\" width=\"19.995502\" id=\"rect8163\" style=\"opacity:1;color:#000000;fill:none;fill-opacity:1;fill-rule:nonzero;stroke:#9e9e9e;stroke-width:0.99999863;stroke-linecap:butt;stroke-linejoin:miter;marker:none;marker-start:none;marker-mid:none;marker-end:none;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:1;visibility:visible;display:inline;overflow:visible\" /> </g> <rect style=\"color:#000000;fill:#9b9b9b;fill-opacity:0.54970757;fill-rule:nonzero;stroke:none;stroke-width:1;stroke-linecap:round;stroke-linejoin:round;marker:none;marker-start:none;marker-mid:none;marker-end:none;stroke-miterlimit:4;stroke-dashoffset:0;stroke-opacity:0.08187136;visibility:visible;display:block;overflow:visible\" id=\"rect1768\" width=\"20.000006\" height=\"1\" x=\"15.999986\" y=\"9\" rx=\"0.13778631\" ry=\"0.065390877\" /> <rect style=\"color:#000000;fill:#9b9b9b;fill-opacity:0.54970757;fill-rule:nonzero;stroke:none;stroke-width:1;stroke-linecap:round;stroke-linejoin:round;marker:none;marker-start:none;marker-mid:none;marker-end:none;stroke-miterlimit:4;stroke-dashoffset:0;stroke-opacity:0.08187136;visibility:visible;display:block;overflow:visible\" id=\"rect1770\" width=\"1" +
			"4.000014\" height=\"1\" x=\"15.999986\" y=\"11\" rx=\"0.096450485\" ry=\"0.065390877\" /> </g> </svg>";
	private static final String phoneNodeSvg="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> <!-- Creat" +
			"ed with Inkscape " +
			"(http://www.inkscape.org/) --> <svg xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:cc=\"http://crea" +
			"tivecommons.org/ns#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:svg=\"http://www.w3.o" +
			"rg/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:sodip" +
			"odi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\" xmlns:inkscape=\"http://www.inkscape.org/namespa" +
			"ces/inkscape\" width=\"49.99823\" height=\"35.778622\" id=\"svg2\" version=\"1.1\" inkscape:version=\"0.48.4 r9" +
			"939\" sodipodi:docname=\"Iph\" inkscape:export-filename=\"\" inkscape:export-xdpi=\"90\" inkscape:export-ydpi=\"90\" st" +
			"yle=\"display:inline\"> <defs id=\"defs4\"> <linearGradient id=\"linearGradient3846\"> <stop style=\"stop-color:#404040" +
			";stop-opacity:1;\" offset=\"0\" id=\"stop3848\" /> <stop style=\"stop-color:#202020;stop-opacity:1;\" offset=\"1\" id=\"s" +
			"top3850\" /> </linearGradient> <linearGradient id=\"linearGradient3838\"> <stop style=\"stop-color:#0094d2;stop-opacity:1" +
			";\" offset=\"0\" id=\"stop3840\" /> <stop style=\"stop-color:#d8edf9;stop-opacity:1;\" offset=\"1\" id=\"stop3842\" /> </" +
			"linearGradient> <inkscape:path-effect effect=\"sketch\" id=\"path-effect3823\" is_visible=\"true\" nbiter_approxstrokes" +
			"=\"5\" strokelength=\"100\" strokelength_rdm=\"0.3;1\" strokeoverlap=\"0.3\" strokeoverlap_rdm=\"0.3;1\" ends_tolerance=" +
			"\"0.1;1\" parallel_offset=\"1;1\" tremble_size=\"5;1\" tremble_frequency=\"1\" nbtangents=\"5\" tgt_places_rdmness=\"1;" +
			"1\" tgtscale=\"10\" tgtlength=\"100\" tgtlength_rdm=\"0.3;1\" /> <inkscape:path-effect effect=\"gears\" id=\"path-ef" +
			"fect3821\" is_visible=\"true\" teeth=\"10\" phi=\"5\" /> <inkscape:path-effect effect=\"vonkoch\" id=\"path-effect3819\" is_visible=\"true\" ref_path=\"m 48.473,536.80083 1369.5566,0\" generator=\"m 48.473,1025.3672 456.51885,0 m 456.51886,0 456.51889,0\" similar_only=\"false\" nbgenerations=\"1\" drawall=\"true\" maxComplexity=\"1000\" /> <inkscape:path-effect effect=\"curvestitching\" id=\"path-effect3817\" is_visible=\"true\" count=\"5\" startpoint_edge_variation=\"0;1\" startpoint_spacing_variation=\"0;1\" endpoint_edge_variation=\"0;1\" endpoint_spacing_variation=\"0;1\" strokepath=\"m 48.473,536.80083 1369.5566,0\" prop_scale=\"1\" scale_y_rel=\"false\" /> <inkscape:path-effect effect=\"spiro\" id=\"path-effect3815\" is_visible=\"true\" /> <inkscape:path-effect effect=\"knot\" id=\"path-effect3813\" is_visible=\"true\" interruption_width=\"3\" prop_to_stroke_width=\"true\" add_stroke_width=\"true\" add_other_stroke_width=\"true\" switcher_size=\"15\" crossing_points_vector=\"\" /> <inkscape:path-effect effect=\"interpolate\" id=\"path-effect3811\" is_visible=\"true\" trajectory=\"M 0,0 0,0\" equidistant_spacing=\"true\" steps=\"5\" /> <inkscape:path-effect effect=\"rough_hatches\" id=\"path-effect3809\" is_visible=\"true\" direction=\"733.25128,531.80083 , 342.38914,0\" dist_rdm=\"75;1\" growth=\"0\" do_bend=\"true\" bender=\"733.25128,536.80083 , 5,0\" bottom_edge_variation=\"97.713275;1\" top_edge_variation=\"97.713275;1\" bottom_tgt_variation=\"0;1\" top_tgt_variation=\"0;1\" scale_bf=\"1\" scale_bb=\"1\" scale_tf=\"1\" scale_tb=\"1\" top_smth_variation=\"0;1\" bottom_smth_variation=\"0;1\" fat_output=\"true\" stroke_width_top=\"1\" stroke_width_bottom=\"1\" front_thickness=\"1\" back_thickness=\"0.25\" /> <inkscape:path-effect effect=\"envelope\" id=\"path-effect3807\" is_visible=\"true\" yy=\"true\" xx=\"true\" bendpath1=\"m 48.473,48.234452 1369.5566,0\" bendpath2=\"m 1418.0296,48.234452 0,977.132748\" bendpath3=\"m 48.473,1025.3672 1369.5566,0\" bendpath4=\"m 48.473,48.234452 0,977.132748\" /> <inkscape:path-effect effect=\"construct_grid\" id=\"path-effect3805\" is_visible=\"true\" nr_x=\"5\" nr_y=\"5\" /> <inkscape:path-effect effect=\"bend_path\" id=\"path-effect3803\" is_visible=\"true\" bendpath=\"m 48.473,536.80083 1369.5566,0\" prop_scale=\"1\" scale_y_rel=\"false\" vertical=\"false\" /> <inkscape:path-effect effect=\"sketch\" id=\"path-effect3801\" is_visible=\"true\" nbiter_approxstrokes=\"5\" strokelength=\"100\" strokelength_rdm=\"0.3;1\" strokeoverlap=\"0.3\" strokeoverlap_rdm=\"0.4;1\" ends_tolerance=\"0.3;1\" parallel_offset=\"0;1\" tremble_size=\"0;1\" tremble_frequency=\"1\" nbtangents=\"5\" tgt_places_rdmness=\"1;1\" tgtscale=\"10\" tgtlength=\"100\" tgtlength_rdm=\"0.3;1\" /> <radialGradient inkscape:collect=\"always\" xlink:href=\"#linearGradient3846\" id=\"radialGradient3852\" cx=\"355.41455\" cy=\"784.90198\" fx=\"355.41455\" fy=\"784.90198\" r=\"90.456558\" gradientTransform=\"matrix(0.03624353,0,0,0.02038752,41.897386,1005.4685)\" gradientUnits=\"userSpaceOnUse\" /> <linearGradient inkscape:collect=\"always\" xlink:href=\"#linearGradient3838\" id=\"linearGradient3833\" x1=\"43.473\" y1=\"536.80084\" x2=\"1423.0295\" y2=\"536.80084\" gradientUnits=\"userSpaceOnUse\" gradientTransform=\"matrix(0.03624353,0,0,0.03624353,41.897386,993.02306)\" /> </defs> <sodipodi:namedview id=\"base\" pagecolor=\"#ffffff\" bordercolor=\"#666666\" borderopacity=\"1.0\" inkscape:pageopacity=\"0.0\" inkscape:pageshadow=\"2\" inkscape:zoom=\"1.4\" inkscape:cx=\"-123.62822\" inkscape:cy=\"130.08793\" inkscape:document-units=\"px\" inkscape:current-layer=\"g3780\" showgrid=\"false\" inkscape:window-width=\"1920\" inkscape:window-height=\"1025\" inkscape:window-x=\"-2\" inkscape:window-y=\"-3\" inkscape:window-maximized=\"1\" fit-margin-top=\"0\" fit-margin-left=\"0\" fit-margin-right=\"0\" fit-margin-bottom=\"0\" /> <metadata id=\"metadata7\"> <rdf:RDF> <cc:Work rdf:about=\"\"> <dc:format>image/svg+xml</dc:format> <dc:type rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" /> <dc:title /> </cc:Work> </rdf:RDF> </metadata> <g transform=\"translate(-43.470372,-994.57958)\" style=\"display:inline\" inkscape:label=\"Iphone\" id=\"g3780\" inkscape:groupmode=\"layer\"> <path sodipodi:nodetypes=\"ssscccccsss\" inkscape:connector-curvature=\"0\" id=\"path3782\" d=\"m 75.540654,994.84576 c -1.589242,-0.25872 -3.431892,0.16203 -4.686271,1.17157 -8.005518,6.44297 -17.385759,14.04197 -24.310031,19.77027 -2.046309,1.6928 -4.405131,3.8977 -1.610906,7.5419 0.920206,0.9095 2.033963,1.4146 3.148589,1.977 3.247659,1.5823 6.79094,3.024 10.104772,4.247 4.075186,1.173 6.796015,0.7977 9.885103,-1.8306 6.325141,-6.8036 16.553563,-17.5495 24.310033,-25.7745 0.748838,-0.7838 1.006025,-1.7206 0.87868,-2.78248 -0.128182,-1.06877 -1.653571,-1.54702 -2.709255,-1.75735 -4.856437,-0.96758 -10.121451,-1.76682 -15.010714,-2.56281 z\" style=\"fill:url(#linearGradient3833);fill-opacity:1;stroke:#000000;stroke-width:0.36243528;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" /> <path sodipodi:nodetypes=\"csssssssc\" inkscape:connector-curvature=\"0\" id=\"path3786\" d=\"m 46.134442,1016.5564 c -2.190619,1.9544 -2.003141,3.8861 -0.585784,4.5032 4.381932,1.9079 8.689186,3.634 13.692698,5.3819 2.528814,0.8834 5.908725,-0.041 7.798248,-1.9404 8.3149,-8.3553 16.397327,-16.4729 24.529703,-24.82261 1.307863,-1.34283 -0.629206,-1.82544 -1.427852,-1.97702 -4.902661,-0.93059 -9.827836,-1.81071 -14.791042,-2.56281 -1.357903,-0.20577 -2.929512,0.15321 -3.990653,1.02512 -8.269987,6.79522 -17.639011,14.10552 -25.225318,20.39262 z\" style=\"fill:#404040;fill-opacity:1;stroke:#000000;stroke-width:0.18121764;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" /> <path sodipodi:nodetypes=\"ccccc\" inkscape:connector-curvature=\"0\" id=\"path3790\" d=\"m 48.457464,1016.1492 18.794859,6.7309 21.746115,-21.6943 -18.121763,-3.57258 z\" style=\"fill:#202020;fill-opacity:1;stroke:#000000;stroke-width:0.18121764;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" /> <path sodipodi:nodetypes=\"sssssssss\" inkscape:connector-curvature=\"0\" id=\"path3794\" d=\"m 53.029315,1020.0842 c 0.466237,-0.3795 0.864271,-0.4628 1.501518,-0.2589 0.916223,0.2932 1.897805,0.6993 2.79593,0.9838 0.614772,0.1947 0.845665,0.5138 0.429376,0.9623 -0.357548,0.3852 -0.663718,0.6321 -1.087306,0.9837 -0.664297,0.5515 -1.239857,0.552 -1.827341,0.3322 -0.883986,-0.3309 -1.763321,-0.7098 -2.6406,-1.0356 -1.012645,-0.3761 -0.564782,-0.6791 -0.08211,-1.1359 0.255497,-0.2419 0.618686,-0.594 0.91053,-0.8316 z\" style=\"fill:url(#radialGradient3852);fill-opacity:1;fill-rule:nonzero;stroke:#000000;stroke-width:0.18121764;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" /> <path style=\"fill:#76ad37;fill-opacity:1;stroke:#000000;stroke-width:0.10873058;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" d=\"m 52.60217,1014.1817 c -0.486672,0.4125 -1.071558,0.9149 -1.501517,1.3332 -0.04639,0.045 -0.06011,0.1702 0,0.1942 0.610755,0.2439 1.20168,0.4581 1.786288,0.6472 0.132967,0.043 0.307096,0.025 0.414212,-0.064 0.570695,-0.4782 1.086329,-0.8985 1.585194,-1.3774 0.05435,-0.052 0.155997,-0.1569 0.03975,-0.2018 -0.53962,-0.2087 -1.261461,-0.4353 -1.909718,-0.6472 -0.136326,-0.044 -0.304799,0.024 -0.414212,0.1165 z\" id=\"path3054\" inkscape:connector-curvature=\"0\" sodipodi:nodetypes=\"sssssssss\" /> <path style=\"fill:#be4f1a;fill-opacity:1;stroke:#000000;stroke-width:0.10873058;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" d=\"m 55.315497,1016.9591 c 0.453489,-0.3857 1.147171,-1.0329 1.674976,-1.437 0.08534,-0.065 0.216271,-0.064 0.320351,-0.037 0.634663,0.1636 1.267172,0.4032 1.821422,0.5858 0.06511,0.021 0.122192,0.1443 0.07322,0.1922 -0.530155,0.5196 -1.132183,1.0224 -1.638365,1.4645 -0.0884,0.077 -0.227291,0.1378 -0.338656,0.1007 -0.629294,-0.2095 -1.235687,-0.4084 -1.885492,-0.6682 -0.0629,-0.025 -0.07906,-0.1575 -0.02746,-0.2013 z\" id=\"path3824\" inkscape:connector-curvature=\"0\" sodipodi:nodetypes=\"sssssssss\" /> <path style=\"fill:#edd782;fill-opacity:1;stroke:#000000;stroke-width:0.10873058;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" d=\"m 59.663112,1018.3595 c -0.06262,0.062 -0.074,0.2351 0.0092,0.2654 0.749817,0.274 1.478129,0.5236 2.224148,0.7689 0.103451,0.034 0.241153,0.011 0.320351,-0.064 0.526141,-0.4966 1.060322,-1.0102 1.482765,-1.4919 0.05847,-0.067 -0.03498,-0.2096 -0.118987,-0.238 -0.557413,-0.1886 -1.239808,-0.4389 -1.858033,-0.6132 -0.164542,-0.047 -0.378956,-0.088 -0.512561,0.019 -0.499835,0.399 -1.118957,0.927 -1.546835,1.3546 z\" id=\"path3834\" inkscape:connector-curvature=\"0\" sodipodi:nodetypes=\"sssssssss\" /> <path style=\"fill:#65a7c1;fill-opacity:1;stroke:#000000;stroke-width:0.10873058;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" d=\"m 64.413453,1019.9521 c -0.06647,0.072 -0.08112,0.2556 0.0092,0.2929 0.620743,0.2565 1.370016,0.4975 2.013632,0.7231 0.179345,0.063 0.430805,0.077 0.567478,-0.055 0.501943,-0.4849 1.071667,-1.0184 1.574294,-1.5469 0.05567,-0.059 0.0302,-0.2105 -0.04577,-0.2379 -0.618492,-0.2236 -1.436485,-0.4965 -2.19669,-0.7323 -0.126954,-0.039 -0.296604,-0.027 -0.393574,0.064 -0.498344,0.4672 -1.042698,0.9689 -1.528529,1.4919 z\" id=\"path3836\" inkscape:connector-curvature=\"0\" sodipodi:nodetypes=\"sssssssss\" /> </g> </svg>";
	private static final String mailNodeSvg="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> <!-- Created with Inkscape (ht" +
			"tp://www.inkscape.org/) --> <svg xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:cc=\"http://creativecommons.org/ns#" +
			"\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www." +
			"w3.org/2000/svg\" xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\" xmlns:inkscape=\"http://www.inksca" +
			"pe.org/namespaces/inkscape\" width=\"49.8125\" height=\"28.09375\" id=\"svg2987\" version=\"1.1\" inkscape:version=\"0.48." +
			"4 r9939\" sodipodi:docname=\"drawing1.svg\"> <defs id=\"defs2989\" /> <sodipodi:namedview id=\"base\" pagecolor=\"#ffffff\"" +
			" bordercolor=\"#666666\" borderopacity=\"1.0\" inkscape:pageopacity=\"0.0\" inkscape:pageshadow=\"2\" inkscape:zoom=\"0.989" +
			"94949\" inkscape:cx=\"-14.652317\" inkscape:cy=\"0.19894582\" inkscape:document-units=\"px\" inkscape:current-layer=\"layer" +
			"1\" showgrid=\"false\" fit-margin-top=\"0\" fit-margin-left=\"0\" fit-margin-right=\"0\" fit-margin-bottom=\"0\" inkscape:w" +
			"indow-width=\"1920\" inkscape:window-height=\"1025\" inkscape:window-x=\"-2\" inkscape:window-y=\"-3\" inkscape:window-maxi" +
			"mized=\"1\" /> <metadata id=\"metadata2992\"> <rdf:RDF> <cc:Work rdf:about=\"\"> <dc:format>image/svg+xml</dc:format> <dc:t" +
			"ype rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" /> <dc:title></dc:title> </cc:Work> </rdf:RDF> </metadata> <g i" +
			"nkscape:label=\"Layer 1\" inkscape:groupmode=\"layer\" id=\"layer1\" transform=\"translate(-56.78125,-397.46875)\"> <rect s" +
			"tyle=\"fill:none;stroke:#000000;stroke-width:1.88509858;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" id=\"r" +
			"ect2995\" width=\"47.935345\" height=\"26.211853\" x=\"57.72826\" y=\"398.42209\" /> <path style=\"fill:none;stroke:#000000" +
			";stroke-width:1.88509858;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:no" +
			"ne\" d=\"m 57.907795,399.14023 23.159784,24.59605 24.775581,-24.95511\" id=\"path3769\" inkscape:connector-curvature=\"0\" " +
			"/> </g> </svg>";
	private static final  String personNodeSvg="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> <!-- Created w" +
			"ith Inkscape (http://www.inkscape.org/) --> <svg xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:cc=\"http:" +
			"//creativecommons.org/ns#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:svg=\"http://www.w" +
			"3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sod" +
			"ipodi-0.dtd\" xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\" width=\"19.78125\" height=\"49.86" +
			"5517\" id=\"svg2\" version=\"1.1\" inkscape:version=\"0.48.4 r9939\" sodipodi:docname=\"drawing.svg,.svg\"> <d" +
			"efs id=\"defs4\" /> <sodipodi:namedview id=\"base\" pagecolor=\"#ffffff\" bordercolor=\"#666666\" borderopacit" +
			"y=\"1.0\" inkscape:pageopacity=\"0.0\" inkscape:pageshadow=\"2\" inkscape:zoom=\"1.4\" inkscape:cx=\"-53.27892" +
			"9\" inkscape:cy=\"170.01595\" inkscape:document-units=\"px\" inkscape:current-layer=\"layer1\" showgrid=\"fals" +
			"e\" fit-margin-top=\"0\" fit-margin-left=\"0\" fit-margin-right=\"0\" fit-margin-bottom=\"0\" inkscape:window-" +
			"width=\"1920\" inkscape:window-height=\"1025\" inkscape:window-x=\"-2\" inkscape:window-y=\"-3\" inkscape:wind" +
			"ow-maximized=\"1\" /> <metadata id=\"metadata7\"> <rdf:RDF> <cc:Work rdf:about=\"\"> <dc:format>image/svg+xml<" +
			"/dc:format> <dc:type rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" /> <dc:title></dc:title> </cc:Wor" +
			"k> </rdf:RDF> </metadata> <g inkscape:label=\"Layer 1\" inkscape:groupmode=\"layer\" id=\"layer1\" transfo" +
			"rm=\"translate(-141.15625,-457.54073)\"> <path sodipodi:type=\"arc\" style=\"fill:#000000;fill-opacity:1;stroke:" +
			"none\" id=\"path2989\" sodipodi:cx=\"247.14285\" sodipodi:cy=\"213.43361\" sodipodi:rx=\"70\" sodipodi:ry=\"69.6" +
			"4286\" d=\"m 317.14285,213.43361 c 0,38.46269 -31.34006,69.64286 -70,69.64286 -38.65993,0 -70,-31.18017 -70,-69." +
			"64286 0,-38.46269 31.34007,-69.64286 70,-69.64286 38.65994,0 70,31.18017 70,69.64286 z\" transform=\"matrix(0.13" +
			"745706,0,0,0.13745706,117.28401,437.77568)\" /> <path style=\"fill:none;stroke:#000000;stroke-width:2.06185627;s" +
			"troke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" d=\"m 151." +
			"25555,474.13376 -0.14734,26.70594\" id=\"path3783\" inkscape:connector-curvature=\"0\" /> <path style=\"fill:non" +
			"e;stroke:#000000;stroke-width:2.06185627;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-op" +
			"acity:1;stroke-dasharray:none\" d=\"m 151.20643,500.49606 -9.5238,5.74375\" id=\"path3785\" inkscape:connector-c" +
			"urvature=\"0\" /> <path style=\"fill:none;stroke:#000000;stroke-width:2.06185627;stroke-linecap:butt;stroke-line" +
			"join:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" d=\"m 150.86279,500.76606 9.52381,5.7437" +
			"5\" id=\"path3785-6\" inkscape:connector-curvature=\"0\" inkscape:transform-center-x=\"-1.7182004\" /> <path sty" +
			"le=\"fill:none;stroke:#000000;stroke-width:2.06185627;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimi" +
			"t:4;stroke-opacity:1;stroke-dasharray:none\" d=\"m 160.92662,485.42488 -19.53854,0\" id=\"path3805\" inkscape:co" +
			"nnector-curvature=\"0\" /> </g> </svg>";
}
