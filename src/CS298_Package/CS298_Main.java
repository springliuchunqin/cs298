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
		//LinkedHashSet<String>确定唯一，具有集合不重复的特点
		
		HashMap<String, Integer> catNameSet = new HashMap<String, Integer>();
		webs.addAll(VisitedURL.visitedURLs());//将所浏览的网址添加到webs中
	    
		ArrayList<String> web_1 = new ArrayList<String>(webs);//将webs数据循环添加给web_1
	    //ArrayList<String>是List的一个实现类，可以实现数组大小的可变，可以很方便的进行增加和删减数组内元素的操作。
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
	    		catNameSetTemp.addAll(webCategory(web_1.get(i))); //将判断出的网络类别附加到catNameSetTemp中
	    		
	    		if(catNameSetTemp.size() > 0) {
	    			Iterator itr = catNameSetTemp.iterator();
	    			while (itr.hasNext()) {
	    				String cur_key = (String) itr.next();
	    				if(catNameSet.containsKey(cur_key)) {//判断catNameSet中是否包含cur_key(catNameSetTemp)
	    					//System.out.println("catNameSet.get(cur_key)="+catNameSet.get(cur_key));
	    					//catNameSet中cur_key对应的点击次数
	    					Integer cur_val = catNameSet.get(cur_key);
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
	//判断浏览网站的所属类别
	public static HashSet<String> webCategory(String web) {
		//浏览网址分析
		//System.out.println("Current Web : "+web);
		//System.out.println("---------------------------------------------------------");
		HashSet<String> catNameSet = new HashSet<String>();
		LinkedHashMap<CharSequence, String> cats = new LinkedHashMap<CharSequence, String>();
		//CharSequence的值是可读可写序列，而String的值是只读序列
		//类别导入
		cats.put("adventure", "");
		//cats.put("冒险", "");
		cats.put("automobiles", "");
		//cats.put("汽车", "");
		cats.put("crime", "");
		//cats.put("犯罪", "");
		cats.put("education", "");
		//cats.put("教育", "");
		cats.put("entertainment", "");
		//cats.put("娱乐", "");
		cats.put("fashion", "");
		//cats.put("时尚", "");
		cats.put("finance", "");
		//cats.put("金融", "");
		cats.put("food", "");
		//cats.put("美食", "");
		cats.put("gadgets", "");
		//cats.put("机械", "");
		cats.put("health", "");
		//cats.put("健康", "");
		cats.put("leisure", "");
		//cats.put("休闲", "");
		cats.put("media", "");
		//cats.put("媒体", "");
		cats.put("personality", "");
		//cats.put("个性", "");
		cats.put("politics", "");
		//cats.put("政治", "");
		cats.put("region", "");
		//cats.put("地区", "");
		cats.put("religion", "");
		//cats.put("宗教", "");
		cats.put("space", "");
		//cats.put("空间", "");
		cats.put("sports", "");
		//cats.put("体育", "");
		cats.put("transport", "");	
		//cats.put("运输", "");
		//System.out.println("cats : "+cats);//上述类别列表
				
		LinkedHashMap<CharSequence, String> catNs = new LinkedHashMap<CharSequence, String>();
		catNs.put("science", "");
		//catNs.put("科学", "");
		catNs.put("recreation", "");
		//catNs.put("娱乐", "");
		catNs.put("computers", "");
		//catNs.put("电脑", "");
		catNs.put("shopping", "");
		//catNs.put("购物", "");
		catNs.put("regional", "");
		//catNs.put("区域", "");
		catNs.put("world", "");
		//catNs.put("世界", "");
		catNs.put("health", "");
		//catNs.put("健康", "");
		catNs.put("business", "");
		//catNs.put("商业", "");
		catNs.put("arts", "");
		//catNs.put("艺术", "");
		//System.out.println("catNs : "+catNs);//上述类别列表
		
		String dbUrl = "jdbc:mysql://localhost:3306/dmoz";
		String query = "select website, category from dmoz.content_website as w, dmoz.content_category as c where website like \"%"+web+"%\" and w.catid = c.catid;";
		//从dmoz的表content_website中提取website和category
		ArrayList<String> catNames = new ArrayList<String>();
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(dbUrl, "root", "");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				int flag = 0;
				String tempCat = rs.getString(2).toLowerCase();
				
				String[] tempCatArray = tempCat.split("/");
				List<String> tempCatList = Arrays.asList(tempCatArray); 
				//System.out.println("tempCatList : "+tempCatList);
				
				for(int i = 0; i < tempCatList.size(); i++) {
					if(cats.keySet().contains(tempCatList.get(i))) {
						catNames.add(tempCatList.get(i));
						//System.out.println("catNames1 : "+catNames);//类别
						flag = 1;
						break;
					}
				}
				
				if(flag == 0){
					for(int i = 0; i < tempCatList.size(); i++) {
						if(catNs.keySet().contains(tempCatList.get(i))) {
							catNames.add(tempCatList.get(i));
							//System.out.println("catNames2 : "+catNames);//类别
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
		System.out.println("catNameSet : "+catNameSet);//类别
		return catNameSet;
	}
	
	/**
	 * Tweets Analysis
	 * @throws Exception
	 */
	//推文分析
	public static void tweetsAnalysis(String username) throws Exception {
		
		LinkedHashMap<String, String> dbpediaTables = new LinkedHashMap<String, String>();
		//添加类别
		dbpediaTables.put("dbpedia_adventure", "adventure");
		//dbpediaTables.put("dbpedia_adventure", "冒险");
		dbpediaTables.put("dbpedia_automobiles", "automobiles");
		//dbpediaTables.put("dbpedia_automobiles", "汽车");
		dbpediaTables.put("dbpedia_crime", "crime");
		//dbpediaTables.put("dbpedia_crime", "犯罪");
		dbpediaTables.put("dbpedia_education", "education");
		//dbpediaTables.put("dbpedia_education", "教育");
		dbpediaTables.put("dbpedia_entertainment", "entertainment");
		//dbpediaTables.put("dbpedia_entertainment", "娱乐");
		dbpediaTables.put("dbpedia_fashion", "fashion");
		//dbpediaTables.put("dbpedia_fashion", "时尚");
		dbpediaTables.put("dbpedia_finance", "finance");
		//dbpediaTables.put("dbpedia_finance", "金融");
		dbpediaTables.put("dbpedia_food", "food");
		//dbpediaTables.put("dbpedia_food", "美食");
		dbpediaTables.put("dbpedia_gadgets", "gadgets");
		//dbpediaTables.put("dbpedia_gadgets", "机械");
		dbpediaTables.put("dbpedia_health", "health");
		//dbpediaTables.put("dbpedia_health", "机械");
		dbpediaTables.put("dbpedia_leisure", "leisure");
		//dbpediaTables.put("dbpedia_leisure", "休闲");
		dbpediaTables.put("dbpedia_media", "media");
		//dbpediaTables.put("dbpedia_media", "媒体");
		dbpediaTables.put("dbpedia_politics", "politics");
		//dbpediaTables.put("dbpedia_politics", "政治");
		dbpediaTables.put("dbpedia_region", "region");
		//dbpediaTables.put("dbpedia_region", "区域");
		dbpediaTables.put("dbpedia_religion", "religion");
		//dbpediaTables.put("dbpedia_religion", "宗教");
		dbpediaTables.put("dbpedia_space", "space");
		//dbpediaTables.put("dbpedia_space", "空间");
		dbpediaTables.put("dbpedia_sports", "sports");
		//dbpediaTables.put("dbpedia_sports", "体育");
		dbpediaTables.put("dbpedia_transport", "transport");
		//dbpediaTables.put("dbpedia_transport", "运输");
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
		//创建新文件并写入数据
		FileWriter f = new FileWriter(new File("F:\\cs298\\CS298_Java\\inputfile.txt"));
		f.write(sb.toString());
		f.close();
		
		//Stanford分词英文
	    MaxentTagger tagger = new MaxentTagger("E:\\stanford-postagger-2018-02-27\\models\\english-left3words-distsim.tagger");
	    
	    //Stanford分词中文
	    //MaxentTagger tagger = new MaxentTagger("E:\\stanford-postagger-full-2014-01-04\\models\\chinese-distsim.tagger");
	    
	    //对推文本中词进行词性标注
	    StringBuilder sb1 = new StringBuilder();
	    List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader("F:\\cs298\\CS298_Java\\inputfile.txt")));

	    for (List<HasWord> sentence : sentences) {//对每句话处理
	      ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
	      sb1.append(Sentence.listToString(tSentence, false) + "\n");
	      //System.out.println(sb1); //标注且用\n分隔显示
	    }
	    
	    //创建新文件并写入数据
		FileWriter f1 = new FileWriter(new File("F:\\cs298\\CS298_Java\\outputfile.txt"));
		f1.write(sb1.toString());
		f1.close();
		
		//Noun processing
		ArrayList<String> nouns = new ArrayList<String>();
		nouns.addAll(TweetFile.getNouns()); //将推文的名词分类加到nouns中
		
		for(int i = 0; i < nouns.size(); i++)
			System.out.println("nouns.get(i)="+nouns.get(i));
		
		HashMap<String, Integer> catNameTweetsSet = new HashMap<String, Integer>();
		HashSet<String> nounsSet = new HashSet<String>(nouns);//将分好词的名词放入nounsSet
		Iterator iter = nounsSet.iterator();
		
		while (iter.hasNext()) {
		    //System.out.println(iter.next());
		    String cur = (String) iter.next(); //分词后的名词放入迭代器中，依次取出
		    HashSet<String> nounCats = new HashSet<String>();
		    nounCats.addAll(Dbpedia.nounsCategory(cur));//通过Dbpedia获得分词后的名词类别
		    
		    Iterator iter1 = nounCats.iterator();//名词类别放入迭代器 
		    while(iter1.hasNext()) {		    	
		    	String cur_key = (String) iter1.next(); //依次取出
		    	//判断推文中是否包含该类别
				if(catNameTweetsSet.containsKey(dbpediaTables.get(cur_key))) {
					Integer cur_val = catNameTweetsSet.get(dbpediaTables.get(cur_key));
					//System.out.println("dbpediaTables.get(cur_key)"+dbpediaTables.get(cur_key));
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
		//添加类别
		dbpediaTables.put("dbpedia_adventure", "adventure");
		//dbpediaTables.put("dbpedia_adventure", "冒险");
		dbpediaTables.put("dbpedia_automobiles", "automobiles");
		//dbpediaTables.put("dbpedia_automobiles", "汽车");
		dbpediaTables.put("dbpedia_crime", "crime");
		//dbpediaTables.put("dbpedia_crime", "犯罪");
		dbpediaTables.put("dbpedia_education", "education");
		//dbpediaTables.put("dbpedia_education", "教育");
		dbpediaTables.put("dbpedia_entertainment", "entertainment");
		//dbpediaTables.put("dbpedia_entertainment", "娱乐");
		dbpediaTables.put("dbpedia_fashion", "fashion");
		//dbpediaTables.put("dbpedia_fashion", "时尚");
		dbpediaTables.put("dbpedia_finance", "finance");
		//dbpediaTables.put("dbpedia_finance", "金融");
		dbpediaTables.put("dbpedia_food", "food");
		//dbpediaTables.put("dbpedia_food", "美食");
		dbpediaTables.put("dbpedia_gadgets", "gadgets");
		//dbpediaTables.put("dbpedia_gadgets", "机械");
		dbpediaTables.put("dbpedia_health", "health");
		//dbpediaTables.put("dbpedia_health", "机械");
		dbpediaTables.put("dbpedia_leisure", "leisure");
		//dbpediaTables.put("dbpedia_leisure", "休闲");
		dbpediaTables.put("dbpedia_media", "media");
		//dbpediaTables.put("dbpedia_media", "媒体");
		dbpediaTables.put("dbpedia_politics", "politics");
		//dbpediaTables.put("dbpedia_politics", "政治");
		dbpediaTables.put("dbpedia_region", "region");
		//dbpediaTables.put("dbpedia_region", "区域");
		dbpediaTables.put("dbpedia_religion", "religion");
		//dbpediaTables.put("dbpedia_religion", "宗教");
		dbpediaTables.put("dbpedia_space", "space");
		//dbpediaTables.put("dbpedia_space", "空间");
		dbpediaTables.put("dbpedia_sports", "sports");
		//dbpediaTables.put("dbpedia_sports", "体育");
		dbpediaTables.put("dbpedia_transport", "transport");
		//dbpediaTables.put("dbpedia_transport", "运输");
		
		HashMap<String, Integer> catNameFriendSet = new HashMap<String, Integer>();
		TweetFile tf = new TweetFile();
		tf.Consumer = tf.GetConsumer();
		friendsList.addAll(tf.getFriends(username));//获取朋友列表
		HashMap<String, Integer> fmap = new HashMap<String, Integer>();
		fmap.putAll(tf.getFriendDetails(friendsList));
		
		int cou = 0;
		for(String fkey : fmap.keySet()) {
			cou++;
		    HashSet<String> nounCats = new HashSet<String>();
		    nounCats.addAll(Dbpedia.nounsCategory(fkey));//将朋友的所有兴趣类别添加到nounsCats中
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
