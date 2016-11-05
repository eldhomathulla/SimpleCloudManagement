package l2.projects.cloud.management.impl.aws.ec2;

import java.util.concurrent.TimeUnit;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

import l2.projects.cloud.management.inteface.VirtualInstance;
import l2.projects.cloud.management.inteface.VirtualInstanceException;
import l2.projects.cloud.management.utils.TaskExecutor;

public class Ec2Instance implements VirtualInstance {

	private Instance instance;
	private AmazonEC2Client amazonEC2Client;
	private long instanceStartupTimeOut = 20 * 60 * 1000;
	private long instanceShutdownTimeOut = 15 * 60 * 1000;
	private long instanceTerminationTimeOut = instanceStartupTimeOut;

	public static final int INSTANCE_RUNNING_STATUS_CODE = 16;
	public static final int INSTANCE_STOP_STATUS_CODE = 80;
	public static final int INSTANCE_TERMINATION_STATUS_CODE = 48;

	public Ec2Instance(AmazonEC2Client amazonEC2Client, Instance instance) {
		this.amazonEC2Client = amazonEC2Client;
		this.instance = instance;
	}

	@Override
	public void start() {
		StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instance.getInstanceId());
		StartInstancesResult startInstancesResult = amazonEC2Client.startInstances(startInstancesRequest);
		startInstancesResult.getStartingInstances().stream().forEach((InstanceStateChange instanceStateChange) -> checkInstanceStateStatus(getInstanceStartupTimeOut(), instanceStateChange, INSTANCE_RUNNING_STATUS_CODE, "starting"));
	}

	@Override
	public void stop() {
		StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(instance.getInstanceId());
		StopInstancesResult stopInstancesResult = amazonEC2Client.stopInstances(stopInstancesRequest);
		stopInstancesResult.getStoppingInstances().parallelStream().forEach((InstanceStateChange instanceStateChange) -> checkInstanceStateStatus(getInstanceShutdownTimeOut(), instanceStateChange, INSTANCE_STOP_STATUS_CODE, "stoping"));

	}

	@Override
	public void terminate() {
		TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest().withInstanceIds(instance.getInstanceId());
		TerminateInstancesResult terminateInstancesResult = amazonEC2Client.terminateInstances(terminateInstancesRequest);
		terminateInstancesResult.getTerminatingInstances().parallelStream().forEach((InstanceStateChange instanceStateChange) -> checkInstanceStateStatus(getInstanceTerminationTimeOut(), instanceStateChange, INSTANCE_TERMINATION_STATUS_CODE, "termination"));
	}

	private void checkInstanceStateStatus(long timeOut, InstanceStateChange instanceStateChange, int instanceStatusCode, String instanceAction) {
		TaskExecutor taskExecutor = new TaskExecutor(timeOut, TimeUnit.MILLISECONDS);
		boolean status = taskExecutor.executeUntillTrue(() -> instanceStateChange.getCurrentState().getCode() == Ec2Instance.INSTANCE_RUNNING_STATUS_CODE);
		if (!status) {
			throw new VirtualInstanceException(generateVirtualInstanceExceptionMessage(instanceAction, instanceStateChange.getInstanceId()) + instanceStateChange.getInstanceId(), instanceStateChange.getInstanceId());
		}
	}

	private String generateVirtualInstanceExceptionMessage(String instanceAction, String instanceId) {
		return "Virual instance " + instanceAction + " failed for instance id: " + instanceId;
	}

	public long getInstanceStartupTimeOut() {
		return instanceStartupTimeOut;
	}

	public Ec2Instance setInstanceStartupTimeOut(long instanceStartupTimeOut) {
		this.instanceStartupTimeOut = instanceStartupTimeOut;
		return this;
	}

	public long getInstanceShutdownTimeOut() {
		return instanceShutdownTimeOut;
	}

	public Ec2Instance setInstanceShutdownTimeOut(long instanceShutdownTimeOut) {
		this.instanceShutdownTimeOut = instanceShutdownTimeOut;
		return this;
	}

	public long getInstanceTerminationTimeOut() {
		return instanceTerminationTimeOut;
	}

	public Ec2Instance setInstanceTerminationTimeOut(long instanceTerminationTimeOut) {
		this.instanceTerminationTimeOut = instanceTerminationTimeOut;
		return this;
	}

	@Override
	public String getPrivateIPv4Address() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPublicIPv4Address() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrivateIPv6Address() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPublicIPv6Address() {
		// TODO Auto-generated method stub
		return null;
	}

}
