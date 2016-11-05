package l2.projects.cloud.management.impl.aws.ec2;

import com.amazonaws.services.ec2.model.InstanceState;

public class Ec2InstanceState {
	private InstanceState instanceState;
	private String instanceId;

	public Ec2InstanceState(String instanceId, InstanceState instanceState){
		this.setInstanceId(instanceId);
		this.setInstanceState(instanceState);
	}

	public InstanceState getInstanceState() {
		return instanceState;
	}

	public void setInstanceState(InstanceState instanceState) {
		this.instanceState = instanceState;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

}