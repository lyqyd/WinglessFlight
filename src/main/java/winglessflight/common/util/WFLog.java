package winglessflight.common.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WFLog {

	private static Logger logger;

	public static void info(String format, Object... data) {
		log(Level.INFO, format, data);
	}

	public static void init() {
		logger = LogManager.getFormatterLogger("WinglessFlight");
	}

	public static void log(Level level, String format, Object... data) {
		logger.log(level, String.format(format, data));
	}

	public static void severe(String format, Object... data) {
		log(Level.ERROR, format, data);
	}

	public static void warn(String format, Object... data) {
		log(Level.WARN, format, data);
	}
	
	public static void debug(String format, Object... data) {
		log(Level.DEBUG, format, data);
	}

}