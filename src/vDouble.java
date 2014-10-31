
public class vDouble extends Control{
	
	public vDouble(String type, String name, String dataType, Command command, int row) {
		super(type, name, "DBL", command, row);
		polCmd = " {<VAL>}";
	}
	
	public String getDescription() {
		String description = new String("The " + name + " control specifies a user defined double precision floating point value.\n\n" + "Valid Values:\n\n");
		
		description += "Default Value:\n0.0"; 
		
		return description;
	}

	public void writeTag() {
		AttributeList al = new AttributeList();
		al.add("Predefined","1");
		al.add("Type",getDataType());
		XMLWriter.write(XMLWriter.createTag("Numeric",al.attributes,"",true));
		
		XMLWriter.write(XMLWriter.closeTag("DataType"));
		al.clear();
		
		al.add("Class","Numeric");
		al.add("DefaultValue","0.0");
		al.add("Label",getName());
		XMLWriter.write(XMLWriter.createTag("Control",al.attributes,"",true));
	}
	
	public String formatter() {
		return " %g";
	}

}
