package org.example02.test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.model.Market.App.ExtendedInfo;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.ResponseContext;


/**
 * @author lukebusstra
 * Coded using Andriod-market-api https://code.google.com/p/andriod-market-api/
 *
 * This application 
 */
public class TerminalCrawler {
	
	// Path Data to be stored
	private static String path = "/Users/lukebusstra/Documents/workspace/PlayStoreServlet/WebContent/Data/";
	private static String query = "";
	private static String queryname = "";
	private static int Startindex = 0;
	private static int numberApps = 100; //Max number Apps is 250
	private static ArrayList<Application> applications = new ArrayList<Application>();
	
	
	/**
	 * Code used from Open Source Andriod Market Place API
	 * https://code.google.com/p/android-market-api/
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public static String TerminalCrawler(String query) throws IOException {	
		String result;
		// removes spaces from query name
		queryname = query.replace(" ", "_");
		if(!checkFileExsists(queryname)) {
			System.out.println("Splitting query");
			String[] querylist = QuerySplitter(query);
			System.out.println("File Doesnt exsist, No history Found");
			AndroidAPI(querylist);
			System.out.println("Query Results have been saved");
			result = ReadFile(queryname);
			System.out.println("File been Read");
		} else {
			System.out.println("File all ready exsists.");
			result = ReadFile(queryname);
			System.out.println("File been Read");
		}
		return result;
	} //end TerminalCrawler
	
	/**
	 * Call the Android Server and builds the App information
	 * @param querylist
	 * @return
	 * @throws IOException
	 */
	private static void AndroidAPI(String[] querylist) throws IOException {
		Information user = new Information();
		// API information and details
		String login = user.GetUsername();
		String password = user.GetPassword();
		int entries = 10;
	
		
		//Creating a session and login into Android API
		MarketSession session = new MarketSession();
		System.out.println("Login...");
		session.login(login, password);
		System.out.println("Login Complete");
		
		for (int i = 0; i < querylist.length; i++) {
			
			query = querylist[i];
			System.out.println("Starting AndroidAPI for query: " + query); // debugging	
					
			// API is restricted to pulling 10 results at a time, this allow
			// looping of query till results are found.
			for (Startindex = 0; Startindex < numberApps; Startindex = Startindex + 10) {
				System.out.println(Startindex);
	
			numberApps = 200;
			//Builds the query for the Android Server
			AppsRequest appsRequest = AppsRequest.newBuilder()
					.setQuery(query)
					.setStartIndex(Startindex).setEntriesCount(entries)
					.setWithExtendedInfo(true)
					.build();
			
			//Collects all the data
			MarketSession.Callback<AppsResponse> callback = new MarketSession.Callback<AppsResponse>() {
				@Override
				public void onResult(ResponseContext context, AppsResponse response) {
					//Loop for the results of the query
					for (int index = 0; index < response.getAppCount(); index++) {
						//numberApps = response.getEntriesCount();
						boolean contains = false;
						//SetUp Decimal Formating for easier evaluation
						DecimalFormat twoPlaces = new DecimalFormat("0.00");
						
						//Setup main path for easier Code
						ExtendedInfo mainRequest = response.getApp(index).getExtendedInfo();
						String appID = response.getApp(index).getId();
						Iterator<Application> iterator = applications.iterator();
					/*	while (iterator.hasNext()) {
							if (iterator.next().getId().equalsIgnoreCase(appID)){
								contains = true;
								System.out.println("Appid all ready exsists");
							}*/
						
						if (!contains) {
							Application application;
							application = new Application(querylist);
							application.AddAttribute("appId", response.getApp(index).getId());
							application.AddAttribute("title", response.getApp(index).getTitle().replace("\"", ""));
							application.AddAttribute("rating", twoPlaces.format(Double.parseDouble(response.getApp(index).getRating())));
							application.AddAttribute("ratingCount", response.getApp(index).getRatingsCount() + "");
							application.AddAttribute("developer", response.getApp(index).getCreator().replace("\"", ""));
							application.AddAttribute("category", mainRequest.getCategory());
							application.AddAttribute("description", mainRequest.getDescription().replace("\n", "\\n").replace("\"", " ")
									.replace("\t", " ").replace("\\", "\\\\"));
							application.AddAttribute("promoText", mainRequest.getPromoText().replace("\n", "\\n").replace("\"", " ")
									.replace("\t", " ").replace("\\", "\\\\"));
							application.AddAttribute("recentUpdate", mainRequest.getRecentChanges().replace("\n", "\\n").replace("\"", " ")
									.replace("\t", " ").replace("\\", "\\\\"));
							application.AddAttribute("downloadCount", mainRequest.getDownloadsCountText().replace(">250,000", "250000")
									.replace("<50", "50").replace("500-1,000", "500").replace("50,000-250,000", "50000")
									.replace("5,000-10,000", "5000").replace("100-500", "100").replace("1,000-5,000", "1000")
									.replace("10,000-50,000", "10000"));
							application.AddAttribute("version", response.getApp(index).getVersion());
							application.AddAttribute("screenshots", mainRequest.getScreenshotsCount() + "");
							application.AddAttribute("price", response.getApp(index).getPrice());
							applications.add(application);
						}
					}
					//debugging
					int start = Startindex - 10;
					System.out.println("Build response " + start + " to " + Startindex);			
				} // Ends onResult
			};
			// Builds the query
			session.append(appsRequest, callback);
			//System.out.println(result);
			session.flush();

			String JSON = JSONBuilder(applications);
			CreateFile(queryname, JSON);

			// Add a Time Delay to avoid being blocked from server. from 15 to 40sec
			try {
				Thread.sleep((long)Math.random() * 25 * 1000 + 15000);
			} catch (InterruptedException e) {
				System.out.println("Error in Time Delay. " + e.getMessage());
			} // End Try/Catch

		} // Ends for index
			session.flush();
		} // Ends for i
	} // ends AndriodAPI
	

	
	
		

