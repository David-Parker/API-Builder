import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.*;

public class API {

	public static void main(String[] args) {
		/* Open GUI Application */
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE); 
                GUIApp.createAndShowGUI();
            }
        });
		
		try {
			System.setOut(new PrintStream(new File("data/log.txt")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
