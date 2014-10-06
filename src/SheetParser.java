import java.util.ArrayList;
import java.util.StringTokenizer;

public class SheetParser {
	public static final char leftDelim = '{';
	public static final char rightDelim = '}';
	public static final char comment = '#';
	public static String[] keywords = {"prefix","identifier","technology","manufacturer"};
	public static final int NUM_OF_KEYWORDS = 4;
	public static final String multiLine = "\"\"";
	private static int numDelims;
	private static Folder root;
	private static Folder currFolder;
	private static Vi currVi;
	private static CompileError ce;
	private static ArrayList<Vi> allVis;
	
	private static final boolean DEBUG = false;
	
	public SheetParser() {
		numDelims = 0;
		root = new Folder("Public",null,-1, "Public");
		currFolder = root;
		ce = new CompileError();
		allVis = new ArrayList<Vi>();
	}
	
	public Folder parse(String[][] data, int rows, int cols) {
		parseInstrumentData(data,rows,cols);
		parseFolders(data,rows,cols);
		
		/* The API must enforce the Utility folder for the Template Vis */
		if(!hasUtilityFolder(root)) {
			root.subFolders.add(new Folder("Utility",null,-1,"Public\\Utility"));
		}
		ce.printErrors();
		return root;
	}
	
	public static void parseInstrumentData(String[][] data, int rows, int cols) {
		Instrument inst = Instrument.getInstance();
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				if(data[i][j] != null) {
					if(data[i][j].length() > 7 && data[i][j].substring(0,7).toLowerCase().equals("prefix:")) {
						inst.prefix = parseRecord(data[i][j]);
					}
					else if(data[i][j].length() > 11 && data[i][j].substring(0,11).toLowerCase().equals("identifier:")) {
						inst.identifier = parseRecord(data[i][j]);
					}
					else if(data[i][j].length() > 11 && data[i][j].substring(0,11).toLowerCase().equals("technology:")) {
						inst.technology = parseRecord(data[i][j]);
					}
					else if(data[i][j].length() > 13 && data[i][j].substring(0,13).toLowerCase().equals("manufacturer:")) {
						inst.manufacturer = parseRecord(data[i][j]);
					}
				}
			}
		}
		ce.checkError("Instrument",0,0);
	}
	
	@SuppressWarnings("unused")
	public static void parseFolders(String[][] data, int rows, int cols) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				if(data[i][j] != null) {
					/* Read the formatting */
					if(isComment(data[i][j]) || isKeyword(data[i][j])) continue;
					
					if(data[i][j].charAt(0) == leftDelim) {
						String name = parseFolderName(data[i][j]);
						
						/* Ensure that only one Utility folder exists */
						if(name.toLowerCase().equals("utility"))
							name = "Utility";
						
						Folder newFolder = new Folder(name,currFolder,i,currFolder.getPath() + "\\" + name);
						if(newFolder.getName().equals("")) {
							ce.checkError("Folder",i + 1,CompileError.ERROR_1);
						}
						
						/* Make sure that this folder doesn't currently exist in this scope */
						checkFolderName(newFolder.getName(), i + 1);
						currFolder.subFolders.add(newFolder);
						currFolder = newFolder;
						numDelims++;
					}
					
					if(data[i][j].charAt(data[i][j].length() - 1) == rightDelim) {
						/* Check at the end of the folder if the last command has the right amount of controls */
						if(Command.currCommand != null) 
							Command.currCommand.checkNumControlsAbove(Command.currCommand.row);
						
						numDelims--;
						/* Make sure that if there is an extra bracket, we don't go higher than the root level */
						if(currFolder != root)
							currFolder = currFolder.parent;
					}
					
					/* Check for Vis */
					else if(currFolder != root && (data[i][j].charAt(0) != leftDelim)) {
						String control = new String("");
						String command = new String("");
						if(j + 1 < cols && data[i][j + 1] != null && !isComment(data[i][j+ 1]))
							control = data[i][j + 1];
						if(j + 2 < cols && data[i][j + 2] != null && !isComment(data[i][j+ 2]))
							command = data[i][j+2];
						parseVi(data[i][j],control,command, i + 1);
						break;
					}
				}
			}
		}
		
		ce.checkError("Delim", numDelims, currFolder.row + 1);
		if(DEBUG && ce.numErrors() == 0)
			CompileError.printFolders(root,0);
	}
	
	public static boolean hasUtilityFolder(Folder fold) {
		for(Folder f: fold.subFolders) {
			if(hasUtilityFolder(f))
				return true;
			else
				hasUtilityFolder(f);
		}
		
		if(fold.getName().equals("Utility")){
			return true;
		}
		
		else return false;
	}
	
	public static void parseVi(String name, String control, String command, int row) {
		/* Check if this is a Vi name and not a control name */
		if(!name.equals(multiLine) && 
		   !(name.length() >= 3 && name.substring(0,3).toLowerCase().equals("in:")) &&
		   !(name.length() >= 4 && name.substring(0,4).toLowerCase().equals("out:"))) {
				checkViName(name,row);
				Vi newVi = new Vi(name);
				newVi.setFolder(currFolder);
				
				currVi = newVi;
				
				if(Command.currCommand != null)
					Command.currCommand.checkNumControlsAbove(Command.currCommand.row);
				
				Command.currCommand = null;
				
				/* Check if this new Vi is a template Vi, in which case it will be automatically generated by the XML Writer */
				if(newVi.checkSource())
					return;
				
				currFolder.vis.add(newVi);
				
				/* List of all implemented Vis so we don't duplicate them */
				allVis.add(newVi);
	
		}
		
		/* New Control, add it to currVi */
		if(currVi != null) {
			
			/* Parse the control */
			if(control.length() >= 3 && control.substring(0,3).toLowerCase().equals("in:") ||
			   (control.length() >= 4 && control.substring(0,4).toLowerCase().equals("out:"))) {
				Control cont = parseControl(control, command, row);
				
				if(cont == null)
					return;
				
				/* Check if a control with this name already exists for this Vi */
				checkControlName(cont.getName(), row);
				
				currVi.controls.add(cont);
			}
			
			else if(control.toLowerCase().equals("none")) {
				if(command.equals(multiLine)) {
					ce.checkError("Command", row, CompileError.ERROR_1);
				}
				
				command = command.replaceAll("<","&lt;");
				command = command.replaceAll(">","&gt;");
				
				/* The default type for a none control is a double */
				Control cont = new vDouble("none","","",(new Command(command,row)), row);
				currVi.controls.add(cont);
			}
			
			else if(!control.equals("")) {
				ce.checkError("Control", row, CompileError.ERROR_2);
			}
		}
		
		/* We found a control before finding it's corresponding Vi */
		else {
			ce.checkError("Control", row, CompileError.ERROR_1);
		}
	}
	
	public static void checkControlName(String control, int row) {
		for(Control c: currVi.controls) {
			if(c.getName().equals(control)) {
				ce.checkError("Control",row,CompileError.ERROR_3);
			}
		}
	}

	public static Control parseControl(String control, String command, int row) {
		/* XML Can't have < and > in names */
		command = command.replaceAll("<","&lt;");
		command = command.replaceAll(">","&gt;");
		
		/* strs is in order of Type, Name, dataType, ring */
		String[] strs = new String[4];
		int count = 0;
		
		if(!Control.isValidControlName(control)) {;
			ce.checkError("Control", row, CompileError.ERROR_2);
			return null;
		}
		
		StringTokenizer strTok = new StringTokenizer(control, ":");
		
		while(strTok.hasMoreTokens()) {
			if(count > 3) {
				ce.checkError("Control", row, CompileError.ERROR_2);
				return null;
			}
			strs[count] = strTok.nextToken().trim();
			count++;
		}
		
		if(strs[0].toLowerCase().equals("in")) strs[0] = "INPUT";
		else if(strs[0].toLowerCase().equals("out")) strs[0] = "OUTPUT";
		/* Not a recognized type */
		else {
			ce.checkError("Control", row, CompileError.ERROR_2);
			return null;
		}
		
		if(strs[2] == null || !isValidDataType(strs[2])) {
			ce.checkError("Control", row, CompileError.ERROR_2);
			return null;
		}
		
		/* Not a ring or boolean, send empty string */
		if(strs[3] == null) strs[3] = "";
		
		if(!(command.equals("")))
			root.numCommands++;
		
		/* Convert data types to their 3 letter Driver acronym */
		strs[2] = Control.fixDataTypes(strs[2]);
		
		/* If this is a multi-line command, grab the current command's name */
		Command newCommand = null;
		
		if(command.equals(multiLine)) {
			if(Command.currCommand == null) {
				CompileError ce = new CompileError();
				ce.checkError("Command", row, CompileError.ERROR_1);
				return null;
			}
			else {
				Command.currCommand.controlsFound++;
				newCommand = Command.currCommand;
			}
		}
		else {	
			if(Command.currCommand != null)
				Command.currCommand.checkNumControlsAbove(Command.currCommand.row);
			
			newCommand = new Command(command,row);
			Command.currCommand = newCommand;
		}
		
		/*Check if this command still has the correct number of entries */
		newCommand.checkNumControlsBelow(row);
		
		switch(strs[2]) {
		case "Int":
			return new vInteger(strs[0],strs[1],strs[2],newCommand,row);
		case "Double":
			return new vDouble(strs[0],strs[1],strs[2],newCommand,row);
		case "String":
			return new vString(strs[0],strs[1],strs[2],newCommand,row);
		case "Bool":
			return new vBool(strs[0],strs[1],strs[2],newCommand,row,strs[3]);
		case "Ring":
			return new vRing(strs[0],strs[1],strs[2],newCommand,row,strs[3]);
		default:
			throw new IllegalArgumentException("Should not have validated this type!");
		}
		
	}
	
	public static void checkFolderName(String name, int row) {
		for(Folder f: currFolder.subFolders) {
			if(name.toLowerCase().equals(f.getName().toLowerCase())) 
				ce.checkError("Folder", row, CompileError.ERROR_2);
		}
	}
	
	public static void checkViName(String name, int row) {
		for(Vi v: allVis) {
			if(name.equals(v.getName())) {
				ce.checkError("Vi", row, 0);
			}
		}
	}
	
	public static boolean isValidDataType(String str) {
		for(int i = 0; i < Control.NUM_DATA_TYPES; i++) {
			if(str.toLowerCase().equals(Control.dataTypes[i]))
				return true;
		}
		return false;
	}
	
	public static boolean isComment(String str) {
		return str.charAt(0) == comment;
	}
	
	public static boolean isKeyword(String str) {
		for(int i = 0; i < NUM_OF_KEYWORDS; i++) {
			if(str.toLowerCase().equals(keywords[i]))
				return true;
		}
		return false;
	}
	
	public static String parseFolderName(String name) {
		String newStr = new String("");
		for(int i = 0; i < name.length(); i++) {
			if(name.charAt(i) != leftDelim && name.charAt(i) != rightDelim) newStr = newStr + name.charAt(i);
		}
		
		return newStr;
	}
	
	public static String parseRecord(String str) {
		char[] newStr = null;
		int index = 0;
		boolean zeros = false;
		boolean start = false;
		
		/* Creates a new string that is the data after the : and ignores all leading spaces */
		for(int i = 0; i < str.length(); i++) {
			if(!start && zeros && (str.charAt(i) != ' ')){
				start = true;
				newStr = new char[str.length() - i];
			}
			
			if(str.charAt(i) == ':') zeros = true;
			if(start) {
				newStr[index] = str.charAt(i);
				index++;
			}
		}
		if(newStr == null) return null;
		return new String(newStr);
	}

	
	public static void printData(String[][] data, int row, int col) {
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				if(data[i][j] != null)
					System.out.print(data[i][j] + "\t");
			}
			System.out.println("");
		}
	}
}
