package CS298_Package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class Main {

	 public static void main(String[] args) {
//		    LinkedHashMap map = new LinkedHashMap();
//		    map.put("key_1","one");
//		    map.put("key_2","two");
//		    map.put("key_3","three");
//   	    map.put("key_4","four");

//		    System.out.println(getKeyFromValue(map,"four"));
		 
		    double sum = 0.0; //timespent时限数
		    double vsum = 0.0; //点击数
		 
			ArrayList<String> twitterUserNames = new ArrayList<String>();
			//通过维护一个运行于所有条目的双向链表，LinkedHashMap保证了元素迭代的顺序。该迭代顺序可以是插入顺序或者是访问顺序。
			//LinkedHashMap<key,value> 
			LinkedHashMap<String, List<String>> mapTopics = new LinkedHashMap<String, List<String>>();
			LinkedHashMap<String, List<String>> mapClicks = new LinkedHashMap<String, List<String>>();
			LinkedHashMap<String, List<Double>> mapClickInteval = new LinkedHashMap<String, List<Double>>();
			try {//连接SQL
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS298", "root", "");
				String sql ="SELECT DISTINCT(TWITTERUSERNAME) FROM LOGGER2 WHERE TWITTERUSERNAME IS NOT NULL;";
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()){
					twitterUserNames.add(rs.getString(1));
				}
				//读取Twitter数据
				for (int i = 0; i < twitterUserNames.size(); i++) {
					//获取Twitterusername为i的用户的主题偏好
					mapTopics.put(twitterUserNames.get(i), getTopics(twitterUserNames.get(i)));
					sql ="select twitterusername, category, sum(timespent) as time_spent from logger2 where twitterusername = ? group by twitterusername, category order by time_spent desc limit 5;";
					//从数据库中精选twitterusername category timespent
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, twitterUserNames.get(i));
					rs = stmt.executeQuery(); //twitter的用户名队列
						
					while(rs.next()){ //rs.getString(2)类别; rs.getString(1)Twitter用户名; rs.getDouble(3) timespent用时
						mapClicks.put(twitterUserNames.get(i), getResults(rs.getString(2), mapClicks.get(rs.getString(1))));
						mapClickInteval.put(twitterUserNames.get(i), getResults1(rs.getDouble(3), mapClickInteval.get(rs.getString(1))));// mapClickInteval点击间隔时间
						sum = sum + rs.getDouble(3); //第三列用时累加
					}
				}
				
			}
			catch(SQLException se){
			      //Handle errors for JDBC
			      se.printStackTrace();
			}catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			}
			//主题偏好、点击行为及点击次数
			LinkedHashMap<String, Float> mapRes = new LinkedHashMap<String, Float>();
			for(String keyValue : mapClicks.keySet()) {
				//ArrayList<String> string类型的动态数组
				ArrayList<String> mapTopicList = new ArrayList<String>(mapTopics.get(keyValue));
				ArrayList<String> mapClickList = new ArrayList<String>(mapClicks.get(keyValue));
				ArrayList<Double> mapClickNumList = new ArrayList<Double>(mapClickInteval.get(keyValue));
				
				float mapSize = mapClickList.size(); //点击列表的大小
				float cur = 0;//统计数量：region替换为regional的数量
				for(int i = 0; i < mapSize; i++) {
					if(mapTopicList.contains(mapClickList.get(i).replaceAll("region", "regional"))) {
						cur = cur + 1;
						vsum = vsum + mapClickNumList.get(i);//点击数累加
					}
				}
				mapRes.put(keyValue, cur/mapSize);
			}
			for(String keyVal : mapRes.keySet()) {
				System.out.println("Key = "+keyVal + "Value = " +mapRes.get(keyVal));
			}
			System.out.println("Sum = "+sum);//时限累加
			System.out.println("VSum = "+vsum);//点击数累积
	}
	//类别、用户的点击类别列表传递过来，判断列表是否为空，添加返回新列表
	private static ArrayList<String> getResults(String cur, List<String> curList) {
		 ArrayList<String> newList = new ArrayList<String>();
		 if(curList == null || curList.size() == 0)
			 newList.add(cur);
		 else
		 {
			 newList.addAll(curList);
			 newList.add(cur);
		 }
		 return newList;
	 }
	//用时、时间间隔列表传递过来， 判断列表是否为空，添加返回新列表
	private static ArrayList<Double> getResults1(Double cur, List<Double> curList) {
		 ArrayList<Double> newList = new ArrayList<Double>();
		 if(curList == null || curList.size() == 0)
			 newList.add(cur);
		 else
		 {
			 newList.addAll(curList);
			 newList.add(cur);
		 }
		 return newList;
	 }
	//获取key value值
	private static ArrayList<Object> getKeyFromValue(LinkedHashMap hm, Object value) {
		ArrayList<Object> o1 = new ArrayList<Object>();
	    
		for (Object o : hm.keySet()) {
			
	    	//System.out.println("hm.get(o)="+hm.get(o));
	        if (hm.get(o).equals(value)==true) {//分别判断hm各类别次数与value是否相同	        	
	            o1.add(o);	        
	        }	       
	    }
		//System.out.println("o1="+o1);//返回次数相同的类别   
	    return o1;
	  }
	//判断该类别是否存在，不存在添加，存在次数累加
	private static LinkedHashMap<String, Integer> getCurMap(String curKey, Integer curValue, LinkedHashMap<String, Integer> prev) {
		LinkedHashMap<String, Integer> newCur = new LinkedHashMap<String, Integer>();
		if(prev == null || prev.size() == 0){
			newCur.put(curKey, curValue);
		}
		else{
			newCur.putAll(prev);
			if(!prev.keySet().contains(curKey)) {
				newCur.put(curKey, curValue);
			}
			else
			{
				Integer newCurVal = newCur.get(curKey) + curValue;
				newCur.put(curKey, newCurVal);
			}
			
		}
		return newCur;
	}
	//返回前五个热门话题  将twitterUserNames.get(i)传递给twitterName
    public static List<String> getTopics(String twitterName) {
    	//clickMap(topic,score)、tweetMap(topic,score)、followerMap(topic,score)、mergeMap整合
			LinkedHashMap<String, Integer> clickMap = new LinkedHashMap<String, Integer>();
			LinkedHashMap<String, Integer> tweetMap = new LinkedHashMap<String, Integer>();
			LinkedHashMap<String, Integer> followerMap = new LinkedHashMap<String, Integer>();
			LinkedHashMap<String, Integer> mergeMap = new LinkedHashMap<String, Integer>();
			ArrayList<String> topics = new ArrayList<String>();
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS298", "root", "");
				String sql ="SELECT * FROM CLICKTHROUGHDETAILS WHERE TWITTERUSERNAME = ? ORDER BY SCORE DESC;";
				//从clickthroughdetails表中提取用户i的点击详情
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, twitterName);
				ResultSet rs = stmt.executeQuery();

				while(rs.next()){
					clickMap.put(rs.getString("topic"), rs.getInt("score"));
				}
				
				sql ="SELECT * FROM TWEETDETAILS WHERE TWITTERUSERNAME = ? ORDER BY SCORE DESC;";
				//从tweetdetails表中提取用户i的tweet类型详情
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, twitterName);
				rs = stmt.executeQuery();

				while(rs.next()){
					tweetMap.put(rs.getString("topic"), rs.getInt("score"));
				}
				
				
				sql ="SELECT * FROM FOLLOWERDETAILS WHERE TWITTERUSERNAME = ? ORDER BY SCORE DESC;";
				//从followerdetails表中提取用户i的朋友点击类型详情
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, twitterName);
				rs = stmt.executeQuery();

				while(rs.next()){
					followerMap.put(rs.getString("topic"), rs.getInt("score"));
				}
				
				//全部大小=用户i的点击类别+Tweet类别+用户i的朋友点击类别
				double totalSize = clickMap.size() + tweetMap.size() + followerMap.size();
				totalSize = totalSize / 3; //平均数量
				totalSize = Math.round(totalSize);//四舍五入
								
				//点击数量<平均数量
				if(clickMap.size() < totalSize) {
					topics.addAll(clickMap.keySet());					
					mergeMap.putAll(clickMap);//整合在一起
				}
				else//大于平均值时，从0开始循环处理，直到等于这个平均值
				{
					int curCount = 0;
					for(String keyValue : clickMap.keySet()) {
						
						topics.add(keyValue);				
						mergeMap.put(keyValue, clickMap.get(keyValue));
						
						curCount = curCount + 1;
						if(curCount == totalSize)
							break;
					}
				}
				//推文数量<平均数量
				if(tweetMap.size() < totalSize) {
					topics.addAll(tweetMap.keySet());
					//若整合大小为0, 将tweetMap整个赋值给mergeMap
					if(mergeMap.size() == 0) {
						mergeMap.putAll(tweetMap);
					}
					else
					{
						//从tweet.keySet=[health,leisure]中依次取出元素赋值给keyValue, 然后循环处理
						for(String keyValue : tweetMap.keySet()) {
							//System.out.println("mergeMap = "+mergeMap);
							mergeMap.putAll(getCurMap(keyValue, tweetMap.get(keyValue), mergeMap));
							//tweetMap.get(keyValue):推文中keyValue类别的次数
							//mergeMap: 整合后的列表（类型次数）
						}
					}
				}
				else//推文数量>平均数量
				{
					int curCount = 0; //执行次数为（推文数量==平均数量）
					for(String keyValue : tweetMap.keySet()) {
						topics.add(keyValue);						
						mergeMap.putAll(getCurMap(keyValue, tweetMap.get(keyValue),mergeMap));
						
						curCount = curCount + 1;
						if(curCount == totalSize)
							break;
					}
				}
				//朋友数量<平均数量
				if(followerMap.size() < totalSize) {
					topics.addAll(followerMap.keySet());
					//如果整合的列表大小为0, 将所有followerMap赋值为mergeMap
					if(mergeMap.size() == 0) {
						mergeMap.putAll(followerMap);
					}
					else
					{
						//从followerMap.keySet()中依次取出元素赋值给keyValue, 然后循环处理
						for(String keyValue : followerMap.keySet()) {
							mergeMap.putAll(getCurMap(keyValue, followerMap.get(keyValue),mergeMap));
						}
					}
				}
				else
				{
					int curCount = 0;
					
					//System.out.println("followerMap.keySet = "+followerMap.keySet());	朋友偏好类型		
					for(String keyValue : followerMap.keySet()) {
						topics.add(keyValue);
						
						//System.out.println("followerMap.get(keyValue) = "+followerMap.get(keyValue));偏好类型次数
						mergeMap.putAll(getCurMap(keyValue, followerMap.get(keyValue), mergeMap));
						
						curCount = curCount + 1;
						if(curCount == totalSize)
							break;
					}
				}
				
				//对整合列表进行处理
				ArrayList<Integer> curValList = new ArrayList<Integer>();
								
				for(String keyStr: mergeMap.keySet()) {
					curValList.add(mergeMap.get(keyStr));
					//System.out.println("mergeMap.get(keyStr) = "+mergeMap.get(keyStr));次数
				}
				
				HashSet<Integer> curValSet = new HashSet<Integer>(curValList);
				//System.out.println("curValSet = "+curValSet); //次数集
				
				ArrayList<Integer> curValList_1 = new ArrayList<Integer>(curValSet);
				//System.out.println("curValList_1 = "+curValList_1); //将curValSet转变为有序的次数集
				
				//ArrayList<Integer> curValList_2 = new ArrayList<Integer>();
				Collections.reverse(curValList_1); //降序排序
				//System.out.println("curValList_1 = "+curValList_1);
				ArrayList<Object> curValListString = new ArrayList<Object>();
				for(int i = 0; i < curValList_1.size(); i++) {
					//System.out.println("mergeMap = "+mergeMap);
					//System.out.println("curValList_1.get(i) = "+curValList_1.get(i));
					curValListString.addAll(getKeyFromValue(mergeMap, curValList_1.get(i)));
				}
				//System.out.println("curValListString = "+curValListString);
				
				topics.clear();
				//topics.addAll(curValListString);
				rs.close();
				stmt.close();
				conn.close();
				LinkedHashSet<Object> finalTopics = new LinkedHashSet<Object>(curValListString);
				topics.clear();
				for(int i =0; i < finalTopics.size(); i++) {
					if(i == 5)
						break;
					topics.add((String) curValListString.get(i));
				}
				//topics.addAll(finalTopics);
				//System.out.println("topics = "+topics);
				return topics;  //返回前五个热门话题
			}
			catch(SQLException se){
			      //Handle errors for JDBC
			      se.printStackTrace();
			}catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			}
			return topics;
		}

}
