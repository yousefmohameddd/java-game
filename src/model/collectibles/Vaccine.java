package model.collectibles;

import java.awt.Point;
import java.util.Random;

import javax.security.auth.AuthPermission;

import engine.Game;
import exceptions.NoAvailableResourcesException;
import model.characters.Hero;
import model.world.CharacterCell;

public class Vaccine implements Collectible {

	public Vaccine() {
		
	}

	@Override
	public void pickUp(Hero h) {
		h.getVaccineInventory().add(this);
	}

	public void use(Hero h) throws NoAvailableResourcesException
	{
		if(h.getVaccineInventory().isEmpty())
			throw new NoAvailableResourcesException("No vaccines available to use.");
		h.getVaccineInventory().remove(this);
		Game.zombies.remove(h.getTarget());
		Random random = new Random();
		int ran = random.nextInt(Game.availableHeroes.size());
		Hero curedZombie = Game.availableHeroes.get(ran);
		Game.availableHeroes.remove(ran);
		Point curedZombiePos = h.getTarget().getLocation();
		curedZombie.setLocation(curedZombiePos);
		Game.map [curedZombiePos.x][curedZombiePos.y] = new CharacterCell(curedZombie); 
		Game.heroes.add(curedZombie);
		h.setTarget(curedZombie);
		if(Game.map[1][1]!=null)
			Hero.setAdjacentVisibilty(h.getTarget().getLocation());
	}

}
