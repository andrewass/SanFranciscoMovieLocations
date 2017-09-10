package com.example.sf_movies;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Netcom {

	final String apiKey = ""; //Currently removed


	
	String findActorImage(String actor){
		String url = "https://api.themoviedb.org/3/search/person?";
		DefaultHttpClient client = new DefaultHttpClient();

		try{
			List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
			nvp.add(new BasicNameValuePair("query",actor));
			String params = URLEncodedUtils.format(nvp,"UTF-8");

			HttpGet get = new HttpGet(url+params+"&"+apiKey);
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			JSONObject temp = new JSONObject(EntityUtils.toString(entity));
			if(temp.getInt("total_results") == 0)
				return "null";
			JSONArray tempArr = temp.getJSONArray("results");
			return tempArr.getJSONObject(0).getString("profile_path");
			
		}
		catch(Exception e){
			return null;
		}
	}
	
	JSONArray findMovies(){
		String url = "http://data.sfgov.org/resource/yitu-d5am.json";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try{
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (entity != null)
				return new JSONArray(EntityUtils.toString(entity));
			else
				return null;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	String findBaseUrl() {

		String retval = null;
		String url = "https://api.themoviedb.org/3/configuration?";


		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url+apiKey);
		try{
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			JSONObject jobj = null;
			if(entity != null){
				jobj = new JSONObject(EntityUtils.toString(entity));
				JSONObject details = new JSONObject(jobj.getString("images"));
				retval = details.getString("secure_base_url");
			}
			return retval;
		}
		catch(Exception e){
			return null;
		}

	}

	
	String getPlot(String id, String type){
		String url = null;
		if(type == null)
			return null;
		if(type.equals("movie"))
			url = "https://api.themoviedb.org/3/movie/"+id+"?"+apiKey;
		else if(type.equals("tv"))
			url = "https://api.themoviedb.org/3/tv/"+id+"?"+apiKey;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try{
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			JSONObject jobj = null;
			jobj = new JSONObject(EntityUtils.toString(entity));
			return jobj.getString("overview");
		}
		catch(Exception e){
			return null;
		}
	}

	
	JSONObject findImg(String name, String year,boolean movieSearch){
		
		String url = null;
		if(movieSearch)
			url = "https://api.themoviedb.org/3/search/movie?";
		else
			url = "https://api.themoviedb.org/3/search/multi?";
		DefaultHttpClient client = new DefaultHttpClient();

		try{
			List<NameValuePair> nvp = new ArrayList<NameValuePair>(4);
			nvp.add(new BasicNameValuePair("query",name));
			nvp.add(new BasicNameValuePair("year",year));
			nvp.add(new BasicNameValuePair("search_type","ngram"));
			String params = URLEncodedUtils.format(nvp,"UTF-8");

			HttpGet get = new HttpGet(url+params+"&"+apiKey);
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			return new JSONObject(EntityUtils.toString(entity));
			
		}
		catch(Exception e){
			return null;
		}
	}
}
