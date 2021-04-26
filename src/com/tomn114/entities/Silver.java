package com.tomn114.entities;

import com.tomn114.util.ResourceLoader;

public class Silver extends Tile{
	
	private static float BASE_DURABILITY = 50;
	public static int RARITY = 16;
	protected int VALUE = 50;

	public Silver(float x, float y) {
		super(x, y);
		image = ResourceLoader.getImage("silver");
		durability = BASE_DURABILITY;
	}
	
	public int getValue() { return VALUE; }

}
