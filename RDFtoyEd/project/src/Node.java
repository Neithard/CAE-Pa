import java.util.List;



public class Node extends NamedGraphElement {
	private static int nodeIdCounter=0;
	private final int id;
	private List<Edge> edges;
	
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public void addEdge(Edge e)
	{
		edges.add(e);
	}


	public int getId() {
		return id;
	}


	public Node(String name){
		super(name);
		id=nodeIdCounter;
		nodeIdCounter++;
	}

}
