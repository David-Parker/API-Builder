
public class vString extends Control{
	
	public vString(String type, String name, String dataType, Command command, int row) {
		super(type, name, "String", command, row);
		polCmd = " {<VAL>}";
	}
	
	public String getDescription() {
		String description = new String("The " + name + " control specifies a user defined string value.\n\n" + "Valid Values:\n\n");
		
		description += "Default Value:\n&quot;&quot;"; 
		
		return description;
	}

	public void writeTag() {
		AttributeList al = new AttributeList();
		al.add("Dimension", "-1");
		al.add("Encoding","ASCII");
		XMLWriter.write(XMLWriter.createTag("String",al.attributes,"",true));
		
		XMLWriter.write(XMLWriter.closeTag("DataType"));
		al.clear();
		
		al.add("Class","String");
		al.add("DefaultValue","&quot;&quot;");
		al.add("Label",getName());
		XMLWriter.write(XMLWriter.createTag("Control",al.attributes,"",true));
	}
	
	public String formatter() {
		return " %s";
	}
}
