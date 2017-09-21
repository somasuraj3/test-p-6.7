package com.cybage.sonar.report.pdf.builder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonarqube.ws.QualityProfiles.SearchWsResponse;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.qualityprofile.SearchWsRequest;

import com.cybage.sonar.report.pdf.entity.QualityProfile;

public class QualityProfileBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectStatusBuilder.class);

	private static QualityProfileBuilder builder;

	private WsClient wsClient;

	public QualityProfileBuilder(final WsClient wsClient) {
		this.wsClient = wsClient;
	}

	public static QualityProfileBuilder getInstance(final WsClient wsClient) {
		if (builder == null) {
			return new QualityProfileBuilder(wsClient);
		}

		return builder;
	}
	
	public List<QualityProfile> initProjectQualityProfilesByProjectKey(final String key) {
		
		LOGGER.info("Retrieving quality profile info for " + key);
		
		SearchWsRequest searchWsReq = new SearchWsRequest();
		searchWsReq.setProjectKey(key);
		SearchWsResponse searchWsRes = wsClient.qualityProfiles().search(searchWsReq);
		
		List<QualityProfile> profiles = new ArrayList<>(); 
		
		for(org.sonarqube.ws.QualityProfiles.SearchWsResponse.QualityProfile profile : searchWsRes.getProfilesList()){
			profiles.add(new QualityProfile(profile.getKey(), profile.getName(), profile.getLanguage(), profile.getLanguageName(), profile.getIsInherited(), profile.getIsDefault(), profile.getActiveRuleCount(), profile.getRulesUpdatedAt(), profile.getProjectCount()));
		}
		
		return profiles;
				
	}
}
