import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class RDFforGood {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		//DOM Paser ertsllen		
		File fXmlFile = new File("./res/Export.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc =  dBuilder.parse(fXmlFile);
		
		doc.getDocumentElement().normalize();
		
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		
		NodeList nList = doc.getElementsByTagName("Geräte");
		 
		System.out.println("----------------------------");
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			 
			Node nNode = nList.item(temp);
	 
			System.out.println("\nCurrent Element :" + nNode.getNodeName());
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
	 
				System.out.println("UID : " + eElement.getElementsByTagName("UID").item(0).getTextContent());
				System.out.println("Name : " + eElement.getElementsByTagName("Name").item(0).getTextContent());
				System.out.println("Kennzeichnung : " + eElement.getElementsByTagName("Kennzeichnung").item(0).getTextContent());
				System.out.println("Stammobjekt : " + eElement.getElementsByTagName("Stammobjekt").item(0).getTextContent());
				
				NodeList pNode = nNode.getChildNodes();
				
				for(int j = 0 ; j < pNode.getLength() ; j++ )
	              {
	                  Node rNode = pNode.item(j);
	                  if(rNode.getNodeType() == Node.ELEMENT_NODE)
	                  {
	                	  
	                	  Element rElement = (Element) rNode;
				
	                	  System.out.println(rElement.getNodeName() + ": " + rElement.getTextContent());
	                  
	                  }
	              }

			}
		}
	}
	
	protected static Node getNode(String tagName, NodeList nodes) {
	    for ( int x = 0; x < nodes.getLength(); x++ ) {
	        Node node = nodes.item(x);
	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
	            return node;
	        }
	    }
	 
	    return null;
	}
}
