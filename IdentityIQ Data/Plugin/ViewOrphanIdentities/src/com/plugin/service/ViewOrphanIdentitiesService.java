package com.plugin.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.plugin.dto.LinkDTO;
import com.plugin.dto.OrphanIdentities;

import sailpoint.api.SailPointContext;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.QueryOptions;
import sailpoint.plugin.PluginContext;
import sailpoint.server.BasePluginService;
import sailpoint.tools.GeneralException;
import sailpoint.tools.Util;

public class ViewOrphanIdentitiesService extends BasePluginService {
	private static Log logger = LogFactory.getLog("ViewOrphanIdentitiesService.class");
	// Logger.getLogger("ViewOrphanIdentitiesService.class");
	private PluginContext pluginContext;
	private SailPointContext context;

	public ViewOrphanIdentitiesService(PluginContext pluginContext, SailPointContext context) {
		this.pluginContext = pluginContext;
		this.context = context;
	}

	public List<OrphanIdentities> getUncorrelatedUsers(String userName) throws GeneralException {
		return getUncorrelatedIdentities(userName);
	}

	public List<OrphanIdentities> getOrphanUserData(String userName) throws GeneralException {
		return getOrphanIdentitiesData(userName);
	}  

	private List<OrphanIdentities> getOrphanIdentitiesData(String userName) throws GeneralException {
		if (logger.isTraceEnabled()) {
			logger.info("Entering method getOrphanIdentitiesData...");
		}
		List<OrphanIdentities> orphanUsers = new ArrayList<>();
		try {
			QueryOptions options = new QueryOptions();
			options.setCloneResults(true);
			Filter baseFilter = null;

			baseFilter = Filter.collectionCondition("links", Filter.and(Filter.ne("application.name", "HR-Application"), Filter.ne("application.name", "HR2")));

			if (!Util.isNullOrEmpty(userName)) {
				options.addFilter(Filter.and(baseFilter, Filter.like("name", userName, Filter.MatchMode.ANYWHERE)));
			} else {
				options.addFilter(baseFilter);
			}

			Iterator<Object[]> ite = context.search(Identity.class, options, Util.csvToList("name,id"));
			OrphanIdentities user = null;
			while (ite.hasNext()) {
				Object[] obj = ite.next();
				user = new OrphanIdentities();
				String identityName = (String) obj[0];
				String identityId = (String) obj[1];

				int linksCount;
				List<Link> links = null;
				if (Util.isNotNullOrEmpty(identityId)) {
					QueryOptions linkOptions = new QueryOptions();
					linkOptions.addFilter(Filter.eq("identity.id", identityId));
					links = context.getObjects(Link.class, linkOptions);
				}
				
				//System.out.println("links List  before links loop start : " + links);
				List<LinkDTO> dtoList = new ArrayList<>();
				for(Link link : links){

				    LinkDTO dto = new LinkDTO();
				    dto.setId(link.getId());

				    if(link.getApplication()!=null){
				        dto.setApplication(link.getApplication().getName());
				    }

				    dto.setNativeIdentity(link.getNativeIdentity());
				    dto.setDisplayName(link.getDisplayName());

				    if(link.getIdentity()!=null){
				        dto.setIdentityName(link.getIdentity().getName());
				    }
				    dto.setDisabled(link.isDisabled());
				    dto.setAttributes(link.getAttributes());
				    dtoList.add(dto);
				}

				user.setLinks(dtoList);
				//System.out.println("dtoList at the end of dtoList loop : " + dtoList);

				if (obj != null) {
					user.setName(identityName);
					if (links != null) {
						user.setLinksCount(links.size());
					} else {
						user.setLinksCount(0);
						user.setLinksNames("Identity doesnot have accounts");
					}
					orphanUsers.add(user);
				}
			}
		} catch (Exception e) {
			throw new GeneralException(e);
		}
		if (logger.isTraceEnabled()) {
			logger.info("Exiting method getOrphanIdentitiesData...");
		}
		return orphanUsers;
	}

	@SuppressWarnings("unchecked")
	private List<OrphanIdentities> getUncorrelatedIdentities(String userName) throws GeneralException {
		if (logger.isTraceEnabled()) {
			logger.info("Entering method getOrphanIdentities...");
		}
		List<OrphanIdentities> orphanUsers = new ArrayList<>();
		try {
			QueryOptions options = new QueryOptions();
			options.setCloneResults(true);
			Filter baseFilter = null;

			/*
			 * Custom custom = context.getObjectByName(Custom.class,
			 * "Aramco Custom Object"); if (custom != null) { String authSource = (String)
			 * custom.get("authSource"); if (Util.isNotNullOrEmpty(authSource)) { if
			 * (authSource.contains(",")) { baseFilter = Filter.collectionCondition("links",
			 * Filter.and(Filter.not(Filter.in("application.name",
			 * Util.csvToList(authSource))))); } else { baseFilter =
			 * Filter.collectionCondition("links", Filter.and(Filter.ne("application.name",
			 * authSource))); } } else { baseFilter = Filter.collectionCondition("links",
			 * Filter.and(Filter.ne("application.name", "HR-Application"),
			 * Filter.ne("application.name", "HR2"))); } } else { baseFilter =
			 * Filter.collectionCondition("links", Filter.and(Filter.ne("application.name",
			 * "HR-Application"), Filter.ne("application.name", "HR2"))); }
			 */

			baseFilter = Filter.collectionCondition("links", Filter.and(Filter.ne("application.name", "HR-Application"), Filter.ne("application.name", "HR2")));

			if (!Util.isNullOrEmpty(userName)) {
				options.addFilter(Filter.and(baseFilter, Filter.like("name", userName, Filter.MatchMode.ANYWHERE)));
			} else {
				options.addFilter(baseFilter);
			}

			Iterator<Object[]> ite = context.search(Identity.class, options, Util.csvToList("name,id"));
			OrphanIdentities user = null;
			while (ite.hasNext()) {
				Object[] obj = ite.next();
				user = new OrphanIdentities();
				String identityName = (String) obj[0];
				String identityId = (String) obj[1];

				int linksCount;
				List linkNames = null;
				if (Util.isNotNullOrEmpty(identityId)) {
					QueryOptions linkOptions = new QueryOptions();
					linkOptions.addFilter(Filter.eq("identity.id", identityId));
					Iterator linksIterator = context.search(Link.class, linkOptions, "application.name");
					linkNames = new ArrayList();
					while (linksIterator.hasNext()) {
						Object[] linkNameIterator = (Object[]) linksIterator.next();
						linkNames.add(linkNameIterator[0]);
					}
					//System.out.println("linksNames List : " + linkNames + "  linksCount from linksNames list :" + linkNames.size());
				}

				if (obj != null) {
					user.setName(identityName);
					if (linkNames != null) {
						user.setLinksCount(linkNames.size());
						user.setLinksNames(Util.listToCsv(linkNames));
						linkNames.clear();
						//System.out.println("linksNames List after clear  : " + linkNames);
					} else {
						user.setLinksCount(0);
						user.setLinksNames("Identity doesnot have accounts");
					}
					orphanUsers.add(user);
				}
			}
		} catch (Exception e) {
			throw new GeneralException(e);
		}
		if (logger.isTraceEnabled()) {
			logger.info("Exiting method getOrphanIdentities...");
		}
		return orphanUsers;
	}

	@Override
	public String getPluginName() {
		return "ViewOrphanIdentities";
	}

}