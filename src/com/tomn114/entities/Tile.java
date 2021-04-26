package com.tomn114.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.tomn114.gamestate.PlayState;

public class Tile {
	
	
	protected float x, y;
	protected static float BASE_DURABILITY;
	protected float durability;
	protected BufferedImage image;
	public static int TILE_SIZE = 40;
	protected int VALUE = 0;
	
	public Tile(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getDurability() { return durability; }
	public int getValue() { return VALUE; }
	
	public void breakTile(float power) {
		durability -= power;
	}
	
	public void update() {}
	
	public void render(Graphics g) {
		g.drawImage(image, (int) x, (int) (y - PlayState.cameraY1), null);
	}
	public boolean hasCollision() { return true; }
	public boolean hasSlowDown() { return false; }
	public boolean isLethal() { return false; }
}
