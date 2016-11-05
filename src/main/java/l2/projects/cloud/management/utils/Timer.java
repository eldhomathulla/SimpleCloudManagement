package l2.projects.cloud.management.utils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Timer implements Runnable {
	private long startTime = -1;
	private TimeUnit durationTimeUnit = TimeUnit.SECONDS;
	private long duration = -1;
	private Thread executingThread = null;
	private Thread timerThread = null;
	private long interval = 1000;
	private TimeUnit intevalTimeUnit = TimeUnit.SECONDS;
	private boolean stop = false;

	private static Logger LOGGER = Logger.getLogger(Timer.class.getName());

	public Timer(long duration) {
		this.duration = duration;
	}

	public Timer(long duration, TimeUnit timeUnit) {
		this(convertToMilliSeconds(duration, timeUnit));
		this.setDurationTimeUnit(timeUnit);
	}

	public Timer(long duration, long interval, TimeUnit timeUnit, TimeUnit intervalTimeUnit) {
		this(duration, timeUnit);
		this.setInterval(convertToMilliSeconds(interval, intervalTimeUnit));
		this.setIntevalTimeUnit(intervalTimeUnit);
	}

	public Timer start() {
		getTimerThread().map(Thread::isAlive).ifPresent((Boolean isExecuting) -> {
			if (isExecuting) {
				throw new TimerException("The timer is still running");
			}
		});
		;
		startTime = System.currentTimeMillis();
		executingThread = Thread.currentThread();
		timerThread = new Thread(this);
		timerThread.start();
		return this;
	}

	public boolean hasTimedOut() {
		if ((System.currentTimeMillis() - startTime) > duration) {
			return true;
		} else {
			return false;
		}
	}

	private static long convertToMilliSeconds(long time, TimeUnit timeUnit) {
		switch (timeUnit) {
		case SECONDS:
			return time * 1000;
		case MINUTES:
			return time * 1000 * 60;
		case HOURS:
			return time * 1000 * 60 * 60;
		case MILLISECONDS:
			return time;
		default:
			throw new TimerException("Unsupported Time Unit: " + timeUnit);
		}

	}

	public TimeUnit getDurationTimeUnit() {
		return durationTimeUnit;
	}

	public Timer setDurationTimeUnit(TimeUnit timeUnit) {
		this.durationTimeUnit = timeUnit;
		return this;
	}

	public Timer setInterval(long interval, TimeUnit intervalTimeUnit) {
		this.interval = convertToMilliSeconds(interval, getIntevalTimeUnit());
		return this;
	}

	@Override
	public void run() {
		while (true) {
			if (hasTimedOut()) {
				executingThread.interrupt();
				LOGGER.info("Timer has completed executing successfully");
				break;
			} else if (stop) {
				LOGGER.info("Timer has been stopped");
				stop = false;
				break;
			}
			try {
				Thread.sleep(getInterval());
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, "Timer was interrupted", e);
			}
		}

	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	private Optional<Thread> getTimerThread() {
		return Optional.of(timerThread);
	}

	public TimeUnit getIntevalTimeUnit() {
		return intevalTimeUnit;
	}

	public void setIntevalTimeUnit(TimeUnit intevalTimeUnit) {
		this.intevalTimeUnit = intevalTimeUnit;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

}
