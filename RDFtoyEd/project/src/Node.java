import java.util.ArrayList;
import java.util.List;

public class Node extends NamedGraphElement {
	private static int nodeIdCounter=0;
	private final int id;
	private List<Edge> edges;
	private NodeType type;
	
	
	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public List<Edge> getEdges() {
		return edges;
	}
	
	public void addEdge(Edge e)
	{
		edges.add(e);
		System.out.println("New Edge: " + this.name.toString()  + " " + e.name + " " + e.getTarget().getName());

	}


	public int getId() {
		return id;
	}


	public Node(String name, NodeType type){
		super(name);
		this.id=Node.nodeIdCounter;
		nodeIdCounter++;
		edges=new ArrayList<Edge>();
		this.type=type;
		
		System.out.println("New " + this.type.toString() + ": " + this.name);
	}

}
