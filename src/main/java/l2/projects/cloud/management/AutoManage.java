package l2.projects.cloud.management;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import l2.projects.cloud.management.exceptions.CloudManagementException;
import l2.projects.cloud.management.inteface.VirtualInstance;
import l2.projects.cloud.management.inteface.VirtualInstanceCreator;
import l2.projects.cloud.management.utils.TaskExecutor;
import l2.projects.cloud.management.utils.Timer;

/**
 * This class handles starting and termination of an instance based on given
 * starting and shutdown conditions, during runtime
 * 
 * @author Eldho Mathulla
 *
 */
public class AutoManage implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(AutoManage.class.getName());

	private VirtualInstanceCreator virtualInstanceCreator;
	private Function<VirtualInstanceCreator, Integer> instanceStartCondition;
	private BiPredicate<VirtualInstanceCreator, VirtualInstance> instanceTerminationCondition;
	private List<VirtualInstance> currentActiveVirtualInstances = new LinkedList<>();
	private Thread autoManageThread = null;
	private TimeUnit pollingTimeUnit = TimeUnit.MINUTES;
	private long pollingTime = 5;
	private long stoppingTime = 3;

	public AutoManage(VirtualInstanceCreator virtualInstanceCreator, Function<VirtualInstanceCreator, Integer> instanceStartCondition, BiPredicate<VirtualInstanceCreator, VirtualInstance> instanceTerminationCondition) {
		this(virtualInstanceCreator);
		this.instanceStartCondition = instanceStartCondition;
		this.instanceTerminationCondition = instanceTerminationCondition;

	}

	public AutoManage(VirtualInstanceCreator virtualInstanceCreator) {
		this.setVirtualInstanceCreator(virtualInstanceCreator);
	}

	/**
	 * Set the condition for starting an instance with it returning the number of instances to be started
	 * @param instanceStartCondition
	 */
	public void setInstanceStartCondition(Function<VirtualInstanceCreator, Integer> instanceStartCondition) {
		this.instanceStartCondition = instanceStartCondition;
	}

	/**
	 * Set the condition for termination of instances that is alread been managed by the auto manage
	 * @param instanceTerminationCondition
	 */
	public void setInstanceTerminationCondition(BiPredicate<VirtualInstanceCreator, VirtualInstance> instanceTerminationCondition) {
		this.instanceTerminationCondition = instanceTerminationCondition;
	}

	public VirtualInstanceCreator getVirtualInstanceCreator() {
		return virtualInstanceCreator;
	}

	private void setVirtualInstanceCreator(VirtualInstanceCreator virtualInstanceCreator) {
		this.virtualInstanceCreator = virtualInstanceCreator;
	}

	/**
	 * Specify the time unti of polling time
	 * @param pollingTimeUnit
	 * @return
	 */
	public AutoManage setPollingTimeUnit(TimeUnit pollingTimeUnit) {
		this.pollingTimeUnit = pollingTimeUnit;
		return this;
	}

	/**
	 * Specify the polling time used by auto manage
	 * @param pollingTime
	 * @return
	 */
	public AutoManage setPollingTime(long pollingTime) {
		this.pollingTime = pollingTime;
		return this;
	}

	/**
	 * Set the stopping time in minutes
	 * @param stoppingTime
	 * @return
	 */
	public AutoManage setStoppingTime(long stoppingTime) {
		this.stoppingTime = stoppingTime;
		return this;
	}

	/**
	 * Start the auto manage
	 */
	public void startManaging() {
		if (autoManageThread != null && autoManageThread.isAlive()) {
			return;
		}
		autoManageThread = new Thread(this);
		autoManageThread.start();
		LOGGER.info("Auto manage has been started");
	}

	/**
	 * Stop the auto manage
	 */
	public void stopManaging() {
		if (autoManageThread != null && autoManageThread.isAlive()) {
			autoManageThread.interrupt();
			TaskExecutor taskExecutor = new TaskExecutor(stoppingTime, TimeUnit.MINUTES);
			boolean stop = taskExecutor.executeUntillTrue(() -> !autoManageThread.isAlive());
			if (stop) {
				throw new CloudManagementException("Auto manage did not stop within " + stoppingTime + " minutes");
			}
		}
	}

	@Override
	public void run() {
		long actualPollingTime = Timer.convertToMilliSeconds(pollingTime, pollingTimeUnit);
		try {
			while (!Thread.interrupted()) {
				int instancesToStart = instanceStartCondition.apply(virtualInstanceCreator);
				List<VirtualInstance> instancesToBeTerminated = currentActiveVirtualInstances.stream().filter((VirtualInstance virtualInstance) -> instanceTerminationCondition.test(virtualInstanceCreator, virtualInstance)).collect(Collectors.toList());
				instancesToBeTerminated.forEach(VirtualInstance::terminate);
				currentActiveVirtualInstances.removeAll(instancesToBeTerminated);
				if (instancesToStart > 0) {
					List<VirtualInstance> startedVirtualInstances = virtualInstanceCreator.createInstances(instancesToStart);
					currentActiveVirtualInstances.addAll(startedVirtualInstances);
				}
				Thread.sleep(actualPollingTime);
			}
		} catch (InterruptedException interruptedException) {
			LOGGER.log(Level.INFO, "Auto manage thread execution has been interrupted", interruptedException);
		}

	}

}
