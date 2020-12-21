# TweetFilter
Hatio Recruitment Test
Info
	This application uses "Filtered stream" Api for filtering/searching with realtime tweets.
	This Application is built in as like a standalone application (with no user section created).

Application is Built and tested with : 
						  Java version :11
						  Server :apache-tomcat-9.0.40
						  DataBase server : mysql
						  
Note : 
1.please change values in application.properties file under src\main\resources 
	Folder with appropriate values for the following keys:
		spring.datasource.url= mysql database server ip,port and schema name for database conection
		spring.datasource.username=schema user name
		spring.datasource.password=schema user password
		app.user.beaerer.token=bearer token generated with the developer mode twitter account
		
since the application is showing new incoming real time tweets in descending order of time ,without having to refreshthe page,
pagination is not applied and shows only the latest 20 tweets.
It might be possible to not have able to read any tweets completely whenthere is a large Filtered tweets are getting streamed with searched criteria.
As a solution for the above either sorting of tweets to ascending order of time will help.(But due to point 4. Under Expectations in the shared interview question it is kept descending order of time)
