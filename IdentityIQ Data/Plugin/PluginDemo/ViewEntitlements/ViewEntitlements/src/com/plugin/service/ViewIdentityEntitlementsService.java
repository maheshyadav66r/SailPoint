package com.plugin.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.plugin.dto.Entitlements;
import com.plugin.dto.Roles;

import sailpoint.api.SailPointContext;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.IdentityEntitlement;
import sailpoint.object.IdentityRequestItem;
import sailpoint.object.ManagedAttribute;
import sailpoint.object.QueryOptions;
import sailpoint.plugin.PluginContext;
import sailpoint.server.BasePluginService;
import sailpoint.tools.GeneralException;
import sailpoint.tools.Util;

public class ViewIdentityEntitlementsService extends BasePluginService {
	private static Log logger = LogFactory.getLog("ViewIdentityEntitlementsService.class");
	// Logger.getLogger("ViewIdentityEntitlementsService.class");
	private PluginContext pluginContext;
	private SailPointContext context;

	public ViewIdentityEntitlementsService(PluginContext pluginContext, SailPointContext context) {
		this.pluginContext = pluginContext;
		this.context = context;
	}

	public List<Roles> getUsersRoles(String roleName) throws GeneralException {
		return getIdentityRoles(roleName);
	}

	public List<Entitlements> getUsersEntitlements(String application, String attribute) throws GeneralException {
		return getIdentityEntitlements(application, attribute);
	}

	private List<Entitlements> getIdentityEntitlements(String application, String entitlementName) throws GeneralException {
		if (logger.isTraceEnabled()) {
			logger.info("Entering method getIdentityEntitlements...");
		}
		String identityName = context.getUserName();
		List<Entitlements> entitlements = new ArrayList<>();
		List<Entitlements> entFinalList = null;
		try {
			if (Util.isNotNullOrEmpty(identityName)) {
				QueryOptions options = new QueryOptions();
				options.setCloneResults(true);
				Filter baseFilter = Filter.and(Filter.eq("identity.name", identityName), Filter.ne("name", "assignedRoles"), Filter.ne("name", "detectedRoles"), Filter.notnull("value"));
				List<Filter> filters = new ArrayList<>();
				filters.add(baseFilter);

				if (!Util.isNullOrEmpty(application)) {
					filters.add(Filter.like("application.name", application, Filter.MatchMode.ANYWHERE));
				}
				if (!Util.isNullOrEmpty(entitlementName)) {
					Filter valueFilter = Filter.like("value", entitlementName, Filter.MatchMode.ANYWHERE);
					Filter subqueryFilter = Filter.subquery("value", ManagedAttribute.class, "value", Filter.like("displayableName", entitlementName, Filter.MatchMode.ANYWHERE));
					filters.add(Filter.or(valueFilter, subqueryFilter));
				}
				options.addFilter(Filter.and(filters));
				options.addFilter(Filter.join("name", "ManagedAttribute.attribute"));
				options.addFilter(Filter.join("value", "ManagedAttribute.value"));
				options.addFilter(Filter.join("application", "ManagedAttribute.application.id"));

				Iterator<Object[]> iterator = context.search(IdentityEntitlement.class, options, Util.csvToList("ManagedAttribute.displayableName,displayName,name,application.name,pendingRequestItem"));
				Entitlements ent = null;
				while (iterator.hasNext()) {
					Object[] obj = iterator.next();
					ent = new Entitlements();
					if (obj[4] == null) {
						ent.setEntitlement((String) obj[0]);
						ent.setAccountName((String) obj[1]);
						ent.setAttribute((String) obj[2]);
						ent.setApplicationName((String) obj[3]);
						ent.setStatus("committed");
						entitlements.add(ent);
					}
				}
				entFinalList = getPendingEntitlements(entitlements, entitlementName, application);
			}
		} catch (Exception e) {
			throw new GeneralException(e);
		}
		if (logger.isTraceEnabled()) {
			logger.info("Exiting method getIdentityEntitlements...");
		}
		return entFinalList;
	}

	@SuppressWarnings("unchecked")
	private List<Roles> getIdentityRoles(String roleName) throws GeneralException {
		if (logger.isTraceEnabled()) {
			logger.info("Entering method getIdentityRoles...");
		}
		String identityName = context.getUserName();
		List<Roles> roles = new ArrayList<>();
		List<Roles> finalList;
		try {
			QueryOptions options = new QueryOptions();
			options.setCloneResults(true);
			Filter baseFilter = Filter.and(Filter.eq("identity.name", identityName), Filter.in("name", Util.csvToList("assignedRoles,detectedRoles")), Filter.notnull("value"));

			if (!Util.isNullOrEmpty(roleName)) {
				options.addFilter(Filter.and(baseFilter, Filter.like("value", roleName, Filter.MatchMode.ANYWHERE)));
			} else {
				options.addFilter(baseFilter);
			}
			Iterator<Object[]> ite = context.search(IdentityEntitlement.class, options, Util.csvToList("value,assigner,name,application.name,pendingRequestItem"));
			Roles role = null;
			while (ite.hasNext()) {
				Object[] obj = ite.next();
				role = new Roles();
				if (obj[4] == null) {
					role.setRoleName((String) obj[0]);
					role.setAssignedBy((String) obj[1]);
					role.setAcquired((String) obj[2]);
					role.setApplicationName((String) obj[3]);
					role.setStatus("committed");
					roles.add(role);
				}
			}

			finalList = getPendingRoles(roles, roleName);
		} catch (Exception e) {
			throw new GeneralException(e);
		}
		if (logger.isTraceEnabled()) {
			logger.info("Exiting method getIdentityRoles...");
		}
		return finalList;
	}

