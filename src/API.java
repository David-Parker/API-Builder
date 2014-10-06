
public class API {

	public static void main(String[] args) {
		SheetReader sr = new SheetReader();
		String[][] data = sr.readSheet("test/test.xlsx");
		SheetParser parser = new SheetParser();
		Folder root = parser.parse(data, sr.getRows(), sr.getCols());
		XMLWriter x = new XMLWriter("./test1.driver",root);
		x.createXML();
		System.out.println("Done.");
	}
}
