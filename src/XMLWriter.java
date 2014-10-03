import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;


public class XMLWriter {
	private static PrintStream specFile;
	private static Folder root;
	private static CompileError ce;
	public static boolean generateTemplate = true;
	
	public static final boolean toConsole = false;
	
	public XMLWriter(String file, Folder fold) {
		root = fold;
		ce = new CompileError();
		
		if(toConsole)
			specFile = System.out;
		else {
			try {
				specFile = new PrintStream(new File(file));
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createXML() {
		writeMetaData();
		writeInstrumentInfo();
		writeFolders(root);
		writeFunctions();
		writeCommands();
		write(closeTag("IDSpecification"));
	}
	
	public static String createTag(String name, ArrayList<Attribute> att, String contents, boolean close) {
		String formatted = "<" + name;
		
		if(att != null) {
			for(Attribute a: att) {
				formatted += " " + a.name + "=\"" + a.value + "\"";
			}
		}
		
		formatted += ">" + contents;
		
		if(close)
			formatted += closeTag(name);
		
		return formatted;
	}
	
	public static String closeTag(String name) {
		return "</" + name + ">";
	}
	
	public static void writeMetaData() {
		Instrument instr = Instrument.getInstance();
		write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
		write("<!-- This Driver Specification file was created with API Builder -->");
		write("<IDSpecification xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Author=\"Liping\" Company=\"String\" Fax=\"String\" Phone=\"String\" Version=\"String\" xsi:noNamespaceSchemaLocation=\"ID%20Specification.xsd\">");
		AttributeList al = new AttributeList();
		al.add("Identifier",instr.identifier);
		al.add("Template","C:\\Program Files\\National Instruments\\LabVIEW 8.6\\instr.lib\\_Template - Generic");
		write(createTag("Application",al.attributes,"",false));
		write(closeTag("Application"));
		write("<ModificationHistory>");
		write("<Creation Application=\"text\" Comments=\"String\" Date=\"1967-08-13\"/>");
		write("<Modification Application=\"String\" Comments=\"\" Date=\"2014-09-25\"/>");
		write("</ModificationHistory>");
	}
	
	public static void writeInstrumentInfo() {
		Instrument inst = Instrument.getInstance();
		write(createTag("General",null,"",false));
		write(createTag("Prefix",null,inst.prefix,true));
		write(createTag("Identifier",null,inst.identifier,true));
		write(createTag("Technology",null,"LabVIEW PNP",true));
		write(createTag("Manufacturer",null,inst.manufacturer,true));
		write("<InstrumentModels><InstrumentModel/></InstrumentModels><CommunicationInterface><IndustoryBus>USB</IndustoryBus></CommunicationInterface><ModelTested><InstrumentModel/></ModelTested><FirmwareTested><Manufacturer/><InstrumentModel/><SerialNO/><BuildVersion/></FirmwareTested><InterfaceTested><IndustoryBus>USB</IndustoryBus></InterfaceTested>");
	}
	
	public static void writeFolders(Folder fold) {
		write(createTag("Folders",null,"",false));
		writeFoldersRecurse(fold);
		write(closeTag("Folders"));
		write(closeTag("General"));
	}
	
	public static void writeFoldersRecurse(Folder fold) {	
		write(createTag("FolderPath",null,fold.getPath(),true));
		
		for(Folder f: fold.subFolders) {
			writeFoldersRecurse(f);
		}
	}
	
	public static void writeFunctions() {
		write("<API Version=\"String\">");
		write("<Interface Identifier=\"String\">");
		
		if(generateTemplate)
			writeTemplateFunctions();
		
		writeCustomFunctions();
		write(closeTag("Interface"));
		write(closeTag("API"));
	}
	
	public static void writeTemplateFunctions() {
		try {
			FileReader reader = new FileReader("data/template.data");
			BufferedReader br = new BufferedReader(reader);
			String line;
			
			while((line = br.readLine()) != null) {
				write(line);
			}
			
			br.close();
		}
		
		catch(FileNotFoundException ex) {
			/* Missing template.data file */
			ce.checkError("File",CompileError.ERROR_1,0);
		}
		
		catch(IOException ex) {
			/* Corrupted Template data file */
			ce.checkError("File",CompileError.ERROR_2,0);
		}
	}
	
	public static void writeCustomFunctions() {
		AttributeList al = new AttributeList();
		writeCustomFunctionsRecurse(root,al);
	}
	
	public static void writeCustomFunctionsRecurse(Folder fold, AttributeList al) {
		for(Vi v: fold.vis) {
			al.clear();
			al.add("Identifier",v.getName());
			al.add("Source","Customized");
			write(createTag("Function",al.attributes,"",false));
			
			write(createTag("Flag",null,"",false));
			write(createTag("DesignerFlag",null,"Configuration/Action-Status",true));
			write(closeTag("Flag"));
			
			write(createTag("Path",null,v.getFolder().getPath(),true));
			write(createTag("Description",null,"Specifies ... Valid Range: Min: Max: Default Value:",true));
			write(createTag("ErrorQuery",null,"false",true));
			write(createTag("ManualUpdate",null,"false",true));
			write(createTag("BlockDiagramComments",null,"",true));
			
			al.clear();
			al.add("TotalNumber","" + v.controls.size());
			write(createTag("Parameters",al.attributes,"",false));
			
			for(Control c: v.controls) {
				/* This is just a command, user specified none for the control */
				if(!c.getName().equals(""))
					writeControl(c,al);
			}
			
			write(closeTag("Parameters"));
			//write("<Parameters TotalNumber=\"0\"/>");
			

			/* TODO Continue After bug-fixes */
			/* TODO Add command parsing here */
			for(Control c: v.controls) {
				//System.out.println(c.getCommand().getName());
			}
			
			int numCommands = getNumberOfCommands(v);;
			
			//write("<Commands TotalNumber=\"0\"/>");
			
			al.clear();
			al.add("TotalNumber","" + numCommands);
			write(createTag("Commands",al.attributes,"",false));
			
			for(Iterator<Control> i = v.controls.iterator(); i.hasNext();) {
				Control curr = i.next();
				if(!curr.getCommand().getName().equals("")) {
					Control multiLineControl = curr;
					ArrayList<Control> controls = getMultiLineControls(v, curr);
					
					al.clear();
					al.add("Identifier",curr.getCommand().getName());
					al.add("Implementation","1");
					write(createTag("Command",al.attributes,"",false));
					write(createTag("Description",null,"",true));
					write(createTag("PolishedCMD",null,curr.getCommand().getPolishedCommand(controls),true));
					write(createTag("FormattedWrite",null,"",false));
					write(createTag("FormattedCmd",null,curr.getCommand().getFormattedCommand(controls),true));
					
					/* Just write the command without a control */
					if(!curr.getName().equals("")) {
						writeCommandParameter(curr);
					}
					
					for(int j = 0; j < curr.getCommand().controlsFound - 1; j++) {
						multiLineControl = i.next();
						writeCommandParameter(multiLineControl);
					}
					
					write(closeTag("FormattedWrite"));
					write(closeTag("Command"));
					//System.out.println(curr.getName() + " = " + curr.getCommand().controlsFound);
	
					//System.out.println(curr.getName());
				}
			}
	
			write(closeTag("Commands"));
			
			write(closeTag("Function"));
		}
		
		for(Folder f: fold.subFolders) {
			writeCustomFunctionsRecurse(f,al);
		}
	}
	
	public static int getNumberOfCommands(Vi v) {
		int num = 0;
		for(Iterator<Control> i = v.controls.iterator(); i.hasNext();) {
			Control curr = i.next();
			num++;
			for(int j = 0; j < curr.getCommand().controlsFound - 1; j++) {
				i.next();
			}
		}
		return num;
	}
	
	public static ArrayList<Control> getMultiLineControls(Vi v, Control c) {
		ArrayList<Control> controls = new ArrayList<Control>();
		controls.add(c);
		
		for(Iterator<Control> i = v.controls.iterator(); i.hasNext();) {
			if(i.next() == c) {
				for(int j = 0; j < c.getCommand().controlsFound - 1; j++) {
					controls.add(i.next());
				}
			}
		}
		
		return controls;
	}
	
	public static void writeCommandParameter(Control c) {
		write(createTag("ParamIdentifier",null,c.getName(),true));
		write(createTag("StartOffset",null,"" + c.startOffset,true));
		write(createTag("EndOffset",null,"" + c.endOffset,true));
	}
	
	public static void writeControl(Control c, AttributeList al) {
		al.clear();
		al.add("Access","RW");
		al.add("Extension","true");
		al.add("IDValue","");
		al.add("Identifier",c.getName());
		al.add("InputOutput",c.getType());
		write(createTag("Parameter",al.attributes,"",false));
		al.clear();
		al.add("NativeDescriptor","");
		write(createTag("DataObject",al.attributes,"",false));
		write(createTag("DataType",null,"",false));
		
		writeControlDataType(c,al);
		
		write(closeTag("DataObject"));
		write(createTag("Description",null,"",true));
		write(createTag("Unit",null,"",true));
		
		/* Booleans need a range table */
		if(c.getDataType().equals("Bool"))
			c.writeRanges();
		
		write(closeTag("Parameter"));
	}
	
	public static void writeControlDataType(Control c, AttributeList al) {
		al.clear();
		c.writeTag();
	}
	
	public static void writeCommands() {
		AttributeList al = new AttributeList();
		al.add("NumberOfCommands","" + root.numCommands);
		write(createTag("Commands",al.attributes,"",false));
		writeCommandsRecurse(root,al);
		write("<Command Identifier=\"*IDN?\" NumberOfImplementation=\"1\"/><Command Identifier=\"*RST\" NumberOfImplementation=\"1\"/><Command Identifier=\"*TST?\" NumberOfImplementation=\"1\"/><Command Identifier=\":SYST:ERR?\" NumberOfImplementation=\"1\"/>");
		write(closeTag("Commands"));
	}
	
	public static void writeCommandsRecurse(Folder fold, AttributeList al) {
		ArrayList<String> commandsWritten = new ArrayList<String>();
		for(Vi v : fold.vis) {
			for(Control c: v.controls) {
				if(!c.getCommand().getName().equals("") && !commandIsWritten(commandsWritten,c.getCommand().getName())) {
					commandsWritten.add(c.getCommand().getName());
					al.clear();
					al.add("Identifier",c.getCommand().getName());
					al.add("NumberOfImplementation","1");
					write(createTag("Command",al.attributes,"",true));
				}
			}
		}
		
		for(Folder f: fold.subFolders) {
			writeCommandsRecurse(f,al);
		}
	}
	
	public static boolean commandIsWritten(ArrayList<String> commandsWritten, String name) {
		for(String s: commandsWritten) {
			if(s.equals(name))
				return true;
		}
		return false;
	}
	
	public static void writeln(String str) {
		specFile.println(str);
	}
	
	public static void write(String str) {
		if(toConsole)
			specFile.println(str);
		else specFile.print(str);
	}
}
