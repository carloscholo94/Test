package job1;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JobLoggerTest {


	@Test
	public void testLogMessage() throws Exception {
		Map<String, String> dbParamsMap = new HashMap<String, String>();
		
		dbParamsMap.put("userName", "SQLREALTIMEAPP");
		dbParamsMap.put("password", "P0st1l10n02018");
		dbParamsMap.put("dbms", "sqlserver");
		dbParamsMap.put("serverName", "10.130.3.152");
		dbParamsMap.put("portNumber", "1450");
		dbParamsMap.put("logFileFolder", "c:");
		
		JobLogger test = new JobLogger(true, false, true, true, false, false, dbParamsMap);
		//test.
		JobLogger.LogMessage("PRUEBA...", true, false, false);

}}

