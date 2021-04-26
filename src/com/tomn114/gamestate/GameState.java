package com.tomn114.gamestate;

import java.awt.Graphics;

public abstract class GameState {
	
	GameStateManager gsm;
	
	protected GameState(GameStateManager gsm) {
		this.gsm = gsm;
	}
	
	protected abstract void update(float dt);
	protected abstract void render(Graphics g);

}
