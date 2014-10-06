
public class API {

	public static void main(String[] args) {
		
		if(args.length < 1 || args.length > 2)
			usage();
		
		String path = args[0];
		
		if(args.length == 2) {
			if(args[1].equals("-t")) {
				XMLWriter.generateTemplate = false;
			}
			else {
				usage();
			}
		}
		
		SheetReader sr = new SheetReader();
		String[][] data = sr.readSheet(path);
		SheetParser parser = new SheetParser();
		Folder root = parser.parse(data, sr.getRows(), sr.getCols());
		XMLWriter x = new XMLWriter("./spec.driver",root);
		x.createXML();
		System.out.println("Done.");
	}
	
	public static void usage() {
		System.out.println("[Usage]: path/to/apibuilder.jar path/to/file.xlsx [-t]");
		System.exit(0);
	}
}
