package job1;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
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
      private static Map<String, String> dbParams; 
      private static Logger logger;

      public JobLogger(boolean logToFileParam, boolean logToConsoleParam,
                  boolean logToDatabaseParam, boolean logMessageParam,
                  boolean logWarningParam, boolean logErrorParam,
                  Map<String, String> dbParamsMap) {
            logger = Logger.getLogger("MyLog");
            logError = logErrorParam;
            logMessage = logMessageParam;
            logWarning = logWarningParam;
            logToDatabase = logToDatabaseParam;
            logToFile = logToFileParam;
            logToConsole = logToConsoleParam;
            dbParams = dbParamsMap;
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

            Connection connection = null;
            Properties connectionProps = new Properties();
            connectionProps.put("user", dbParams.get("userName"));// String
            connectionProps.put("password", dbParams.get("password")); // String
            try {
                  connection = DriverManager
                              .getConnection(
                                         "jdbc:" + dbParams.get("dbms") + "://"
                                                     + dbParams.get("serverName") + ":"
                                                     + dbParams.get("portNumber") + "/"
                                                    // + dbParams.get("portNumber") + "/"
                                                     ,
                                         connectionProps);// SQL connection

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

                  Statement stmt = connection.createStatement();

                  try {
                        String logMessageText = null;
                        File logFile = new File(dbParams.get("logFileFolder")
                                   + "/logFile.txt");
                        if (!logFile.exists()) {
                              logFile.createNewFile();
                        }

                        FileHandler fh = new FileHandler(dbParams.get("logFileFolder")
                                   + "/logFile.txt");
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
                      if(stmt != null)  
                	  stmt.close();
                  }
            } finally {
            	if (connection != null)
                  connection.close();
            }
      }
}

