package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import exceptions.InvalidTargetException;
import exceptions.MovementException;
import exceptions.NoAvailableResourcesException;
import exceptions.NotEnoughActionsException;
import model.characters.Direction;
import model.characters.Explorer;
import model.characters.Fighter;
import model.characters.Hero;
import model.characters.Medic;
import model.characters.Zombie;
import model.collectibles.Supply;
import model.collectibles.Vaccine;
import model.world.Cell;
import model.world.CharacterCell;
import model.world.CollectibleCell;
import model.world.TrapCell;

public class Game {
	
	public static Cell [][] map = new Cell[15][15];
	public static ArrayList <Hero> availableHeroes = new ArrayList<Hero>();
	public static ArrayList <Hero> heroes =  new ArrayList<Hero>();
	public static ArrayList <Zombie> zombies =  new ArrayList<Zombie>();
	public static ArrayList <Hero> damagedHeroes = new ArrayList<Hero>();
	private static Zombie lastSpawnedZombie;

	
	public static Zombie getLastSpawnedZombie() {
		return lastSpawnedZombie;
	}


	public static void loadHeroes(String filePath)  throws IOException {
		
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Hero hero=null;
			switch (content[1]) {
			case "FIGH":
				hero = new Fighter(content[0], Integer.parseInt(content[2]), Integer.parseInt(content[4]), Integer.parseInt(content[3]));
				break;
			case "MED":  
				hero = new Medic(content[0], Integer.parseInt(content[2]), Integer.parseInt(content[4]), Integer.parseInt(content[3])) ;
				break;
			case "EXP":  
				hero = new Explorer(content[0], Integer.parseInt(content[2]), Integer.parseInt(content[4]), Integer.parseInt(content[3]));
				break;
			}
			availableHeroes.add(hero);
			line = br.readLine();
			
			
		}
		br.close();

		
		
	}
	

	public static void startGame(Hero h)
	{
		//map = new Cell[15][15];
		intializeMap();//creates character cells with the character null to fill the map
		spawnVaccines();//spawn 5 vaccines
		spawnSupplies();//spawn 5 supplies
		spawnTraps();//spawn 5 traps
		spawnZombie(10);//spawn 10 zombies
		spawnHero(h);//spawn one hero bottom left
	}

	public static void intializeMap()
	{
		for(int i = 0;i<15;i++)
		{
			for(int j = 0;j<15;j++)
			{
				map[i][j]=new CharacterCell(null);
			}
		}
	}


	private static void spawnVaccines()
	{
		int count = 0;
		Random random = new Random();
		while(count<5)
		{
			int x = random.nextInt(15);
			int y = random.nextInt(15);
			if(map[x][y] instanceof CharacterCell)
			{
				CharacterCell currentCell = (CharacterCell) map[x][y];
				if(currentCell.getCharacter() == (null) && !(x==0 && y==0))
				{
					map[x][y] = new CollectibleCell(new Vaccine());
					count++;
					
				}
			}
		}
	}
	private static void spawnSupplies()
	{
		int count = 0;
		Random random = new Random();
		while(count<5)
		{
			int x = random.nextInt(15);
			int y = random.nextInt(15);
			if(map[x][y] instanceof CharacterCell)
			{
				CharacterCell currentCell = (CharacterCell) map[x][y];				
				if(currentCell.getCharacter() == null && !(x==0 && y==0))
				{
					map[x][y] = new CollectibleCell(new Supply());
					count++;
				}
			}
		}
	}
	private static void spawnTraps()
	{
		int count = 0;
		Random random = new Random();
		while(count<5)
		{
			int x = random.nextInt(15);
			int y = random.nextInt(15);
			if(map[x][y] instanceof CharacterCell)
				{
				CharacterCell currentCell = (CharacterCell) map[x][y];				
				if(currentCell.getCharacter() == null && !(x==0 && y==0))
				{
					map[x][y] = new TrapCell();
					count++;
				}
			}
		}
	}
	public static void spawnZombie(int limit)
	{
		int count = 0;
		Random random = new Random();
		while(count<limit)
		{
			int x = random.nextInt(15);
			int y = random.nextInt(15);
			if(map[x][y] instanceof CharacterCell)
			{
				CharacterCell currentCell = (CharacterCell) map[x][y];				
				if(currentCell.getCharacter() == null && !(x==0 && y==0))
				{
					Zombie zombie = new Zombie();
					zombies.add(zombie);
					map[x][y] = new CharacterCell(zombie);
					zombie.setLocation(new Point(x,y));
					count++;
					lastSpawnedZombie = zombie;
				}
			}
		}
	}
	private static void spawnHero(Hero h)
	{
		availableHeroes.remove(h);
		heroes.add(h);
		h.setLocation(new Point(0,0));
		map[0][0] = new CharacterCell(h);
		Hero.setAdjacentVisibilty(h.getLocation());
	}
	
	
	
	
	

	public static boolean checkWin()
	{
		if(!vaccineInMap() && !heroHasVaccine() && heroes.size()>=5)//methods explained in gameover
			return true;
		return false;
	}
	
	
	
	


	public static boolean checkGameOver()
	{
		if(heroes.isEmpty())//sees if all heroes are dead
			return true;
		else if(!vaccineInMap() && !heroHasVaccine())//check if there is a vaccine on the map 
			return true; 					  		 //check if any hero has a vaccine
		return false;
	}
	private static boolean vaccineInMap()
	{
		for(int i = 0;i<15;i++)
		{
			for(int j = 0;j<15;j++)
			{
			if(isVaccine(map[i][j]))//checks if current cell is has a vaccine
				return true;
			}
		}
		return false;
	}
	private static boolean isVaccine(Cell currentCell)
	{
		if(currentCell instanceof CollectibleCell)
		{
			CollectibleCell currentCollectible = (CollectibleCell) currentCell;
			if(currentCollectible.getCollectible() instanceof Vaccine)
				return true;
		}
		return false;
	}	
	private static boolean heroHasVaccine()
	{
		for(int i = 0;i<heroes.size();i++)
		{
			if(!heroes.get(i).getVaccineInventory().isEmpty())
				return true;
		}
		return false;

	}

	
	
	
	
	public static void endTurn() throws NotEnoughActionsException, InvalidTargetException 
	{
		zombieAttack();//all zombies attack adjacent if found
		spawnZombie(1);//spawn
		resetHeroes();//reset every hero's action points, target, and special
		updateVisibilty();//have only the squares next to the hero visible
	}
	private static void zombieAttack() throws NotEnoughActionsException, InvalidTargetException
	{
		for(int i = 0;i<zombies.size();i++)
		{
			zombies.get(i).attack();
			if(zombies.get(i).getTarget()!=null && !(damagedHeroes.contains(zombies.get(i).getTarget())))
				damagedHeroes.add( (Hero) zombies.get(i).getTarget());
			zombies.get(i).setTarget(null);
		}
	}
	private static void resetHeroes()
	{
		for(int i = 0;i<heroes.size();i++)
		{
			Hero heroReset = heroes.get(i);
			heroReset.setActionsAvailable(heroReset.getMaxActions());
			heroReset.setTarget(null);
			heroReset.setSpecialAction(false);
		}
	}
	private static void updateVisibilty()
	{
		setAllInvisible();//set the entire map invisible
		setHeroVisible();//have the squares around the hero visible
	}
	private static void setAllInvisible()
	{
		for(int i = 0;i<15;i++)
			for(int j = 0;j<15;j++)
				map[i][j].setVisible(false);
	}
	public static void setHeroVisible()
	{
		for(int i =0;i<15;i++)
		{
			for(int j = 0;j <15;j++)
			{
				if(map[i][j] instanceof CharacterCell)
				{
					CharacterCell currentCharacter = (CharacterCell) map[i][j];
					if(currentCharacter.getCharacter()!=null)
						if(currentCharacter.getCharacter() instanceof Hero)
							Hero.setAdjacentVisibilty(currentCharacter.getCharacter().getLocation());
				}
			}
	
		}
	}

}
