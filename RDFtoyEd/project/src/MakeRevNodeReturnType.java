
public class MakeRevNodeReturnType {
	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	private Node node;
	private String date;
	
	public MakeRevNodeReturnType(String date, Node node) {
		setDate(date);
		setNode(node);
	}
	
	public MakeRevNodeReturnType()
	{
		setDate("");
		setNode(new Node("",NodeType.EMPTY));
	}

}
