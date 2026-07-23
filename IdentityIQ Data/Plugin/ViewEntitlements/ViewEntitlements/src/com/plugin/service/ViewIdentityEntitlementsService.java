package com.plugin.service;

import sailpoint.object.Identity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.plugin.dto.DataObjects;
import com.plugin.dto.Entitlements;

import sailpoint.api.SailPointContext;
import sailpoint.object.Application;
import sailpoint.object.ApprovalItem;
import sailpoint.object.AttributeDefinition;
import sailpoint.object.Filter;
import sailpoint.object.IdentityEntitlement;
import sailpoint.object.IdentityRequest;
import sailpoint.object.IdentityRequestItem;
import sailpoint.object.ManagedAttribute;
import sailpoint.object.QueryOptions;
import sailpoint.plugin.PluginContext;
import sailpoint.server.BasePluginService;
import sailpoint.tools.GeneralException;
import sailpoint.tools.Util;

public class ViewIdentityEntitlementsService extends BasePluginService {

	@SuppressWarnings("unused")
	private PluginContext pluginContext;
	private SailPointContext context;

	public ViewIdentityEntitlementsService(PluginContext pluginContext2, SailPointContext context) {
		this.pluginContext = pluginContext2;
		this.context = context;
	}

	// ===================== PUBLIC METHODS =====================

	public List<DataObjects> getUsersRoles(String roleName) throws GeneralException {
		return getIdentityRoles(roleName);
	}

	public List<Entitlements> getUsersEntitlements(String application, String attribute) throws GeneralException {
		return getIdentityEntitlements(application, attribute);
	}

	// ===================== ENTITLEMENTS =====================

	private List<Entitlements> getIdentityEntitlements(String application, String attribute) throws GeneralException {

		String identityName = context.getUserName();
		List<Entitlements> objects = new ArrayList<>();
		int limit = 25;
		QueryOptions options = new QueryOptions();

		try {
			// Base filter (exclude roles)
			Filter baseFilter = Filter.and(Filter.eq("identity.name", identityName), Filter.ne("name", "assignedRoles"), Filter.ne("name", "detectedRoles"));
			List<Filter> filters = new ArrayList<>();
			filters.add(baseFilter);

			// Search Filter by application
			if (!Util.isNullOrEmpty(application)) {
				// filters.add(Filter.eq("application.name", application));
				filters.add(Filter.like("application.name", application, Filter.MatchMode.ANYWHERE));
			}

			// Search Filter by entitlement displayName
			if (!Util.isNullOrEmpty(attribute)) {
				// filters.add(Filter.eq("name", attribute));
				Filter valueF = Filter.like("value", attribute, Filter.MatchMode.ANYWHERE);
				Filter subF = Filter.subquery("value", ManagedAttribute.class, "value", Filter.like("displayableName", attribute, Filter.MatchMode.ANYWHERE));
				filters.add(Filter.or(valueF, subF));

			}

			options.addFilter(Filter.and(filters));
			options.addFilter(Filter.join("name", "ManagedAttribute.attribute"));
			options.addFilter(Filter.join("value", "ManagedAttribute.value"));
			options.addFilter(Filter.join("application", "ManagedAttribute.application.id"));

			options.setResultLimit(limit);

			System.out.println("QueryOptions in getIdentityEntitlements  : " + options);

			Iterator<Object[]> ite = context.search(IdentityEntitlement.class, options, Util.csvToList("ManagedAttribute.displayableName,displayName,name,application,pendingRequestItem"));

			while (ite.hasNext()) {
				Entitlements ents = new Entitlements();
				Object[] obj = (Object[]) ite.next();
				String status = obj[4] != null ? "Pending" : "Committed";

				Application appObject = (Application) obj[3];
				String appName = appObject != null ? appObject.getName() : "";

				ents.setEntitlement((String) obj[0]);
				ents.setAccountName((String) obj[1]);
				ents.setAttribute((String) obj[2]);
				ents.setApplicationName(appName);
				ents.setStatus(status);
				objects.add(ents);
			}
			/*
			 * List pendingEntitlements = getPendingRolesAndEntitlements("Entitlement",
			 * identityName); if (Util.nullSafeSize(pendingEntitlements) > 0) {
			 * objects.addAll(pendingEntitlements); }
			 */
		} catch (Exception e) {
			throw new GeneralException(e);
		}
		return objects;
	}

	// ===================== ROLES =====================

