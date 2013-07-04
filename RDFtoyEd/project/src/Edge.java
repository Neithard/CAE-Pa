
public class Edge extends NamedGraphElement {
	private static int edgeIdCounter=0;
	private final int id;
	public int getId() {
		return id;
	}

	public Node getTarget() {
		return target;
	}

	private final Node target;
	
	public Edge(String name, Node target) {
		super(name);
		this.target=target;
		id=edgeIdCounter;
		edgeIdCounter++;
	}

}
