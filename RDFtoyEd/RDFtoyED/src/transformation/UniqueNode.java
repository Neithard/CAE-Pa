package transformation;

public class UniqueNode extends Node {
	private  String uid;
	public UniqueNode(String name, String uid) {
		super(name);
		this.uid=uid;

	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}

}
