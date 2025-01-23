package com.finderfail.ld22;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


import javax.swing.JFrame;


public class Game extends Canvas implements Runnable {
	public static final String NAME = "UNK Screen (Java code, Engine JDK 8)";
	public static final int Height = 240;
	public static final int Width = Height * 16 / 9;
	
	private BufferedImage image = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	private boolean running = false;
	private int tickCount;
	
	public void start() {
		running = true;
		new Thread(this).start();
	}
	
	public void stop() { running = false; }
	
	public void run() {
		long lastTime = System.nanoTime();
		double unprocessed = 0;
		double nsPerTick = 1000000000.0 / 60.0;
		int frames = 0;
		long lastTimer1 = System.currentTimeMillis();
		
		
		while (running) {
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			while (unprocessed >= 1) {
				tick();
				unprocessed -= 1;
			}
			{
				frames++;
				render();
			}
		
			if (System.currentTimeMillis() - lastTimer1 > 1000) {
				lastTimer1 += 1000;
				System.out.println(frames + " fps");
				frames = 0;
			}
			
		}	
	}
	
	public void tick() { tickCount++; }
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = i+tickCount;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
	
	
	static GraphicsDevice device = GraphicsEnvironment
	        .getLocalGraphicsEnvironment().getScreenDevices()[0];
	
	public static void main(String[] args) {
		Game game = new Game();
		game.setMinimumSize(new Dimension(Width*2, Height*2));
		game.setMaximumSize(new Dimension(Width*2, Height*2));
		game.setPreferredSize(new Dimension(Width*2, Height*2));
		
		JFrame frame = new JFrame(Game.NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(game);
		frame.pack();
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.addKeyListener(new KeyAdapter() {
			  public void keyPressed(KeyEvent e) {
			    int keyCode = e.getKeyCode();
			    boolean fullscreen = false;
			    if (keyCode == KeyEvent.VK_F11) {
			    	if (fullscreen == false) {
			    		System.out.println("F11 is pressed!");
					    fullscreen = true;
					    device.setFullScreenWindow(frame);
					    System.out.println(fullscreen);
			    	} else {
			    		System.out.println("Exiting Fullscreen!");
			    		fullscreen = false;
		                device.setFullScreenWindow(null); 
		                System.out.println(fullscreen);
		                frame.setVisible(true);
		                frame.pack();
			    	}
			      ;
			    }
			    else if (keyCode == KeyEvent.VK_ESCAPE) {
			      System.out.println("ESC is pressed!");
			      System.exit(0);
			    }
			  }
			});
		
		game.start();
	}
}
