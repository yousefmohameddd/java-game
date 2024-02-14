package model.characters;

import exceptions.InvalidTargetException;
import exceptions.NoAvailableResourcesException;
import exceptions.NotEnoughActionsException;
import model.collectibles.Supply;

public class Fighter extends Hero{

	
	public Fighter(String name,int maxHp, int attackDmg, int maxActions) {
		super( name, maxHp,  attackDmg,  maxActions) ;
		
	}

	
	public void useSpecial() throws InvalidTargetException, NoAvailableResourcesException, NotEnoughActionsException
	{
		if(this.isSpecialAction()==true)//do we need this part?
			return;
		if(this.getSupplyInventory().isEmpty())
			throw new NoAvailableResourcesException();
		this.getSupplyInventory().get(0).use(this);
		this.setSpecialAction(true);
	}

	
	
	
	

}
