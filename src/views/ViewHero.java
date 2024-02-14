package views;

import java.awt.Point;
import java.util.ArrayList;

import engine.Game;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.characters.Explorer;
import model.characters.Fighter;
import model.characters.Hero;
import model.characters.Medic;
import model.world.CharacterCell;

public class ViewHero extends ImageView 
{
	private Point location;
	public static ArrayList<Square> possibleMoves;
	public static ArrayList<Square> possibleTargets;
	private Hero hero;
	public static VBox sideInfoRoot = new VBox();

	public ViewHero(int x,int y)
	{
		hero = (Hero)((CharacterCell)Game.map[x][y]).getCharacter();
		this.location = new Point(x,y);
		if(hero instanceof Fighter)
			this.setImage(new Image("images/fighter.png", 30, 30, false, false));
		if(hero instanceof Medic)
			this.setImage(new Image("images/medic.png",30,30,false,false));
		if(hero instanceof Explorer)
			this.setImage(new Image("images/explorer.png",30,30,false,false));
		addEventHandler();
	}
	
	//getter and setter
	public Hero getHero() {
		return hero;
	}
	public void setHero(Hero hero) {
		this.hero = hero;
	}
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}

	public String toString()
	{
		return "Hero";
	}

	private void addEventHandler() {
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(View.chosenHero==null)
				{
					getAllPossibleActions();
					showInfo();
				}	
			}
		});

	}
	//goes through the entire map and checks which moves are valid; 
	public void getAllPossibleActions()
	{
		possibleMoves = new ArrayList<>();
		possibleTargets = new ArrayList<>();
		for(int i = 0;i<15;i++)
			for(int j = 0;j<15;j++)
			{
				if(this.location.distance(new Point(i,j))<=1 && !this.location.equals(new Point(i,j)))
					isPossibleSquare(i,j);//adds it to possible moves only if square isn't occupied
				if(this.location.distance(new Point(i,j))<=1.5 && !this.location.equals(new Point(i,j)))
					isPossibleTarget(i,j);//checks if there's a zombie/hero to target there
			}
	}
	private void isPossibleTarget(int x, int y)
	{
		Square checkPossible = Square.getSquare(x,y);
		if(!checkPossible.getChildren().isEmpty())
		{
			if (checkPossible.getChildren().get(0) instanceof ViewZombie ||
					checkPossible.getChildren().get(0) instanceof ViewHero)
				possibleTargets.add(checkPossible);
		}

	}
	private void isPossibleSquare(int i, int j) {
		Square checkPossible = Square.getSquare(i, j);
		if(!checkPossible.isHasHero() || !checkPossible.isHasZombie())
			possibleMoves.add(Square.getSquare(i,j));

	}
	//looks for hero in the arraylist and gets all their stats
	public void showInfo()
	{
		for(Hero hero : Game.heroes)
		{
			if(hero.getLocation().equals(location))
			{
				getInfo(hero);
				break;
			}
		}
	}
	private void getInfo(Hero hero)
	{
		sideInfoRoot.getChildren().clear();
		String allInfo = hero.getName() + "\n" + hero.getHeroType() + "\n" +"HP: " + hero.getCurrentHp() 
		+ "\n" + "Damage: " + hero.getAttackDmg() + "\n" +"Actions left: " + hero.getActionsAvailable();
		if(!hero.getVaccineInventory().isEmpty())
			allInfo+="\nVaccines left: " + hero.getVaccineInventory().size();
		if(!hero.getSupplyInventory().isEmpty())
			allInfo+="\nSupplies left: " + hero.getSupplyInventory().size();
		if(hero.isSpecialAction() && !(hero instanceof Medic))
			allInfo+="\nHero is using their special ability.";
		Label info = new Label(allInfo);
		info.setFont(new Font("Verdana",15));
		sideInfoRoot.getChildren().add(info);
		View.sideBox.setTop(sideInfoRoot);
	}
	//shows all possible moves if character is selected or hides them if player clicks somewhere else
	public void showAllPossibleActions(boolean show)
	{
		if(show)
		{
			Glow glow = new Glow();
			glow.setLevel(0.3);
			for(Square square : possibleMoves){
				square.setEffect(glow);
				square.setBorder(new Border(new BorderStroke(Color.ALICEBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.2))));
			}
			for(Square square : possibleTargets){
				square.setEffect(glow);
				if(!square.getChildren().isEmpty() && square.getChildren().get(0).toString().equals("Zombie"))
					square.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.2))));
				else
					square.setBorder(new Border(new BorderStroke(Color.AQUA, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.2))));
			}
		}
		else
		{
			for(Square square : possibleMoves)
			{
				square.setEffect(null);
				square.setBorder(new Border(new BorderStroke(Color.BLACK,
						BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			}
			for(Square square : possibleTargets)
			{
				square.setEffect(null);
				square.setBorder(new Border(new BorderStroke(Color.BLACK,
						BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			}
		}
	}





}
