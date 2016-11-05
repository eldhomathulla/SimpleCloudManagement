package l2.projects.cloud.management.inteface;

import java.util.List;

import l2.projects.cloud.management.utils.InstanceProperties;

public interface VirtualInstanceCreator {

	public VirtualInstance createInstance(InstanceProperties instanceProperties);

	public List<VirtualInstance> createInstances(InstanceProperties instanceProperties, int instanceCount);

	public VirtualInstance createInstance();

	public List<VirtualInstance> createInstances(int instanceCount);

}
