package linkmatrx;

import java.io.IOException;
import java.util.logging.*; 

public class Logging {
	
	private static Logger LOGGER; 
	
	Logging(String classname) {
		// classname  = LinkMatrix.class.getName()
		Logging.setLOGGER(Logger.getLogger(classname)); 
		
		// Logger setup
		LOGGER.addHandler(new LogHandler());
		Handler fileHandler;
		try {
			fileHandler = new FileHandler("./logger.log", 2000, 5);
	        fileHandler.setFormatter(new LogFormatter());
	        fileHandler.setFilter(new LogFilter());
	        LOGGER.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void log(String mess) {
		// Level.FINE
		LOGGER.log(Level.FINE, mess); 
	}

	public static Logger getLOGGER() {
		return LOGGER;
	}

	public static void setLOGGER(Logger lOGGER) {
		LOGGER = lOGGER;
	}

}
