package model.characters;

import java.awt.Point;

import engine.Game;
import exceptions.InvalidTargetException;
import exceptions.NotEnoughActionsException;
import model.world.Cell;
import model.world.CharacterCell;

public class Zombie extends Character {
	static int ZOMBIES_COUNT = 1;

	public Zombie() {
		super("Zombie " + ZOMBIES_COUNT, 40, 10);
		ZOMBIES_COUNT++;
	}

	public void attack() throws NotEnoughActionsException,InvalidTargetException {
		findAdjacentHero2();
		if(this.getTarget()!=null)
		{
			super.attack();
		}
		

	}

	/*go through the entire map looking for any possible characters (hero or zombie) in the map, 
	 * check if they are adjacent to the zombie, then have the zombie's target that hero/zombie */
	private void findAdjacentHero2()
	{
		for(int i = 0;i<15;i++)
		{
			for(int j = 0;j<15;j++)
			{
				if(Game.map[i][j] instanceof CharacterCell)
				{
					CharacterCell tmp = (CharacterCell)Game.map[i][j];
					if(tmp.getCharacter()!=null && tmp.getCharacter() instanceof Hero && this.getLocation().distance(new Point(i,j))<=1.5)
					{
						this.setTarget(tmp.getCharacter());
						break;
					}
				}
			}
		}
	}
	
	public void onCharacterDeath()
	{
		super.onCharacterDeath();
		Game.zombies.remove(this);
		Game.spawnZombie(1);

	}
}


