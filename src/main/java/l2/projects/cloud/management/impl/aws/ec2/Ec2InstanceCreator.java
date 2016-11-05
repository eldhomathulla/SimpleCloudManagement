package l2.projects.cloud.management.impl.aws.ec2;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

import l2.projects.cloud.management.inteface.VirtualInstance;
import l2.projects.cloud.management.inteface.VirtualInstanceCreator;
import l2.projects.cloud.management.inteface.VirtualIntanceCreationException;
import l2.projects.cloud.management.utils.InstanceProperties;
import l2.projects.cloud.management.utils.TaskExecutionException;
import l2.projects.cloud.management.utils.TaskExecutor;

public class Ec2InstanceCreator implements VirtualInstanceCreator {
	public static final String IMAGE_ID = "image.id";
	public static final String INSTANCE_TYPE = "instance.type";
	public static final String MIN_COUNT = "min.count";
	public static final String MAX_COUNT = "max.count";
	public static final String SECURITY_GROUP = "security.group";
	public static final String KEY_PAIR = "key.pair";

	private AmazonEC2Client amazonEC2Client;
	private InstanceProperties defaultInstanceProperties = null;
	private long instanceStartupTimeOut = 20 * 60 * 1000;

	public Ec2InstanceCreator(AmazonEC2Client amazonEC2Client, InstanceProperties instanceProperties) {
		this.amazonEC2Client = amazonEC2Client;
		this.defaultInstanceProperties = instanceProperties;
	}

	@Override
	public VirtualInstance createInstance(InstanceProperties instanceProperties) {
		return createInstances(instanceProperties, 1).get(0);
	}

	@Override
	public List<VirtualInstance> createInstances(InstanceProperties instanceProperties, int instanceCount) {
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		getImageId(instanceProperties).ifPresent((String imageId) -> runInstancesRequest.withImageId(imageId));
		getInstanceType(instanceProperties).ifPresent((String instanceType) -> runInstancesRequest.withInstanceType(instanceType));
		getKeyPair(instanceProperties).ifPresent((String keyPair) -> runInstancesRequest.withKeyName(keyPair));
		getSecurityGroups(instanceProperties).ifPresent((String[] securityGroups) -> runInstancesRequest.withSecurityGroups(securityGroups));
		getMinCount(instanceProperties).ifPresent((Integer minCount) -> runInstancesRequest.withMinCount(minCount));
		getMaxCount(instanceProperties).ifPresent((Integer maxCount) -> runInstancesRequest.withMaxCount(maxCount));
		RunInstancesResult runInstancesResult = amazonEC2Client.runInstances(runInstancesRequest);
		List<Instance> instances = runInstancesResult.getReservation().getInstances();
		return instances.parallelStream().map((Instance instance) -> {
			TaskExecutor taskExecutor = new TaskExecutor(instanceStartupTimeOut, TimeUnit.MILLISECONDS);
			try {
				return taskExecutor.executeTask(() -> {
					if (instance.getState().getCode() == Ec2Instance.INSTANCE_RUNNING_STATUS_CODE) {
						return (VirtualInstance) new Ec2Instance(amazonEC2Client, instance);
					} else {
						return null;
					}
				});
			} catch (TaskExecutionException e) {
				throw new VirtualIntanceCreationException("Instance creation failed for instance id: " + instance.getInstanceId(), e);
			}
		}).collect(Collectors.toList());
	}

	@Override
	public VirtualInstance createInstance() {
		return createInstances(1).get(0);
	}

	@Override
	public List<VirtualInstance> createInstances(int instanceCount) {
		return createInstances(defaultInstanceProperties, instanceCount);
	}

	public void setImageId(String imageId) {
		this.defaultInstanceProperties.put(IMAGE_ID, imageId);
	}

	public void setInstanceType(String instanceType) {
		defaultInstanceProperties.put(INSTANCE_TYPE, instanceType);
	}

	public void setMinCount(int minCount) {
		defaultInstanceProperties.put(MIN_COUNT, minCount);
	}

	public void setMaxCount(int maxCount) {
		defaultInstanceProperties.put(MAX_COUNT, maxCount);
	}

	public void setSecurityGroups(String[] securityGroups) {
		defaultInstanceProperties.put(SECURITY_GROUP, securityGroups);
	}

	public Optional<String> getImageId(InstanceProperties instanceProperties) {
		return fetchInstanceProperty(IMAGE_ID, instanceProperties);
	}

	public Optional<String> getInstanceType(InstanceProperties instanceProperties) {
		return fetchInstanceProperty(INSTANCE_TYPE, instanceProperties);
	}

	public Optional<Integer> getMinCount(InstanceProperties instanceProperties) {
		return fetchInstanceProperty(MIN_COUNT, instanceProperties);
	}

	public Optional<Integer> getMaxCount(InstanceProperties instanceProperties) {
		return fetchInstanceProperty(MAX_COUNT, instanceProperties);
	}

	public Optional<String[]> getSecurityGroups(InstanceProperties instanceProperties) {
		return fetchInstanceProperty(SECURITY_GROUP, instanceProperties);
	}

	public Optional<String> getKeyPair(InstanceProperties instanceProperties) {
		return fetchInstanceProperty(KEY_PAIR, instanceProperties);
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> fetchInstanceProperty(String property, InstanceProperties instanceProperties) {
		return Optional.of((T) instanceProperties.get(property));
	}

	public <T> Optional<T> fetchInstanceProperty(String property) {
		return fetchInstanceProperty(property, defaultInstanceProperties);
	}

}
