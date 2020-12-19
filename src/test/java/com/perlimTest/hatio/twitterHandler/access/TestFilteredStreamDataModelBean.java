package com.perlimTest.hatio.twitterHandler.access;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.perlimTest.hatio.twitterHandler.entityClasses.TweetsFilter;

/*
 * Test class for FilteredStreamDataModelBean
 * @author amalchand
 * @since Dec 13
 * */
class TestFilteredStreamDataModelBean
{
	private FilteredStreamDataModelBean instance;
	private FilteredTweetsStreamDataModeler dataModeler;
	private HttpClient clint;
	private final static String SAMPLE_RESPONCE_STRING = "{\"data\": [{\"author_id\": \"2244994945\",\"created_at\": \"2020-02-14T19:00:55.000Z\",\"id\": \"1228393702244134912\",\"text\": \"What did the developer write in their Valentine’s card?\\n  \\nwhile(true) {\\n    I = Love(You);  \\n}\"},{\"author_id\": \"2244994945\",\"created_at\": \"2020-02-12T17:09:56.000Z\",\"id\": \"1227640996038684673\",\"text\": \"Doctors: Googling stuff online does not make you a doctor\\n\\nDevelopers: https://t.co/mrju5ypPkb\"},{\"author_id\": \"2244994945\",\"created_at\": \"2019-11-27T20:26:41.000Z\",\"id\": \"1199786642791452673\",\"text\": \"C#\"}],\"includes\":{\"users\":[{\"created_at\": \"2013-12-14T04:35:55.000Z\",\"id\": \"2244994945\",\"name\": \"Twitter Dev\",\"username\": \"TwitterDev\"}]}}"
			+ "\r\n{\\\"data\\\": [{\\\"author_id\\\": \\\"2244994945\\\",\\\"created_at\\\": \\\"2020-02-14T19:00:55.000Z\\\",\\\"id\\\": \\\"1228393702244134912\\\",\\\"text\\\": \\\"What did the developer write in their Valentine’s card?\\\\n  \\\\nwhile(true) {\\\\n    I = Love(You);  \\\\n}\\\"},{\\\"author_id\\\": \\\"2244994945\\\",\\\"created_at\\\": \\\"2020-02-12T17:09:56.000Z\\\",\\\"id\\\": \\\"1227640996038684673\\\",\\\"text\\\": \\\"Doctors: Googling stuff online does not make you a doctor\\\\n\\\\nDevelopers: https://t.co/mrju5ypPkb\\\"},{\\\"author_id\\\": \\\"2244994945\\\",\\\"created_at\\\": \\\"2019-11-27T20:26:41.000Z\\\",\\\"id\\\": \\\"1199786642791452673\\\",\\\"text\\\": \\\"C#\\\"}],\\\"includes\\\":{\\\"users\\\":[{\\\"created_at\\\": \\\"2013-12-14T04:35:55.000Z\\\",\\\"id\\\": \\\"2244994945\\\",\\\"name\\\": \\\"Twitter Dev\\\",\\\"username\\\": \\\"TwitterDev\\\"}]}}";

	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{

	}

	@BeforeEach
	void setUp() throws Exception
	{
		instance = new FilteredStreamDataModelBean();

		InputStream in = new ByteArrayInputStream(SAMPLE_RESPONCE_STRING.getBytes(StandardCharsets.UTF_8));
		clint = Mockito.mock(HttpClient.class);
		HttpResponse resp = Mockito.mock(HttpResponse.class);
		HttpEntity entity = Mockito.mock(HttpEntity.class);
		dataModeler = Mockito.mock(FilteredTweetsStreamDataModeler.class);
		Field field1 = instance.getClass().getDeclaredField("dataModeler");
		field1.setAccessible(true);
		field1.set(instance, dataModeler);

		Mockito.when(entity.getContent()).thenReturn(in);
		Mockito.when(resp.getEntity()).thenReturn(entity);
		Mockito.when(clint.execute(Mockito.any(HttpGet.class))).thenReturn(resp);
		Mockito.when(clint.execute(Mockito.any(HttpPost.class))).thenReturn(resp);
		Field field = instance.getClass().getDeclaredField("httpClient");
		field.set("httpClient", clint);

	}

	@Test
	void testGetFilteredStream() throws Exception
	{
		instance.getFilteredStream(null, null);
		final Map<String, String> rules = new LinkedHashMap<String, String>();
		instance.getFilteredStream(rules, null);
		final LinkedList<TweetsFilter> tweetsFiltered = new LinkedList<TweetsFilter>();
		instance.getFilteredStream(null, tweetsFiltered);

		instance.getFilteredStream(rules, tweetsFiltered);

		rules.put("cats has:images", "cat images");
		rules.put("dogs has:images", "dog images");
		instance.getFilteredStream(rules, tweetsFiltered);

		Mockito.doThrow(IOException.class).when(dataModeler).readModelAndSaveData(Mockito.any(), Mockito.any());
		instance.getFilteredStream(rules, tweetsFiltered);

	}

}
