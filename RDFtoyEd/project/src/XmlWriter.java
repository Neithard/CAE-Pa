import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlWriter {
	private String OutPutPath;
	private Document xmlDoc;
	private static final String nodeIdChar="n";
	private static final String portIdChar="d";
	private int nodeCounter;
	
	public XmlWriter(String OutPutPath) {
		this.OutPutPath=OutPutPath;
		nodeCounter=0;
		
		
	}
	 

}