	/**
	 * Builds the JSON File
	 * @param Title
	 * @param text
	 * @throws IOException
	 */
	public static void CreateFile(String Title, String text) throws IOException {
		//if(!checkFileExsists(Title)) {
			File file = new File(path + Title + ".json");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
		//}
	}
	
	/**
	 * Checks if file exists
	 * @param title
	 * @return
	 */
	private static boolean checkFileExsists(String title) {
		return new File(path + title + ".json").isFile();	
		
	}
		
	/**
	 * Reads an exsisting file
	 * @param title
	 * @return
	 * @throws IOException
	 */
	private static String ReadFile(String title) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(path + title + ".json")), "UTF8"));
		String str;
		String json = "";
		while ((str = in.readLine()) != null)
			json += str + "\n";
		System.out.println("returning file");
		return json;

	}
	
	
	/**
	 * Splits query it elements or words
	 * @param query
	 * @return
	 */
	private static String[] QuerySplitter(String query) {
		String[] individaulQueries = query.split(" ");
		System.out.println(individaulQueries.toString());
		ArrayList<String> queries = new ArrayList<>();

			for (int i = 0; i < individaulQueries.length; i++) {
				for (int j = i; j < individaulQueries.length; j++) {
					int count = j+1;
					String temp = "";
					while (i != count) {
						temp = individaulQueries[count-1] + " " + temp;
						count--;
					}
					queries.add(temp);
				}
				
			}
		String[] querylist = new String[queries.size()];
		for (int j = 0; j < querylist.length; j++) {
			querylist[j] = queries.get(j).toString();
		}
		return querylist;
	}
	
	/**
	 * 
	 * @param results 
	 * @param querylist
	 * @return A Build JSON String
	 */
	private static String JSONBuilder(ArrayList<Application> Applications) {
		String result = "[\n";
		Iterator<Application> iterator = applications.iterator();
		while (iterator.hasNext()) {
			result += iterator.next().BuildJSON();
			result += ",";
		}
		result = result.substring(0, result.length()-1);
		result += "\n]";
			
		return result;
	}
}
