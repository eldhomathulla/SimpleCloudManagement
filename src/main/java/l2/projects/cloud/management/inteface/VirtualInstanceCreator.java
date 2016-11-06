package l2.projects.cloud.management.inteface;

import java.util.List;

import l2.projects.cloud.management.utils.InstanceProperties;

/**
 * This interface provides functionalities for creating virtual instances. For each platform that create Virtual Instances like Amazon Ec2, Microsoft Azure should implement this interface in order to use other functionlities
 * @author Eldho Mathulla
 *
 */
public interface VirtualInstanceCreator {

	/**
	 * Create an instance based on the provided instance properties
	 * @param instanceProperties instance Properties used create the instance
	 * @return Virtual Instance object
	 */
	public VirtualInstance createInstance(InstanceProperties instanceProperties);

	/**
	 * Create instances based on the provided instance property and number of instances needed to be created
	 * @param instanceProperties instance properties used to create the instances
	 * @param instanceCount number of instances to be created
	 * @return list of virtual instances created
	 */
	public List<VirtualInstance> createInstances(InstanceProperties instanceProperties, int instanceCount);


	/**
	 * Create an instance based on default instance properties
	 * @return virtual instance objects
	 */
	public VirtualInstance createInstance();


	/**
	 * Create multiple instances based on number of instances needed to be create, based on default instance properites
	 * @param instanceCount number of instances to be create
	 * @return list of virtual instances
	 */
	public List<VirtualInstance> createInstances(int instanceCount);

}
