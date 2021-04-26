package com.tomn114.entities;

import com.tomn114.util.ResourceLoader;

public class Air extends Tile {
	
	public static int RARITY = 1;

	public Air(float x, float y) {
		super(x, y);
		image = ResourceLoader.getImage("air");
	}
	
	public boolean hasCollision() { return false; }

}
