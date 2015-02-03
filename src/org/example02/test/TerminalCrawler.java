package org.example02.test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.ResponseContext;

public class TerminalCrawler {
	private static String path = "/Users/lukebusstra/Documents/workspace/PlayStoreServlet/WebContent/Data/";

	
	public TerminalCrawler(){
	}
	
	
	public static String TerminalCrawler(String query) throws IOException {	
			String login = "*******@gmail.com";
			String password = "******";
			int numberApps = 5;
			//String query = "google";
			String json;
			System.out.println("Query searching: " + query);
			
			if(!checkFileExsists(query)){
				System.out.println("File doesn't exsist");
				try {
					MarketSession session = new MarketSession();
					System.out.println("Login...");
					session.login(login, password);
					System.out.println("Login Complete");
			
					AppsRequest appsRequest = AppsRequest.newBuilder()
								.setQuery(query)
								.setStartIndex(1).setEntriesCount(numberApps)
								.setWithExtendedInfo(true)
							.	build();
				
					MarketSession.Callback<AppsResponse> callback = new MarketSession.Callback<AppsResponse>() {
						@Override
						public void onResult(ResponseContext context, AppsResponse response) {
							String result = null;
							result = "[\n";
							for (int i = 0; i < numberApps; i++) {
								result += "{\n";
								result += "  \"title\":\"" + response.getApp(i).getTitle() + "\",\n";
								result += "  \"rating\":\"" + response.getApp(i).getRating() + "\",\n";
								result += "  \"ratingCount\":\"" + response.getApp(i).getRatingsCount() + "\",\n";
								result += "  \"category\":\"" + response.getApp(i).getExtendedInfo().getCategory() + "\",\n";
								result += "  \"description\":\"" + response.getApp(i).getExtendedInfo().getDescription().replace("\n", "\\n").replace("\"", " ") + "\",\n";
								result += "  \"promoText\":\"" + response.getApp(i).getExtendedInfo().getPromoText().replace("\n", "\\n").replace("\"", " ") + "\",\n";						
								result += "  \"recentUpdate\":\"" + response.getApp(i).getExtendedInfo().getRecentChanges().replace("\n", "\\n").replace("\"", " ") + "\",\n";
								result += "  \"downloadCount\":\"" + response.getApp(i).getExtendedInfo().getDownloadsCount() + "\",\n";
								result += "  \"Version\":\"" + response.getApp(i).getVersion() + "\",\n";
								result += "  \"price\":\"" + response.getApp(i).getPrice() + "\"\n";
								result += "}";
								if(i != (numberApps-1))
									result += ",\n";
							}
							result += "\n]";
						
							try {
								CreateFile(query, result);
							} catch (IOException e) {
								System.out.println("error in CreatingFile");
								//e.printStackTrace();
							}
						}		
					};
					session.append(appsRequest, callback);
					session.flush();
				
				} catch(Exception ex) {
					ex.printStackTrace();
				
				}
			
			} 
			
			try {
				return (ReadFile(query));
			} catch (IOException e) {
				return "error";
				
			}

	}
	
	public static void CreateFile(String Title, String text) throws IOException {
		if(!checkFileExsists(Title)) {
			File file = new File(path + Title + ".json");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
		}
	}
	
	private static boolean checkFileExsists(String title) {
		return new File(path + title + ".json").isFile();	
	}
		
	private static String ReadFile(String title) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(path + title + ".json")), "UTF8"));
		String str;
		String json = "";
		while ((str = in.readLine()) != null)
			json += str + "\n";
		
		return json;

	}

}
