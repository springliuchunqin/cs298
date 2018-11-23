package CS298_Package;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class TweetFile {

	//get user profile details
	//机密、密钥、访问令牌、oauth验证
	public static final String CONSUMER_SECRET = "acdb59c588a367e313ec328963f76842";
	public static final String CONSUMER_KEY = "1273749970";
	public static final String ACCESS_TOKEN = "2.00tWPLuG3YWM5B57a1559ae1F6ZQBD";
	
	public static final String ACCESS_SECRET = "963b39ceeb8b210fef5f2122d59f623f";
	
	public static final String REQUEST_TOKEN_URL = "https://api.weibo.com/oauth2/request_token";
	public static final String AUTHORIZE_URL = "https://api.weibo.com/oauth2/authorize";
	public static final String ACCESS_TOKEN_URL = "https://api.weibo.com/oauth2/access_token";

    public OAuthConsumer Consumer;
    OAuthTokenSecret OAuthTokens;
    String DEF_FILENAME = "searchresults.json";
    BufferedWriter OutFileWriter;
    /**
     * Creates a OAuthConsumer with the current consumer & user access tokens and secrets
     * @return consumer
     */
    public static OAuthConsumer GetConsumer()
    {
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,CONSUMER_SECRET);
        consumer.setTokenWithSecret(ACCESS_TOKEN,ACCESS_SECRET);
        return consumer;
    }
    
    /**
     * Retrieved the friends list of the user
     * @param username the name of the user whose friends need to be retrieved
     * @return a list of friend's ids
     * @throws Exception 
     */
    
    public ArrayList<String> getFriends(String username) throws Exception
    {
    	BufferedReader bRead = null;
    	ArrayList<String> friends = new ArrayList<String>();
    	JSONArray statuses = new JSONArray();
		try {
			System.out.println("Processing status messages of " + username);
			URL url = null;    
			url = new URL("https://api.weibo.com/1.1/users/lookup.json?screen_name=" + username);
			HttpURLConnection huc1 = (HttpURLConnection) url.openConnection();
            huc1.setReadTimeout(5000);
            Consumer.sign(huc1);
            huc1.connect();
		   	BufferedReader bRead1 = new BufferedReader(new InputStreamReader((InputStream) huc1.getInputStream()));
	    	StringBuilder content1 = new StringBuilder();
	    	String temp1 = "";
	    	while((temp1 = bRead1.readLine())!=null)
	    	{	
	    	   content1.append(temp1);
	    	}
            JSONArray statusarr = new JSONArray(content1.toString());
            if(statusarr.length() > 0)
            {
            	long id = 0;
                for(int i=0;i<statusarr.length();i++)
                {
                    JSONObject jobj = statusarr.getJSONObject(i);
                    statuses.put(jobj);
                    id = jobj.getLong("id");
                    System.out.println(id);
                }
                statuses = null;
                statuses = new JSONArray();
                url = new URL("https://api.weibo.com/1.1/friends/ids.json?&user_id=" +id+"&screen_name=" + username);
           
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(5000);
                Consumer.sign(huc);
                huc.connect();
                if(huc.getResponseCode()==400||huc.getResponseCode()==404)
                {
                    System.out.println(huc.getResponseCode());
                    //break;
                }
                else
                if(huc.getResponseCode()==500||huc.getResponseCode()==502||huc.getResponseCode()==503)
                {
                    try {System.out.println(huc.getResponseCode());
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                       // Logger.getLogger(RESTApiExample.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                    // Step 3: If the requests have been exhausted, then wait until the quota is renewed
                	// 如果请求耗尽，请等待配额重新更新
                if(huc.getResponseCode()==429)
                {
                    huc.disconnect();
					//Thread.sleep(this.GetWaitTime("/statuses/user_timeline"));
					//continue;
                }
                bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getInputStream()));
                StringBuilder content = new StringBuilder();
                String temp = "";
                while((temp = bRead.readLine())!=null)
                {
                    content.append(temp);
                }
                System.out.println(content.toString());
                
                JSONObject jobj1 = new JSONObject(content.toString());
                JSONArray ids_Array =  jobj1.getJSONArray("ids"); 
                for(int i=0;i<ids_Array.length();i++)
                {
                  Long jobj_id = ids_Array.getLong(i);
                  friends.add(Long.toString(jobj_id));
                }
                System.out.println(jobj1.getJSONArray("ids"));
//                JSONArray statusarr1 = new JSONArray(content.toString());
//                if(statusarr1.length() > 0)
//                {
//                	int id1 = 0;
//                    for(int i=0;i<statusarr1.length();i++)
//                    {
//                        JSONObject jobj1 = statusarr1.getJSONObject(i);
//                        id1 = jobj1.getInt("id");
//                        System.out.println(id1);
//                        friends.add(Integer.toString(id1));
//                    }
//                }
            
            
            }

		} catch (Exception ex) {
			ex.printStackTrace();
		}
//		for(int tweetList = 0; tweetList < tweets.size(); tweetList++)
//			System.out.println(tweets.get(tweetList));
//		System.out.println();
    	return friends;
    }

    /**
     * Retrieved the status messages of a user
     * @param username the name of the user whose status messages need to be retrieved
     * @return a list of status messages
     * @throws Exception 
     */
    
    public ArrayList<String> getTweets(String username) throws Exception
    {
    	BufferedReader bRead = null;
    	int tweetcount = 20;
    	ArrayList<String> tweets = new ArrayList<String>();
    	JSONArray statuses = new JSONArray();
		try {
			System.out.println("Processing status messages of " + username);
			URL url = null;    
			url = new URL("https://api.weibo.com/1.1/users/lookup.json?screen_name=" + username);
			HttpURLConnection huc1 = (HttpURLConnection) url.openConnection();
            huc1.setReadTimeout(5000);
            Consumer.sign(huc1);
            huc1.connect();  //错误
		   	BufferedReader bRead1 = new BufferedReader(new InputStreamReader((InputStream) huc1.getInputStream()));
	    	StringBuilder content1 = new StringBuilder();
	    	String temp1 = "";
	    	while((temp1 = bRead1.readLine())!=null)
	    	{	
	    	   content1.append(temp1);
	    	}
            JSONArray statusarr = new JSONArray(content1.toString());
            if(statusarr.length() > 0)
            {
            	long id = 0;
                for(int i=0;i<statusarr.length();i++)
                {
                    JSONObject jobj = statusarr.getJSONObject(i);
                    statuses.put(jobj);
                    id = jobj.getLong("id");
                    System.out.println(id);
                }
                statuses = null;
                statuses = new JSONArray();
                url = new URL("https://api.weibo.com/1.1/statuses/user_timeline.json?count="+tweetcount + "&user_id=" +id+"&screen_name=" + username);
           
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(5000);
                Consumer.sign(huc);
                huc.connect();
                if(huc.getResponseCode()==400||huc.getResponseCode()==404)
                {
                    System.out.println(huc.getResponseCode());
                    //break;
                }
                else
                if(huc.getResponseCode()==500||huc.getResponseCode()==502||huc.getResponseCode()==503)
                {
                    try {System.out.println(huc.getResponseCode());
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                       // Logger.getLogger(RESTApiExample.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                    // Step 3: If the requests have been exhausted, then wait until the quota is renewed
                if(huc.getResponseCode()==429)
                {
                    huc.disconnect();
					//Thread.sleep(this.GetWaitTime("/statuses/user_timeline"));
					//continue;
                }
                bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getInputStream()));
                StringBuilder content = new StringBuilder();
                String temp = "";
                while((temp = bRead.readLine())!=null)
                {
                    content.append(temp);
                }
                try {
                	statusarr = null;
                    statusarr = new JSONArray(content.toString());
                    if(statusarr.length() > 0)
                    {
                        //break;
                        for(int i=0;i<statusarr.length();i++)
                        {
                            JSONObject jobj = statusarr.getJSONObject(i);
                            statuses.put(jobj);
                            String curTweet = jobj.getString("text");
                            //System.out.println(curTweet);
                            
                    		Pattern pattern = Pattern.compile("@\\w+");

                    		Matcher matcher = pattern.matcher(curTweet);
                    		ArrayList<String> uNames = new ArrayList<String>();
                    		while (matcher.find())
                    		{
                    		    //System.out.println(matcher.group());
                    		    uNames.add(matcher.group());
                    		}
                    		if(!uNames.isEmpty())
                    		{
                    			ArrayList<String> fullName = new ArrayList<String>();
                    			for(int k = 0; k < uNames.size(); k++) 
                    			{
                    				url = new URL("https://api.weibo.com/1.1/users/lookup.json?screen_name=" + uNames.get(k).substring(1));
                    				HttpURLConnection huc2 = (HttpURLConnection) url.openConnection();
                    				huc2.setReadTimeout(5000);
                    				Consumer.sign(huc2);
                    				huc2.connect();
                    				BufferedReader bRead2 = new BufferedReader(new InputStreamReader((InputStream) huc2.getInputStream()));
                    				StringBuilder content2 = new StringBuilder();
                    				String temp2 = "";
                    				while((temp2 = bRead2.readLine())!=null)
                    				{	
                    					content2.append(temp2);
                    					String tempName = "";
                    		            JSONArray statusarr2 = new JSONArray(content2.toString());
                    		            if(statusarr2.length() > 0)
                    		            {
                    		                for(int i2=0;i2<statusarr2.length();i2++)
                    		                {
                    		                    JSONObject jobj2 = statusarr2.getJSONObject(i2);
                    		                    tempName = jobj2.getString("name");
                    		                    //System.out.println(tempName);
                    		                    fullName.add(tempName);
                    		                }
                    		            }
                    				}
                    			}
                    			StringBuilder sb1 = new StringBuilder();
                    			String[] tempArray = curTweet.split(" ");
                    			int listIndex = 0;
                    			for(int tempALen = 0; tempALen < tempArray.length; tempALen++) {
                    				if(tempArray[tempALen].startsWith("@")) {
                    					sb1.append(fullName.get(listIndex).replaceAll(" ", "@"));
                    					sb1.append(" ");
                    					listIndex++;
                    				}
                    				else
                    				{
                    					sb1.append(tempArray[tempALen]);
                    					sb1.append(" ");
                    				}
                    			}
                    			tweets.add(sb1.toString());
                    		}
                    		else
                    		{
                    			tweets.add(curTweet);
                    		}
                        }
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            
            
            }

		} catch (Exception ex) {
			ex.printStackTrace();
		}
//		for(int tweetList = 0; tweetList < tweets.size(); tweetList++)
//			System.out.println(tweets.get(tweetList));
//		System.out.println();
    	return tweets;
    }
    
    /**
     * Obtain the Tweets Parts of Speech
     * @param tweets
     * @return
     * @throws Exception
     */
	public static ArrayList<String> getNouns() throws Exception {

		ArrayList<String> nouns = new ArrayList<String>();
		MaxentTagger tagger = new MaxentTagger(
				"E:\\stanford-postagger-2018-02-27\\models\\english-left3words-distsim.tagger");
		int listCount = 0;
//		for (int i = 0; i < tweets.size(); i++) {
//			List<List<HasWord>> sentences = tagger
//					.tokenizeText(new BufferedReader(
//							new FileReader(
//									"C:\\Users\\IBM_ADMIN\\workspace_sachin\\stanford-postagger-2011-04-20\\sample-input.txt")));
//			for (List<HasWord> sentence : sentences) {
//				ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
//				System.out.println(Sentence.listToString(tSentence, false));
//				String tempArraySen[] = Sentence.listToString(tSentence, false)
//						.split(" ");
//				String prev = "";
//				for (int j = 0; j < tempArraySen.length; j++) {
//					String[] parts = tempArraySen[j].split("/");
//					if (parts[1].equals("NNP")) {
//						nouns.add(parts[0]);
//						listCount++;
//					} else if (parts[0].equals("@")) {
//						String temp = nouns.get(listCount - 1);
//						nouns.remove(listCount - 1);
//						j++;
//						String[] parts_temp = tempArraySen[j].split("/");
//						temp = temp + " " + parts_temp[0];
//						nouns.add(temp);
//						listCount--;
//						listCount++;
//					}
//					prev = parts[0];
//				}
//			}
//		}
		
		try {
			// File reader
			BufferedReader reader = new BufferedReader(new FileReader("F:\\cs298\\CS298_Java\\outputfile.txt"));

			String line;
			// read each line
			while ((line = reader.readLine()) != null) {
				String tempArraySen[] = line.split(" ");
				String prev = "";
				for (int j = 0; j < tempArraySen.length; j++) {
					String[] parts = tempArraySen[j].split("/");
					if (parts[1].equals("NNP")) {
						nouns.add(parts[0]);
						listCount++;
					} else if (parts[0].equals("@")) {
						String temp = nouns.get(listCount - 1);
						nouns.remove(listCount - 1);
						j++;
						String[] parts_temp = tempArraySen[j].split("/");
						temp = temp + " " + parts_temp[0];
						nouns.add(temp);
						listCount--;
						listCount++;
					}
					prev = parts[0];
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nouns;
	}
	
    /**
     * Obtain the friend's details based on the ID. Obtain the name and the country.
     * @param friendDetails
     * @return
     * @throws Exception
     */
    public HashMap<String, Integer> getFriendDetails(ArrayList<String> friends) throws Exception {
    	HashMap<String, Integer> friendDetails = new HashMap<String, Integer>();
    	
    	int modValue = 0;
    	int quo = 0;
    	System.out.println(friends.size());
    	if(friends.size() > 100) {
    		modValue = friends.size() % 100;
    		quo = friends.size() / 100;
    	}
    	
    	if(friends.size() <= 100) {
    		StringBuilder sb = new StringBuilder();
    		for(int i = 0; i < friends.size() - 1 ; i ++) {
    			sb.append(friends.get(i));
    			sb.append(",");
    		}
    		sb.append(friends.get(friends.size() - 1));
    		String fParam = sb.toString();
    		URL url = null; 	
			url = new URL("https://api.weibo.com/1.1/users/lookup.json?user_id=" + fParam);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setReadTimeout(5000);
            Consumer.sign(huc);
			huc.connect();
			BufferedReader bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getInputStream()));
			StringBuilder content = new StringBuilder();
			String temp = bRead.readLine();
//			while(temp !=null)
//			{	
				content.append(temp);
	            JSONArray statusarr = new JSONArray(content.toString());
	            if(statusarr.length() > 0)
	            {
	                for(int i = 0;i < statusarr.length(); i++)
	                {
	                    JSONObject jobj = statusarr.getJSONObject(i);
	                    friendDetails.put(jobj.getString("name"), jobj.getInt("followers_count"));
	                }
//	            }
			}    
    	}
    	else
    	{
    		for(int j = 0; j < quo; j++) {
        		StringBuilder sb = new StringBuilder();
        		for(int i = j * 100; i <= 98 +  j * 100; i ++) {
        			sb.append(friends.get(i));
        			sb.append(",");
        		}
        		sb.append(friends.get(99 + j * 100));
        		String fParam = sb.toString();
        		URL url = null; 	
    			url = new URL("https://api.weibo.com/1.1/users/lookup.json?user_id=" + fParam);
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(5000);
                Consumer.sign(huc);
    			huc.connect();
    			BufferedReader bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getInputStream()));
    			StringBuilder content = new StringBuilder();
    			String temp = bRead.readLine();
//    			while(temp !=null)
//    			{	
    				content.append(temp);
    	            JSONArray statusarr = new JSONArray(content.toString());
    	            if(statusarr.length() > 0)
    	            {
    	                for(int i = 0;i < statusarr.length(); i++)
    	                {
    	                    JSONObject jobj = statusarr.getJSONObject(i);
    	                    friendDetails.put(jobj.getString("name"), jobj.getInt("followers_count"));
    	                }
//    	            }
    			} 
    		}

    		StringBuilder sb = new StringBuilder();
    		for(int i = quo * 100; i < (modValue - 1) +  (quo * 100); i ++) {
    			sb.append(friends.get(i));
    			sb.append(",");
    		}
    		sb.append(friends.get((modValue - 1) +  (quo * 100)));
    		String fParam = sb.toString();
    		URL url = null; 	
			url = new URL("https://api.weibo.com/1.1/users/lookup.json?user_id=" + fParam);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setReadTimeout(5000);
            Consumer.sign(huc);
			huc.connect();
			BufferedReader bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getInputStream()));
			StringBuilder content = new StringBuilder();
			String temp = bRead.readLine();
//			while(temp !=null)
//			{	
				content.append(temp);
	            JSONArray statusarr = new JSONArray(content.toString());
	            if(statusarr.length() > 0)
	            {
	                for(int i = 0;i < statusarr.length(); i++)
	                {
	                    JSONObject jobj = statusarr.getJSONObject(i);
	                    friendDetails.put(jobj.getString("name"), jobj.getInt("followers_count"));
	                }
//	            }
			} 
	            
	        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
	        sortedMap.putAll(sortHashMapByValuesDesc(friendDetails)); 
	        return sortedMap;
    	}
    	
    	return friendDetails;
    }
    
    /**
     * Sort the hashmap
     * @param passedMap
     * @return
     */
    public LinkedHashMap<String, Integer> sortHashMapByValuesDesc(HashMap<String, Integer> passedMap) {
    	   List mapKeys = new ArrayList(passedMap.keySet());
    	   List mapValues = new ArrayList(passedMap.values());
    	   Collections.sort(mapValues);
    	   Collections.reverse(mapValues);
    	   Collections.sort(mapKeys);

    	   LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

    	   Iterator valueIt = mapValues.iterator();
    	   while (valueIt.hasNext()) {
    	       Object val = valueIt.next();
    	       Iterator keyIt = mapKeys.iterator();

    	       while (keyIt.hasNext()) {
    	           Object key = keyIt.next();
    	           String comp1 = passedMap.get(key).toString();
    	           String comp2 = val.toString();

    	           if (comp1.equals(comp2)){
    	               passedMap.remove(key);
    	               mapKeys.remove(key);
    	               sortedMap.put((String)key, (Integer)val);
    	               break;
    	           }

    	       }

    	   }
    	   
    	   LinkedHashMap<String, Integer> sortedMapDesc = new LinkedHashMap<String, Integer>();
    	   int counter = 0;
    	   for(String keyDesc : sortedMap.keySet()) {
    		   sortedMapDesc.put(keyDesc, sortedMap.get(keyDesc));
    		   counter++;
    		   if(counter >= 98) {
    			   break;
    		   }
    	   }
    	   
    	   return sortedMapDesc;
    	}
}
