package CS298_Package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Dbpedia {

	/**
	 * Obtain the nouns category获取名词类别.
	 * @param nouns
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static HashSet<String> nounsCategory(String noun) throws ClassNotFoundException, SQLException {
		
		ArrayList<String> catNames = new ArrayList<String>();
		HashSet<String> catNameSet = new HashSet<String>();
		LinkedHashMap<String, String> dbpediaTables = new LinkedHashMap<String, String>();
		dbpediaTables.put("dbpedia_adventure", "attribute_adventure");
		dbpediaTables.put("dbpedia_automobiles", "attribute_automobiles");
		dbpediaTables.put("dbpedia_crime", "attribute_crime");
		dbpediaTables.put("dbpedia_education", "attribute_education");
		dbpediaTables.put("dbpedia_entertainment", "attribute_entertainment");
		dbpediaTables.put("dbpedia_fashion", "attribute_fashion");
		dbpediaTables.put("dbpedia_finance", "attribute_finance");
		dbpediaTables.put("dbpedia_food", "attribute_food");
		dbpediaTables.put("dbpedia_gadgets", "attribute_gadgets");
		dbpediaTables.put("dbpedia_health", "attribute_health");
		dbpediaTables.put("dbpedia_leisure", "attribute_leisure");
		dbpediaTables.put("dbpedia_media", "attribute_media");
		//dbpediaTables.put("dbpedia_personality", "attribute_personality");
		dbpediaTables.put("dbpedia_politics", "attribute_politics");
		dbpediaTables.put("dbpedia_region", "attribute_region");
		dbpediaTables.put("dbpedia_religion", "attribute_religion");
		dbpediaTables.put("dbpedia_space", "attribute_space");
		dbpediaTables.put("dbpedia_sports", "attribute_sports");
		dbpediaTables.put("dbpedia_transport", "attribute_transport");
		
		String dbUrl = "jdbc:mysql://localhost:3306/dbpedia";
		String query;
		
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(dbUrl, "root", "");
		Statement stmt = con.createStatement();
		
		for(String dbpedia_key : dbpediaTables.keySet()) {
			query = "select * from " + dbpedia_key + " where " +dbpediaTables.get(dbpedia_key) + " like '%"+noun.replaceAll("'", "")+"%'";
			//System.out.println(query);
			//根据类别选出所有的内容
			ResultSet rs = stmt.executeQuery(query);  //将刚从数据库中精选的数据放入结果集中
			while(rs.next()) {
				catNames.add(dbpedia_key); //并将类别添加到catNames
			}
		}
		catNameSet.addAll(catNames); //将所有类别添加到catNameSet中
		
		return catNameSet; //返回类别集
	}
	
	/**
	 * Obtain the nouns category
	 * @param nouns
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static HashSet<String> nounsCategoryMongo(String noun) throws ClassNotFoundException, SQLException {
		
		ArrayList<String> catNames = new ArrayList<String>();
		HashSet<String> catNameSet = new HashSet<String>();
		LinkedHashMap<String, String> dbpediaTables = new LinkedHashMap<String, String>();
		//将类别属性添加到dbpediaTables中
		dbpediaTables.put("dbpedia_adventure", "attribute_adventure");
		//dbpediaTables.put("dbpedia_adventure", "冒险");
		dbpediaTables.put("dbpedia_automobiles", "attribute_automobiles");
		//dbpediaTables.put("dbpedia_adventure", "汽车");
		dbpediaTables.put("dbpedia_crime", "attribute_crime");
		//dbpediaTables.put("dbpedia_adventure", "犯罪");
		dbpediaTables.put("dbpedia_education", "attribute_education");
		//dbpediaTables.put("dbpedia_adventure", "教育");
		dbpediaTables.put("dbpedia_entertainment", "attribute_entertainment");
		//dbpediaTables.put("dbpedia_adventure", "娱乐");
		dbpediaTables.put("dbpedia_fashion", "attribute_fashion");
		//dbpediaTables.put("dbpedia_adventure", "时尚");
		dbpediaTables.put("dbpedia_finance", "attribute_finance");
		//dbpediaTables.put("dbpedia_adventure", "金融");
		dbpediaTables.put("dbpedia_food", "attribute_food");
		//dbpediaTables.put("dbpedia_adventure", "美食");
		dbpediaTables.put("dbpedia_gadgets", "attribute_gadgets");
		//dbpediaTables.put("dbpedia_adventure", "机械");
		dbpediaTables.put("dbpedia_health", "attribute_health");
		//dbpediaTables.put("dbpedia_adventure", "健康");
		dbpediaTables.put("dbpedia_leisure", "attribute_leisure");
		//dbpediaTables.put("dbpedia_adventure", "休闲");
		dbpediaTables.put("dbpedia_media", "attribute_media");
		//dbpediaTables.put("dbpedia_adventure", "媒体");
		//dbpediaTables.put("dbpedia_personality", "attribute_personality");
		dbpediaTables.put("dbpedia_politics", "attribute_politics");
		//dbpediaTables.put("dbpedia_adventure", "政治");
		dbpediaTables.put("dbpedia_region", "attribute_region");
		//dbpediaTables.put("dbpedia_adventure", "区域");
		dbpediaTables.put("dbpedia_religion", "attribute_religion");
		//dbpediaTables.put("dbpedia_adventure", "宗教");
		dbpediaTables.put("dbpedia_space", "attribute_space");
		//dbpediaTables.put("dbpedia_adventure", "空间");
		dbpediaTables.put("dbpedia_sports", "attribute_sports");
		//dbpediaTables.put("dbpedia_adventure", "体育");
		dbpediaTables.put("dbpedia_transport", "attribute_transport");
		//dbpediaTables.put("dbpedia_adventure", "运输");
		
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB("dbpedia");//使用MongoDB服务连接到数据库
		
		for(String dbpedia_key : dbpediaTables.keySet()) {
			DBCollection coll = db.getCollection(dbpedia_key);//根据dbpedia_key关键词找到他的集锦数据
		    //条件查找
			BasicDBObject query = new BasicDBObject(dbpediaTables.get(dbpedia_key), new BasicDBObject("$regex", noun.replaceAll("'", "")).append("$options", "i"));
	                //new BasicDBObject("$regex", "/"+noun.replaceAll("'", "")+"/").append("$options", "i"))   ???
		    DBObject doc = coll.findOne(query);
		    
		    if(doc!=null) {
		    	//String tempCat = ((String) doc.get(dbpediaTables.get(dbpedia_key))).toLowerCase();
		    	catNames.add(dbpedia_key);
		    }
		}
		
		catNameSet.addAll(catNames);
		return catNameSet;
	}
}
