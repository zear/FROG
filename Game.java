// Class Game gathers objects from all the other classes, that are necessary to run the program. This is where the main program loop is.
public class Game
{
	private GameStateMenu stateMenu = new GameStateMenu();
	private GameStateGame stateGame = new GameStateGame();
	private GameState activeState = null;
	private GameState newState = stateMenu;

	private static boolean quit = false;

	public static void setQuit(boolean value)
	{
		quit = value;
	}

	private void checkState()
	{
		if(newState != activeState)
		{
			if(activeState != null)
				activeState.unloadState();
			if(newState != null)
				newState.loadState();

			activeState = newState;
		}

		if(activeState == null) // exit state
			setQuit(true);
	}

	public void changeState(GameStateEnum state)
	{
		switch(state)
		{
			case STATE_EXIT:
				newState = null;
			break;
			case STATE_MENU:
				newState = stateMenu;
			break;
			case STATE_GAME:
				newState = stateGame;

			default:
			break;
		}
	}

	// this is the main program loop
	public boolean mainLoop()
	{
		if(Sdl.fps())
		{
			checkState();
			Input.getInput();
			activeState.logic();
			activeState.draw();
		}
		
		return quit;
	}
}
