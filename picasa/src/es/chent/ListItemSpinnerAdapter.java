package es.chent;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class ListItemSpinnerAdapter extends ArrayAdapter {

	//private ArrayList<ListItem> items;
	
	private LayoutInflater mInflater;
	
	private int layoutResource;
	
	
	
	
	
	
	public ListItemSpinnerAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	//	this.items = new ArrayList<ListItem>();
		this.mInflater = LayoutInflater.from(context);
		
		this.layoutResource = textViewResourceId;
		
	}

	
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
	
		View v = convertView;
		
		if (v==null) {
			v = mInflater.inflate(layoutResource, null);
			
		} 
		
		
		 
		ListItem item = (ListItem)this.getItem(position);
		
		TextView tv = (TextView) v.findViewById(android.R.id.text1);
				
		Log.d("LISTA",item.getDescription());
		tv.setText(item.getDescription());
		
		return v;
		
	}
	
}
