import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.ws.handler.Handler;


public class MyLogger {
	  static private FileHandler fileTxt;
	  static private SimpleFormatter formatterTxt;
	  static public void setup(){
		 

		    // get the global logger to configure it
		    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		    int FILE_SIZE = 1024 * 1024;
		    logger.setLevel(Level.INFO);
		    try {
				fileTxt = new FileHandler("C:\\Harvest-FF\\logs\\validador.log", FILE_SIZE, 1, true);
			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    // create a TXT formatter
		    formatterTxt = new SimpleFormatter();
		    fileTxt.setFormatter(formatterTxt);
		    logger.addHandler(fileTxt);

		  }

}
