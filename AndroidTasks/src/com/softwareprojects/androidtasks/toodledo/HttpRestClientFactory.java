package com.softwareprojects.androidtasks.toodledo;

import com.domaindriven.toodledo.RestClient;
import com.domaindriven.toodledo.RestClientFactory;


public class HttpRestClientFactory implements RestClientFactory {

	@Override
	public RestClient create(final String url) {
		return new HttpRestClient(url);
	}
}
