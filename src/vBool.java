
public class vBool extends Control{
	
	public vBool(String type, String name, String dataType, Command command, int row) {
		super(type, name, "Bool", command, row);
		polCmd = " {ON|OFF}";
	}

	public void writeTag() {
		AttributeList al = new AttributeList();
		XMLWriter.write(XMLWriter.createTag("Boolean",null,"",false));
		al.add("Text", "Enabled");
		al.add("Value", "1");
		XMLWriter.write(XMLWriter.createTag("True",al.attributes,"",true));
		al.clear();
		
		al.add("Text", "Disabled");
		al.add("Value", "0");
		XMLWriter.write(XMLWriter.createTag("False",al.attributes,"",true));
		XMLWriter.write(XMLWriter.closeTag("Boolean"));
		al.clear();
		
		XMLWriter.write(XMLWriter.closeTag("DataType"));
		al.add("Class","Slide");
		al.add("DefaultValue","1");
		al.add("Label",getName());
		XMLWriter.write(XMLWriter.createTag("Control",al.attributes,"",true));

	}
	
	public void writeRanges() {
		AttributeList al = new AttributeList();
		al.add("CustomInfo","");
		al.add("HasMax","true");
		al.add("HasMin","true");
		al.add("Type","IVI_VAL_DISCRETE");
		XMLWriter.write(XMLWriter.createTag("RangeTable", al.attributes, "", false));
		al.clear();
		
		writeSingleRange("Enabled",1,0);
		writeSingleRange("Disabled",0,1);
		
		XMLWriter.write(XMLWriter.closeTag("RangeTable"));
		

	}
	
	public static void writeSingleRange(String cmd, int value, int num) {
		XMLWriter.write(XMLWriter.createTag("Ranges", null, "", false));
		XMLWriter.write(XMLWriter.createTag("DiscreteOrMinValue", null, "" + num, true));
		XMLWriter.write(XMLWriter.createTag("MaxValue", null, "", true));
		XMLWriter.write(XMLWriter.createTag("CorcedValue", null, "", true));
		XMLWriter.write(XMLWriter.createTag("CmdString", null, cmd, true));
		XMLWriter.write(XMLWriter.createTag("CmdValue", null, "" + value, true));
		XMLWriter.write(XMLWriter.createTag("Description", null, "", true));
		XMLWriter.write(XMLWriter.closeTag("Ranges"));
	}

	public String formatter() {
		return " %s";
	}
}
