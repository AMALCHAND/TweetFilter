package com.perlimTest.hatio.twitterHandler.entityClasses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tblTweetesFilteredDetails")
public class TweetsFilter
{
	@Id
	@Column(length = 70)
	private String id;
	@Column(length = 50)
	private String userName;
	@Column(length = 25)
	private String createDate;
	@Column(length = 800)
	private String text;
	@Column(length = 105)
	private String matchingRules;
	@Column(length = 300)
	private String profileImage;

	public TweetsFilter()
	{
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getMatchingRules()
	{
		return matchingRules;
	}

	public void setMatchingRules(String matchingRules)
	{
		this.matchingRules = matchingRules;
	}

	public String getCreateDate()
	{
		return createDate;
	}

	public void setCreateDate(String createDate)
	{
		this.createDate = createDate;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getProfileImage()
	{
		return profileImage;
	}

	public void setProfileImage(String profileImage)
	{
		this.profileImage = profileImage;
	}

}
