package transformation;
import java.util.ArrayList;
import java.util.List;

public class Node extends NamedGraphElement {
	private static int nodeIdCounter=0;
	private List<Edge> edges;
	
	

	public List<Edge> getEdges() {
		return edges;
	}
	
	public void addEdge(Edge e)
	{
		edges.add(e);
		System.out.println("New Edge: " + this.name.toString()  + " " + e.name + " " + e.getTarget().getName());

	}

	public Node(String name){
		super(name,nodeIdCounter);
		nodeIdCounter++;
		edges=new ArrayList<Edge>();

		System.out.println("New Node: " + this.name);
	}

}
