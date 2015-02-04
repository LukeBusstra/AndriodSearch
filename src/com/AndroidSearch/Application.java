package com.AndroidSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {
	String[] querys;
	int numberKeywords = 1;
	int titleKeyword = 0;
	int descritpionKeyword = 0;
	int recentChangesKeyword = 0;
	int promoTextKeyword = 0;

	HashMap<String, String> application = new HashMap<String, String>();
	//constructor
	public Application(String[] query){
		querys = query;
	}
	public String getId() {
		return application.get("appid");
	}

	
	public void AddAttribute(String attribute, String entity){
		if (!entity.isEmpty())
		{
			application.put(attribute, entity);
			KeywordCheck(attribute, entity);
		}
		
	}
	
	public void ReplaceAttribute(String attribute, String entity){
		application.replace(attribute, entity);
		KeywordCheck(attribute, entity);
	}
	
	/**
	 * This takes the application object and converts it into a JSON String 
	 * @return returns application as a JSON String
	 */
	public String BuildJSON(){
		String JSON;
		JSON =  "\t{\n";
		for (HashMap.Entry<String, String> entry : application.entrySet()) {
			JSON += "\t\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",\n";
		}
		JSON +=  "\t\"keywords\":\"" + KeywordEquation() + "\"\n";
		JSON += "\t}";
		return JSON;
	}
	
	/**
	 * Checks if attribute is important for keywords to be counted
	 * @param attribute - Attribute of the data
	 * @param entity - Entity of the data
	 */
	private void KeywordCheck(String attribute, String entity){
		if (attribute.equalsIgnoreCase("title")){
			titleKeyword = KeywordCount(entity);
		} else if (attribute.equalsIgnoreCase("description")){
			descritpionKeyword = KeywordCount(entity);
		} else if (attribute.equalsIgnoreCase("recentchanges")){
			recentChangesKeyword = KeywordCount(entity);
		} else if (attribute.equalsIgnoreCase("promotext")){
			promoTextKeyword = KeywordCount(entity);
		}
	}
	
	/**
	 * Takes the entity and counts the number of times it the query will appear.
	 * @param entity - the text to be examined
	 * @return the number of keywords found
	 */
	private int KeywordCount(String entity) {
		int count = 0;
		for (int i = 0; i < querys.length; i++) {
			Pattern p = Pattern.compile(querys[i].toLowerCase());
			Matcher m = p.matcher(entity.toLowerCase());
			while (m.find()){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * This collects the keyword count and runs it though equations to evaluate a score
	 * @return The result of the keyword algorithm
	 */
	private int KeywordEquation(){
		int titleScore = titleKeyword / numberKeywords;
		int descriptionScore = descritpionKeyword / (numberKeywords * 5);
		int recentChangesScore = recentChangesKeyword / (numberKeywords * 20);
		int promoTextScore = promoTextKeyword / (numberKeywords * 10);
		
		int totalScore = titleScore + descriptionScore + recentChangesScore + promoTextScore;
		return totalScore;
	}


	
	
}

