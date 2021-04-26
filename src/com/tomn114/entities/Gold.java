package com.tomn114.entities;

import com.tomn114.util.ResourceLoader;

public class Gold extends Tile{
	
	private static float BASE_DURABILITY = 100;
	public static int RARITY = 8;
	protected int VALUE = 100;

	public Gold(float x, float y) {
		super(x, y);
		image = ResourceLoader.getImage("gold");
		durability = BASE_DURABILITY;
	}
	
	public int getValue() { return VALUE; }

}
