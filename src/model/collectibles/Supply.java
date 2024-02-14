package model.collectibles;

import exceptions.NoAvailableResourcesException;
import model.characters.Hero;

public class Supply implements Collectible  {

	

	
	public Supply() {
		
	}

	public void pickUp(Hero h) {
		h.getSupplyInventory().add(this);
	}

	public void use(Hero h) throws NoAvailableResourcesException
	{
		if(h.getSupplyInventory().isEmpty())
			throw new NoAvailableResourcesException("No supplies available to use");
		h.getSupplyInventory().remove(this);
	}
	


	
		
		

}
