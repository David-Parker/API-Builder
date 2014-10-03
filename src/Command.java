import java.util.ArrayList;


public class Command {
	private String name;
	private int numControls;
	public int controlsFound;
	public static Command currCommand;
	public int row;
	public String polishedCommand;
	
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
		/* TODO Temp function */
		
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
		//System.out.println(controlsFound + " " + numControls );
		if(controlsFound > numControls) {
			//System.out.println("Below");
			CompileError ce = new CompileError();
			ce.checkError("Command", row, CompileError.ERROR_2);
		}
	}
	
	public void checkNumControlsAbove(int row) {
		//System.out.println(controlsFound + " " + numControls );
		if(controlsFound < numControls) {
			//System.out.println("Above");
			CompileError ce = new CompileError();
			ce.checkError("Command", row, CompileError.ERROR_3);
		}
	}
	
	public String getFormattedCommand(ArrayList<Control> controls) {
		String formCmd = "";
		for(int i = 0; i < name.length(); i++) {
			if(name.charAt(i) == ' ')
				break;
			
				formCmd += name.charAt(i);
		}
		
		for(Control c: controls) {
			formCmd += c.formatter();
		}
		return formCmd + ";";
	}
	
	public String getPolishedCommand(ArrayList<Control> controls) {
		String polCmd = new String("");
		
		for(int i = 0; i < name.length(); i++) {
			if(isParseChracter(name.charAt(i))) {
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
			//System.out.println("Start:  " + startOffset + " End: " + endOffset);
		}
		
		polCmd = polCmd.replaceAll("<","&lt;");
		polCmd = polCmd.replaceAll(">","&gt;");
		return polCmd;
	}
	
	public static boolean isParseChracter(char c) {
		char[] delims = {':','(',')'};
		for(char ch: delims) {
			if(c == ch || Character.isUpperCase(c))
				return true;
		}
		return false;
	}
}
