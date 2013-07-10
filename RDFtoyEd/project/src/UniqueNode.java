
public class UniqueNode extends Node {
	private  String uid;
	public UniqueNode(String name, NodeType type, String uid) {
		super(name, type);
		this.uid=uid;

	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}

}
