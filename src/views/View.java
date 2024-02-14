package views;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import engine.Game;
import exceptions.InvalidTargetException;
import exceptions.MovementException;
import exceptions.NoAvailableResourcesException;
import exceptions.NotEnoughActionsException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.characters.Direction;
import model.characters.Hero;
import model.characters.Medic;
import model.characters.Zombie;
import model.collectibles.Supply;
import model.collectibles.Vaccine;
import model.world.Cell;
import model.world.CharacterCell;
import model.world.CollectibleCell;
import model.world.TrapCell;

public class View extends Application
{

	/*wht is left:
	 * spawn zombie visually on map after one is killed
	 */
	Scene mainMenu, selectCharacter, game;
	Stage currentStage;
	public static ViewHero chosenHero;
	public static VBox errorMessages = new VBox();
	public static BorderPane sideBox;
	public static GridPane gameRoot;
	public static ScrollPane bottomBox;
	public static ArrayList<Square> squares = new ArrayList<>();

	//start by showing the main menu screen
	public void start(Stage mainStage) throws Exception 
	{
		currentStage = mainStage;
		mainMenuScreen();
		mainStage.show();
	}

	//one single button that leads to the character select screen
	public void mainMenuScreen()
	{
		BorderPane main = new BorderPane();
		Button startGame = new Button("Start Game!");
		Label howToPlay = new Label("Win by curing atleast of 4 zombies and having atleast 5 heroes alive \n You lose if you have no"
				+ " heroes left alive or you use all 5 vaccines while having less than 5 heroes");
		howToPlay.setFont(new Font("Verdana",20));
		startGame.setOnAction(e -> {try {
			selectCharacterScreen();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}});
		main.setTop(howToPlay);
		main.setCenter(startGame);
		mainMenu = new Scene(main, 1000, 770);
		currentStage.setScene(mainMenu);
	}

	//loads the hero csv file and puts them up as available choices
	//after selecting a character it moves onto the gameScreen
	public void selectCharacterScreen() throws IOException
	{
		VBox rootSelect = new VBox();
		Game.loadHeroes("Heros.csv");
		Label choose = new Label("Choose your character.");
		choose.setFont(new Font("Verdana",20));
		rootSelect.getChildren().add(choose);
		for(Hero chosenHero : Game.availableHeroes)
		{
			Button currentButton = new Button(chosenHero.toString());
			currentButton.setOnAction(e -> {gameScreen(chosenHero);});
			rootSelect.getChildren().add(currentButton);
		}
		selectCharacter = new Scene(rootSelect, 1000, 770);
		currentStage.setScene(selectCharacter);
	}

