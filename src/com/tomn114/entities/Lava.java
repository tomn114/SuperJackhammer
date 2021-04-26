package com.tomn114.entities;

import com.tomn114.util.ResourceLoader;

public class Lava extends Tile{
	
	public static int RARITY = 1;
	public static int BASE_DURABILITY = 10000;

	public Lava(float x, float y) {
		super(x, y);
		image = ResourceLoader.getImage("lava");
		durability = BASE_DURABILITY;
	}
	
	public boolean hasCollision() { return true; }
	
	public boolean isLethal() { return true; }

}
