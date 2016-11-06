package l2.projects.cloud.management.inteface;

import l2.projects.cloud.management.utils.InstanceProperties;

/**
 * This interface provides the functionality for interacting instances created by VirtualInstanceCreator
 * @author Eldho Mathulla
 *
 */
public interface VirtualInstance {

	/**
	 * Start the instance , if it is stopped
	 */
	public void start();

	/**
	 * Stopping the instance if it is running
	 */
	public void stop();

	/**
	 * Terminating the instance
	 */
	public void terminate();

	/**
	 * Get private IP Address of the instance
	 * @param ipVersion the IP version for which the ip address is needed, i.e. IP v4 or v6
	 * @return IP address
	 */
	public String getPrivateIPAddress(IP ipVersion);

	/**
	 * Get public ip address of the instance
	 * @param ipVersion ipVersion the IP version for which the ip address is needed, i.e. IP v4 or v6
	 * @return IP Address
	 */
	public String getPublicIPAddress(IP ipVersion);

	/**
	 * Get the instance properties of the instance
	 * @return instance properties
	 */
	public InstanceProperties getInstanceProperties();

}
