package model.characters;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Scanner;

import engine.Game;
import exceptions.InvalidTargetException;
import exceptions.MovementException;
import exceptions.NoAvailableResourcesException;
import exceptions.NotEnoughActionsException;
import model.collectibles.Supply;
import model.collectibles.Vaccine;
import model.world.Cell;
import model.world.CharacterCell;
import model.world.CollectibleCell;
import model.world.TrapCell;

import java.awt.Point;

import exceptions.InvalidTargetException;
import exceptions.MovementException;
import exceptions.NotEnoughActionsException;

public abstract class Hero extends Character {


	private int actionsAvailable;
	private int maxActions;
	private ArrayList<Vaccine> vaccineInventory = new ArrayList();
	private ArrayList<Supply> supplyInventory = new ArrayList();
	private boolean specialAction;


	public Hero(String name,int maxHp, int attackDmg, int maxActions) {
		super(name,maxHp, attackDmg);
		this.maxActions = maxActions;
		this.actionsAvailable = maxActions;
		this.vaccineInventory = new ArrayList<Vaccine>();
		this.supplyInventory=new ArrayList<Supply>();
		this.specialAction=false;
	}


	

	public String getHeroType()
	{
		if(this instanceof Fighter)
			return "Fighter";
		if(this instanceof Medic)
			return "Medic";
		else
			return "Explorer";
	}
	





	public boolean isSpecialAction() {
		return specialAction;
	}



	public void setSpecialAction(boolean specialAction) {
		this.specialAction = specialAction;
	}



	public int getActionsAvailable() {
		return actionsAvailable;
	}



	public void setActionsAvailable(int actionsAvailable) {
		this.actionsAvailable = actionsAvailable;
	}



	public int getMaxActions() {
		return maxActions;
	}



	public ArrayList<Vaccine> getVaccineInventory() {
		return vaccineInventory;
	}


	public ArrayList<Supply> getSupplyInventory() {
		return supplyInventory;
	}




	public void move(Direction d) throws MovementException, NotEnoughActionsException
	{
		if(this.getCurrentHp()==0)
		{
			this.onCharacterDeath();
			return;
		}
		if(getActionsAvailable()<1)
			throw new NotEnoughActionsException("No action point available");
		if(!checkValid(d))
			throw new MovementException("Cannot move in that direction");	

		Point old = this.getLocation();//location before moving
		Point current = new Point();//location after moving
		movePoint(old,d,current);//move it old to current
		Cell currentCell = Game.map[current.x][current.y];//get the cell of where the character is moving
		Game.map[old.x][old.y] = new CharacterCell(null);//make the old position on Game.map null
		Game.map[current.x][current.y] = new CharacterCell(this);//finally change position on Game.map
		Game.map[current.x][current.y].setVisible(true);
		this.setActionsAvailable(actionsAvailable-1);
		this.setLocation(current);
		if(currentCell instanceof TrapCell)
		{
			damageCell(this,currentCell);//damage the hero and check if they die 
			if(!(Game.heroes.contains(this)))
				return;
		}
		if(currentCell instanceof CollectibleCell)
		{
			collectCollectible(this,currentCell);//check if its vaccine or supply and add to inventory
		}
		setAdjacentVisibilty(current);
		
	}

