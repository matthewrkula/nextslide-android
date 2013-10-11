package com.mattkula.hackmitapp.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Slideshow implements Serializable{
	
	private static final long serialVersionUID = -2755023155465248458L;
	public long id;
	public int slide_num;
	public String url;
	public String name;
	public long event_id;
	public String first_image_url;
	public ArrayList<Slide> slides;
}
