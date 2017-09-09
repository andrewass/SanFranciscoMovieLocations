package com.example.sf_movies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CyclicBarrier;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {

	Netcom netcom;
	JSONArray result;
	ArrayList<Movie> movies;
	HashMap<String,Location> locations;
	CyclicBarrier cb = new CyclicBarrier(2);
	String res, nm, yr, base_url;
	TextView notifications;
	EditText searchField;
	Button searchButton;
	RadioGroup rg;
	RadioButton radioLoc,radioMov;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		netcom = new Netcom();
		setContentView(R.layout.first_view);
		searchButton = (Button) findViewById(R.id.searchbutton);
		searchField = (EditText) findViewById(R.id.searchfield);
		notifications = (TextView) findViewById(R.id.notifications);
		rg = (RadioGroup) findViewById(R.id.radioGroup);
		radioLoc = (RadioButton) findViewById(R.id.radioLoc);
		radioMov = (RadioButton) findViewById(R.id.radioMov);
		movies = new ArrayList<Movie>();
		locations = new HashMap<String,Location>();
		try{
			Thread t = new Thread(new Worker());
			t.start();
			cb.await();
		}
		catch(Exception e){}
		fillList();
		searchButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				notifications.setText("");
				String query = searchField.getText().toString();
				ArrayList<Movie> temp;

				if(query.equals("")){
					temp = movies;
				}
				else{
					int checkedID = rg.getCheckedRadioButtonId();
					if(checkedID == radioLoc.getId())
						temp = fillTemp(query,true);
					else
						temp = fillTemp(query,false);
				}
				if(temp.isEmpty())
					notifications.setText("No results found!");
				else{
					Intent intent = new Intent(view.getContext(),DisplayActivity.class);
					intent.putExtra("baseUrl", base_url);
					Bundle bundle = new Bundle();
					bundle.putSerializable("movies", temp);
					intent.putExtras(bundle);
					startActivity(intent);
				}

			}
		});
	}



	ArrayList<Movie> fillTemp(String key, boolean locationSearch){

		HashMap<String,Movie> temp = new HashMap<String,Movie>();

		if(locationSearch){
			ArrayList<Location> locs = new ArrayList<Location>(locations.values());
			for(Location l : locs){
				if(l.locname.toLowerCase().contains(key.toLowerCase())){
					ArrayList<Movie> localMovs = new ArrayList<Movie>(l.movies.values());
					for(Movie m: localMovs){
						if(!temp.containsKey(m.title))
							temp.put(m.title,m);
					}
				}		

			}
		}
		else{
			for(Movie m : movies){
				if(m.title.toLowerCase().contains(key.toLowerCase()))
					temp.put(m.title,m);
			}
		}
		return new ArrayList<Movie>(temp.values());
	}


	
	private class Worker implements Runnable{

		public void run(){
			try{
				result = netcom.findMovies();
				base_url = netcom.findBaseUrl();
				cb.await();
			}
			catch(Exception e){}
		}
	}

	
	/*fill an ArrayList wit Job objects*/
	private void fillList(){
		try{
			Movie prev = null;
			for(int i=0; i<result.length(); i++){
				Movie temp;
				boolean added = true;
				JSONObject res = result.getJSONObject(i);
				//if it is the first movie or it is not already added
				if ((prev == null) || !(prev.title.equals(res.getString("title").trim())) ){
					temp = new Movie();
					temp.title = res.getString("title").trim();
					temp.release = res.getString("release_year");
					temp.director = res.getString("director");
					
					String act = "actor_";
					for(int j=1; j<=2; j++){
						if(res.has(act+j))
							temp.actors.add(res.getString(act+j));
					}
					added = false;
				}
				else
					temp = prev;
				if(res.has("locations")){
					//store the location from a movie element in a string (ok)
					String locString = res.getString("locations");
					//add the location string to the movie object (ok)
					temp.addLocation(locString);
					//look up in the 
					Location tempLoc = locations.get(locString.toLowerCase());
					boolean isListed = true;
					if(tempLoc == null){
						tempLoc = new Location();
						tempLoc.locname = locString;
						isListed = false;
					}
					if(res.has("fun_facts")){
						temp.addFunfact(res.getString("fun_facts"));
						tempLoc.funFact = res.getString("fun_facts");
					}
					if(!tempLoc.movies.containsKey(temp.title))
						tempLoc.movies.put(temp.title.toLowerCase(),temp);
					if(!isListed)
						locations.put(locString.toLowerCase(),tempLoc);
				}
				if(!added)
					movies.add(temp);
				temp.index++;
				prev = temp;
			}
		}
		catch(Exception e){}
	}




}


