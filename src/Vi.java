import java.util.ArrayList;

public class Vi {
	private String name;
	private String source;
	private Folder folder;
	public ArrayList<Control> controls;
	public static String[] templateVis = {"Self-Test","Self Test","Revision Query","Reset","Error Query","VI Tree","Close","Initialize"};
	public static final int NUM_TEMPLATE_VIS = 8;
	
	public Vi(String name) {
		this.name = name;
		controls = new ArrayList<Control>();
		source = new String();
	}
	
	public String getName() {
		return name;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String type) {
		source = type;
	}
	
	public void setFolder(Folder fold) {
		folder = fold;
	}
	
	public Folder getFolder() {
		return folder;
	}
	
	public boolean checkSource() {
		for(int i =0 ; i < Vi.NUM_TEMPLATE_VIS; i++) {
			if(name.toLowerCase().equals(Vi.templateVis[i].toLowerCase())) {
				source = "Template";
				return true;
			}
		}
		if(!(source.equals("Template")))
			source = "Customized";
		
		return false;
	}
}
