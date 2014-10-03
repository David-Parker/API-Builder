
public class Instrument {
	public String prefix;
	public String identifier;
	public String technology;
	public String manufacturer;
	
	private static Instrument singleton = new Instrument();
	
	private Instrument() {};
	
	public static Instrument getInstance() {
		return singleton;
	}
}
