import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
 
public class GUIApp extends JPanel implements ActionListener {
	private static final long serialVersionUID = 3395388270674327150L;
	static private final String newline = "\n";
    JButton openButton, compileButton;
    JCheckBox templateButton, shortSCPIButton;
    JTextArea log;
    JFileChooser fc;
    String path;
    Boolean template = true;
    Boolean shortSCPI = false;
 
    public GUIApp() {
        super(new BorderLayout());
 
        log = new JTextArea(20,55);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        fc = new JFileChooser(".");

        openButton = new JButton("Open Excel API");
        openButton.addActionListener(this);
 
        compileButton = new JButton("Compile File");
        compileButton.addActionListener(this);
        
        templateButton = new JCheckBox("Do Not Generate Template Vis");
        templateButton.addActionListener(this);
        
        shortSCPIButton = new JCheckBox("Format With Short SCPI");
        shortSCPIButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(); 
        buttonPanel.add(openButton);
        buttonPanel.add(compileButton);
        buttonPanel.add(templateButton);
        buttonPanel.add(shortSCPIButton);
 
        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }
 
    public void actionPerformed(ActionEvent e) {
        /* Handle open button action. */
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(GUIApp.this);
 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                log.append("Opening: " + file.getName() + "." + newline);
                path = file.getPath();
            } 
            
            else {
                log.append("Open command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());
 
        /* Handle save button action. */
        } 
        
        else if (e.getSource() == compileButton) {
        	if(path != null) {
        		CompileError.forceContinue = true;
                SheetReader sr = new SheetReader();
        		String[][] data = sr.readSheet(path);
        		SheetParser parser = new SheetParser();
        		Folder root = parser.parse(data, sr.getRows(), sr.getCols());
        		
        		if(CompileError.errors.size() == 0) {
        			XMLWriter.generateTemplate = template;
        			Command.shortSCPI = shortSCPI;
	        		XMLWriter x = new XMLWriter("./spec.driver",root);
	        		x.createXML();
	        		System.out.println("Done, spec.driver was created in your API Builder directory." + newline);
        		}
        		
        		else {
        			CompileError.errors = new ArrayList<String>();
        		}
        		
        		log.append(outputContents() + newline);
        	}
        	else {
        		log.append("No file selected." + newline);
        	}
        	
            log.setCaretPosition(log.getDocument().getLength());
        }
        
        else if(e.getSource() == templateButton) {
        	template = !template;
        }
        
        else if(e.getSource() == shortSCPIButton) {
        	shortSCPI = !shortSCPI;
        }
        
    }
 
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("API Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        frame.add(new GUIApp());

        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE); 
                createAndShowGUI();
            }
        });
    }
    
    public static String outputContents() {
    	String text = new String("");
    	try {
			FileReader reader = new FileReader("data/log.txt");
			BufferedReader br = new BufferedReader(reader);
			String line;
			
			while((line = br.readLine()) != null) {
				text += line;
				text += newline;
			}
			br.close();
		}
		
		catch(FileNotFoundException ex) {
			/* Missing template.data file */
			System.out.println("Could not open log file");
		}
		
		catch(IOException ex) {
			ex.printStackTrace();
		}
    	return text;
    }
}
