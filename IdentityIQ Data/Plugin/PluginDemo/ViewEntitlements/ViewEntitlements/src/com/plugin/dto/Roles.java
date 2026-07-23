package com.plugin.dto;

public class Roles {

	private String roleName;  
	
	private String description;

	private String classification;
	
	private String assignedBy;  
	
	private String acquired;  

	private String applicationName;  

	private boolean isGrantedByRole;
	
	private String status;

	private String assigner;
	
	private String identityRequestId;


	@Override
	public String toString() {
		return "DataObjects [roleName=" + roleName + ", description=" + description + ", classification=" + classification + ", assignedBy=" + assignedBy + ", acquired=" + acquired + ", applicationName=" + applicationName + ", isGrantedByRole=" + isGrantedByRole + ", assigner=" + assigner + ", requestId=" + identityRequestId + "]";
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

	public String getIdentityRequestId() {
		return identityRequestId;
	}

	public void setIdentityRequestId(String requestId) {
		this.identityRequestId = requestId;
	}


	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(Object item) {
		this.assignedBy = (String) item;
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
