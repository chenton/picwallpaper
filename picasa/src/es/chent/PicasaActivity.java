package es.chent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


/**
 * Main activity for GUI. Enables service and configures access
 * @author jmfc
 *
 */
public class PicasaActivity extends Activity {

	
	TextView user;
	TextView passwd;
	ListView disabledAlbums;
	Spinner comboAlbums;
	Button addButton;
	Button delButton;
	Button updButton;

	
	Switch enableButton;

	ListItemAdapter listAdapter;

	ListItemSpinnerAdapter comboAdapter;


	PicasaIfz picasa;

	Map<String, String> albumIds = new HashMap<String, String>();


	Context appContext;

	WallPaperChangerService service;


	ListItem listedItem;
	
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			
			service = ((WallPaperChangerService.WallPaperChangerServiceBinder) binder).getService();
			service.readSettings();
			
		

			if (user!=null) {
				user.setText(service.user);
					
			}
			if (passwd!=null) {
				passwd.setText(service.password);
					
			}
			
            
	}
		public void onServiceDisconnected(ComponentName className) {
			service = null;
		}
	};



	void doBindService() {
		bindService(new Intent(this, WallPaperChangerService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {



		super.onCreate(savedInstanceState);

		doBindService();



		setContentView(R.layout.main);




		registerReceiver(receiver, new IntentFilter(WallPaperChangerService.INITIALIZE_INTENT));
		 



		user = (TextView)findViewById(R.id.editText1);
		passwd = (TextView)findViewById(R.id.editText2);

		if (service!=null) {
			user.setText(service.user);
			passwd.setText(service.password);
		}
		
		
		disabledAlbums = (ListView)findViewById(R.id.listView1);

		comboAlbums = (Spinner)findViewById(R.id.spinner1);

		addButton = (Button)findViewById(R.id.button1);

		delButton = (Button)findViewById(R.id.button2);
		updButton = (Button)findViewById(R.id.button3);

		enableButton = (Switch)findViewById(R.id.switch1);

		disabledAlbums.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		disabledAlbums.setOnItemClickListener
		(
				   new AdapterView.OnItemClickListener() 
				   {
				       public void onItemClick(AdapterView adapterView, View view,int position, long arg3)
				       {
				          
				    	   disabledAlbums.setItemChecked(position, true);
				          
				          
				         
				          Log.d("WWWWW","Position: "+position);
				       }
				   }
				);
		
		delButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (disabledAlbums.getCheckedItemPosition()!=-1) {
				
					listAdapter.remove(disabledAlbums.getItemAtPosition(disabledAlbums.getCheckedItemPosition()));
							
				}
				
			}
		});

		addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ListItem item;
				if (comboAlbums.getSelectedItem()!=null) {
					item = (ListItem)comboAlbums.getSelectedItem();
					if (listAdapter.getPosition(item)==-1) {
					listAdapter.add(item);
					Log.d("MAIN",item.getDescription());
					}
				}
				
			}
		});

		updButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				if (service!=null) {
					
					service.disallowedAlbumsIds.clear();
					int size = disabledAlbums.getCount();
					for (int i=0;i<size;i++) {						
						ListItem item = (ListItem) disabledAlbums.getItemAtPosition(i);
						service.disallowedAlbumsIds.add(item.getId());
					
					}
					
					service.calculateUrls=true;
					
					if ((!user.getText().toString().equals(service.user))||
					(!passwd.getText().toString().equals(service.password))) {
						service.user = user.getText().toString();
						service.password = passwd.getText().toString();
				
						service.restartPending = true;
						
					}
					
					service.saveSettings();
					
				}				
			}
		});

		enableButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if (service == null) {
					return;
				}

				if (isChecked)  {
					doBindService();

				} else {
					service.stopSelf();
				}

			}
		});


		listAdapter = new ListItemAdapter(this, 
				android.R.layout.simple_list_item_single_choice);

		comboAdapter = new ListItemSpinnerAdapter(this,android.R.layout.simple_spinner_dropdown_item);

		disabledAlbums.setAdapter(listAdapter);

		comboAlbums.setAdapter(comboAdapter);
	}

	//This will handle the broadcast
	public BroadcastReceiver receiver = new BroadcastReceiver() {
	                //@Override
	                public void onReceive(Context context, Intent intent) {
	                        
	                        String action = intent.getAction();
	                        if (action.equals(WallPaperChangerService.INITIALIZE_INTENT)) {
	                        	listAdapter.clear();
	                        	comboAdapter.clear();
	                        	listAdapter.addAll(ListItem.getListItems(service.albumIds,service.disallowedAlbumsIds));
	                        	comboAdapter.addAll(ListItem.getListItems(service.albumIds));
	                        	
	                    		
	                        }
	                }
	};
	

}