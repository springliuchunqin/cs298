 package CS298_Package;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class CS298_Main {

	@SuppressWarnings("rawtypes") 
	//上面注释：去除感叹号
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		LinkedHashSet<String> webs = new LinkedHashSet<String>();
		HashMap<String, Integer> catNameSet = new HashMap<String, Integer>();
		webs.addAll(VisitedURL.visitedURLs());//将所浏览的网址添加到webs中
		
	    ArrayList<String> web_1 = new ArrayList<String>(webs);//将webs数据循环添加给web_1
	    int indCount = 0;
	    //限制网络类别数最大为15
	    if(webs.size() > 15)
	    	indCount = 15;
	    else
	    	indCount = webs.size();
	    
	    for(int i = 0; i < indCount; i++){
	    	if(web_1.get(i).length() > 0)
	    	{
	    		HashSet<String> catNameSetTemp = new HashSet<String>();
	    		catNameSetTemp.addAll(webCategory(web_1.get(i))); //i<=15
	    		
	    		if(catNameSetTemp.size() > 0) {
	    			Iterator itr = catNameSetTemp.iterator();
	    			while (itr.hasNext()) {
	    				String cur_key = (String) itr.next();
	    				if(catNameSet.containsKey(cur_key)) {
	    					Integer cur_val = catNameSet.get(cur_key);//根据key得到value
	    					cur_val = cur_val + 1; //value值累加
	    					catNameSet.put(cur_key, cur_val);
	    				}
	    				else //若key值不存在set中，则将1赋值给value
	    				{
	    					catNameSet.put(cur_key, 1);
	    				}
	    			}
	    		}
	    	}
	    }
	    
	    //依次输出
	    for(String curKey : catNameSet.keySet()) {
	    	System.out.println("Key = "+curKey + " Value = "+catNameSet.get(curKey));
	    }
	    
	    //End of URL processing
		
		tweetsAnalysis("suraj_nm");
		friendAnalysis("suraj_nm");
	}
	
	/**
	 * Obtain the website category
	 * @param web
	 */
	//网络类别
	public static HashSet<String> webCategory(String web) {
		//浏览网址分析
		//System.out.println("Current Web : "+web);
		//System.out.println("---------------------------------------------------------");
		HashSet<String> catNameSet = new HashSet<String>();
		LinkedHashMap<CharSequence, String> cats = new LinkedHashMap<CharSequence, String>();
		
		//分类
		cats.put("adventure", "");
		cats.put("automobiles", "");
		cats.put("crime", "");
		cats.put("education", "");
		cats.put("entertainment", "");
		cats.put("fashion", "");
		cats.put("finance", "");
		cats.put("food", "");
		cats.put("gadgets", "");
		cats.put("health", "");
		cats.put("leisure", "");
		cats.put("media", "");
		cats.put("personality", "");
		cats.put("politics", "");
		cats.put("region", "");
		cats.put("religion", "");
		cats.put("space", "");
		cats.put("sports", "");
		cats.put("transport", "");
		
		//System.out.println("catNameSet : "+catNameSet);
		
		LinkedHashMap<CharSequence, String> catNs = new LinkedHashMap<CharSequence, String>();
		catNs.put("science", "");
		catNs.put("recreation", "");
		catNs.put("computers", "");
		catNs.put("shopping", "");
		catNs.put("regional", "");
		catNs.put("world", "");
		catNs.put("health", "");
		catNs.put("business", "");
		catNs.put("arts", "");
		
		String dbUrl = "jdbc:mysql://localhost:3306/dmoz";
		String query = "select website, category from dmoz.content_website as w, dmoz.content_category as c where website like \"%"+web+"%\" and w.catid = c.catid;";
		
		ArrayList<String> catNames = new ArrayList<String>();
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(dbUrl, "root", "");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			//System.out.println("result : "+rs);
			while (rs.next()) {
				int flag = 0;
				String tempCat = rs.getString(2).toLowerCase();
				//System.out.println("tempCat : "+tempCat);
				String[] tempCatArray = tempCat.split("/");
				List<String> tempCatList = Arrays.asList(tempCatArray); 
				
				for(int i = 0; i < tempCatList.size(); i++) {
					if(cats.keySet().contains(tempCatList.get(i))) {
						catNames.add(tempCatList.get(i));
						flag = 1;
						break;
					}
				}
				
				if(flag == 0){
					for(int i = 0; i < tempCatList.size(); i++) {
						if(catNs.keySet().contains(tempCatList.get(i))) {
							catNames.add(tempCatList.get(i));
							flag = 1;
							break;
						}
					}
				}
			} // end while

			con.close();
			catNameSet.addAll(catNames);
		} // end try

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return catNameSet;
	}
	
	/**
	 * Tweets Analysis
	 * @throws Exception
	 */
	//推文分析
	public static void tweetsAnalysis(String username) throws Exception {
		
		LinkedHashMap<String, String> dbpediaTables = new LinkedHashMap<String, String>();
		dbpediaTables.put("dbpedia_adventure", "adventure");
		dbpediaTables.put("dbpedia_automobiles", "automobiles");
		dbpediaTables.put("dbpedia_crime", "crime");
		dbpediaTables.put("dbpedia_education", "education");
		dbpediaTables.put("dbpedia_entertainment", "entertainment");
		dbpediaTables.put("dbpedia_fashion", "fashion");
		dbpediaTables.put("dbpedia_finance", "finance");
		dbpediaTables.put("dbpedia_food", "food");
		dbpediaTables.put("dbpedia_gadgets", "gadgets");
		dbpediaTables.put("dbpedia_health", "health");
		dbpediaTables.put("dbpedia_leisure", "leisure");
		dbpediaTables.put("dbpedia_media", "media");
		dbpediaTables.put("dbpedia_politics", "politics");
		dbpediaTables.put("dbpedia_region", "region");
		dbpediaTables.put("dbpedia_religion", "religion");
		dbpediaTables.put("dbpedia_space", "space");
		dbpediaTables.put("dbpedia_sports", "sports");
		dbpediaTables.put("dbpedia_transport", "transport");
		TweetFile obj = new TweetFile();
		obj.Consumer = obj.GetConsumer();
		StringBuilder sb = new StringBuilder();
		//StringBuilder是一个可变的字符序列。此类提供一个与 StringBuffer兼容的 API，但不保证同步。
		//该类被设计用作 StringBuffer 的一个简易替换，用在字符串缓冲区被单个线程使用的时候（这种情况很普遍）。
		ArrayList<String> tweets = new ArrayList<String>();
		tweets.addAll(obj.getTweets(username));
		for(int tweetList = 0; tweetList < tweets.size(); tweetList++) {
			sb.append(tweets.get(tweetList) + "\n");
		}
		FileWriter f = new FileWriter(new File("F:\\cs298\\CS298_Java\\inputfile.txt"));
		//写入文件
		f.write(sb.toString());
		f.close();
		
		//Stanford分词
	    MaxentTagger tagger = new MaxentTagger("E:\\stanford-postagger-2018-02-27\\models\\english-left3words-distsim.tagger");
	    
	    StringBuilder sb1 = new StringBuilder();
	    @SuppressWarnings("unchecked")
	    List<List<HasWord>> sentences = tagger.tokenizeText(new BufferedReader(new FileReader("F:\\cs298\\CS298_Java\\inputfile.txt")));
	    for (List<HasWord> sentence : sentences) {
	      ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
	      sb1.append(Sentence.listToString(tSentence, false) + "\n");
	    }
	    
		FileWriter f1 = new FileWriter(new File("F:\\cs298\\CS298_Java\\outputfile.txt"));
		f1.write(sb1.toString());
		f1.close();
		
		//Noun processing
		ArrayList<String> nouns = new ArrayList<String>();
		nouns.addAll(TweetFile.getNouns());
		
//		for(int i = 0; i < nouns.size(); i++)
//			System.out.println(nouns.get(i));
		
		HashMap<String, Integer> catNameTweetsSet = new HashMap<String, Integer>();
		HashSet<String> nounsSet = new HashSet<String>(nouns);
		Iterator iter = nounsSet.iterator();
		while (iter.hasNext()) {
		    //System.out.println(iter.next());
		    String cur = (String) iter.next();
		    HashSet<String> nounCats = new HashSet<String>();
		    nounCats.addAll(Dbpedia.nounsCategory(cur));
		    Iterator iter1 = nounCats.iterator();
		    while(iter1.hasNext()) {
		    	
		    	String cur_key = (String) iter1.next();
				if(catNameTweetsSet.containsKey(dbpediaTables.get(cur_key))) {
					Integer cur_val = catNameTweetsSet.get(dbpediaTables.get(cur_key));
					cur_val = cur_val + 1;
					catNameTweetsSet.put(dbpediaTables.get(cur_key), cur_val);
				}
				else
				{
					catNameTweetsSet.put(dbpediaTables.get(cur_key), 1);
				}
		    }
		}
		//System.out.println(nouns.size());
		for(String keyValue : catNameTweetsSet.keySet()) {
			System.out.println("Tweets Key  = " + keyValue + " Value = " + catNameTweetsSet.get(keyValue));
		}
		System.out.println("I am done");
	}
	
	/**
	 * Twitter Friend Analysis
	 * @throws Exception
	 */
	//朋友列表分析
	public static void friendAnalysis(String username) throws Exception {
		//Friends Analysis
		ArrayList<String> friendsList = new ArrayList<String>();
		
		LinkedHashMap<String, String> dbpediaTables = new LinkedHashMap<String, String>();
		dbpediaTables.put("dbpedia_adventure", "adventure");
		dbpediaTables.put("dbpedia_automobiles", "automobiles");
		dbpediaTables.put("dbpedia_crime", "crime");
		dbpediaTables.put("dbpedia_education", "education");
		dbpediaTables.put("dbpedia_entertainment", "entertainment");
		dbpediaTables.put("dbpedia_fashion", "fashion");
		dbpediaTables.put("dbpedia_finance", "finance");
		dbpediaTables.put("dbpedia_food", "food");
		dbpediaTables.put("dbpedia_gadgets", "gadgets");
		dbpediaTables.put("dbpedia_health", "health");
		dbpediaTables.put("dbpedia_leisure", "leisure");
		dbpediaTables.put("dbpedia_media", "media");
		dbpediaTables.put("dbpedia_politics", "politics");
		dbpediaTables.put("dbpedia_region", "region");
		dbpediaTables.put("dbpedia_religion", "religion");
		dbpediaTables.put("dbpedia_space", "space");
		dbpediaTables.put("dbpedia_sports", "sports");
		dbpediaTables.put("dbpedia_transport", "transport");
		
		HashMap<String, Integer> catNameFriendSet = new HashMap<String, Integer>();
		TweetFile tf = new TweetFile();
		tf.Consumer = tf.GetConsumer();
		friendsList.addAll(tf.getFriends(username));
		HashMap<String, Integer> fmap = new HashMap<String, Integer>();
		fmap.putAll(tf.getFriendDetails(friendsList));
		int cou = 0;
		for(String fkey : fmap.keySet()) {
			cou++;
		    HashSet<String> nounCats = new HashSet<String>();
		    nounCats.addAll(Dbpedia.nounsCategory(fkey));
		    Iterator iter1 = nounCats.iterator();
		    while(iter1.hasNext()) {
		    	String cur_key = (String) iter1.next();
				if(catNameFriendSet.containsKey(dbpediaTables.get(cur_key))) {
					Integer cur_val = catNameFriendSet.get(dbpediaTables.get(cur_key));
					cur_val = cur_val + 1;
					catNameFriendSet.put(dbpediaTables.get(cur_key), cur_val);
				}
				else
				{
					catNameFriendSet.put(dbpediaTables.get(cur_key), 1);
				}
		    }
		}
		for(String keyValue : catNameFriendSet.keySet()) {
			System.out.println("Friend Key  = " + keyValue + " Value = " + catNameFriendSet.get(keyValue));
		}
	}

}
