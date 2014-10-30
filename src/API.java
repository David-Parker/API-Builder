/* Main class for API Builder */

public class API {
	private static String[] commands = {"-t","-s"};

	public static void main(String[] args) {
		
		if(args.length < 1)
			usage();
		
		String path = args[0];
		
		if(args.length >= 2) {
			for(int i = 1; i < args.length; i++) {
				if(isCommandAndIssueRequest(args[i]));
				else {
					usage();
				}
			}
		}
		
		SheetReader sr = new SheetReader();
		String[][] data = sr.readSheet(path);
		SheetParser parser = new SheetParser();
		Folder root = parser.parse(data, sr.getRows(), sr.getCols());
		XMLWriter x = new XMLWriter("../spec.driver",root);
		x.createXML();
		System.out.println("Done, spec.driver was created in your API Builder directory.");
	}
	
	public static void usage() {
		System.out.println("[Usage]: path/to/apibuilder.jar path/to/file.xlsx [-t]");
		System.exit(0);
	}
	
	public static boolean isCommandAndIssueRequest(String s) {
		for(int i = 0; i < commands.length; i++) {
			if(s.equals(commands[i])) {
				if(s.equals("-t")) {
					XMLWriter.generateTemplate = false;
					return true;
				}
				else if(s.equals("-s")) {
					Command.shortSCPI = true;
					return true;
				}
			}
		}
		return false;
	}
}
