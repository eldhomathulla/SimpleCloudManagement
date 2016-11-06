package l2.projects.cloud.management.test;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import l2.projects.cloud.management.exceptions.TaskExecutionException;
import l2.projects.cloud.management.utils.TaskExecutor;

public class TaskExecutorTest {
	private static final String OUTPUT = "Testing__";
	private TaskExecutor taskExecutor;

	@Before
	public void setUp() throws Exception {
		taskExecutor = new TaskExecutor(10, TimeUnit.SECONDS);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecuteTask() throws TaskExecutionException {
		assertEquals(OUTPUT, taskExecutor.executeTask(() -> {
			return OUTPUT;
		}));
	}

	@Test(expected = TaskExecutionException.class)
	public void testExecuteTaskFail() throws TaskExecutionException {
		taskExecutor.executeTask(() -> {
			return null;
		});
	}

	@Test
	public void testExecuteUntillTrue() {
		long before = System.currentTimeMillis();
		assertTrue(taskExecutor.executeUntillTrue(() -> {
			if (System.currentTimeMillis() - before < 8700) {
				return false;
			} else {
				return true;
			}
		}));
	}

}
