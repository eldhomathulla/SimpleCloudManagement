package l2.projects.cloud.management.utils;

import com.xebialabs.overthere.OverthereConnection;

import l2.projects.cloud.management.inteface.VirtualInstance;

public class ConnectionFactory {

	private static final ConnectionFactory connectionFactory = new ConnectionFactory();

	protected ConnectionFactory() {
	}

	public static ConnectionFactory getConnectionfactory() {
		return connectionFactory;
	}

	private OverthereConnection createConnection(VirtualInstance virtualInstance) {
		return null;
	}

}
