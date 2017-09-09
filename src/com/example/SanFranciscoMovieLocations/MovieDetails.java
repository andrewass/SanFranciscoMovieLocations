package com.example.sf_movies;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CyclicBarrier;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MovieDetails extends Activity {

	Netcom netcom;
	TextView title,director,year,plot,locations,act1, act2, locHead, plotHead;
	String imgUrl,baseUrl,type;
	Movie mov;
	ImageView iv,actor1,actor2;
	CyclicBarrier cb;
	Drawable img;
	boolean movieSearch, secondSearch;
	String[] actImgAdr;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		movieSearch = true;
		secondSearch = false;
		type = null;
		netcom = new Netcom();
		setContentView(R.layout.detail_view);
		title = (TextView) findViewById(R.id.title);
		director = (TextView) findViewById(R.id.director);
		year = (TextView) findViewById(R.id.year);
		plot = (TextView) findViewById(R.id.plot);
		act1 = (TextView) findViewById(R.id.act1);
		act2 = (TextView) findViewById(R.id.act2);
		locHead = (TextView) findViewById(R.id.locHead);
		plotHead = (TextView) findViewById(R.id.plotHead);
		locations = (TextView) findViewById(R.id.locations);

		mov = (Movie) getIntent().getSerializableExtra("aMovie");
		baseUrl = getIntent().getStringExtra("baseUrl");

		iv = (ImageView) findViewById(R.id.posterImg);
		actImgAdr = new String[2];
		actor1 = (ImageView) findViewById(R.id.actor1);
		actor2 = (ImageView) findViewById(R.id.actor2);

		cb = new CyclicBarrier(2);
		Thread t = new Thread(new Worker());
		t.start();
		try {
			cb.await();
		} 
		catch(Exception  e) {}

	}
	//method returns a drawable object from an input stream
	Drawable ImageOperations(String adr) {
		try {
			URL url = new URL(adr);
			InputStream is = (InputStream) url.getContent();
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (Exception e) {
			return null;
		}
	}




	private class Worker implements Runnable{


		@Override
		public void run() {
			try{
				title.setText(mov.title);
				year.setText(mov.release);
				director.setText(Html.fromHtml("<i>directed by  <i>"));
				director.append(mov.director);
				fillLocations();
				extractData(netcom.findImg(mov.title,mov.release,movieSearch));

				if(!imgUrl.equals("none")){
					img = ImageOperations(imgUrl);
					iv.setImageDrawable(img);
				}
				if(!mov.id.equals("none")){	
					String movPlot = netcom.getPlot(mov.id,type);
					plotHead.setText("\nOverview :");
					plot.setText(movPlot);
				}
				//find actor images
				for(int i=0; i < mov.actors.size(); i++){
					boolean foundImgPic = false;
					actImgAdr[i] = netcom.findActorImage(mov.actors.get(i));
					if(!actImgAdr[i].equals("null")){
						foundImgPic = true;
						img = ImageOperations(baseUrl+"w154"+actImgAdr[i]);
					}
					if(i==0){
						if(foundImgPic)
							actor1.setImageDrawable(img);
						act1.setText(mov.actors.get(0));
					}
					else{
						if(foundImgPic)
							actor2.setImageDrawable(img);
						act2.setText(mov.actors.get(1));
					}
				}
				cb.await();
			}
			catch (Exception e) {}
		}
	}

	
	void fillLocations(){

		if(mov.locations.size() > 0) {
			locHead.setText("\n\n San Francisco Locations:\n");
			locations.setText("");
			for(int i=0; i<mov.index; i++){
				String format = "<b>"+mov.locations.get(i)+"</b>";
				locations.append(Html.fromHtml(format));
				locations.append("\n\n");
				String ffact = mov.funfacts.get(i);
				if(ffact != null){
					format = "<i>"+ffact+"</i>";
					locations.append(Html.fromHtml(format));
					locations.append("\n\n");
				}
			}
		}
	}

	void extractData(JSONObject jObj){
		try{
			mov.id = "none";
			imgUrl = "none";
			if(jObj.getInt("total_results") != 0){
				JSONArray jArr = jObj.getJSONArray("results");
				JSONObject obj2 = jArr.getJSONObject(0);
				if(!obj2.getString("poster_path").equals("null"))
					imgUrl = baseUrl+"w154"+obj2.getString("poster_path");
				mov.id = obj2.getString("id");
				if(secondSearch == true)
					type = obj2.getString("media_type");
				else 
					type = "movie";
			}
			else if(!secondSearch){
				secondSearch = true;
				movieSearch = false;
				extractData(netcom.findImg(mov.title,mov.release,movieSearch));
			}
		}
		catch(Exception e){}
	}
}
