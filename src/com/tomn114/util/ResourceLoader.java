package com.tomn114.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.tomn114.entities.Tile;

public class ResourceLoader {
	
	public static final String RESOURCE_PATH = "res/";
	
	private static Map<String, BufferedImage> images;
	
	public ResourceLoader() {
		images = new HashMap<String, BufferedImage>();
	}
	
	public static void loadTiles(String path) {
		try {
			BufferedImage tileset = ImageIO.read(new File(RESOURCE_PATH + path));
			
			images.put("dirt", tileset.getSubimage(0, 0, Tile.TILE_SIZE, Tile.TILE_SIZE));
			images.put("grass", tileset.getSubimage(Tile.TILE_SIZE, 0, Tile.TILE_SIZE, Tile.TILE_SIZE));
			images.put("obsidian", tileset.getSubimage(2 * Tile.TILE_SIZE, 0, Tile.TILE_SIZE, Tile.TILE_SIZE));
			images.put("silver", tileset.getSubimage(3 * Tile.TILE_SIZE, 0, Tile.TILE_SIZE, Tile.TILE_SIZE));
			images.put("gold", tileset.getSubimage(4 * Tile.TILE_SIZE, 0, Tile.TILE_SIZE, Tile.TILE_SIZE));
			images.put("diamond", tileset.getSubimage(5 * Tile.TILE_SIZE, 0, Tile.TILE_SIZE, Tile.TILE_SIZE));
			images.put("lava", tileset.getSubimage(6 * Tile.TILE_SIZE, 0, Tile.TILE_SIZE, Tile.TILE_SIZE));
			images.put("air", tileset.getSubimage(7 * Tile.TILE_SIZE, 0, Tile.TILE_SIZE, Tile.TILE_SIZE));
			
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void load(String key, String path) {
		try {
			images.put(key, ImageIO.read(new File(RESOURCE_PATH + path)));	
		} 
		catch(IOException e) { e.printStackTrace(); }
		
	}
	
	public static BufferedImage getImage(String key) {
		return images.get(key);
	}

}
