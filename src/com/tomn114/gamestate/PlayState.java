package com.tomn114.gamestate;

import java.awt.Color;
import java.awt.Graphics;

import com.tomn114.entities.Player;
import com.tomn114.entities.TileManager;
import com.tomn114.main.Game;

public class PlayState extends GameState {
	
	public static float cameraY1, cameraY2;
	
	private boolean beginningScrolling; // scrolling at the beginning to catch up with player
	private float beginningScrollVY;
	
	private boolean normalScroll; // then keeps camera fixed, with distance NORMAL_SCROLL_DISTANCE from top to player
	public static float NORMAL_SCROLL_DISTANCE = 80f;
	
	private TileManager tm;
	private Player player;
	private float playerDY;
	private int score;
	private int moneyMade;

	protected PlayState(GameStateManager gsm) {
		super(gsm);
		tm = new TileManager();
		// passing everything into this bitch idk how else to do it
		player = new Player(Game.WIDTH / 2, tm.getStartY(), tm, this);
		beginningScrollVY = player.BASE_VY * 2;
		
		beginningScrolling = true;
		normalScroll = false;
		cameraY1 = 0;
		score = 0;
		moneyMade = 0;
		playerDY = 0;
	}
	
	
	// Fuck a getter, im makin it public
	/*
	public float getCameraY1() {
		return cameraY1;
	}

	public float getCameraY2() {
		return cameraY2;
	}*/
	
	public void update(float dt) {
		if(player.isDead() || player.hasLostPower()) {
			if(Game.getKeyInput().enter) {
				tm = new TileManager();
				player = new Player(Game.WIDTH / 2, tm.getStartY(), tm, this);
				beginningScrollVY = player.BASE_VY * 2;
				
				beginningScrolling = true;
				normalScroll = false;
				cameraY1 = 0;
				score = 0;
				moneyMade = 0;
				playerDY = 0;
			}
		}
		
		else {
			tm.update();
			
			float oldPlayerY = player.getY();
			
			if(!player.isDead() && !player.hasLostPower())
				player.update(dt);
			
			playerDY = player.getY() - oldPlayerY;
			
			scrollCamera();
			updateScore();
		}
	}
	
	public void updateScore() {
		score = (int) ((player.getY() + player.getHeight() - tm.getStartY()) / 10) + moneyMade;
	}
	
	public void incrementMoney(int value) {
		moneyMade += value;
	}
	
	public void render(Graphics g) {
		tm.render(g);
		player.render(g);
		
		g.setColor(Color.WHITE);
		g.drawString("Score: " + Integer.toString(score), 5, 25);
		
		g.drawString("Y Velocity " + Integer.toString((int)player.getYVelocity()), 200, 25);
		
		if(player.isDead()) {
			g.drawString("You died to lava. Press enter to restart :(", 100, 400);
		}
		else if(player.hasLostPower()) {
			g.drawString("You lost power by drilling into obsidian too fast. Press enter to restart :(", 10, 400);
		}
	}
	
	public void scrollCamera() {
		if(beginningScrolling && playerDY > 0) {
			cameraY1 += beginningScrollVY;
			if(player.getY() - cameraY1 <= NORMAL_SCROLL_DISTANCE)
				setNormalScroll();
		}
		
		if(normalScroll) {
			cameraY1 = player.getY() - NORMAL_SCROLL_DISTANCE;
		}
	}
	
	public void setNormalScroll() {
		beginningScrolling = false;
		normalScroll = true;
	}
	
	

}
