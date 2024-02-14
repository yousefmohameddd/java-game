package views;

import java.awt.Point;
import java.util.ArrayList;

import engine.Game;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.characters.Hero;
import model.characters.Zombie;
import model.world.CharacterCell;

public class ViewZombie extends ImageView 
{
	private Point location;
	private Zombie zombie;

	public ViewZombie(int x,int y)
	{
		zombie = (Zombie)((CharacterCell)Game.map[x][y]).getCharacter();
		location = new Point(x,y);
		this.setImage(new Image("images/zombie.png", 30, 30, false, false));
		if(!Game.map[x][y].isVisible())
			this.setVisible(false);
		
		
	}

	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}
	public Zombie getZombie() {
		return zombie;
	}
	public void setZombie(Zombie zombie) {
		this.zombie = zombie;
	}

	public String toString()
	{
		return "Zombie";
	}

}

