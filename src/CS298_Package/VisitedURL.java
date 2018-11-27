package CS298_Package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class VisitedURL {
	
	/**
	 * Obatin the Recently visited Websites by the user
	 * @return
	 */
	public static LinkedHashSet<String> visitedURLs() {
		ArrayList<String> websites = new ArrayList<String>();
		LinkedHashSet<String> webs = new LinkedHashSet<String>();
	    
	    Connection connection = null;
	    ResultSet resultSet = null;
	    Statement statement = null;

	    try 
	    {
	        Class.forName ("org.sqlite.JDBC");
	        connection = DriverManager
	            .getConnection ("jdbc:sqlite:C:\\Users\\dell\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\tcmb50ok.default\\places.sqlite");
	        statement = connection.createStatement ();
	        Integer count = 200;
	        resultSet = statement
		            .executeQuery ("SELECT url FROM moz_places order by visit_count desc");

	        while (resultSet.next()) 
	        {
	        	//System.out.println ("URL [" + resultSet.getString ("url") + "]");
	        	if(resultSet.getString ("url").contains("//")) {
	            	String[] url_1 = resultSet.getString ("url").split("//");
	            	if(url_1[1].contains("/")) {
	            		String[] url_2 = url_1[1].split("/");
	            		//System.out.println(url_2[0]);
	            		websites.add(url_2[0]);
	            	}
	            	else
	            	{
	            		websites.add(url_1[1]);
	            	}
	        	}

	        }
	    } 

	    catch (Exception e) 
	    {
	        e.printStackTrace ();
	    } 
	    if(websites.size() > 0)
	    	webs.addAll(websites);

	    return webs;
	}

}
