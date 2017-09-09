package com.example.sf_movies;



import java.util.ArrayList;

//import com.example.sf_movies.MainActivity.CustomAdapter;
//import com.example.sf_movies.MainActivity.CustomAdapter.ViewHolder;





import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayActivity extends Activity implements OnItemClickListener {
	
	TextView header;
	ListView lv;
	CustomAdapter adap;
	ArrayList<Movie> movies;
	String base_url;
	
	
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		header = (TextView) findViewById(R.id.header);
		lv = (ListView) findViewById(R.id.listV);
		base_url = getIntent().getStringExtra("baseUrl");
		Bundle bundle = getIntent().getExtras();
		movies = (ArrayList<Movie>)bundle.getSerializable("movies");
		adap = new CustomAdapter(this,movies);
		lv.setAdapter(adap);
		lv.setOnItemClickListener(this);
	}
	
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Movie temp = movies.get(position);
		Intent intent = new Intent(this,MovieDetails.class);
		intent.putExtra("baseUrl", base_url);
		intent.putExtra("aMovie",temp);
		startActivity(intent);
	}
	
	
	/*adapter used to display multiple TextViews in a ListView row*/
	private class CustomAdapter extends BaseAdapter{

		private ArrayList<Movie> objects;
		Context context;

		CustomAdapter(Context context,ArrayList<Movie> objects){
			this.context = context;
			this.objects = objects;
		}

		private class ViewHolder{
			public TextView title;
			public TextView location;
			public TextView director;
		}

		public int getCount(){
			return objects.size();
		}

		public Movie getItem(int position) {
			return objects.get(position);
		}

		public long getItemId(int position) {
			return objects.indexOf(getItem(position));
		}

		public View getView(int position, View convertView, ViewGroup parent){

			ViewHolder vHold = null;
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			if(convertView == null){
				convertView  = inflater.inflate(R.layout.list_views,null);
				vHold = new ViewHolder();
				vHold.title = (TextView) convertView.findViewById(R.id.titleView);
				vHold.location = (TextView) convertView.findViewById(R.id.releaseView);
				vHold.director = (TextView) convertView.findViewById(R.id.directorView);
				convertView.setTag(vHold);
			}
			else 
				vHold = (ViewHolder) convertView.getTag();

			Movie m = (Movie) getItem(position);
			vHold.title.setText(m.title);
			vHold.location.setText(m.release);
			vHold.director.setText(m.director);
			return convertView;
		}
	}


}
