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
		 
		    double sum = 0.0;
		    double vsum = 0.0;
		 
			ArrayList<String> twitterUserNames = new ArrayList<String>();
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
					//从数据库中精选
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, twitterUserNames.get(i));
					rs = stmt.executeQuery(); //twitter的用户名队列
					while(rs.next()){ //rs.getString(2)类别; rs.getString(1)Twitter用户名
						mapClicks.put(twitterUserNames.get(i), getResults(rs.getString(2), mapClicks.get(rs.getString(1))));
						mapClickInteval.put(twitterUserNames.get(i), getResults1(rs.getDouble(3), mapClickInteval.get(rs.getString(1))));
						sum = sum + rs.getDouble(3); //第三列时限累加
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
				ArrayList<String> mapTopicList = new ArrayList<String>(mapTopics.get(keyValue));
				ArrayList<String> mapClickList = new ArrayList<String>(mapClicks.get(keyValue));
				ArrayList<Double> mapClickNumList = new ArrayList<Double>(mapClickInteval.get(keyValue));
				
				float mapSize = mapClickList.size();
				float cur = 0;
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
	//类别、用户名传递过来，如果不存在则添加
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
	      if (hm.get(o).equals(value)) {
	        o1.add(o);
	      }
	    }
	    return o1;
	  }
	//获取当前内容
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
	//获取主题
    public static List<String> getTopics(String twitterName) {
			LinkedHashMap<String, Integer> clickMap = new LinkedHashMap<String, Integer>();
			LinkedHashMap<String, Integer> tweetMap = new LinkedHashMap<String, Integer>();
			LinkedHashMap<String, Integer> followerMap = new LinkedHashMap<String, Integer>();
			LinkedHashMap<String, Integer> mergeMap = new LinkedHashMap<String, Integer>();
			ArrayList<String> topics = new ArrayList<String>();
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS298", "root", "");
				String sql ="SELECT * FROM CLICKTHROUGHDETAILS WHERE TWITTERUSERNAME = ? ORDER BY SCORE DESC;";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, twitterName);
				ResultSet rs = stmt.executeQuery();

				while(rs.next()){
					clickMap.put(rs.getString("topic"), rs.getInt("score"));
				}
				
				sql ="SELECT * FROM TWEETDETAILS WHERE TWITTERUSERNAME = ? ORDER BY SCORE DESC;";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, twitterName);
				rs = stmt.executeQuery();

				while(rs.next()){
					tweetMap.put(rs.getString("topic"), rs.getInt("score"));
				}
				
				
				sql ="SELECT * FROM FOLLOWERDETAILS WHERE TWITTERUSERNAME = ? ORDER BY SCORE DESC;";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, twitterName);
				rs = stmt.executeQuery();

				while(rs.next()){
					followerMap.put(rs.getString("topic"), rs.getInt("score"));
				}
				//整个大小
				double totalSize = clickMap.size() + tweetMap.size() + followerMap.size();
				totalSize = totalSize / 3;
				totalSize = Math.round(totalSize);
				//点击数量与总数量的平均数量
				if(clickMap.size() < totalSize) {
					topics.addAll(clickMap.keySet());
					
					mergeMap.putAll(clickMap);
				}
				else
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
				//推文数量与总数量的平均数量
				if(tweetMap.size() < totalSize) {
					topics.addAll(tweetMap.keySet());
					
					if(mergeMap.size() == 0) {
						mergeMap.putAll(tweetMap);
					}
					else
					{
						for(String keyValue : tweetMap.keySet()) {
							mergeMap.putAll(getCurMap(keyValue, tweetMap.get(keyValue),mergeMap));
						}
					}
				}
				else
				{
					int curCount = 0;
					for(String keyValue : tweetMap.keySet()) {
						topics.add(keyValue);
						
						mergeMap.putAll(getCurMap(keyValue, tweetMap.get(keyValue),mergeMap));
						
						curCount = curCount + 1;
						if(curCount == totalSize)
							break;
					}
				}
				//粉丝数量与总数量的平均数量
				if(followerMap.size() < totalSize) {
					topics.addAll(followerMap.keySet());
					
					if(mergeMap.size() == 0) {
						mergeMap.putAll(followerMap);
					}
					else
					{
						for(String keyValue : followerMap.keySet()) {
							mergeMap.putAll(getCurMap(keyValue, followerMap.get(keyValue),mergeMap));
						}
					}
				}
				else
				{
					int curCount = 0;
					for(String keyValue : followerMap.keySet()) {
						topics.add(keyValue);
						
						mergeMap.putAll(getCurMap(keyValue, followerMap.get(keyValue),mergeMap));
						
						curCount = curCount + 1;
						if(curCount == totalSize)
							break;
					}
				}
				
				ArrayList<Integer> curValList = new ArrayList<Integer>();
				for(String keyStr: mergeMap.keySet()) {
					curValList.add(mergeMap.get(keyStr));
				}
				
				HashSet<Integer> curValSet = new HashSet<Integer>(curValList);
				
				ArrayList<Integer> curValList_1 = new ArrayList<Integer>(curValSet);
				//ArrayList<Integer> curValList_2 = new ArrayList<Integer>();
				Collections.reverse(curValList_1);
				
				ArrayList<Object> curValListString = new ArrayList<Object>();
				for(int i = 0; i < curValList_1.size(); i++) {
					curValListString.addAll(getKeyFromValue(mergeMap, curValList_1.get(i)));
				}
				
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
				
				return topics;
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
