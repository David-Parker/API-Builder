
public class vInteger extends Control {

	public vInteger(String type, String name, String dataType, Command command, int row) {
		super(type, name, "I32", command, row);
		polCmd = " {<VAL>}";
	}

	public void writeTag() {
		AttributeList al = new AttributeList();
		al.add("Predefined","1");
		al.add("Type",getDataType());
		XMLWriter.write(XMLWriter.createTag("Numeric",al.attributes,"",true));
		
		XMLWriter.write(XMLWriter.closeTag("DataType"));
		al.clear();
		
		al.add("Class","Numeric");
		al.add("DefaultValue","0");
		al.add("Label",getName());
		XMLWriter.write(XMLWriter.createTag("Control",al.attributes,"",true));
	}
	
	public String formatter() {
		return " %d";
	}

}
