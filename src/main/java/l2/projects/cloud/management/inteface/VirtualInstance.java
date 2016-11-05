package l2.projects.cloud.management.inteface;

public interface VirtualInstance {

	public void start();

	public void stop();

	public void terminate();

	public String getPrivateIPv4Address();

	public String getPublicIPv4Address();

	public String getPrivateIPv6Address();

	public String getPublicIPv6Address();

}
