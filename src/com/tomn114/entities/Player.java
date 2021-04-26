package com.tomn114.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.tomn114.gamestate.PlayState;
import com.tomn114.main.Game;
import com.tomn114.util.ResourceLoader;

public class Player {
	
	//TODO: FIX GETTING STUCK ON THE SIDE AT HIGH SPEEDS
	//TODO: power/durability implement?
	
	private float x, y;
	private float power = 100f;
	
	
	private TileManager tm;
	private PlayState ps;
	
	public static float BOTTOM_OFFSET = 12;
	public static float BOTTOM_WIDTH = 8;
	private float BASE_VX = 5f;
	public float BASE_VY = 3.5f;
	public static int WIDTH, HEIGHT;
	private static float vx, vy;
	private boolean dead = false;
	private boolean lostPower = false;
	private BufferedImage image;
	
	public Player(float x, float y, TileManager tm, PlayState ps) {
		this.tm = tm;
		this.ps = ps;
				
		image = ResourceLoader.getImage("player");
		WIDTH = image.getWidth();
		HEIGHT = image.getHeight();
		
		this.x = x - WIDTH / 2;
		this.y = y - HEIGHT;
		
		vy = 0;
	}
	
	public void update(float dt) {
		//System.out.println("x: " + x);
		if(Game.getKeyInput().left) {
			vx = BASE_VX * -1 * dt;
		}
		else if(Game.getKeyInput().right) {
			vx = BASE_VX * dt;
		}
		else {
			vx = 0;
		}
		
		if(!onTile()) {
			vy = BASE_VY * dt;
		}
		else {
			vy = 0;
		}
		
		tm.onSideTiles(x + vx, x + WIDTH + vx, y + HEIGHT);
		//System.out.println(tm.onSideTiles(x + vx, x + WIDTH + vx, y + HEIGHT));
		
		move();
		
		if(hitLava()) {
			dead = true;
		}
		if(hitObsidian()) {
			decreaseVelocity();
		}
		
		if((Game.getKeyInput().down || BASE_VY >= 10) && onTile()) {
			demolish();
			 //increase speed
			if(BASE_VY + 0.1f <= 50.0f) {
				BASE_VY += 0.1f;
			}
			if(BASE_VX + 0.1f <= 10.0f) {
				BASE_VX += 0.1f;
			}
			//System.out.println("BASE VY: " + BASE_VY);
		}
	}
	
	public boolean onTile() {
		return tm.onTile(x + BOTTOM_OFFSET, x + BOTTOM_OFFSET + BOTTOM_WIDTH, y + HEIGHT) != null;
	}
	
