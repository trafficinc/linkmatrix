package linkmatrx;

import java.util.logging.LogRecord;
import java.util.logging.Filter;
import java.util.logging.Level;

public class LogFilter implements Filter {

	@Override
	public boolean isLoggable(LogRecord log) {
		if(log.getLevel() == Level.CONFIG) return false;
		return true;
	}

}
