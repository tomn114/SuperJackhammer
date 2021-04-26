package com.tomn114.entities;

import com.tomn114.util.ResourceLoader;

public class Obsidian extends Tile{
	
	private static float BASE_DURABILITY = 100;
	public static int RARITY = 1;
	
	
	public Obsidian(float x, float y) {
		super(x, y);
		image = ResourceLoader.getImage("obsidian");
		durability = BASE_DURABILITY;
	}
	
	public boolean hasSlowDown() { return true; }

}
