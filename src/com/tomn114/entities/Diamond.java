package com.tomn114.entities;

import com.tomn114.util.ResourceLoader;

public class Diamond extends Tile {
	
	private static float BASE_DURABILITY = 300;
	public static int RARITY = 1;
	protected int VALUE = 200;

	public Diamond(float x, float y) {
		super(x, y);
		image = ResourceLoader.getImage("diamond");
		durability = BASE_DURABILITY;
	}
	
	public int getValue() { return VALUE; }

}
