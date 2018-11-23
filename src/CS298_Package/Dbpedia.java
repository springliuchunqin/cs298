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
	 * Obtain the nouns category
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
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				catNames.add(dbpedia_key);
			}
		}
		catNameSet.addAll(catNames);
		
		return catNameSet;
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
		
		MongoClient mongoClient = new MongoClient("localhost");
		DB db = mongoClient.getDB("dbpedia");
		
		for(String dbpedia_key : dbpediaTables.keySet()) {
			DBCollection coll = db.getCollection(dbpedia_key);
		    BasicDBObject query = new BasicDBObject(dbpediaTables.get(dbpedia_key), new BasicDBObject("$regex", noun.replaceAll("'", "")).append("$options", "i"));
	                //new BasicDBObject("$regex", "/"+noun.replaceAll("'", "")+"/").append("$options", "i"));
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
