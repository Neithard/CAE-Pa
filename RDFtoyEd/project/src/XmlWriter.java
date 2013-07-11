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
					graph.appendChild(makeGenericElement(n));
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
		key.appendChild(doc.createElement("y:ArcEdge")).appendChild(label);
//		label.setAttribute("alignment", "center");
//		label.setAttribute("configuration", "AutoFlippingLabel");
//		label.setAttribute("fontFamily", "Dialog");
//		label.setAttribute("fontSize", "12");
//		label.setAttribute("fontStyle", "plain");
//		label.setAttribute("hasBackgroundColor", "false");
//		label.setAttribute("hasLineColor", "false");
//		label.setAttribute("modelName", "custom");
//		label.setAttribute("textColor", "#000000");
//		label.setAttribute("preferredPlacement", "anywhere");		
//		label.setAttribute("ratio", "0.5");
//		label.setAttribute("height", "20");		
//		label.setAttribute("width", "20");				
//		label.setAttribute("visible", "true");
		label.setTextContent(e.getName());
		
		Element desc=doc.createElement("y:PreferredPlacementDescriptor");
		label.appendChild(desc);
		desc.setAttribute("angle", "0.0");
		desc.setAttribute("angleReference", "absolute");		
		desc.setAttribute("angle", "0.0");		
		
		
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
		Element compElement=makeGenericElement(compNode);
		
		return compElement;
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
		fill.setAttribute("color", "#FFCC00");
		fill.setAttribute("transparent", "false");
		shN.appendChild(fill);
		
		
		Element borderStyleElement=doc.createElement("y:BorderStyle");
		borderStyleElement.setAttribute("color", "#000000");
		borderStyleElement.setAttribute("type", "line");
		borderStyleElement.setAttribute("width", "1.0");
		shN.appendChild(borderStyleElement);
		
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
		label.setAttribute("visible", "false");
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
	
	
	private Element makeEquipmentPieceElement(Node equiNode)
	{
		Element equElement=makeGenericElement(equiNode);
		
		return equElement;
	}
	
	private Element makePhoneElement(Node phoneNode)
	{
		Element phElement=makeSvgNode(50, 35.8, phoneNode, 3);
		
		return phElement;
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
	
	private static final String phoneNodeSvg="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> <!-- Created with Inkscape (http://www.inkscape.org/) --> <svg xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:cc=\"http://creativecommons.org/ns#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\" xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\" width=\"49.99823\" height=\"35.778622\" id=\"svg2\" version=\"1.1\" inkscape:version=\"0.48.4 r9939\" sodipodi:docname=\"Iph\" inkscape:export-filename=\"\" inkscape:export-xdpi=\"90\" inkscape:export-ydpi=\"90\" style=\"display:inline\"> <defs id=\"defs4\"> <linearGradient id=\"linearGradient3846\"> <stop style=\"stop-color:#404040;stop-opacity:1;\" offset=\"0\" id=\"stop3848\" /> <stop style=\"stop-color:#202020;stop-opacity:1;\" offset=\"1\" id=\"stop3850\" /> </linearGradient> <linearGradient id=\"linearGradient3838\"> <stop style=\"stop-color:#0094d2;stop-opacity:1;\" offset=\"0\" id=\"stop3840\" /> <stop style=\"stop-color:#d8edf9;stop-opacity:1;\" offset=\"1\" id=\"stop3842\" /> </linearGradient> <inkscape:path-effect effect=\"sketch\" id=\"path-effect3823\" is_visible=\"true\" nbiter_approxstrokes=\"5\" strokelength=\"100\" strokelength_rdm=\"0.3;1\" strokeoverlap=\"0.3\" strokeoverlap_rdm=\"0.3;1\" ends_tolerance=\"0.1;1\" parallel_offset=\"1;1\" tremble_size=\"5;1\" tremble_frequency=\"1\" nbtangents=\"5\" tgt_places_rdmness=\"1;1\" tgtscale=\"10\" tgtlength=\"100\" tgtlength_rdm=\"0.3;1\" /> <inkscape:path-effect effect=\"gears\" id=\"path-effect3821\" is_visible=\"true\" teeth=\"10\" phi=\"5\" /> <inkscape:path-effect effect=\"vonkoch\" id=\"path-effect3819\" is_visible=\"true\" ref_path=\"m 48.473,536.80083 1369.5566,0\" generator=\"m 48.473,1025.3672 456.51885,0 m 456.51886,0 456.51889,0\" similar_only=\"false\" nbgenerations=\"1\" drawall=\"true\" maxComplexity=\"1000\" /> <inkscape:path-effect effect=\"curvestitching\" id=\"path-effect3817\" is_visible=\"true\" count=\"5\" startpoint_edge_variation=\"0;1\" startpoint_spacing_variation=\"0;1\" endpoint_edge_variation=\"0;1\" endpoint_spacing_variation=\"0;1\" strokepath=\"m 48.473,536.80083 1369.5566,0\" prop_scale=\"1\" scale_y_rel=\"false\" /> <inkscape:path-effect effect=\"spiro\" id=\"path-effect3815\" is_visible=\"true\" /> <inkscape:path-effect effect=\"knot\" id=\"path-effect3813\" is_visible=\"true\" interruption_width=\"3\" prop_to_stroke_width=\"true\" add_stroke_width=\"true\" add_other_stroke_width=\"true\" switcher_size=\"15\" crossing_points_vector=\"\" /> <inkscape:path-effect effect=\"interpolate\" id=\"path-effect3811\" is_visible=\"true\" trajectory=\"M 0,0 0,0\" equidistant_spacing=\"true\" steps=\"5\" /> <inkscape:path-effect effect=\"rough_hatches\" id=\"path-effect3809\" is_visible=\"true\" direction=\"733.25128,531.80083 , 342.38914,0\" dist_rdm=\"75;1\" growth=\"0\" do_bend=\"true\" bender=\"733.25128,536.80083 , 5,0\" bottom_edge_variation=\"97.713275;1\" top_edge_variation=\"97.713275;1\" bottom_tgt_variation=\"0;1\" top_tgt_variation=\"0;1\" scale_bf=\"1\" scale_bb=\"1\" scale_tf=\"1\" scale_tb=\"1\" top_smth_variation=\"0;1\" bottom_smth_variation=\"0;1\" fat_output=\"true\" stroke_width_top=\"1\" stroke_width_bottom=\"1\" front_thickness=\"1\" back_thickness=\"0.25\" /> <inkscape:path-effect effect=\"envelope\" id=\"path-effect3807\" is_visible=\"true\" yy=\"true\" xx=\"true\" bendpath1=\"m 48.473,48.234452 1369.5566,0\" bendpath2=\"m 1418.0296,48.234452 0,977.132748\" bendpath3=\"m 48.473,1025.3672 1369.5566,0\" bendpath4=\"m 48.473,48.234452 0,977.132748\" /> <inkscape:path-effect effect=\"construct_grid\" id=\"path-effect3805\" is_visible=\"true\" nr_x=\"5\" nr_y=\"5\" /> <inkscape:path-effect effect=\"bend_path\" id=\"path-effect3803\" is_visible=\"true\" bendpath=\"m 48.473,536.80083 1369.5566,0\" prop_scale=\"1\" scale_y_rel=\"false\" vertical=\"false\" /> <inkscape:path-effect effect=\"sketch\" id=\"path-effect3801\" is_visible=\"true\" nbiter_approxstrokes=\"5\" strokelength=\"100\" strokelength_rdm=\"0.3;1\" strokeoverlap=\"0.3\" strokeoverlap_rdm=\"0.4;1\" ends_tolerance=\"0.3;1\" parallel_offset=\"0;1\" tremble_size=\"0;1\" tremble_frequency=\"1\" nbtangents=\"5\" tgt_places_rdmness=\"1;1\" tgtscale=\"10\" tgtlength=\"100\" tgtlength_rdm=\"0.3;1\" /> <radialGradient inkscape:collect=\"always\" xlink:href=\"#linearGradient3846\" id=\"radialGradient3852\" cx=\"355.41455\" cy=\"784.90198\" fx=\"355.41455\" fy=\"784.90198\" r=\"90.456558\" gradientTransform=\"matrix(0.03624353,0,0,0.02038752,41.897386,1005.4685)\" gradientUnits=\"userSpaceOnUse\" /> <linearGradient inkscape:collect=\"always\" xlink:href=\"#linearGradient3838\" id=\"linearGradient3833\" x1=\"43.473\" y1=\"536.80084\" x2=\"1423.0295\" y2=\"536.80084\" gradientUnits=\"userSpaceOnUse\" gradientTransform=\"matrix(0.03624353,0,0,0.03624353,41.897386,993.02306)\" /> </defs> <sodipodi:namedview id=\"base\" pagecolor=\"#ffffff\" bordercolor=\"#666666\" borderopacity=\"1.0\" inkscape:pageopacity=\"0.0\" inkscape:pageshadow=\"2\" inkscape:zoom=\"1.4\" inkscape:cx=\"-123.62822\" inkscape:cy=\"130.08793\" inkscape:document-units=\"px\" inkscape:current-layer=\"g3780\" showgrid=\"false\" inkscape:window-width=\"1920\" inkscape:window-height=\"1025\" inkscape:window-x=\"-2\" inkscape:window-y=\"-3\" inkscape:window-maximized=\"1\" fit-margin-top=\"0\" fit-margin-left=\"0\" fit-margin-right=\"0\" fit-margin-bottom=\"0\" /> <metadata id=\"metadata7\"> <rdf:RDF> <cc:Work rdf:about=\"\"> <dc:format>image/svg+xml</dc:format> <dc:type rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" /> <dc:title /> </cc:Work> </rdf:RDF> </metadata> <g transform=\"translate(-43.470372,-994.57958)\" style=\"display:inline\" inkscape:label=\"Iphone\" id=\"g3780\" inkscape:groupmode=\"layer\"> <path sodipodi:nodetypes=\"ssscccccsss\" inkscape:connector-curvature=\"0\" id=\"path3782\" d=\"m 75.540654,994.84576 c -1.589242,-0.25872 -3.431892,0.16203 -4.686271,1.17157 -8.005518,6.44297 -17.385759,14.04197 -24.310031,19.77027 -2.046309,1.6928 -4.405131,3.8977 -1.610906,7.5419 0.920206,0.9095 2.033963,1.4146 3.148589,1.977 3.247659,1.5823 6.79094,3.024 10.104772,4.247 4.075186,1.173 6.796015,0.7977 9.885103,-1.8306 6.325141,-6.8036 16.553563,-17.5495 24.310033,-25.7745 0.748838,-0.7838 1.006025,-1.7206 0.87868,-2.78248 -0.128182,-1.06877 -1.653571,-1.54702 -2.709255,-1.75735 -4.856437,-0.96758 -10.121451,-1.76682 -15.010714,-2.56281 z\" style=\"fill:url(#linearGradient3833);fill-opacity:1;stroke:#000000;stroke-width:0.36243528;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" /> <path sodipodi:nodetypes=\"csssssssc\" inkscape:connector-curvature=\"0\" id=\"path3786\" d=\"m 46.134442,1016.5564 c -2.190619,1.9544 -2.003141,3.8861 -0.585784,4.5032 4.381932,1.9079 8.689186,3.634 13.692698,5.3819 2.528814,0.8834 5.908725,-0.041 7.798248,-1.9404 8.3149,-8.3553 16.397327,-16.4729 24.529703,-24.82261 1.307863,-1.34283 -0.629206,-1.82544 -1.427852,-1.97702 -4.902661,-0.93059 -9.827836,-1.81071 -14.791042,-2.56281 -1.357903,-0.20577 -2.929512,0.15321 -3.990653,1.02512 -8.269987,6.79522 -17.639011,14.10552 -25.225318,20.39262 z\" style=\"fill:#404040;fill-opacity:1;stroke:#000000;stroke-width:0.18121764;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" /> <path sodipodi:nodetypes=\"ccccc\" inkscape:connector-curvature=\"0\" id=\"path3790\" d=\"m 48.457464,1016.1492 18.794859,6.7309 21.746115,-21.6943 -18.121763,-3.57258 z\" style=\"fill:#202020;fill-opacity:1;stroke:#000000;stroke-width:0.18121764;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" /> <path sodipodi:nodetypes=\"sssssssss\" inkscape:connector-curvature=\"0\" id=\"path3794\" d=\"m 53.029315,1020.0842 c 0.466237,-0.3795 0.864271,-0.4628 1.501518,-0.2589 0.916223,0.2932 1.897805,0.6993 2.79593,0.9838 0.614772,0.1947 0.845665,0.5138 0.429376,0.9623 -0.357548,0.3852 -0.663718,0.6321 -1.087306,0.9837 -0.664297,0.5515 -1.239857,0.552 -1.827341,0.3322 -0.883986,-0.3309 -1.763321,-0.7098 -2.6406,-1.0356 -1.012645,-0.3761 -0.564782,-0.6791 -0.08211,-1.1359 0.255497,-0.2419 0.618686,-0.594 0.91053,-0.8316 z\" style=\"fill:url(#radialGradient3852);fill-opacity:1;fill-rule:nonzero;stroke:#000000;stroke-width:0.18121764;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" /> <path style=\"fill:#76ad37;fill-opacity:1;stroke:#000000;stroke-width:0.10873058;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" d=\"m 52.60217,1014.1817 c -0.486672,0.4125 -1.071558,0.9149 -1.501517,1.3332 -0.04639,0.045 -0.06011,0.1702 0,0.1942 0.610755,0.2439 1.20168,0.4581 1.786288,0.6472 0.132967,0.043 0.307096,0.025 0.414212,-0.064 0.570695,-0.4782 1.086329,-0.8985 1.585194,-1.3774 0.05435,-0.052 0.155997,-0.1569 0.03975,-0.2018 -0.53962,-0.2087 -1.261461,-0.4353 -1.909718,-0.6472 -0.136326,-0.044 -0.304799,0.024 -0.414212,0.1165 z\" id=\"path3054\" inkscape:connector-curvature=\"0\" sodipodi:nodetypes=\"sssssssss\" /> <path style=\"fill:#be4f1a;fill-opacity:1;stroke:#000000;stroke-width:0.10873058;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" d=\"m 55.315497,1016.9591 c 0.453489,-0.3857 1.147171,-1.0329 1.674976,-1.437 0.08534,-0.065 0.216271,-0.064 0.320351,-0.037 0.634663,0.1636 1.267172,0.4032 1.821422,0.5858 0.06511,0.021 0.122192,0.1443 0.07322,0.1922 -0.530155,0.5196 -1.132183,1.0224 -1.638365,1.4645 -0.0884,0.077 -0.227291,0.1378 -0.338656,0.1007 -0.629294,-0.2095 -1.235687,-0.4084 -1.885492,-0.6682 -0.0629,-0.025 -0.07906,-0.1575 -0.02746,-0.2013 z\" id=\"path3824\" inkscape:connector-curvature=\"0\" sodipodi:nodetypes=\"sssssssss\" /> <path style=\"fill:#edd782;fill-opacity:1;stroke:#000000;stroke-width:0.10873058;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" d=\"m 59.663112,1018.3595 c -0.06262,0.062 -0.074,0.2351 0.0092,0.2654 0.749817,0.274 1.478129,0.5236 2.224148,0.7689 0.103451,0.034 0.241153,0.011 0.320351,-0.064 0.526141,-0.4966 1.060322,-1.0102 1.482765,-1.4919 0.05847,-0.067 -0.03498,-0.2096 -0.118987,-0.238 -0.557413,-0.1886 -1.239808,-0.4389 -1.858033,-0.6132 -0.164542,-0.047 -0.378956,-0.088 -0.512561,0.019 -0.499835,0.399 -1.118957,0.927 -1.546835,1.3546 z\" id=\"path3834\" inkscape:connector-curvature=\"0\" sodipodi:nodetypes=\"sssssssss\" /> <path style=\"fill:#65a7c1;fill-opacity:1;stroke:#000000;stroke-width:0.10873058;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none;display:inline\" d=\"m 64.413453,1019.9521 c -0.06647,0.072 -0.08112,0.2556 0.0092,0.2929 0.620743,0.2565 1.370016,0.4975 2.013632,0.7231 0.179345,0.063 0.430805,0.077 0.567478,-0.055 0.501943,-0.4849 1.071667,-1.0184 1.574294,-1.5469 0.05567,-0.059 0.0302,-0.2105 -0.04577,-0.2379 -0.618492,-0.2236 -1.436485,-0.4965 -2.19669,-0.7323 -0.126954,-0.039 -0.296604,-0.027 -0.393574,0.064 -0.498344,0.4672 -1.042698,0.9689 -1.528529,1.4919 z\" id=\"path3836\" inkscape:connector-curvature=\"0\" sodipodi:nodetypes=\"sssssssss\" /> </g> </svg>";
	private static final String mailNodeSvg="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> <!-- Created with Inkscape (http://www.inkscape.org/) --> <svg xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:cc=\"http://creativecommons.org/ns#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\" xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\" width=\"49.8125\" height=\"28.09375\" id=\"svg2987\" version=\"1.1\" inkscape:version=\"0.48.4 r9939\" sodipodi:docname=\"drawing1.svg\"> <defs id=\"defs2989\" /> <sodipodi:namedview id=\"base\" pagecolor=\"#ffffff\" bordercolor=\"#666666\" borderopacity=\"1.0\" inkscape:pageopacity=\"0.0\" inkscape:pageshadow=\"2\" inkscape:zoom=\"0.98994949\" inkscape:cx=\"-14.652317\" inkscape:cy=\"0.19894582\" inkscape:document-units=\"px\" inkscape:current-layer=\"layer1\" showgrid=\"false\" fit-margin-top=\"0\" fit-margin-left=\"0\" fit-margin-right=\"0\" fit-margin-bottom=\"0\" inkscape:window-width=\"1920\" inkscape:window-height=\"1025\" inkscape:window-x=\"-2\" inkscape:window-y=\"-3\" inkscape:window-maximized=\"1\" /> <metadata id=\"metadata2992\"> <rdf:RDF> <cc:Work rdf:about=\"\"> <dc:format>image/svg+xml</dc:format> <dc:type rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" /> <dc:title></dc:title> </cc:Work> </rdf:RDF> </metadata> <g inkscape:label=\"Layer 1\" inkscape:groupmode=\"layer\" id=\"layer1\" transform=\"translate(-56.78125,-397.46875)\"> <rect style=\"fill:none;stroke:#000000;stroke-width:1.88509858;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" id=\"rect2995\" width=\"47.935345\" height=\"26.211853\" x=\"57.72826\" y=\"398.42209\" /> <path style=\"fill:none;stroke:#000000;stroke-width:1.88509858;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" d=\"m 57.907795,399.14023 23.159784,24.59605 24.775581,-24.95511\" id=\"path3769\" inkscape:connector-curvature=\"0\" /> </g> </svg>";
	private static final  String personNodeSvg="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> <!-- Created with Inkscape (http://www.inkscape.org/) --> <svg xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:cc=\"http://creativecommons.org/ns#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\" xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\" width=\"19.78125\" height=\"49.865517\" id=\"svg2\" version=\"1.1\" inkscape:version=\"0.48.4 r9939\" sodipodi:docname=\"drawing.svg,.svg\"> <defs id=\"defs4\" /> <sodipodi:namedview id=\"base\" pagecolor=\"#ffffff\" bordercolor=\"#666666\" borderopacity=\"1.0\" inkscape:pageopacity=\"0.0\" inkscape:pageshadow=\"2\" inkscape:zoom=\"1.4\" inkscape:cx=\"-53.278929\" inkscape:cy=\"170.01595\" inkscape:document-units=\"px\" inkscape:current-layer=\"layer1\" showgrid=\"false\" fit-margin-top=\"0\" fit-margin-left=\"0\" fit-margin-right=\"0\" fit-margin-bottom=\"0\" inkscape:window-width=\"1920\" inkscape:window-height=\"1025\" inkscape:window-x=\"-2\" inkscape:window-y=\"-3\" inkscape:window-maximized=\"1\" /> <metadata id=\"metadata7\"> <rdf:RDF> <cc:Work rdf:about=\"\"> <dc:format>image/svg+xml</dc:format> <dc:type rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" /> <dc:title></dc:title> </cc:Work> </rdf:RDF> </metadata> <g inkscape:label=\"Layer 1\" inkscape:groupmode=\"layer\" id=\"layer1\" transform=\"translate(-141.15625,-457.54073)\"> <path sodipodi:type=\"arc\" style=\"fill:#000000;fill-opacity:1;stroke:none\" id=\"path2989\" sodipodi:cx=\"247.14285\" sodipodi:cy=\"213.43361\" sodipodi:rx=\"70\" sodipodi:ry=\"69.64286\" d=\"m 317.14285,213.43361 c 0,38.46269 -31.34006,69.64286 -70,69.64286 -38.65993,0 -70,-31.18017 -70,-69.64286 0,-38.46269 31.34007,-69.64286 70,-69.64286 38.65994,0 70,31.18017 70,69.64286 z\" transform=\"matrix(0.13745706,0,0,0.13745706,117.28401,437.77568)\" /> <path style=\"fill:none;stroke:#000000;stroke-width:2.06185627;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" d=\"m 151.25555,474.13376 -0.14734,26.70594\" id=\"path3783\" inkscape:connector-curvature=\"0\" /> <path style=\"fill:none;stroke:#000000;stroke-width:2.06185627;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" d=\"m 151.20643,500.49606 -9.5238,5.74375\" id=\"path3785\" inkscape:connector-curvature=\"0\" /> <path style=\"fill:none;stroke:#000000;stroke-width:2.06185627;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" d=\"m 150.86279,500.76606 9.52381,5.74375\" id=\"path3785-6\" inkscape:connector-curvature=\"0\" inkscape:transform-center-x=\"-1.7182004\" /> <path style=\"fill:none;stroke:#000000;stroke-width:2.06185627;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\" d=\"m 160.92662,485.42488 -19.53854,0\" id=\"path3805\" inkscape:connector-curvature=\"0\" /> </g> </svg>";
}
