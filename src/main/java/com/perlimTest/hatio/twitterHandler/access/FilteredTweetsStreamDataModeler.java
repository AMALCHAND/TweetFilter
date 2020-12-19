package com.perlimTest.hatio.twitterHandler.access;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;

import javax.faces.bean.SessionScoped;
import javax.persistence.PersistenceException;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perlimTest.hatio.twitterHandler.entityClasses.TweetsFilter;
import com.perlimTest.hatio.twitterHandler.repositoryInterfaces.TweetsFilterRepository;
import com.perlimTest.hatio.twitterHandler.utils.StringUtilz;

/*
 * Data Modeling class For TwitterFilter
 * @author amalchand
 * @since Dec 04,2020
 */
@Service
@SessionScoped
public class FilteredTweetsStreamDataModeler
{
	private static final String LITTERAL_DATA = "data";
	private static final String LITTERAL_ID = "id";
	private static final String LITTERAL_TEXT = "text";
	private static final String LITTERAL_CREATED_AT = "created_at";
	private static final String LITTERAL_INCLUDES = "includes";
	private static final String LITTERAL_USERS = "users";
	private static final String LITTERAL_USERNAME = "username";
	private static final String LITTERAL_PROFILEIMAGE = "profile_image_url";
	private static final String LITTERAL_MATCHING_RULES = "matching_rules";
	private static final String LITTERAL_TAG = "tag";
	private static final Predicate<Queue<TweetsFilter>> tweetsCountMax20 = tweets -> tweets.size() > 20;
	private boolean resetStreaming;

	@Autowired
	private TweetsFilterRepository filterRepostry;

	public FilteredTweetsStreamDataModeler()
	{
	}

	public void readModelAndSaveData(final BufferedReader reader, final LinkedList<TweetsFilter> tweetsFiltered)
			throws IOException
	{
		if (Objects.nonNull(reader))
			{
				int garbageCounter = 0;
				String line;
				while ((line = reader.readLine()) != null && !isResetStreaming())
					{
						try
							{
								final JSONObject json = new JSONObject(line);
								final JSONObject data = json.getJSONObject(LITTERAL_DATA);
								final TweetsFilter filteredTweet = new TweetsFilter();
								filteredTweet.setId(data.optString(LITTERAL_ID));
								filteredTweet.setCreateDate(data.optString(LITTERAL_CREATED_AT));
								filteredTweet.setText(data.optString(LITTERAL_TEXT));
								final JSONObject includes = json.optJSONObject(LITTERAL_INCLUDES);
								final JSONArray userArray = includes.optJSONArray(LITTERAL_USERS);
								final JSONObject userJsonObj = userArray.optJSONObject(0);
								filteredTweet.setUserName(userJsonObj.optString(LITTERAL_USERNAME));
								filteredTweet.setProfileImage(userJsonObj.optString(LITTERAL_PROFILEIMAGE));
								final JSONArray matching_rules = json.optJSONArray(LITTERAL_MATCHING_RULES);
								if (Objects.nonNull(matching_rules))
									{
										matching_rules.forEach(rules ->
										{
											final JSONObject rule = (JSONObject) rules;
											filteredTweet.setMatchingRules(StringUtilz
													.getTrimValueAfterNullCheck(filteredTweet.getMatchingRules())
													+ rule.optString(LITTERAL_TAG) + "\r");
										});
									}

								filterRepostry.save(filteredTweet);
								tweetsFiltered.addFirst(filteredTweet);
								if (tweetsCountMax20.test(tweetsFiltered))
									{
										tweetsFiltered.removeLast();
									}
							}
						catch (IllegalArgumentException | PersistenceException e)
							{
								e.printStackTrace();
							}
						catch (JSONException e)
							{
								System.out.println(line);
								if (!StringUtilz.isNullorEmpty(line) )
									{
										final JSONObject json = new JSONObject(line);
										final TweetsFilter tweetFiltered = new TweetsFilter();
										tweetFiltered.setText(json.optString("detail"));
										tweetsFiltered.add(tweetFiltered);
										
									}
							}
						finally
							{
								garbageCounter++;
								if (garbageCounter > 100)
									{
										garbageCounter = 0;
										System.gc();
									}
							}
						
					}
				reader.close();
			}

	}

	public boolean isResetStreaming()
	{
		return resetStreaming;
	}

	public void setResetStreaming(boolean resetStreaming)
	{
		this.resetStreaming = resetStreaming;
	}

}
