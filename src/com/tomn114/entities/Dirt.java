package com.tomn114.entities;

import com.tomn114.util.ResourceLoader;

public class Dirt extends Tile{
	
	private static float BASE_DURABILITY = 10;
	
	public Dirt(float x, float y) {
		super(x, y);
		image = ResourceLoader.getImage("dirt");
		
		//TODO: fix inheritance on this bruh 
		durability = BASE_DURABILITY;
	}
}
