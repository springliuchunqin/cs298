package CS298_Package;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class CS298_Main_Thread implements Runnable{
	
	private Thread t;
	private String threadName;

	CS298_Main_Thread(String name) {
		threadName = name;
		System.out.println("Creating " + threadName);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (threadName.equals("Thread-1")) {
			try {
				CS298_Main.tweetsAnalysis("narendramodi");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (threadName.equals("Thread-2")) {
			try {
				CS298_Main.friendAnalysis("narendramodi");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			LinkedHashSet<String> webs = new LinkedHashSet<String>();
			HashMap<String, Integer> catNameSet = new HashMap<String, Integer>();
			webs.addAll(VisitedURL.visitedURLs());
			
		    ArrayList<String> web_1 = new ArrayList<String>(webs);
		    int indCount = 0;
		    if(webs.size() > 15)
		    	indCount = 15;
		    else
		    	indCount = webs.size();
		    for(int i = 0; i < indCount; i++){
		    	if(web_1.get(i).length() > 0)
		    	{
		    		HashSet<String> catNameSetTemp = new HashSet<String>();
		    		catNameSetTemp.addAll(CS298_Main.webCategory(web_1.get(i)));
		    		if(catNameSetTemp.size() > 0) {
		    			Iterator itr = catNameSetTemp.iterator();
		    			while (itr.hasNext()) {
		    				String cur_key = (String) itr.next();
		    				if(catNameSet.containsKey(cur_key)) {
		    					Integer cur_val = catNameSet.get(cur_key);
		    					cur_val = cur_val + 1;
		    					catNameSet.put(cur_key, cur_val);
		    				}
		    				else
		    				{
		    					catNameSet.put(cur_key, 1);
		    				}
		    			}
		    		}
		    	}
		    }
		    
		    for(String curKey : catNameSet.keySet()) {
		    	System.out.println("Key = "+curKey + " Value = "+catNameSet.get(curKey));
		    }
		    
		}
	}
	
	public void start() {
		System.out.println("Starting " + threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		CS298_Main_Thread R1 = new CS298_Main_Thread( "Thread-1");
	    R1.start();
	    
	    CS298_Main_Thread R3 = new CS298_Main_Thread( "Thread-3");
	    R3.start();
	      
	    CS298_Main_Thread R2 = new CS298_Main_Thread( "Thread-2");
	    R2.start();
	}

}
