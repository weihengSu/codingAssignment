package assignment;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrlProcessor {
	
   public UrlProcessor() {}
	
   public void sendHttpGet(String line, FileInfo fileInfo) {
	 fileInfo.total++;
	   
	 try {
		URL obj = new URL(line);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		   if (responseCode == 200) {
			   fileInfo.success++;
		   } else {
			   fileInfo.failure++;
		   }
	} catch (Exception e) {
		System.out.println("Line processing Error " + e);
		fileInfo.failure++;
	} 
	   
   }
}