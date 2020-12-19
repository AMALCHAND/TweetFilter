package com.perlimTest.hatio.twitterHandler.access;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedList;

import javax.faces.bean.SessionScoped;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.perlimTest.hatio.twitterHandler.entityClasses.TweetsFilter;

/*
 * Data Modeling Bean class For TwitterFilter
 * @author amalchand
 * @since Dec 04,2020
 */
@Service
@SessionScoped
public class FilteredStreamDataModelBean
{
	private static final String STREAM_RULES_URL = "https://api.twitter.com/2/tweets/search/stream/rules";
	private static final String STREAM_URL = "https://api.twitter.com/2/tweets/search/stream";
	private static final String LITTERAL_AUTHORIZATION = "Authorization";
	private static final String LITTERAL_CONTENT_TYPE = "Content-Type";
	private static final String LITTERAL_APPLICATION_JSON = "application/json";
	private static final String LITTERAL_UTF8 = "UTF-8";
	private static final String LITTERAL_DATA = "data";
	private static final String LITTERAL_ID = "id";

	private static final String LITTERAL_CREATED_AT = "created_at";
	private static final String LITTERAL_EXPANSIONS = "expansions";
	private static final String LITTERAL_TWEETSFIELDS = "tweet.fields";
	private boolean resetingRules;
	@Value("${app.user.beaerer.token}")
	private String BEARER_TOKEN;
	private HttpPost rulesHttpPost;
	private HttpGet streamHttpGet;
	private int retryConectionCount = 0;
	static HttpClient httpClient = HttpClients.custom()
			.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
	@Autowired
	FilteredTweetsStreamDataModeler dataModeler;
	private Thread tweetsStreamerThread;

	/*
	 * the default constructor for FilteredStreamDataModelBean
	 */
	public FilteredStreamDataModelBean()
	{

	}

	public void getFilteredStream(final Map<String, String> rules, final LinkedList<TweetsFilter> tweetsFiltered)
			throws IOException, URISyntaxException
	{
		final String bearerToken = String.format("Bearer %s", BEARER_TOKEN);
		if (Objects.nonNull(rules) && Objects.nonNull(tweetsFiltered))
			{
				retryConectionCount=0;
				setupRules(bearerToken, rules);
				connectandGetTweets(bearerToken, tweetsFiltered);
			}
	}

	/*
	 * the filtered stream endpoint and streams Tweets from it and save to DB
	 */
	private void connectandGetTweets(final String bearerToken, final LinkedList<TweetsFilter> tweetsFiltered)
	{
		if (retryConectionCount < 20)
			{
				if (Objects.nonNull(tweetsStreamerThread) && tweetsStreamerThread.isAlive())
					{
						dataModeler.setResetStreaming(true);
						tweetsFiltered.clear();
						tweetsStreamerThread.interrupt();
					}
				tweetsStreamerThread = new Thread(() ->
				{
					try
						{
							final HttpGet httpGet = getStreamHttpGet(bearerToken);
							final HttpResponse response = httpClient.execute(httpGet);
							final HttpEntity entity = response.getEntity();
							if (null != entity)
								{

									try (final BufferedReader reader = new BufferedReader(
											new InputStreamReader((entity.getContent()))))
										{
											retryConectionCount++;
											System.out.println("threadStart:" + retryConectionCount);
											dataModeler.setResetStreaming(false);
											dataModeler.readModelAndSaveData(reader, tweetsFiltered);
										}
									catch (IOException e2)
										{
											connectandGetTweets(bearerToken, tweetsFiltered);
											retryConectionCount++;
										}
								}
						}
					catch (URISyntaxException e)
						{
							e.printStackTrace();
						}
					catch (IOException e2)
						{
							connectandGetTweets(bearerToken, tweetsFiltered);
							retryConectionCount++;
						}
				});
				tweetsStreamerThread.start();
			}
	}

	/*
	 * Helper method to setup rules before streaming data
	 */
	private void setupRules(final String bearerToken, final Map<String, String> rules)
			throws IOException, URISyntaxException
	{
		final List<String> existingRules = getRules(bearerToken);
		if (existingRules.size() > 0)
			{
				deleteRules(bearerToken, existingRules);
			}
		System.out.println("rules.size()" + rules.size());
		if (rules.size() > 0)
			{
				createRules(bearerToken, rules);
			}
	}

	/*
	 * Helper method to create rules for filtering
	 */
	private void createRules(final String bearerToken, final Map<String, String> rules)
			throws URISyntaxException, IOException
	{
		final HttpPost httpPost = getRulesHttpPost(bearerToken);
		final StringEntity body = new StringEntity(getFormattedString("{\"add\": [%s]}", rules));
		httpPost.setEntity(body);
		final HttpResponse response = httpClient.execute(httpPost);
		final HttpEntity entity = response.getEntity();
		if (null != entity)
			{
				System.out.println(EntityUtils.toString(entity, LITTERAL_UTF8));
			}
	}

