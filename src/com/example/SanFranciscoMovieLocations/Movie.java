package com.example.sf_movies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class Movie implements Serializable {
	
	private static final long serialVersionUID = 1L;

	HashMap<Integer,String> locations = new HashMap<Integer,String>();
	HashMap<Integer,String> funfacts = new HashMap<Integer, String>();
	ArrayList<String> actors = new ArrayList<String>(2);
	String title, release, director, imgSrc, plot, id;
	int index;
	
	Movie(){
		index = 0;
	}
	
	public void addLocation(String loc){
		locations.put(index,loc);
	}
	
	public void addFunfact(String fun){
		funfacts.put(index, fun);
	}
	
}
