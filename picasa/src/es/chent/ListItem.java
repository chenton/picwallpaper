package es.chent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ListItem {

	String id;
	String description;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public static Collection<ListItem> getListItems(Map<String, String> hash) {
	
		ArrayList<ListItem> list = new ArrayList<ListItem>();
		
		for (String key : hash.keySet()) {
			
			ListItem listItem = new ListItem();
			listItem.setDescription(key);
			listItem.setId(hash.get(key));
			
			list.add(listItem);
			
		}
		
		return list;
		
	}
	

	public String toString() {
		return description;
	}
	
	public static Collection<ListItem> getListItems(Map<String, String> hash, Collection<String> keys) {
	
		ArrayList<ListItem> list = new ArrayList<ListItem>();
		
		for (String key : keys) {
			
			ListItem listItem = new ListItem();
			listItem.setDescription(hash.get(key));
			listItem.setId(key);
			
			list.add(listItem);
			
		}
		
		return list;
		
	}
	
	
}