	private List<DataObjects> getIdentityRoles(String roleName) throws GeneralException {

		System.out.println("===getIdentityRoles_Method_Start===");
		String identityName = context.getUserName();
		List<DataObjects> objects = new ArrayList<>();
		int limit = 25;
		QueryOptions options = new QueryOptions();

		try {
			// Base filter (only roles)
			Filter baseFilter = Filter.and(Filter.eq("identity.name", identityName), Filter.in("name", Util.csvToList("assignedRoles,detectedRoles")));

			// Search Apply roleName filter
			if (!Util.isNullOrEmpty(roleName)) {
				Filter roleFilter = Filter.like("value", roleName, Filter.MatchMode.ANYWHERE);
				options.addFilter(Filter.and(baseFilter, roleFilter));
			} else {
				options.addFilter(baseFilter);
			}
			options.setResultLimit(limit);
			System.out.println("QueryOptions in getIdentityRoles  : " + options);

			Iterator<Object[]> ite = context.search(IdentityEntitlement.class, options, Util.csvToList("value,assigner,name,application,pendingRequestItem"));

			while (ite.hasNext()) {
				DataObjects dataObjects = new DataObjects();
				Object[] obj = (Object[]) ite.next();
				String status = obj[4] != null ? "Pending" : "Committed";

				Application appObject = (Application) obj[3];
				String appName = appObject != null ? appObject.getName() : "";

				dataObjects.setRoleName((String) obj[0]);
				dataObjects.setAssignedBy((String) obj[1]);
				dataObjects.setAcquired((String) obj[2]);
				dataObjects.setApplicationName(appName);
				dataObjects.setStatus(status);
				objects.add(dataObjects);
			}
			/*
			 * List pendingRoles = getPendingRolesAndEntitlements("Roles", identityName); if
			 * (Util.nullSafeSize(pendingRoles) > 0) { objects.addAll(pendingRoles); }
			 */
		} catch (Exception e) {
			throw new GeneralException(e);
		}
		return objects;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getPendingRolesAndEntitlements(String type, String idenName) {

		List returnList = new ArrayList();
		try {
			Identity iden = context.getObjectByName(Identity.class, idenName);
			Filter f = Filter.and(Filter.eq("targetId", iden.getId()), Filter.or(Filter.eq("completionStatus", "Pending"), Filter.eq("executionStatus", "Executing")));

			QueryOptions options = new QueryOptions();
			options.addFilter(f);
			Iterator ite = context.search(IdentityRequest.class, options);
			while (ite.hasNext()) {
				IdentityRequest idenRequest = (IdentityRequest) ite.next();
				List<IdentityRequestItem> requestItems = idenRequest.getItems();
				if (Util.nullSafeSize(requestItems) > 0) {
					for (IdentityRequestItem idenRequestItem : requestItems) {
						if ("Add".equalsIgnoreCase(idenRequestItem.getOperation())) {
							String appName = idenRequestItem.getApplication();
							String name = idenRequestItem.getName();
							if ("IIQ".equalsIgnoreCase(appName) && ApprovalItem.ProvisioningState.Pending.equals(idenRequestItem.getProvisioningState()) && type.equals("Roles")) {
								DataObjects dataObjects = new DataObjects();
								dataObjects.setAssignedBy(idenRequestItem.getOwnerName());
								dataObjects.setRoleName(idenRequestItem.getValue());
								dataObjects.setAcquired(name);
								dataObjects.setApplicationName(appName);
								dataObjects.setStatus("Pending");
								returnList.add(dataObjects);
							} else if (type.equals("Entitlement")) {
								boolean isGroupAttribute = false;

								Application appObj = context.getObjectByName(Application.class, appName);
								List<AttributeDefinition> groupAttributes = appObj.getGroupAttributes();
								for (AttributeDefinition attrDef : groupAttributes) {
									if (attrDef.isEntitlement()) {
										if (attrDef.getName().equals(name)) {
											isGroupAttribute = true;
										}
									}
								}

								if (isGroupAttribute) {
									Entitlements ents = new Entitlements();
									ents.setAccountName(idenName);
									ents.setAttribute(name);
									ents.setEntitlement(idenRequestItem.getValue());
									ents.setApplicationName(appName);
									ents.setStatus("Pending");
									returnList.add(ents);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return returnList;
	}

	// ===================== PLUGIN NAME =====================

	@Override
	public String getPluginName() {
		return "viewIdentityEntitlementsPlugin";
	}
}