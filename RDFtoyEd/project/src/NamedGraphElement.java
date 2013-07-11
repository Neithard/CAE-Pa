
public class NamedGraphElement {
	protected  String name;
	protected final int id;
	public int getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NamedGraphElement(String name, int id) {
		super();
		this.name = name;
		this.id=id;
	}

	public String getName() {
		return name;
	}
	
	
}
