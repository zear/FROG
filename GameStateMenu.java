public class GameStateMenu implements GameState
{
	public void loadState()
	{
	}
	public void unloadState()
	{
	}

	public void logic()
	{
		Program.game.changeState(GameStateEnum.STATE_GAME); // temp
	}
	public void draw()
	{
		Sdl.flip(Sdl.screen);
	}
}
