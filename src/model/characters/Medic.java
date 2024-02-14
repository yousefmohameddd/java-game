package model.characters;

import java.awt.Point;

import exceptions.InvalidTargetException;
import exceptions.NoAvailableResourcesException;
import exceptions.NotEnoughActionsException;
import model.collectibles.Supply;

public class Medic extends Hero {
	//Heal amount  attribute - quiz idea
	

	public Medic(String name,int maxHp, int attackDmg, int maxActions) {
		super(name, maxHp,  attackDmg,  maxActions) ;
		
		
	}
	
	public void useSpecial() throws InvalidTargetException, NoAvailableResourcesException, NotEnoughActionsException
	{
		if (this.getTarget() instanceof Zombie)
			throw new InvalidTargetException("You can only heal heroes, not zombies.");
		if(this.getTarget() == null)
			throw new InvalidTargetException("No target is selected.");
		Hero healingTarget = (Hero) this.getTarget();
		if(!Hero.adjacent(this,healingTarget)) 
			throw new InvalidTargetException("Target selected is not in range.");
		super.useSpecial();
		getTarget().setCurrentHp(getTarget().getMaxHp());
	}
	
	

}