	//creates a 15 by 15 grid of buttons and intializes each of the buttons
	public void gameScreen(Hero chosenHero)
	{
		Game.startGame(chosenHero);
		gameRoot = new GridPane();//where the map is shown
		sideBox = new BorderPane();//where any info is shown(errors, attacks, special, etc.)
		bottomBox = new ScrollPane();//where info about current alive heroes is shown
		bottomBox.setPrefSize(500, 200);//lengthxwidth
		createMap();
		Button endTurn = new Button("End Turn");
		Button attack = new Button("Attack");
		Button useSpecial = new Button("Use Special");
		Button cure = new Button("Cure");
		endTurn.setPrefSize(95,40);
		attack.setPrefSize(95,40);
		useSpecial.setPrefSize(95,40);
		cure.setPrefSize(95,40);
		endTurn.setOnAction(e -> {try {//set up what happens if player clicks on end turn
			errorMessages.getChildren().clear();//clear up any error message left on the side
			Game.endTurn();
			Square.showVisibilty();//resets visibilty of map
			deselectHero();//unhighlights hero if we have any picked
			showDamagedKilledHeroes();//shows what heroes were dmged and which were killed in viewHero.infobox
			removeKilledHeroes();//removes any hero killed by the zombies due to attack
			checkForDead();//removes any zombie killed by hero due to defend
			spawnNewZombie();//spawns one new zombie visually
			setUpBottom();//reset hero details @ the bottom
			checkIfOver();
		} catch (NotEnoughActionsException | InvalidTargetException e1) {e1.printStackTrace();}});


		//todo attack label is not showing for some reaosn?
		attack.setOnAction(e -> {try {//handles what happens if player clicks attack
			errorMessages.getChildren().clear();
			if(View.chosenHero != null)//make sure hero is selected
			{
				Hero attacking = View.chosenHero.getHero();
				attacking.attack();
				Label attackInfo = new Label("Successfully attacked Zombie for " + attacking.getAttackDmg() + " damage! \nZombie defended and attacked " + attacking.getName() +	"\nfor 5 damage.");
				attackInfo.setFont(new Font("Verdana",15));
				attackInfo.setTextFill(Color.LIMEGREEN);
				errorMessages.getChildren().add(attackInfo);
				if(attacking.getTarget().getCurrentHp()==0)
				{
					spawnNewZombie();
					removeZombie((Zombie)View.chosenHero.getHero().getTarget());//remove the dead zombie
					attacking.setTarget(null);
					View.chosenHero.showAllPossibleActions(false);
				}
				setUpBottom();
				deselectHero();//unselect hero so it doesnt override sideinfobox
				checkForDead();//check if hero dies due to zombie defending
				checkIfOver();
			}
			else
			{
				Label error = new Label("No hero is selected!");
				error.setFont(new Font("Verdana",15));
				error.setTextFill(Color.DARKRED);
				errorMessages.getChildren().add(error);
			}
		} catch (NotEnoughActionsException | InvalidTargetException e1) {
			Label attackError = new Label(e1.getMessage());
			attackError.setFont(new Font("Verdana",15));
			attackError.setTextFill(Color.DARKRED);
			errorMessages.getChildren().add(attackError);
		} });



		useSpecial.setOnAction(e -> {//handles what happens when player clicks use special
			try {
				errorMessages.getChildren().clear();
				if(View.chosenHero != null)
				{
					View.chosenHero.getHero().useSpecial();
					Square.showVisibilty();
					View.chosenHero.showInfo();
					if(View.chosenHero.getHero() instanceof Medic)
					{
						Label healed = new Label("You've used your special ability to heal " + 	
								View.chosenHero.getHero().getTarget().getName() + ".");
						healed.setFont(new Font("Verdana", 15));
						healed.setTextFill(Color.LIGHTSEAGREEN);
						ViewHero.sideInfoRoot.getChildren().add(healed);
					}
					else
					{
						Label special = new Label(View.chosenHero.getHero().getName() + " used their special ability!");
						special.setFont(new Font("Verdana",15));
						special.setTextFill(Color.LIGHTSEAGREEN);
						ViewHero.sideInfoRoot.getChildren().add(special);
					}
					setUpBottom();
					checkIfOver();
				}
				else
				{
					Label useSpecialError = new Label("No hero is selected!");
					useSpecialError.setFont(new Font("Verdana",15));
					useSpecialError.setTextFill(Color.DARKRED);
					errorMessages.getChildren().add(useSpecialError);
				}
			} catch (InvalidTargetException | NoAvailableResourcesException | NotEnoughActionsException e1) {
				Label useSpecialError = new Label(e1.getMessage() + "");
				useSpecialError.setFont(new Font("Verdana",15));
				useSpecialError.setTextFill(Color.DARKRED);
				errorMessages.getChildren().add(useSpecialError);
			}

		});

		cure.setOnAction(e -> {
			try {
				errorMessages.getChildren().clear();
				if(View.chosenHero==null)
				{
					Label cureError = new Label("No hero is selected.");
					cureError.setFont(new Font("Verdana",15));
					cureError.setTextFill(Color.DARKRED);
					errorMessages.getChildren().add(cureError);

				}
				else
				{
					View.chosenHero.getHero().cure();
					switchZombieViewToHeroView(Game.heroes.get(Game.heroes.size()-1));
					Label cured = new Label(View.chosenHero.getHero().getTarget().getName() + " has been cured and added to the team!");
					cured.setFont(new Font("Verdana",15));
					cured.setTextFill(Color.LIMEGREEN);
					errorMessages.getChildren().add(cured);
					deselectHero();
					setUpBottom();
					checkIfOver();
					Square.showVisibilty();
				}
			} catch (InvalidTargetException | NoAvailableResourcesException | NotEnoughActionsException e1) {
				Label cureError = new Label(e1.getMessage() + "");
				cureError.setFont(new Font("Verdana",15));
				cureError.setTextFill(Color.DARKRED);
				errorMessages.getChildren().add(cureError);				}
		});
		HBox buttons = new HBox();
		buttons.getChildren().addAll(endTurn,attack,useSpecial,cure);
		buttons.setSpacing(10);
		sideBox.setTop(ViewHero.sideInfoRoot);
		sideBox.setBottom(buttons);
		sideBox.setCenter(errorMessages);
		setUpBottom();
		gameRoot.add(bottomBox, 0,16,16,2);
		gameRoot.add(sideBox, 16, 0);
		gameRoot.add(ViewHero.sideInfoRoot,16, 0);
		addEventHandlers(gameRoot);
		game = new Scene(gameRoot,1000, 770);
		currentStage.setScene(game);
	}
	private void addEventHandlers(GridPane gameRoot2)
	{
		gameRoot2.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				EventTarget target = event.getTarget();
				System.out.println(target);
				errorMessages.getChildren().clear();
				//player clicks hero without previously having smth else selected
				if(target.toString().equals("Hero") && chosenHero==null)
				{
					chosenHero = (ViewHero) target;
					showPossibleActions();
				}
				//player clicks on square adjacent to hero selected
				else if(chosenHero!=null)
				{
					//if square move to that square
					if(target.toString().contains("Square"))
					{
						Square square = (Square) target;
						moveOnMap(square);
						checkIfOver();
					}
					//if collectible then use that collectible to get the parent square
					else if(target.toString().contains("Collectible"))
					{
						Square square = (Square) ((ViewCollectible) target).getParent();
						moveOnMap(square);
						checkIfOver();
					}
					//if zombie then set target to zombie
					else if(target.toString().equals("Zombie"))
					{
						chosenHero.getHero().setTarget(((ViewZombie) target).getZombie());
						Label targetName = new Label("Target set to Zombie!");
						targetName.setFont(new Font("Verdana",15));
						targetName.setTextFill(Color.LIMEGREEN);
						errorMessages.getChildren().add(targetName);
					}
					//if hero then set target to hero
					else if(target.toString().equals("Hero"))
					{
						chosenHero.getHero().setTarget(((ViewHero) target).getHero());
						Label targetName = new Label("Target set to " + chosenHero.getHero().getTarget().getName());
						targetName.setFont(new Font("Verdana",15));
						targetName.setTextFill(Color.LIMEGREEN);
						ViewHero.sideInfoRoot.getChildren().add(targetName);
					}
				}
			}
		});
	}
	//create the map and set up the images
	//creates the squares by creating columns of squares via VBox, then adds them to the grid
	//this prevents the squares from tearing up when adding labels to the side
	//after creating the map, we call setMap() to add pictures to each of squares that need it
	private void createMap()
	{
		for(int i = 0;i<Game.map.length;i++)
		{
			VBox col = new VBox();
			for(int j = 0;j<Game.map.length;j++)
			{
				Square square = new Square(i,j);
				square.setPrefHeight(40);
				square.setPrefWidth(40);
				square.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));		
				col.getChildren().add(square);
				squares.add(square);
			}
			gameRoot.add(col, i, 0);
		}
		setMap();
	}
	//adds icons to each of the tiles depending on what is on the squares
	private void setMap()
	{
		for(int i = 0;i<15;i++)
			for(int j = 0;j<15;j++)
			{
				Cell current = Game.map[i][j];
				if(current instanceof CharacterCell && ((CharacterCell) current).getCharacter() instanceof Hero)
				{
					Square square = Square.getSquare(i,j);
					square.setHasHero(true);
					ViewHero x = new ViewHero(i,j);
					square.getChildren().add(x);
				}
				if(current instanceof CharacterCell && ((CharacterCell) current).getCharacter() instanceof Zombie)
				{
					Square square = Square.getSquare(i,j);
					square.setHasZombie(true);
					ViewZombie x = new ViewZombie(i,j);
					square.getChildren().add(x);
				}
				if(current instanceof CollectibleCell && ((CollectibleCell) current).getCollectible() instanceof Supply)
				{
					Square square = Square.getSquare(i,j);
					ViewCollectible x = new ViewCollectible(i,j);
					square.getChildren().add(x);
				}
				if(current instanceof CollectibleCell && ((CollectibleCell) current).getCollectible() instanceof Vaccine)
				{
					Square square = Square.getSquare(i,j);
					ViewCollectible x = new ViewCollectible(i,j);
					square.getChildren().add(x);
				}
			}
	}
	
	//visual movement
	private void moveOnMap(Square finalLocation)  
	{	
		if(!ViewHero.possibleMoves.contains(finalLocation))
		{
			deselectHero();
			return;
		}
		Direction movedDirection = findDirection(chosenHero.getHero(),finalLocation);
		try 
		{
			ViewHero.sideInfoRoot.getChildren().clear();
			TrapCell trapLocation = null;
			if(Game.map[finalLocation.getLocation().x][finalLocation.getLocation().y] instanceof TrapCell)
				trapLocation = (TrapCell)Game.map[finalLocation.getLocation().x][finalLocation.getLocation().y];
			chosenHero.getHero().move(movedDirection);
			Square intialLocation = (Square) chosenHero.getParent();
			if(!finalLocation.getChildren().isEmpty())
				finalLocation.getChildren().clear();
			finalLocation.getChildren().add(chosenHero);
			finalLocation.setHasHero(true);
			intialLocation.getChildren().remove(chosenHero);
			intialLocation.setHasHero(false);
			chosenHero.setLocation(new Point(finalLocation.getLocation().x,finalLocation.getLocation().y));	
			Square.showVisibilty();
			checkIfTrap(trapLocation);
			checkAdjacentSquareImageVisibility(finalLocation.getLocation());
			setUpBottom();
		}
		catch(MovementException e)
		{
			Label movementError = new Label("Cannot move in that direction!"+ "\n"+"Please select another direction.");
			movementError.setFont(new Font("Verdana",20));
			movementError.setTextFill(Color.DARKRED);
			errorMessages.getChildren().add(movementError);
		}
		catch(NotEnoughActionsException e1)
		{
			Label movementError = new Label(chosenHero.getHero().getName() + " is out of actions points!"+ "\n"+"Press end turn to reset their action points.");
			movementError.setFont(new Font("Verdana",18));
			movementError.setTextFill(Color.DARKRED);
			errorMessages.getChildren().add(movementError);
		}
		finally
		{
			deselectHero();
		}

	}
	//helper for moveOnMap that tells the hero he was damaged
	private void checkIfTrap(TrapCell trap)
	{
		if(trap!=null)
		{
			Label damaged = new Label(chosenHero.getHero().getName() + " has stepped on a trap\nand has taken " + trap.getTrapDamage() + " damage!");
			damaged.setFont(new Font("Verdana",20));
			damaged.setTextFill(Color.DARKRED);
			errorMessages.getChildren().add(damaged);
			System.out.println("damaged");
		}
	}
	//helper for moveOnMap that finds which direction the player chose to go
	private Direction findDirection(Hero pastLocation, Square movedLocation)
	{
		if(movedLocation.getLocation().x - pastLocation.getLocation().x == 1)
			return Direction.UP;
		if(movedLocation.getLocation().x - pastLocation.getLocation().x == -1)
			return Direction.DOWN;
		if(movedLocation.getLocation().y - pastLocation.getLocation().y == 1)
			return Direction.RIGHT;
		if(movedLocation.getLocation().y - pastLocation.getLocation().y == -1)
			return Direction.LEFT;
		return null;
	}
	//helper for moveOnMap that makes adjacent cells visually visible
	private void checkAdjacentSquareImageVisibility(Point location)
	{
		for(Square square : Square.squares)
		{
			if(location.distance(square.getLocation())<=1.5 && !square.getChildren().isEmpty())
			{
				ImageView nearbyThing = (ImageView)square.getChildren().get(0);
				nearbyThing.setVisible(true);
			}
		}
	}



	//has the selected hero glow and shows all possible actions
	//give hero a blue glow and lights up any possible actions
	private void showPossibleActions()
	{
		DropShadow borderGlow = new DropShadow();
		borderGlow.setColor(Color.DEEPSKYBLUE);
		borderGlow.setOffsetX(0f);
		borderGlow.setOffsetY(0f);
		chosenHero.setEffect(borderGlow);
		chosenHero.showAllPossibleActions(true);
	}
	//unhighlights hero and all his possible actions
	private void deselectHero()
	{
		if(chosenHero==null)
			return;
		chosenHero.setEffect(null);
		chosenHero.showAllPossibleActions(false);
		chosenHero.getHero().setTarget(null);
		ViewHero.sideInfoRoot.getChildren().clear();
		chosenHero = null;
	}
	//show any heroes that were damaged/killed
	private void showDamagedKilledHeroes()
	{
		ViewHero.sideInfoRoot.getChildren().clear();
		String damagedHeroes = "The following heroes were attacked by Zombies: \n";
		for(Hero damaged : Game.damagedHeroes)
		{
			if(damaged.getCurrentHp()!=0)
			{
				damagedHeroes += damaged.getName() + "\n";
			}
		}
		if(damagedHeroes.length()<49)
			damagedHeroes = "No Heroes were attacked this turn.";
		Label dmgInfo = new Label(damagedHeroes);
		dmgInfo.setFont(new Font("Verdana",15));
		ViewHero.sideInfoRoot.getChildren().add(dmgInfo);

		String killedHeroes = "The following heroes were killed by zombies: \n";
		for(Hero killed : Game.damagedHeroes)
		{
			if(killed.getCurrentHp()==0)
				killedHeroes += killed.getName() + "\n";
		}
		if(killedHeroes.length()<47)
			killedHeroes = "No Heroes were killed this turn.";
		Label killedInfo = new Label(killedHeroes);
		killedInfo.setFont(new Font("Verdana",15));
		ViewHero.sideInfoRoot.getChildren().add(killedInfo);

	}
	//removes any killed hero
	private void removeKilledHeroes()
	{
		for(Hero currHero : Game.damagedHeroes)
			if(currHero.getCurrentHp()==0)
			{
				Square loc = Square.getSquare(currHero.getLocation().x, currHero.getLocation().y);
				loc.getChildren().clear();
			}


		Game.damagedHeroes.clear();
	}
	//visually spawns the new zombie
	private void spawnNewZombie()
	{
		ViewZombie newZombieSpawned = new ViewZombie (Game.getLastSpawnedZombie().getLocation().x,Game.getLastSpawnedZombie().getLocation().y);
		Square spawnLoc = Square.getSquare(Game.getLastSpawnedZombie().getLocation().x,Game.getLastSpawnedZombie().getLocation().y);
		spawnLoc.getChildren().add(newZombieSpawned);
	}
	//removes any dead hero and zombie
	//hero is already done in rermovekilledheroes and removekilledzombies but cba to remove it
	private void checkForDead()
	{
		for(Square s :Square.squares)
		{
			if(!(s.getChildren().isEmpty()))
			{
				if(s.getChildren().get(0) instanceof ViewHero)
				{
					if (((ViewHero)s.getChildren().get(0)).getHero().getCurrentHp()==0)
						s.getChildren().clear();
				}
				else if(s.getChildren().get(0) instanceof ViewZombie)
					if (((ViewZombie)s.getChildren().get(0)).getZombie().getCurrentHp()==0)
					{
						s.getChildren().clear();
						spawnNewZombie();
					}
			}
		}
	}
	//constantly refreshes bottom of screen info
	private void setUpBottom()
	{
		HBox allHeroes = new HBox();
		allHeroes.setSpacing(5);
		for(Hero hero : Game.heroes)
		{
			VBox heroInfo = new VBox();
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
			heroInfo.getChildren().add(info);
			allHeroes.getChildren().add(heroInfo);
		}
		bottomBox.setContent(allHeroes);

	}
	//checks if game is over and plays the scene depending on result
	private void checkIfOver()
	{
		Button closeGame = new Button("Click here to close the game.");
		closeGame.setOnAction(e -> {currentStage.close();});;
		if(Game.checkGameOver())
		{
			BorderPane endScreen = new BorderPane();
			VBox res = new VBox();
			if(Game.checkWin())
			{
				Label congrats  = new Label("You've won! Congrats!");
				congrats.setFont(new Font("Verdana",30));
				res.getChildren().addAll(congrats,closeGame);
				endScreen.setCenter(res);
				currentStage.setScene(new Scene(endScreen,1000, 770));
			}
			else
			{
				Label loss  = new Label("The heroes have been overrun by the Zombies.");
				loss.setFont(new Font("Verdana",30));
				endScreen.setCenter(loss);
				endScreen.setCenter(closeGame);
				res.getChildren().addAll(loss,closeGame);
				endScreen.setCenter(res);
				currentStage.setScene(new Scene(endScreen,1000, 770));
			}
		}
	}
	//removes any zombie killed
	private void removeZombie(Zombie dead)
	{
		Square loc = Square.getSquare(dead.getLocation().x, dead.getLocation().y);
		loc.getChildren().clear();
	}
	//as said in name; after it cure changes the zombie
	private void switchZombieViewToHeroView(Hero curedZombie)
	{
		Square curedLoc = Square.getSquare(curedZombie.getLocation().x, curedZombie.getLocation().y);
		curedLoc.getChildren().clear();
		ViewHero visualCuredHero = new ViewHero(curedZombie.getLocation().x, curedZombie.getLocation().y);
		curedLoc.getChildren().add(visualCuredHero);
	}
	//handles whenever the player clicks anywhere
	//todo add attack 

	//takes in the square of where the character wants to go, checks if its a valid move, then moves chosenhero to other square
	//also picksup any vaccine/supply in the way
	//todo remove error message after clicking on character again 




	public static void main(String[]args)
	{
		launch(args);
	}
}
