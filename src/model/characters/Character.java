package model.characters;

import java.awt.Point;
import java.util.Collection;

import engine.Game;
import exceptions.InvalidTargetException;
import exceptions.NotEnoughActionsException;
import model.world.CharacterCell;


public abstract class Character {
	private String name;
	private Point location;
	private int maxHp;
	private int currentHp;
	private int attackDmg;
	private Character target;

	public Character() {
	}


	public Character(String name, int maxHp, int attackDmg) {
		this.name=name;
		this.maxHp = maxHp;
		this.currentHp = maxHp;
		this.attackDmg = attackDmg;
	}

	public Character getTarget() {
		return target;
	}

	public void setTarget(Character target) {
		this.target = target;
	}

	public String getName() {
		return name;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public int getCurrentHp() {
		return currentHp;
	}

	public void setCurrentHp(int currentHp) {
		if(currentHp < 0) 
			this.currentHp = 0;
		else if(currentHp > maxHp) 
			this.currentHp = maxHp;
		else 
			this.currentHp = currentHp;
	}

	public int getAttackDmg() {
		return attackDmg;
	}

	public void attack() throws NotEnoughActionsException,InvalidTargetException
	{
		Character attackedCharacter = this.getTarget();
		attackedCharacter.setCurrentHp(attackedCharacter.getCurrentHp() - this.getAttackDmg());//damage the target
		attackedCharacter.defend(this);
		if(attackedCharacter.getCurrentHp()==0)//check if target is dead due to attack
			attackedCharacter.onCharacterDeath();
		if(this.getCurrentHp()==0)//check if zombie is dead due to defend
		{
			if(this instanceof Zombie)
				Game.damagedHeroes.add((Hero)this.getTarget());
			this.onCharacterDeath();
		}
	}

	public void onCharacterDeath() 
	{
		int x = this.getLocation().x;
		int y = this.getLocation().y;
		Game.map[x][y] = new CharacterCell(null);
		this.setTarget(null);
	}
	public void defend(Character c)
	{
		if(this instanceof Hero)
			c.setCurrentHp(c.currentHp - (this.attackDmg/2));
		else
			c.setCurrentHp(c.currentHp - this.attackDmg/2);
		 
	}
}
