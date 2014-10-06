import java.util.ArrayList;
import java.util.StringTokenizer;

public class vBool extends Control{
	private ArrayList<String> entries;
	
	public vBool(String type, String name, String dataType, Command command, int row, String boolValue) {
		super(type, name, "Bool", command, row);
		entries = parseRingEntries(boolValue,row);
		polCmd = createRingCmd();
	}

	public void writeTag() {
		AttributeList al = new AttributeList();
		XMLWriter.write(XMLWriter.createTag("Boolean",null,"",false));
		al.add("Text", entries.get(0));
		al.add("Value", "1");
		XMLWriter.write(XMLWriter.createTag("True",al.attributes,"",true));
		al.clear();
		
		al.add("Text", entries.get(1));
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
		
		writeSingleRange(entries.get(0),1,0);
		writeSingleRange(entries.get(1),0,1);
		
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
	
	public ArrayList<String> parseRingEntries(String ringValue, int row) {
		ringValue = prepareValues(ringValue);
		ArrayList<String> newEntries = new ArrayList<String>();
		StringTokenizer strTok = new StringTokenizer(ringValue, "|, ");
		
		while(strTok.hasMoreTokens()) {
			newEntries.add(strTok.nextToken());
		}
		
		if(newEntries.size() != 2) {
			/* A boolean should have strictly two opposite values */
			CompileError ce = new CompileError();
			ce.checkError("Bool", row, CompileError.ERROR_1);
		}
		
		return newEntries;
	}
	
	public String prepareValues(String ringValue) {
		ringValue = ringValue.replace("{", "");
		ringValue = ringValue.replace("}", "");
		ringValue = ringValue.replace("(", "");
		ringValue = ringValue.replace(")", "");
		
		return ringValue;
	}
	
	public String createRingCmd() {
		String cmd = " {";
		for(String s: entries) {
			cmd += s + "|";
		}
		int lastBar = cmd.lastIndexOf('|');
		if(lastBar > 0)
			cmd = cmd.substring(0,lastBar);
		
		cmd += "}";
		return cmd;
	}

	public String formatter() {
		return " %s";
	}
}
