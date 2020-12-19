package com.perlimTest.hatio.twitterHandler.access;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.primefaces.json.JSONObject;

import com.perlimTest.hatio.twitterHandler.entityClasses.TweetsFilter;
import com.perlimTest.hatio.twitterHandler.repositoryInterfaces.TweetsFilterRepository;

/*
 * Test class for FilteredTweetsStreamDataModeler
 * @author amalchand
 * @since Dec 13
 * */
class TestFilteredTweetsStreamDataModeler
{
	private FilteredTweetsStreamDataModeler instance;
	private LinkedList<TweetsFilter> tweetsFiltered;
	private TweetsFilterRepository filterRepostry;
	private static final String FAILURE_MESSAGE = "test case failed";
	private static final String SAMPLE_RESPONCE_STRING = "{\"data\":{\"author_id\":\"1305866173695758337\",\"id\":\"1338400659410841605\",\"text\":\"RT @kpophappenings_: when that kpop boy snatched his memberâ€™s wig\",\"created_at\":\"2020-12-14T08:29:18.000Z\"}"
			+ ",\"includes\":{\"users\":[{\"name\":\"â˜…Â«kyuâ?¿gsâˆžlat\",\"username\":\"XIUBAOZIII\",\"profile_image_url\":\"https://pbs.twimg.com/profile_images/1338122027836657666/vKqxRnrN_normal.jpg\",\"id\":\"1305866173695758337\"}]}"
			+ ",\"matching_rules\":[{\"id\":1338400683221864449,\"tag\":\"keyword Search\"}]}";
	private static String RESPOSNSES_STRING = SAMPLE_RESPONCE_STRING;

	@BeforeEach
	void setUp() throws Exception
	{
		instance = new FilteredTweetsStreamDataModeler();
		tweetsFiltered = new LinkedList<TweetsFilter>();
		filterRepostry = Mockito.mock(TweetsFilterRepository.class);
		Field field1 = instance.getClass().getDeclaredField("filterRepostry");
		field1.setAccessible(true);
		field1.set(instance, filterRepostry);
		int i = 0;
		do
			{
				RESPOSNSES_STRING = RESPOSNSES_STRING.concat("\r\n" + RESPOSNSES_STRING);
				i++;
			}
		while (i <= 6);
		i = 0;
		do
			{
				RESPOSNSES_STRING = RESPOSNSES_STRING.concat("\r\n{\"detail\":\"connection exception\"}");
				i++;
			}
		while (i <= 20);
	}

	@AfterEach
	void tearDown() throws Exception
	{
	}

	@Test
	void testreadModelAndSaveData() throws IOException
	{
		instance.readModelAndSaveData(null, tweetsFiltered);
		assertEquals(true, tweetsFiltered.isEmpty(), FAILURE_MESSAGE);
		final Reader inputString = new StringReader(RESPOSNSES_STRING);
		final BufferedReader reader = new BufferedReader(inputString);
		final JSONObject js = new JSONObject();
		final JSONObject jsauth = new JSONObject();
		jsauth.append("author", "test");
		js.append("data", jsauth);

		instance.readModelAndSaveData(reader, tweetsFiltered);
		assertEquals(false, tweetsFiltered.isEmpty(), FAILURE_MESSAGE);
		TweetsFilter filtrdto = tweetsFiltered.peek();
		assertTrue(Objects.nonNull(filtrdto));
		assertTrue(Objects.nonNull(filtrdto.getId()));
		assertTrue(Objects.nonNull(filtrdto.getText()));
		assertTrue(Objects.nonNull(filtrdto.getUserName()));
		assertTrue(Objects.nonNull(filtrdto.getCreateDate()));
		assertTrue(Objects.nonNull(filtrdto.getProfileImage()));
		assertTrue(Objects.nonNull(filtrdto.getMatchingRules()));

		tweetsFiltered.clear();
		instance.setResetStreaming(true);
		instance.readModelAndSaveData(reader, tweetsFiltered);
		assertEquals(true, tweetsFiltered.isEmpty(), FAILURE_MESSAGE);

		instance.setResetStreaming(false);
		final Reader inputString1 = new StringReader(SAMPLE_RESPONCE_STRING);
		final BufferedReader reader2 = new BufferedReader(inputString1);
		Mockito.when(filterRepostry.save(Mockito.any(TweetsFilter.class))).thenThrow(IllegalArgumentException.class);
		instance.readModelAndSaveData(reader2, tweetsFiltered);
		assertEquals(true, tweetsFiltered.isEmpty(), FAILURE_MESSAGE);
	}

}
