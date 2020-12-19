package com.perlimTest.hatio.twitterHandler.bean;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.perlimTest.hatio.twitterHandler.access.FilteredStreamDataModelBean;
import com.perlimTest.hatio.twitterHandler.entityClasses.TweetsFilter;
import com.perlimTest.hatio.twitterHandler.utils.StringUtilz;

/*
 * the view Scoped bean class for tweetsFilter Xhtml page
 * @author amalchand
 * @since Dec 04,2020
 */
@Component(value = "tweetsFilterBean")
@SessionScoped
public class TweetsFilterBean implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String filterFollower;
	private String filterKeyword;
	private static final String LITTERAL_AT = "@";
	private static final String LITTERAL_KEYWORD_SHORT = "k:";
	private static final String LITTERAL_FOLLOWER_SHORT = "f:";
	private LinkedList<TweetsFilter> filteredTweetsList;
	private final FilteredStreamDataModelBean tweetsStreamer;

	@Inject
	public TweetsFilterBean(final FilteredStreamDataModelBean tweetsStreamer)
	{
		this.tweetsStreamer = tweetsStreamer;
		filteredTweetsList = new LinkedList<TweetsFilter>();

	}

	public void filterTweets()
	{
		try
			{
				FacesContext.getCurrentInstance().getExternalContext().getSession(true);
				FacesContext.getCurrentInstance().getExternalContext().getSessionId(true);
				final Map<String, String> rules = new HashMap<>();
				if (!StringUtilz.isNullorEmpty(getFilterKeyword()))
					{
						rules.put(getFilterKeyword(),
								LITTERAL_KEYWORD_SHORT + StringUtilz.getTrimValueAfterNullCheck(getFilterKeyword()));
					}
				if (!StringUtilz.isNullorEmpty(getFilterFollower()))
					{
						final String folwrN = getFilterFollower().trim().startsWith(LITTERAL_AT)
								? getFilterFollower().trim()
								: LITTERAL_AT + getFilterFollower().trim();
						rules.put(folwrN, LITTERAL_FOLLOWER_SHORT + folwrN);
					}
				tweetsStreamer.setResetingRules(true);
				filteredTweetsList.clear();
				tweetsStreamer.getFilteredStream(rules, filteredTweetsList);

			}
		catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		catch (URISyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public void eventupdater(ValueChangeEvent e)
	{
		System.out.println(e.getComponent().getId() + "-:_" + e.getNewValue());
	}

	public Queue<TweetsFilter> getFilteredTweetsList()
	{
		return filteredTweetsList;
	}

	public void setFilteredTweetsList(LinkedList<TweetsFilter> filteredTweetsList)
	{
		this.filteredTweetsList = filteredTweetsList;
	}

	public String getFilterKeyword()
	{
		return filterKeyword;
	}

	public void setFilterKeyword(String filterKeyword)
	{
		this.filterKeyword = filterKeyword;
	}

	public String getFilterFollower()
	{
		return filterFollower;
	}

	public void setFilterFollower(String filterFollower)
	{
		this.filterFollower = filterFollower;
	}

}
