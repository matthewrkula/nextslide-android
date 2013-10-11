package com.mattkula.hackmitapp.data;

import java.io.Serializable;

public class Slide implements Serializable, Comparable<Slide>{
	
	private static final long serialVersionUID = 5840815586991074272L;
	public long id;
	public String note;
	public int slide_number;
	
	@Override
	public int compareTo(Slide other) {
		if(this.slide_number < other.slide_number)
			return -1;
		if(this.slide_number > other.slide_number)
			return 1;
		return 0;
	}

}
