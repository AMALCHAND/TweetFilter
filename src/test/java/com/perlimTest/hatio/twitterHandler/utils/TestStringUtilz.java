package com.perlimTest.hatio.twitterHandler.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/*
 * Test class for StringUtilz
 * 
 * @author amalchand
 * @since Dec 06,2020
 */
class TestStringUtilz
{
	private static final String FAILEDMESSAGE = "Failed for test case";

	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
	}

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testisNullorEmpty()
	{
		Assert.isTrue(StringUtilz.isNullorEmpty(null), FAILEDMESSAGE);
		Assert.isTrue(StringUtilz.isNullorEmpty(""), FAILEDMESSAGE);
		Assert.isTrue(StringUtilz.isNullorEmpty(" "), FAILEDMESSAGE);
		Assert.isTrue(!StringUtilz.isNullorEmpty("amal"), FAILEDMESSAGE);
	}

}
