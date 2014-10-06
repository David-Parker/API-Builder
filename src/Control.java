
public abstract class Control {
	private String type;
	private String name;
	private String dataType;
	private Command command;
	protected String polCmd;
	public final static String[] dataTypes = {"double","dbl","integer","int","string","boolean","bool","ring"};
	public final static int NUM_DATA_TYPES = 8;
	public int startOffset;
	public int endOffset;
	public int row;
	
	public Control(String type, String name, String dataType, Command command, int row) {
		this.type = type;
		this.name = name;
		this.dataType = dataType;
		this.command = command;
		this.row = row;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public Command getCommand() {
		return command;
	}
	
	public String getDataType() {
		return dataType;
	}
	
	public String polishCommand() {
		return polCmd;
	}
	
	public int polishedSize() {
		return polCmd.length() - 2;
	}
	
	public void writeRanges() {
		
	}
	
	/* Each type of control will write out its own tag */
	public abstract void writeTag();
	public abstract String formatter();
	
	public static String fixDataTypes(String str) {
		if(str.toLowerCase().equals("int") || str.toLowerCase().equals("integer")) {
			return "Int";
		}
		
		else if(str.toLowerCase().equals("double") || str.toLowerCase().equals("dbl")) {
			return "Double";
		}
		
		else if(str.toLowerCase().equals("string")) {
			return "String";
		}
		
		else if(str.toLowerCase().equals("boolean") || str.toLowerCase().equals("bool")) {
			return "Bool";
		}
		
		else if(str.toLowerCase().equals("ring")) {
			return "Ring";
		}
		
		return str;
	}
	
	public static boolean isValidControlName(String name) {
		char[] arr = name.toCharArray();
		int count = 0;
		
		for(int i = 0; i < name.length(); i++) {
			if(arr[i] == ':') count++;
		}
		
		return (count >= 2 && count < 4);
	}
}
