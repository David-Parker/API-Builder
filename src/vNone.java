public class vNone extends Control {

	public vNone(String type, String name, String dataType, Command command, int row) {
		super(type, name, "I32", command, row);
		polCmd = "";
	}

	public void writeTag() {
		/* Empty */
	}
	
	public String formatter() {
		return "";
	}

}