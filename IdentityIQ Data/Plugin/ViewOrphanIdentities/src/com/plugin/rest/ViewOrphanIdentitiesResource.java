
package com.plugin.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.plugin.dto.OrphanIdentities;
import com.plugin.service.ViewOrphanIdentitiesService;

import sailpoint.plugin.PluginContext;
import sailpoint.rest.plugin.AllowAll;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.tools.GeneralException;

@Path("ViewOrphanIdentities")
@Produces({ "application/json" })
@AllowAll
public class ViewOrphanIdentitiesResource extends BasePluginResource {

	@Override
	public String getPluginName() {
		return "ViewOrphanIdentities";
	}

	@GET
	@Path("/getUncorrelatedUsers")
	@AllowAll
	public Response getOrphanUsers(@QueryParam("userName") String userName) {
		Map<String, Object> response = null;
		try {
			List<OrphanIdentities> list = getViewOrphanIdentitiesService().getUncorrelatedUsers(userName);

			if (list == null) {
				list = Collections.emptyList();
			}
			response = new HashMap<>();
			response.put("users", list);
			response.put("count", list.size());
		} catch (GeneralException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error  in fetching OrphanUsers: " + e.getMessage()).build();
		}
		return Response.ok(response).build();
	}
	
	
	@GET
	@Path("/getOrphanUserAccounts")
	@AllowAll
	public Response getOrphanUserAccounts(@QueryParam("userName") String userName) {
		Map<String, Object> response = null;
		try {
			List<OrphanIdentities> list = getViewOrphanIdentitiesService().getOrphanUserData(userName);

			if (list == null) {
				list = Collections.emptyList();
			}
			response = new HashMap<>();
			response.put("users", list);
			response.put("count", list.size());
		} catch (GeneralException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching OrphanUsers accounts: " + e.getMessage()).build();
		}
		return Response.ok(response).build();
	}

	private ViewOrphanIdentitiesService getViewOrphanIdentitiesService() {
		return new ViewOrphanIdentitiesService((PluginContext) this, this.getContext());
	}
}
