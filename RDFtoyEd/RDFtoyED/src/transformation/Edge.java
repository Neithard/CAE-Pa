package transformation;

public class Edge extends NamedGraphElement {
	private static int edgeIdCounter=0;

	public Node getTarget() {
		return target;
	}

	private final Node target;
	
	public Edge(String name, Node target) {
		super(name,edgeIdCounter);
		this.target=target;
		edgeIdCounter++;
	}

}
