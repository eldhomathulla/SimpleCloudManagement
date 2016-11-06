package l2.projects.cloud.management.test.ec2;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import l2.projects.cloud.management.utils.Timer;

public class TimerTest {
	private static final int TIME_OUT = 30;
	private Timer timer;

	@Before
	public void setUp() throws Exception {
		timer = new Timer(TIME_OUT, TimeUnit.SECONDS);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHasTimedOut() throws InterruptedException {
		timer.start();
		long startTime = System.currentTimeMillis();
		while (!timer.hasTimedOut()) {
			Thread.sleep(1000);
		}
		assertTrue(((System.currentTimeMillis() - startTime) - TIME_OUT * 1000) < (2 * 1000));
	}

}
