public class GameStateGame implements GameState
{
	private Level level = null;

	public void loadState()
	{
		level = new Level("test2.lvl");
	}
	public void unloadState()
	{
		Sdl.frameTime = 1000/Sdl.framesPerSecond; // reset the frameTime
		level = null;
	}

	public void logic()
	{
		if(level != null)
			level.logic();
	}
	public void draw()
	{
		if(level != null)
			level.draw();
		Sdl.flip(Sdl.screen);
	}
}