	private void movePoint(Point starting, Direction d,Point result)
	{
		int xPos = starting.x;
		int yPos = starting.y;
		if(d.equals(Direction.UP))
		{
			xPos++;
			result.setLocation(xPos,yPos);
		}
		if(d.equals(Direction.DOWN))
		{
			xPos--;
			result.setLocation(xPos,yPos);
		}
		if(d.equals(Direction.LEFT))
		{
			yPos--;
			result.setLocation(xPos,yPos);
		}
		if(d.equals(Direction.RIGHT))
		{
			yPos++;
			result.setLocation(xPos,yPos);
		}
	}
	private boolean checkValid(Direction d)
	{
		int xPos = getLocation().x;
		int yPos = getLocation().y;
		if(d.equals(Direction.UP))
		{
			xPos++;
			if(xPos>14)
				return false;
			if(Game.map[xPos][yPos] instanceof CharacterCell)
			{
				CharacterCell tmp = (CharacterCell)Game.map[xPos][yPos];
				if(tmp.getCharacter() != null)
					return false;
			}
			return true;
		}
		if(d.equals(Direction.DOWN))
		{
			xPos--;
			if(xPos<0)
				return false;
			if(Game.map[xPos][yPos] instanceof CharacterCell)
			{
				CharacterCell tmp = (CharacterCell)Game.map[xPos][yPos];
				if(tmp.getCharacter() != null)
					return false;
			}
			return true;
		}
		if(d.equals(Direction.LEFT))
		{
			yPos--;
			if(yPos<0)
				return false;
			if(Game.map[xPos][yPos] instanceof CharacterCell)
			{
				CharacterCell tmp = (CharacterCell)Game.map[xPos][yPos];
				if(tmp.getCharacter() != null)
					return false;
			}
			return true;
		}
		if(d.equals(Direction.RIGHT))
		{
			yPos++;
			if(yPos>14)
				return false;
			if(Game.map[xPos][yPos] instanceof CharacterCell)
			{
				CharacterCell tmp = (CharacterCell)Game.map[xPos][yPos];
				if(tmp.getCharacter() != null)
					return false;
			}
			return true;
		}
		return false;
	}
	private void collectCollectible(Hero hero, Cell current)
	{
		CollectibleCell cell = (CollectibleCell) current;
		if(cell.getCollectible() instanceof Vaccine)
		{
			Vaccine vaccine = (Vaccine) cell.getCollectible();
			vaccine.pickUp(hero);
		}
		if(cell.getCollectible() instanceof Supply)
		{
			Supply supply = (Supply) cell.getCollectible();
			supply.pickUp(hero);
		}
	}
	private void damageCell(Hero hero, Cell current)
	{
		TrapCell trap = (TrapCell) current;
		hero.setCurrentHp(hero.getCurrentHp()-trap.getTrapDamage());
		if(hero.getCurrentHp()==0)
			hero.onCharacterDeath();
	}
	public static void setAdjacentVisibilty(Point location)
	{
		Game.map[location.x][location.y].setVisible(true);
		if(location.x>0 && location.y>0)
			Game.map[location.x-1][location.y - 1].setVisible(true);
		if(location.x>0) 
			Game.map[location.x-1][location.y].setVisible(true);
		if(location.x>0 && location.y<14)
			Game.map[location.x - 1][location.y + 1].setVisible(true);

		if(location.y>0)
			Game.map[location.x][location.y - 1].setVisible(true);
		if(location.y<14)
			Game.map[location.x][location.y + 1].setVisible(true);

		if(location.x<14 && location.y>0)
			Game.map[location.x + 1][location.y -1].setVisible(true);
		if(location.x<14)
			Game.map[location.x+1][location.y].setVisible(true);
		if(location.x<14 && location.y<14)
			Game.map[location.x + 1][location.y + 1].setVisible(true);
	}


	public void useSpecial() throws NoAvailableResourcesException, InvalidTargetException, NotEnoughActionsException {
		if (this.getSupplyInventory().size() == 0)
			throw new NoAvailableResourcesException("This hero has no supplies available to use.");
		this.supplyInventory.get(0).use(this);
		this.setSpecialAction(true);
	}


	public void cure() throws InvalidTargetException, NoAvailableResourcesException, NotEnoughActionsException
	{
		if(this.getVaccineInventory().isEmpty())
			throw new NoAvailableResourcesException("No vaccines are in inventory.");
		if(this.getActionsAvailable()<=0)
			throw new NotEnoughActionsException("Not enough action points.");
		if(this.getTarget()==null)
			throw new InvalidTargetException("Target is not selected.");
		if(!(this.getTarget() instanceof Zombie))
			throw new InvalidTargetException("Target is not a zombie.");
		if(!adjacent(this,this.getTarget()))
			throw new InvalidTargetException("Target is not in reach.");	
		this.getVaccineInventory().get(0).use(this);
		this.setActionsAvailable(this.actionsAvailable-1);	


	}




	@Override
	public String toString() {
		return "Name: "  + getName()+ "\nClass: " + getHeroType() +"\nMax HP:" + getMaxHp() + 
				"\nAttack Damage: " + getAttackDmg() + "\nMax Actions: " + maxActions;  
				
	}




	public void attack() throws NotEnoughActionsException,InvalidTargetException { 
		if(this.getTarget()==null)
			throw new InvalidTargetException("Target not selected.");
		if(this.getTarget() instanceof Hero) 
			throw new InvalidTargetException("The target is not a zombie.");
		Zombie attackedZombie = (Zombie) this.getTarget();
		if(!adjacent(this,attackedZombie))
			throw new InvalidTargetException("Target is not in reach.");
		if(this.actionsAvailable<=0)
			throw new NotEnoughActionsException ("Not enough action points.");
		super.attack();
		if (this instanceof Fighter && (this.isSpecialAction()))
			return;
		this.setActionsAvailable(this.getActionsAvailable()-1);
	}


	public static boolean adjacent(Character character, Character target) {
		if(character.getLocation().distance(target.getLocation())<1.5)
			return true;	
		return false;
	}

	public void onCharacterDeath()
	{
		super.onCharacterDeath();
		Game.heroes.remove(this);
	}



}
