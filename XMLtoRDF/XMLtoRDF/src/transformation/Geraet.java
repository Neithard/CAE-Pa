package transformation;
public class Geraet {
	
	private String uid;
	private String name;
	private String description;
	
	public Geraet(String tmp_uid, String tmp_name, String tmp_description) {
		this.uid = tmp_uid;
		this.name = tmp_name;
		this.description =  tmp_description;		
	}
	
	public String getUid() {
		return uid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