	/*
	 * Helper method to get existing rules
	 */
	private List<String> getRules(String bearerToken) throws URISyntaxException, IOException
	{
		final List<String> rules = new ArrayList<>();
		final URIBuilder uriBuilder = new URIBuilder(STREAM_RULES_URL);
		final HttpGet httpGet = new HttpGet(uriBuilder.build());
		httpGet.setHeader(LITTERAL_AUTHORIZATION, bearerToken);
		httpGet.setHeader(LITTERAL_CONTENT_TYPE, LITTERAL_APPLICATION_JSON);
		final HttpResponse response = httpClient.execute(httpGet);
		final HttpEntity entity = response.getEntity();
		if (null != entity)
			{
				try
					{
						final JSONObject json = new JSONObject(EntityUtils.toString(entity, LITTERAL_UTF8));
						if (json.length() > 1)
							{
								final JSONArray array = (JSONArray) json.get(LITTERAL_DATA);
								for (int i = 0; i < array.length(); i++)
									{
										JSONObject jsonObject = (JSONObject) array.get(i);
										rules.add(jsonObject.getString(LITTERAL_ID));
									}

							}
					}
				catch (JSONException e)
					{
						e.printStackTrace();
					}
			}
		return rules;
	}

	/*
	 * Helper method to delete rules
	 */
	private void deleteRules(final String bearerToken, final List<String> existingRules)
			throws URISyntaxException, IOException
	{

		final HttpPost httpPost = getRulesHttpPost(bearerToken);
		final StringEntity body = new StringEntity(
				getFormattedString("{ \"delete\": { \"ids\": [%s]}}", existingRules));
		httpPost.setEntity(body);
		final HttpResponse response = httpClient.execute(httpPost);
		final HttpEntity entity = response.getEntity();
		if (null != entity)
			{
				System.out.println(EntityUtils.toString(entity, LITTERAL_UTF8));
			}
	}

	private static String getFormattedString(String string, List<String> ids)
	{
		final StringBuilder sb = new StringBuilder();
		if (ids.size() == 1)
			{
				return String.format(string, "\"" + ids.get(0) + "\"");
			}
		else
			{
				for (String id : ids)
					{
						sb.append("\"" + id + "\"" + ",");
					}
				final String result = sb.toString();
				return String.format(string, result.substring(0, result.length() - 1));
			}
	}

	private static String getFormattedString(String string, Map<String, String> rules)
	{
		final StringBuilder sb = new StringBuilder();
		if (rules.size() == 1)
			{
				final String key = rules.keySet().iterator().next();
				return String.format(string, "{\"value\": \"" + key + "\", \"tag\": \"" + rules.get(key) + "\"}");
			}
		else
			{
				for (Map.Entry<String, String> entry : rules.entrySet())
					{
						final String value = entry.getKey();
						final String tag = entry.getValue();
						sb.append("{\"value\": \"" + value + "\", \"tag\": \"" + tag + "\"}" + ",");
					}
				final String result = sb.toString();
				return String.format(string, result.substring(0, result.length() - 1));
			}
	}

	private HttpPost getRulesHttpPost(final String bearerToken) throws URISyntaxException
	{
		if (rulesHttpPost == null || rulesHttpPost.isAborted())
			{
				final URIBuilder uriBuilder = new URIBuilder(STREAM_RULES_URL);
				rulesHttpPost = new HttpPost(uriBuilder.build());
				rulesHttpPost.setHeader(LITTERAL_AUTHORIZATION, bearerToken);
				rulesHttpPost.setHeader(LITTERAL_CONTENT_TYPE, LITTERAL_APPLICATION_JSON);
			}
		rulesHttpPost.reset();
		return rulesHttpPost;
	}

	private HttpGet getStreamHttpGet(final String bearerToken) throws URISyntaxException
	{
		if (streamHttpGet != null)
			{
				streamHttpGet.reset();
				return streamHttpGet;
			}
			{
				final URIBuilder uriBuilder = new URIBuilder(STREAM_URL);
				uriBuilder.setParameter(LITTERAL_TWEETSFIELDS, LITTERAL_CREATED_AT);
				uriBuilder.setParameter(LITTERAL_EXPANSIONS, "author_id");
				uriBuilder.setParameter("user.fields", "profile_image_url,username");
				streamHttpGet = new HttpGet(uriBuilder.build());
				streamHttpGet.setHeader(LITTERAL_AUTHORIZATION, bearerToken);
			}
		return streamHttpGet;
	}

	public boolean isResetingRules()
	{
		return resetingRules;
	}

	public void setResetingRules(boolean resetingRules)
	{
		this.resetingRules = resetingRules;

	}

}