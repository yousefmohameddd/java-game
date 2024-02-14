package views;

import java.awt.Point;
import java.util.ArrayList;

import engine.Game;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Square extends StackPane
{
	private Point location;
	private String name;
	private boolean hasHero;
	private boolean hasZombie;
	public static ArrayList<Square> squares = new ArrayList<>();
	//creates a square and gives it coordinates and a name depending on its location
	public Square(int x, int y)
	{ 
		name = "Square" + x + y; 
		location = new Point(x,y);
		hasHero = false;
		hasZombie = false;
		squares.add(this);
		if(Game.map[x][y].isVisible())
			this.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,CornerRadii.EMPTY, Insets.EMPTY)));
		else
			this.setBackground(new Background(new BackgroundFill(Color.SLATEGREY,CornerRadii.EMPTY, Insets.EMPTY)));
		
	}
	
	

	//basic getters and setters
	public boolean isHasZombie() {
		return hasZombie;
	}
	public void setHasZombie(boolean hasZombie) {
		this.hasZombie = hasZombie;
	}
	public Point getLocation() {
		return location;
	}
	public String getName() {
		return name;
	}
	public String toString()
	{
		return "Square" + this.location.x + this.location.y;
	}
	public boolean isHasHero() {
		return hasHero;
	}
	public void setHasHero(boolean hasHero) {
		this.hasHero = hasHero;
	}
	
	//gets the square from x and y coordinates
	public static Square getSquare(int x, int y)
	{
		for(Square square : View.squares)
			if(square.getLocation().equals(new Point(x,y)))
				return square;
		return null;
	}
	public static void showVisibilty()
	{
		for(Square square:View.squares)
		{
			if(Game.map[square.location.x][square.location.y].isVisible()==true)
			{
				square.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,CornerRadii.EMPTY, Insets.EMPTY)));
				if(!square.getChildren().isEmpty())
				{
					ImageView nearbyThing = (ImageView)square.getChildren().get(0);
					nearbyThing.setVisible(true);
				}
			}
			else
				{
					square.setBackground(new Background(new BackgroundFill(Color.SLATEGREY,CornerRadii.EMPTY, Insets.EMPTY)));
					if(!square.getChildren().isEmpty())
					{
						ImageView nearbyThing = (ImageView)square.getChildren().get(0);
						nearbyThing.setVisible(false);
					}
				}

		}
	}
}
