package com.tomn114.gamestate;

import java.awt.Graphics;
import java.util.Stack;

public class GameStateManager {
	
	private Stack<GameState> gameStates;
	
	public GameStateManager() {
		gameStates = new Stack<GameState>();
		this.push(new PlayState(this));
	}
	
	public void replace(GameState state) {
		gameStates.pop();
		gameStates.push(state);
	}
	
	public void push(GameState state) {
		gameStates.push(state);
	}
	
	public void pop() {
		gameStates.pop();
	}
	
	public void update(float dt) {
		gameStates.peek().update(dt);
	}
	
	public void render(Graphics g) {
		gameStates.peek().render(g);
	}

}
