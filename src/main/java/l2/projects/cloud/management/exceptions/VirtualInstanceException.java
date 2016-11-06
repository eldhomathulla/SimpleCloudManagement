package l2.projects.cloud.management.exceptions;

public class VirtualInstanceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6945178391935872329L;

	private final String instanceId;

	public VirtualInstanceException(String message, String instanceId) {
		super(message);
		this.instanceId = instanceId;
	}

	public VirtualInstanceException(Throwable cause, String instanceId) {
		super(cause);
		this.instanceId = instanceId;
	}

	public VirtualInstanceException(String message, Throwable cause, String instanceId) {
		super(message, cause);
		this.instanceId = instanceId;
	}

	public VirtualInstanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String instanceId) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.instanceId = instanceId;
	}

	public String getInstanceId() {
		return instanceId;
	}

}
