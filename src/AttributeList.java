import java.util.ArrayList;

public class AttributeList {
	public ArrayList<Attribute> attributes;
	
	public AttributeList() {
		attributes = new ArrayList<Attribute>();
	}
	
	public void add(String name, String value) {
		attributes.add(new Attribute(name,value));
	}
	
	public void clear() {
		attributes.clear();
	}
}
