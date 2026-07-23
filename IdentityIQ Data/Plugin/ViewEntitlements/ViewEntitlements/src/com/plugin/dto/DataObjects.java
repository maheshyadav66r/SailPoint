package com.plugin.dto;

public class DataObjects {

	private String roleName;  // value column in identityEntitlement table.
	
	private String description;

	private String classification;
	
	private String assignedBy;  // assigner column in identityEntitlement table.
	
	private String acquired;  // name column in identityEntitlement table.

	private String applicationName;  // application column in identityEntitlement table.

	private boolean isGrantedByRole;
	
	private String status;

	//private String source;

	private String assigner;
	
	private String requestId;

	//private boolean isAssigned;

	

	@Override
	public String toString() {
		return "DataObjects [roleName=" + roleName + ", description=" + description + ", classification=" + classification + ", assignedBy=" + assignedBy + ", acquired=" + acquired + ", applicationName=" + applicationName + ", isGrantedByRole=" + isGrantedByRole + ", assigner=" + assigner + ", requestId=" + requestId + "]";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAssigner() {
		return assigner;
	}

	public void setAssigner(String assigner) {
		this.assigner = assigner;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}


	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(Object object) {
		this.roleName = (String) object;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getAcquired() {
		return acquired;
	}

	public void setAcquired(String acquired) {
		this.acquired = acquired;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public boolean isGrantedByRole() {
		return isGrantedByRole;
	}

	public void setGrantedByRole(boolean isGrantedByRole) {
		this.isGrantedByRole = isGrantedByRole;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
