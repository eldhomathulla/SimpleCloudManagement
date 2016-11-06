package l2.projects.cloud.management.inteface;

import com.xebialabs.overthere.OverthereConnection;

/**
 * This class has been implemented
 * 
 * @author Eldho Mathulla
 *
 */
public interface InstanceAuthentication {

	public OverthereConnection authenticate();

	public String getConnectionType();

}