	private List<Roles> getPendingRoles(List<Roles> roleList, String roleName) {
		if (logger.isTraceEnabled()) {
			logger.info("Entering method getPendingRoles...");
		}
		try {
			Identity identity = context.getObjectByName(Identity.class, context.getUserName());
			if (identity != null) {
				System.out.println("111 getPendingRoles");
				QueryOptions options = new QueryOptions();
				options.setCloneResults(true);
				Filter targetFilter = Filter.eq("identityRequest.targetId", identity.getId());
				Filter finalFilter = Filter.and(Filter.notnull("value"), targetFilter, Filter.in("operation", Util.csvToList("Add,Remove")), Filter.eq("application", "IIQ"), Filter.eq("provisioningState", "Pending"));
				options.addFilter(finalFilter);
				// options.addFilter(Filter.join("identityRequestId", "IdentityRequest.id"));

				// Search Filter for roleName
				if (Util.isNotNullOrEmpty(roleName)) {
					options.addFilter(Filter.like("value", roleName, Filter.MatchMode.ANYWHERE));
				}
				Roles role = null;
				Iterator<Object[]> iterator = context.search(IdentityRequestItem.class, options, "owner , value ,name , identityRequest.name");
				while (iterator.hasNext()) {
					Object[] item = iterator.next();
					role = new Roles();
					role.setStatus("Pending");
					role.setAssignedBy(item[0]);
					role.setRoleName((String) item[1]);
					role.setAcquired((String) item[2]);
					role.setIdentityRequestId((String) item[3]);
					roleList.add(role);
				}
			}
		} catch (Exception e) {
			logger.error("Pending Roles Error: " + e.getMessage());
		}
		if (logger.isTraceEnabled()) {
			logger.info("Exiting method getPendingRoles...");
		}
		return roleList;
	}

	private List<Entitlements> getPendingEntitlements(List<Entitlements> entitlements, String entitlementName, String application) {
		if (logger.isTraceEnabled()) {
			logger.info("Entering method getPendingEntitlements...");
		}
		try {
			Identity identity = context.getObjectByName(Identity.class, context.getUserName());
			if (identity != null) {
				QueryOptions options = new QueryOptions();
				options.setCloneResults(true);
				Filter baseFilter = Filter.and(Filter.notnull("value"), Filter.ne("application", "IIQ"), Filter.eq("identityRequest.targetId", identity.getId()), Filter.or(Filter.eq("identityRequest.completionStatus", "Pending"), Filter.eq("identityRequest.executionStatus", "Executing")));
				options.addFilter(baseFilter);
				// Search Filter for application
				if (Util.isNotNullOrEmpty(application)) {
					Filter applicationSubqueryFilter = Filter.subquery("value", ManagedAttribute.class, "value", Filter.like("application.name", application, Filter.MatchMode.ANYWHERE));
					options.addFilter(applicationSubqueryFilter);
				}
				if (Util.isNotNullOrEmpty(entitlementName)) {
					Filter subqueryFilter = Filter.subquery("identityRequestItem.value", ManagedAttribute.class, "value", Filter.like("displayableName", entitlementName, Filter.MatchMode.ANYWHERE));
					Filter targetFilter = Filter.eq("identityRequest.targetId", identity.getId());
					Filter maFilter = Filter.subquery("value", ManagedAttribute.class, "value", Filter.like("displayableName", entitlementName, Filter.MatchMode.ANYWHERE));
					Filter finalFilter = Filter.and(targetFilter, Filter.in("operation", Util.csvToList("Add,Remove")), Filter.eq("provisioningState", "Pending"), maFilter);
					options.addFilter(finalFilter);
					options.addFilter(subqueryFilter);
					options.setCloneResults(true);
					options.addFilter(Filter.join("value", "ManagedAttribute.value"));
					// options.addFilter(Filter.join("identityRequestId", "IdentityRequest.id"));
				}

				Iterator<Object[]> iterator = context.search(IdentityRequestItem.class, options, "owner , value , name , identityRequest.name ,nativeIdentity , application");
				Entitlements ent = null;
				while (iterator.hasNext()) {
					Object[] item = iterator.next();
					String accountName = Util.isNotNullOrEmpty((String) item[4]) ? (String) item[4] : identity.getName();
					String applicationName = (String) item[5];

					ent = new Entitlements();
					ent.setAccountName(accountName);
					ent.setAttribute((String) item[2]);
					ent.setEntitlement((String) item[1]);
					ent.setApplicationName(applicationName);
					ent.setStatus("Pending");
					ent.setIdentityRequestId((String) item[3]);
					entitlements.add(ent);
				}
			}
		} catch (Exception e) {
			logger.error("Pending Entitlements Error: " + e.getMessage());
		}
		if (logger.isTraceEnabled()) {
			logger.info("Exiting method getPendingEntitlements...");
		}
		return entitlements;
	}

	@Override
	public String getPluginName() {
		return "viewIdentityEntitlementsPlugin";
	}
}