import java.util.ArrayList;
import java.util.StringTokenizer;

public class vRing extends Control {
	private ArrayList<String> entries;
	
	public vRing(String type, String name, String dataType, Command command, int row, String ringValue) {
		super(type,name,"I32",command,row);
		entries = parseRingEntries(ringValue);
		polCmd = createRingCmd();
	}

	public void writeTag() {
		AttributeList al = new AttributeList();
		al.add("HasRangeTable","false");
		al.add("Identifier","");
		al.add("NumberOfMembers","" + entries.size());
		XMLWriter.write(XMLWriter.createTag("Enumeration",al.attributes,"",false));
		XMLWriter.write(XMLWriter.createTag("MemberType",null,"",false));
		al.clear();
		
		al.add("Predefined","1");
		al.add("Type",getDataType());
		XMLWriter.write(XMLWriter.createTag("Numeric",al.attributes,"",true));
		XMLWriter.write(XMLWriter.closeTag("MemberType"));
		
		int num = 0;
		for(String s: entries) {
			al.clear();
			al.add("Identifier",s);
			al.add("Value","" + num);
			XMLWriter.write(XMLWriter.createTag("Member",al.attributes,"",true));
			num++;
		}
		
		XMLWriter.write(XMLWriter.closeTag("Enumeration"));
		XMLWriter.write(XMLWriter.closeTag("DataType"));
		al.clear();
		
		al.add("Class","Ring");
		al.add("DefaultValue","0");
		al.add("Label",getName());
		XMLWriter.write(XMLWriter.createTag("Control",al.attributes,"",true));
		
	}
	
	public String formatter() {
		return " %d";
	}
	
	public ArrayList<String> parseRingEntries(String ringValue) {
		ringValue = prepareValues(ringValue);
		ArrayList<String> newEntries = new ArrayList<String>();
		StringTokenizer strTok = new StringTokenizer(ringValue, "|, ");
		
		while(strTok.hasMoreTokens()) {
			newEntries.add(strTok.nextToken());
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
}
