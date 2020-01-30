package job;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JobLoggerTest {



	@Test
	public void testLogMessage() throws Exception {
		//String messageText, boolean message,
		//boolean warning, boolean error
		
		JobLogger test = new JobLogger(true, true, true, false, true, true);
		
		
		JobLogger.LogMessage("hola", true, false, false);
	
		
		
	}

}
