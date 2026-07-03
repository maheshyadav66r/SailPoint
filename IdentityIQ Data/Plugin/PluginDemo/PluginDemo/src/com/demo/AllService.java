package com.demo;

import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import sailpoint.api.SailPointContext;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.plugin.PluginContext;
import sailpoint.tools.GeneralException;

public class AllService {

	private PluginContext pluginContext;
	private SailPointContext context;
	private String date;

	// ========= constructor ==================
	public AllService(PluginContext paramPluginContext, SailPointContext context, String date) {
		this.pluginContext = paramPluginContext;
		this.context = context;
		this.date = date;
	}

	public AllService(SailPointContext context) {
		this.context = context;
	}

	// ======= Get All Managers list ========
	public ArrayList<ManagerEntity> getManagersList() throws GeneralException, JSONException {
		Filter myFilter = Filter.eq("managerStatus", true);
		QueryOptions qo = new QueryOptions();
		qo.addFilter(myFilter);

		Iterator<Identity> iter = context.search(Identity.class, qo);
		ArrayList<ManagerEntity> managerList = new ArrayList<ManagerEntity>();
		if (null != iter) {
			while (iter.hasNext()) {
				Identity identity = (Identity) iter.next();
				// managerList += (","+identity.getName()+"-"+identity.getDisplayName());
				ManagerEntity managerEntity = new ManagerEntity();
				managerEntity.setDisplayName(identity.getDisplayName());
				managerEntity.setName(identity.getName());
				managerList.add(managerEntity);
			}
		}
		return managerList;

	}

	// ============== Get Sub Ordinates ====
	public ArrayList<String> getSubordinates(String managerName) throws GeneralException {
		ArrayList<String> subOrdinateList = new ArrayList<String>();

		if (null != managerName && !managerName.equalsIgnoreCase("ALL")) {

			Filter myFilter = Filter.eq("manager.name", managerName);
			QueryOptions qo = new QueryOptions();
			qo.addFilter(myFilter);

			Iterator<Identity> iter = context.search(Identity.class, qo);

			if (null != iter) {
				while (iter.hasNext()) {
					Identity identity = (Identity) iter.next();
					subOrdinateList.add(identity.getManager().getDisplayName() + "-" + identity.getName() + "-"
							+ identity.getDisplayName());

				}
			}
		} else if (null != managerName && managerName.equalsIgnoreCase("ALL")) {

			Filter myFilter = Filter.notnull("manager");
			QueryOptions qo = new QueryOptions();
			qo.addFilter(myFilter);

			Iterator<Identity> iter = context.search(Identity.class, qo);

			if (null != iter) {
				while (iter.hasNext()) {
					Identity identity = (Identity) iter.next();
					subOrdinateList.add(identity.getManager().getDisplayName() + "-" + identity.getName() + "-"
							+ identity.getDisplayName());

				}
			}
		}
		return subOrdinateList;

	}

}