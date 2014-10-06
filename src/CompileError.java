import java.util.ArrayList;

public class CompileError {
	private static boolean forceContinue;
	private static ArrayList<String> errors;
	public static final int ERROR_1 = 0;
	public static final int ERROR_2 = 1;
	public static final int ERROR_3 = 2;

	
	public CompileError() {
		forceContinue = false;
		errors = new ArrayList<String>();
	}
	
	public void checkError(String type, int arg1, int arg2) {
		if(type.equals("Instrument")) {
			Instrument inst = Instrument.getInstance();
			if(inst.prefix == null) message("Instrument Prefix has not been set.");
			if(inst.identifier == null) message("Instrument Identifier has not been set.");
			if(inst.technology == null) message("Instrument Technology has not been set.");
			if(inst.manufacturer == null) message("Instrument Manufacturer has not been set.");
		}
		else if(type.equals("Delim")) {
			if(arg1 > 0) message("Missing closing brace for line " + arg2 + ".");
			else if (arg1 < 0) message("Extra brace found.");
		}
		else if(type.equals("Control")) {
			if(arg2 == ERROR_1)
				message("Control found without a Vi at line " + arg1 + ".");
			else if (arg2 == ERROR_2)
				message("Invalid control specification at line " + arg1 + ".");
			else if (arg2 == ERROR_3)
				message("Duplicate control name in the same Vi at line " + arg1 + ".");
		}
		
		else if(type.equals("Folder")) {
			if(arg2 == ERROR_1)
				message("Folders cannot have empty names at line " + arg1 + ".");
			else if (arg2 == ERROR_2)
				message("Duplicate folder name in the same scope found at line " + arg1 + ".");
			
		}
		
		else if(type.equals("Vi")) {
			message("Duplicate Vi name at line " + arg1 + ".");
		}
		
		else if(type.equals("File")) {
			if(arg1 == ERROR_1)
				message("Missing template.data file, please re-download this file and verify it is in your data folder.");
			
			else if(arg1 == ERROR_2)
				message("Possibly corrupt template.data file, re-download this file and verify it is in your data folder.");
		}
		
		else if(type.equals("Command")) {
			if(arg2 == ERROR_1)
				message("No command specified at line " + arg1 + ".");
			else if(arg2 == ERROR_2)
				message("The number of controls found exceeded the command's specifications at line " + arg1 + ".");
			else if(arg2 == ERROR_3)
				message("The number of controls found was less than the command's specifications at line " + arg1 + ".");
		}
		
		else if(type.equals("Bool")) {
			if(arg2 == ERROR_1)
				message("A boolean control does not have the specified two values at line " + arg1 + ".");
		}
	}
	
	public void checkError(String type, int arg1, String str) {
		if(type.equals("Instrument")) {

		}
		else if(type.equals("Delim")) {

		}
		else if(type.equals("Control")) {
			if(str.equals(""))
				message("Invalid control name at line " + arg1 + ".");
		}
	}
	
	public static void message(String str) {
		errors.add("[Compile Error]: " + str);
	}
	
	public void force() {
		forceContinue = true;
	}
	
	public int numErrors() {
		return errors.size();
	}
	
	public void printErrors() {
		if(errors.size() > 0) {
			for(String s: errors) {
				System.out.println(s);
			}
			if(!forceContinue) {
				System.out.println("[Compile Error]: " + errors.size() + " errors found. Compile failed, exiting program.");
				System.exit(-1);
			}
		}	
	}
	
	public static void printFolders(Folder f, int tabs) {
		for(int i = 0; i < tabs; i++)
			System.out.print("   ");
		
		System.out.println(f.getName());
		
		for(Vi v: f.vis) {
			for(int i = 0; i < tabs; i++)
				System.out.print("   ");
			
			System.out.println("--" + v.getName());
			
			for(Control c: v.controls) {
				for(int i = 0; i < tabs; i++)
					System.out.print("   ");
				if(c.getType().equals("INPUT"))
					System.out.println(" > " + c.getName() + " = " + c.getCommand().getName());
				else 
					System.out.println(" < " + c.getName() + " = " + c.getCommand().getName());
			}
		}
		
		for(Folder nf: f.subFolders) {
			printFolders(nf,tabs + 1);
		}
	}
}
