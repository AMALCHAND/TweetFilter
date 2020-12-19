package com.perlimTest.hatio.twitterHandler.utils;

/*
 * @author amalchand
 * @since Dec 04,2020
 */
public final class StringUtilz
{

	public static final String EMPTYSTRING = "";

	private StringUtilz()
	{
		// private constructor
	}

	public static String getTrimValueAfterNullCheck(Object value)
	{
		if (value == null)
			{
				return EMPTYSTRING;
			}
		return value.toString().trim();
	}


	public static boolean isNullorEmpty(String value)
	{
		return EMPTYSTRING.equals(getTrimValueAfterNullCheck(value));
	}

}
