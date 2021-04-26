package com.tomn114.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import com.tomn114.gamestate.GameStateManager;
import com.tomn114.util.ResourceLoader;


public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	private Thread thread;
	private boolean running = false;
	
	private static KeyInput k;
	private static MouseInput m;
	
	private JFrame frame;
	private GameStateManager gsm;
	
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;

	public static final int TARGET_FPS = 60;
	
	public Game() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.add(this);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		
		setFocusable(true);
		requestFocus();
				
		k = new KeyInput();
		addKeyListener(k);	
		
		m = new MouseInput();
		addMouseListener(m);
		addMouseMotionListener(m);
		
		new ResourceLoader();
		ResourceLoader.load("player", "player.png");
		ResourceLoader.loadTiles("tileset.png");
		
		gsm = new GameStateManager();
	}
	
	public void start() {
		running = true;
		thread = new Thread(this, "Game");
		thread.start();
	}
	
	public void stop() {
		running = false;
	}
	
	public void run() {
		long lastLoopTime = System.nanoTime();
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
		
		long lastFpsTime = 0;
		int fps = 0;
		
		while(running) {
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			float dt = updateLength / ((float) OPTIMAL_TIME);
			
			lastFpsTime += updateLength;
			fps++;
			
			
			if(lastFpsTime >= 1000000000) {
				frame.setTitle("Super Jackhammer | FPS: " + fps);
				lastFpsTime = 0;
				fps = 0;
			}
			
			update(dt);
			render();
			
			try {
				if(lastLoopTime - System.nanoTime() + OPTIMAL_TIME > 0)
					Thread.sleep( (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000 );
			}
			catch(Exception e) { 
				System.out.println("LastLoopTime: " + lastLoopTime);
				System.out.println("System Nano Time: " + System.nanoTime());
				System.out.println("Optimal_TIME: " + OPTIMAL_TIME);
				System.out.println((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
				e.printStackTrace(); 
			}
		}	
	}
	
	public void update(float dt) {
		k.update();
		gsm.update(dt);
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		gsm.render(g);
		
		g.dispose();
		bs.show();
	}
	
	public static KeyInput getKeyInput() { return k; }
	public static MouseInput getMouseInput() { return m; }
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
}
