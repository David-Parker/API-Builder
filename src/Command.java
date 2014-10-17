import java.util.ArrayList;


public class Command {
	private String name;
	private int numControls;
	private static final char space = ' ';
	public int controlsFound;
	public static Command currCommand;
	public int row;
	public String polishedCommand;
	public static boolean shortSCPI = false;
	
	
	public Command(String name, int row) {
		/* Check if the user specified a multi-control command */
		controlsFound = 1;
		this.name = name;
		this.row = row;
		numControls = findNumControls();
	}
	
	public String getName() {
		return name;
	}
	
	public int findNumControls() {
		/* Method returns the number of controls the user must specify for this command */
		if(name.equals("")) return 1;
		
		int spaceCount = 0;
		for (char c : name.trim().toCharArray()) {
		    if (c == ' ') {
		         spaceCount++;
		    }
		}
		return spaceCount;
	}
	
	public void checkNumControlsBelow(int row) {
		/* The number of controls from findNumControls was below the number actually specified by the user */
		if(controlsFound > numControls) {
			CompileError ce = new CompileError();
			ce.checkError("Command", row, CompileError.ERROR_2);
		}
	}
	
	public void checkNumControlsAbove(int row) {
		/* The number of controls from findNumControls was above the number actually specified by the user */
		if(controlsFound < numControls) {
			CompileError ce = new CompileError();
			ce.checkError("Command", row, CompileError.ERROR_3);
		}
	}
	
	public String getFormattedCommand(ArrayList<Control> controls) {
		/* Command formatted in the IDDS specified way */
		String formCmd = "";
		for(int i = 0; i < name.length(); i++) {
			if(name.charAt(i) == ' ')
				break;
			
			/* Use the Short SCPI version of this command, not the whole command */
			if(shortSCPI) {
				if(isParseCharacter(name.charAt(i)))
					formCmd += name.charAt(i);
			}
			else {
				formCmd += name.charAt(i);
			}
		}
		
		for(Control c: controls) {
			formCmd += c.formatter();
		}
		return formCmd + ";";
	}
	
	public String getPolishedCommand(ArrayList<Control> controls) {
		/* Similar to getFormattedCommand, but always formats in the short SCPI format */
		String polCmd = new String("");
		
		for(int i = 0; i < name.length(); i++) {
			if(name.charAt(i) == ' ')
				break;
			
			if(isParseCharacter(name.charAt(i))) {
				polCmd += name.charAt(i);
			}
		}
		
		int startOffset = polCmd.length() + 2;
		int endOffset = startOffset;
		
		for(Control c: controls) {
			polCmd += c.polishCommand();
			endOffset = startOffset + c.polishedSize();
			c.startOffset = startOffset;
			c.endOffset = endOffset;

			startOffset = endOffset + 2;
		}
		
		polCmd = polCmd.replaceAll("<","&lt;");
		polCmd = polCmd.replaceAll(">","&gt;");
		return polCmd;
	}
	
	public static boolean isParseCharacter(char c) {
		char[] delims = {':','(',')'};
		for(char ch: delims) {
			if(c == ch || Character.isUpperCase(c))
				return true;
		}
		return false;
	}
}
