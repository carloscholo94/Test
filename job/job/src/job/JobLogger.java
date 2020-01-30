package job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLogger {
	private static boolean logToFile;
	private static boolean logToConsole;
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	private static boolean logToDatabase;
	private static Logger logger;
	private static Connection connection;
	private static Properties connectionProps;
	private static JobLogger jobLog;
	
	private JobLogger() throws SQLException {

		connectionProps = new Properties();
		try {
			InputStream in = new FileInputStream("config.properties");
			connectionProps.load(in);
			in.close();

			connection = DriverManager.getConnection(
					"jdbc:" + connectionProps.get("dbms") + "://"
							+ connectionProps.get("serverName") + ":"
							+ connectionProps.get("portNumber") + "/",
					connectionProps.getProperty("userName"),
					connectionProps.getProperty("password"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public  Connection getConnection() {
		return connection;
	}

	public static JobLogger getInstance() throws SQLException {
		if (jobLog == null)
			jobLog = new JobLogger();
		return jobLog;
	}

	public static void closeConnection() throws SQLException {
		if (connection != null)
			connection.close();
	}

	public JobLogger(boolean logToFileParam, boolean logToConsoleParam,
			boolean logToDatabaseParam, boolean logMessageParam,
			boolean logWarningParam, boolean logErrorParam) {
		logger = Logger.getLogger("MyLog");
		logError = logErrorParam;
		logMessage = logMessageParam;
		logWarning = logWarningParam;
		logToDatabase = logToDatabaseParam;
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
	}

	public static void LogMessage(String messageText, boolean message,
			boolean warning, boolean error) throws Exception {
		messageText.trim();
		if (messageText == null || messageText.length() == 0) {
			return;
		}
		if (!logToConsole && !logToFile && !logToDatabase) {
			throw new Exception("Invalid configuration");
		}
		if ((!logError && !logMessage && !logWarning)
				|| (!message && !warning && !error)) {
			throw new Exception("Error or Warning or Message must be specified");
		}

		int type = 0;
		if (message && logMessage) {
			type = 1;
		}

		if (error && logError) {
			type = 2;
		}

		if (warning && logWarning) {
			type = 3;
		}

		JobLogger instance = JobLogger.getInstance();
		
		try {
			Statement stmt = instance.getConnection().createStatement();

			try {
				String logMessageText = null;
				File logFile = new File(connectionProps.get("logFileFolder")
						+ "/logFile.txt");
				if (!logFile.exists()) {
					logFile.createNewFile();
				}

				FileHandler fh = new FileHandler(
						connectionProps.get("logFileFolder") + "/logFile.txt");
				ConsoleHandler ch = new ConsoleHandler();

				if (error && logError) {
					logMessageText = logMessageText
							+ "error "
							+ DateFormat.getDateInstance(DateFormat.LONG)
									.format(new Date()) + messageText;
				}

				if (warning && logWarning) {
					logMessageText = logMessageText
							+ "warning "
							+ DateFormat.getDateInstance(DateFormat.LONG)
									.format(new Date()) + messageText;
				}

				if (message && logMessage) {
					logMessageText = logMessageText
							+ "message "
							+ DateFormat.getDateInstance(DateFormat.LONG)
									.format(new Date()) + messageText;
				}

				if (logToFile) {
					logger.addHandler(fh);
					logger.log(Level.INFO, messageText);
				}

				if (logToConsole) {
					logger.addHandler(ch);
					logger.log(Level.INFO, messageText);
				}

				if (logToDatabase) {
					stmt.executeUpdate("insert into Log_Values('" + message
							+ "', " + String.valueOf(type) + ")");
				}
			} finally {
				if(stmt != null )
					stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JobLogger.closeConnection();
		}
	}
}
