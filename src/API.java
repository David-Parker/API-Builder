
public class API {

	public static void main(String[] args) {
		for(int i = 0; i < args.length; i ++) {
			System.out.println(args[i]);
		}
		
		SheetReader sr = new SheetReader();
		String[][] data = sr.readSheet("test/test.xlsx");
		SheetParser parser = new SheetParser();
		Folder root = parser.parse(data, sr.getRows(), sr.getCols());
		XMLWriter x = new XMLWriter("test/test1.driver",root);
		x.createXML();
		System.out.println("Done.");
	}
}
