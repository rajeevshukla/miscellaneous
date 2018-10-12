package com.healthcheck.service.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.healthcheck.service.model.AppInstanceDetails;

public class AppUtils {

	public static List<AppInstanceDetails> loadStaticInstances() {
		
		List<AppInstanceDetails> persons = null;
		try {
			persons = loadData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ImmutableList.copyOf(persons);

	}

	private static List<AppInstanceDetails> loadData() throws IOException {
		// Resources from Guava library
		InputStream inputStream = Resources.getResource("listofinstances.json").openStream();
		String jsonData = IOUtils.toString(inputStream, "UTF-8");

		java.lang.reflect.Type listType = new TypeToken<ArrayList<AppInstanceDetails>>() {
			private static final long serialVersionUID = -2769410970603534670L;
		}.getType();

		List<AppInstanceDetails> persons = new Gson().fromJson(jsonData, listType);
		return persons;
	}

}
