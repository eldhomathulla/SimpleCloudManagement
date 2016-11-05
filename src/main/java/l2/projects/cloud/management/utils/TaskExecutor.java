package l2.projects.cloud.management.utils;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskExecutor {

	private static final Logger LOGGER = Logger.getGlobal();

	private long interval = 1000;
	private TimeUnit intervalTimeUnit = TimeUnit.MILLISECONDS;
	private Timer timer;

	public TaskExecutor(long duration, TimeUnit durationTimeUnit) {
		this(new Timer(duration, durationTimeUnit));
		timer.setInterval(interval);
		timer.setIntevalTimeUnit(intervalTimeUnit);
	}

	public TaskExecutor(long duration, long interval, TimeUnit durationTimeUnit, TimeUnit intervalTimeUnit) {
		this(new Timer(duration, interval, durationTimeUnit, intervalTimeUnit));
	}

	public TaskExecutor(Timer timer) {
		this.timer = timer;

	}

	public <T> T executeTask(Supplier<T> task) throws TaskExecutionException {
		T result = executeTask(task, (T res) -> true);
		if (result == null) {
			throw new TaskExecutionException("Task Execution Failed after");
		} else {
			return result;
		}
	}

	public boolean executeUntillTrue(Supplier<Boolean> task) {
		Boolean result = executeTask(task, (Boolean res) -> res == false ? null : res);
		if (result == null) {
			return false;
		} else {
			return result;
		}
	}

	private <T> T executeTask(Supplier<T> task, Predicate<T> resultPredicate) {
		timer.start();
		T result = null;
		try {
			while (!Thread.interrupted()) {
				try {
					result = task.get();
					if (result != null && resultPredicate.test(result)) {
						return result;
					}
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "An exception has been throw while executing a task", e);
				}
				Thread.sleep(interval);
			}
			return result;
		} catch (InterruptedException ie) {
			LOGGER.log(Level.WARNING, "Thread has been interrupted", ie);
			return result;
		}
	}

}
