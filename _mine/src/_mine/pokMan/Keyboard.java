package _mine.pokMan;

import static java.awt.event.KeyEvent.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {
	private Game game;
	private int lastDirectionKeyPressed;
	
	public Keyboard(Game game) {
		this.game = game;
		this.lastDirectionKeyPressed = -1;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		processKeyEvent(e);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		processKeyEvent(e);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		processKeyEvent(e);
	}
	
	public void processKeyEvent(KeyEvent e) {
		if(e.getID() != KEY_PRESSED)
			return;
		
		// gets the code so it's easier to work with
		int key = e.getKeyCode();
		
		if(key == VK_ESCAPE) {
			game.stopGame();
			return;
		}
		
		if(key == VK_G) {
			game.toggleDebug();
			return;
		}
		
		if(key == VK_LEFT || key == VK_A) { // left arrow
			lastDirectionKeyPressed = key;
			
			if (game.dx[0] == 0 && game.dy[0] == 0) {
				game.dx[0] = -game.getSpeed();
				game.dx[1] = -game.getSpeed();
				game.dy[0] = 0;
				game.dy[1] = 0;
			} else {
				game.dx[1] = -game.getSpeed();
				game.dy[1] = 0;
			}
			return;
		}
		
		if(key == VK_RIGHT || key == VK_D) { // right arrow
			lastDirectionKeyPressed = key;
			
			if (game.dx[0] == 0 && game.dy[0] == 0) {
				game.dx[0] = game.getSpeed();
				game.dx[1] = game.getSpeed();
				game.dy[0] = 0;
				game.dy[1] = 0;
			} else {
				game.dx[1] = game.getSpeed();
				game.dy[1] = 0;
			}
			return;
		}
		
		if(key == VK_UP || key == VK_W) { // up arrow
			lastDirectionKeyPressed = key;
			
			if (game.dx[0] == 0 && game.dy[0] == 0) {
				game.dx[0] = 0;
				game.dx[1] = 0;
				game.dy[0] = -game.getSpeed();
				game.dy[1] = -game.getSpeed();
			} else {
				game.dx[1] = 0;
				game.dy[1] = -game.getSpeed();
			}
			return;
		}
		
		if(key == VK_DOWN || key == VK_S) { // down arrow
			lastDirectionKeyPressed = key;
			
			if (game.dx[0] == 0 && game.dy[0] == 0) {
				game.dx[0] = 0;
				game.dx[1] = 0;
				game.dy[0] = game.getSpeed();
				game.dy[1] = game.getSpeed();
			} else {
				game.dx[1] = 0;
				game.dy[1] = game.getSpeed();
			}
			return;
		}
		
		if(key == VK_EQUALS || key == VK_ADD) {
			game.setSpeed(game.getSpeed() + 1);
			
			// Movement speeds for the player are set when a
			// directional button is pressed.
			// So, when the speed is changed, recursively call
			// this method so the correct speeds can be computed
			
			if(lastDirectionKeyPressed != -1) {
				e.setKeyCode(lastDirectionKeyPressed);
				processKeyEvent(e);
			}
			
			return;
		}
		
		if(key == VK_MINUS || key == VK_SUBTRACT) {
			game.setSpeed(game.getSpeed() - 1);
			
			if(lastDirectionKeyPressed != -1) {
				e.setKeyCode(lastDirectionKeyPressed);
				processKeyEvent(e);
			}
			
			return;
		}
	}
}