package views;

import java.awt.Point;

import engine.Game;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.characters.Hero;
import model.collectibles.Collectible;
import model.collectibles.Supply;
import model.world.CharacterCell;
import model.world.CollectibleCell;

public class ViewCollectible extends ImageView
{
	private Image image;
	private Point location;
	private boolean isVaccine;
	private boolean isSupply;
	private Collectible collectible;

	public ViewCollectible(int x,int y)
	{
		collectible = ((CollectibleCell)Game.map[x][y]).getCollectible();

		location = new Point(x,y);
		if(((CollectibleCell) Game.map[x][y]).getCollectible() instanceof Supply)
		{
			this.setImage(new Image("images/supply.jpg", 30, 30, false, false));
			isSupply = true;
		}
		else
		{
			this.setImage(new Image("images/vaccine.jpg", 30, 30, false, false));
			isVaccine = true;
		}
		if(!Game.map[x][y].isVisible())
			this.setVisible(false);
	}

	public boolean isVaccine() {
		return isVaccine;
	}
	public boolean isSupply() {
		return isSupply;
	}
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}

	public String toString()
	{
		if (((CollectibleCell) Game.map[location.x][location.y]).getCollectible() instanceof Supply)
			return "CollectibleSupply";
		else
			return "CollectibleVaccine";
	}
}