	public boolean hitLava() {
		ArrayList<Integer> tiles = tm.onTile(x + BOTTOM_OFFSET, x + BOTTOM_OFFSET + BOTTOM_WIDTH, y + HEIGHT);
		if(tiles == null)
			return false;
		
		for(int i = 0; i<tiles.size(); i++) {
			if(tm.isTileLethal(tm.getRow(y+HEIGHT), tiles.get(i))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hitObsidian() {
		ArrayList<Integer> tiles = tm.onTile(x + BOTTOM_OFFSET, x + BOTTOM_OFFSET + BOTTOM_WIDTH, y + HEIGHT);
		if(tiles == null)
			return false;
		
		for(int i = 0; i<tiles.size(); i++) {
			if(tm.isTileSlowDown(tm.getRow(y+HEIGHT), tiles.get(i))) {
				return true;
			}
		}
		return false;
	}
	
	public void move() {
		int sideTile = tm.onSideTiles(x + vx, x + WIDTH + vx, y + HEIGHT);
		//System.out.println(sideTile);
		
		if(x + vx < 0)
			x = 0;
		else if(x + WIDTH + vx > Game.WIDTH)
			x = Game.WIDTH - WIDTH;
		else if(sideTile != -1) {
			if(sideTile == tm.getCol(x + vx)) {
				x = tm.getColRightXCoordinate(sideTile);
				//System.out.println("Moving this bitch #1 " + sideTile);
			}
			else if(sideTile == tm.getCol(x + WIDTH + vx)) {
				x = tm.getColLeftXCoordinate(sideTile) - WIDTH;
				//System.out.println("Moving this bitch #2 " + sideTile);
			}
		}
		else x += vx;
		
		
		if(!onTile()) {
			//Doesn't call native onTile() method because it wants to test if y + vy is on the tile. (Fixing y so it doesnt go into ground)
			if(tm.onTile(x + BOTTOM_OFFSET, x + BOTTOM_OFFSET + BOTTOM_WIDTH, y + HEIGHT + vy) != null) {
				y = tm.getRowTopYCoordinate(tm.getRow(y + HEIGHT + vy)) - HEIGHT;
			}
			else {
				y += vy;
			}
		}
	}
	
	public void demolish() {
		//System.out.println(tm.onTile(x + BOTTOM_OFFSET, x + BOTTOM_OFFSET + BOTTOM_WIDTH, y + HEIGHT));
		//System.out.println(tm.getRow(y + HEIGHT));
		int sideTile = tm.onSideTiles(x + vx, x + WIDTH + vx, y + HEIGHT);
		ArrayList<Integer> colsToDemolish = tm.onTile(x + BOTTOM_OFFSET, x + BOTTOM_OFFSET + BOTTOM_WIDTH, y + HEIGHT);
		
		//Hmm is this really the best approach
		ArrayList<int[]> tilesToDemolish = new ArrayList<int[]>();
		
		/*
		for(int i = 0; i < colsToDemolish.size(); i++) {
			//System.out.println("Y + height: " + (y + HEIGHT));
			//System.out.println("adding tile to demolish: row: " + tm.getRow(y + HEIGHT) + ", col: " + colsToDemolish.get(i));
			tilesToDemolish.add(new int[] {tm.getRow(y + HEIGHT), colsToDemolish.get(i)});
		}*/
		
		
		//Janky hard coding right here
		if(sideTile != -1 && Game.getKeyInput().left) {
			if(tm.getRow(y + HEIGHT) - 1 >= 0) {
				tilesToDemolish.add(new int[] {tm.getRow(y + HEIGHT) - 1, sideTile});
				//System.out.println("the diagonal #1");
			}
			tilesToDemolish.add(new int[] {tm.getRow(y + HEIGHT), sideTile});
		}
		else if(sideTile != -1 && Game.getKeyInput().right) {
			if(tm.getRow(y + HEIGHT) - 1 >= 0) {
				tilesToDemolish.add(new int[] {tm.getRow(y + HEIGHT) - 1, sideTile});
				//System.out.println("the diagonal #2");
			}
			tilesToDemolish.add(new int[] {tm.getRow(y + HEIGHT), sideTile});
		}
		
		
		for(int i = 0; i < colsToDemolish.size(); i++) {
			//System.out.println("Y + height: " + (y + HEIGHT));
			//System.out.println("adding tile to demolish: row: " + tm.getRow(y + HEIGHT) + ", col: " + colsToDemolish.get(i));
			tilesToDemolish.add(new int[] {tm.getRow(y + HEIGHT), colsToDemolish.get(i)});
		}
		/*
		for(int i = 0; i<tilesToDemolish.size(); i++) {
			System.out.println("Tile #" + i + ": row: " + tilesToDemolish.get(i)[0] + " col: " + tilesToDemolish.get(i)[1]);
		}*/
		
		
		
		int moneyMade = tm.demolish(tilesToDemolish, power);
		//System.out.println(moneyMade);
		ps.incrementMoney(moneyMade);
		//tm.demolish(tm.getRow(y + HEIGHT), tm.onTile(x + BOTTOM_OFFSET, x + BOTTOM_OFFSET + BOTTOM_WIDTH, y + HEIGHT), power);
		
		
		
		//BASE_VY += 0.1f;
	}
	
	public void render(Graphics g) {
		g.drawImage(image, (int) x, (int) (y - PlayState.cameraY1), null);
	}
	
	public float getX() { return x; }
	public float getY() { return y; }
	public boolean isDead() { return dead; }
	public boolean hasLostPower() { return lostPower; }
	public int getHeight() { return HEIGHT; }
	
	public float getYVelocity() {
		return vy;
	}
	/*
	public float getVelocity() {
		return (float)(Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2)));
	}*/
	
	public void decreaseVelocity() {
		if(BASE_VY / 1.15 < 1) {
			lostPower = true;
			BASE_VY = 0;
		}
		else if(BASE_VY / 1.15 > 1) {
			System.out.print("BASE VY: " + BASE_VY + " -> ");
			BASE_VY /= 1.15;
			System.out.print(BASE_VY);
			System.out.println();
		}
			
		
	}

}
