package com.plugin.dto;

import java.util.Map;

public class LinkDTO {
	private String id;
	private String application;
	private String nativeIdentity;
	private String displayName;
	private String identityName;
	private boolean disabled;
	private Map<String, Object> attributes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getNativeIdentity() {
		return nativeIdentity;
	}

	public void setNativeIdentity(String nativeIdentity) {
		this.nativeIdentity = nativeIdentity;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getIdentityName() {
		return identityName;
	}

	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "LinkDTO [id=" + id + ", application=" + application + ", nativeIdentity=" + nativeIdentity + ", displayName=" + displayName + ", identityName=" + identityName + ", disabled=" + disabled + ", attributes=" + attributes + "]";
	}

}
