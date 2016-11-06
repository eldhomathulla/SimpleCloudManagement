package l2.projects.cloud.management.inteface;

public interface VirtualInstance {

	public void start();

	public void stop();

	public void terminate();

	public String getPrivateIPAddress(IP ipVersion);

	public String getPublicIPAddress(IP ipVersion);

}
