public class GameStateGame implements GameState
{
	private Level level = null;

	public void loadState()
	{
		String fileName = new String(); // temp

		level = new Level("test2.lvl");
	}
	public void unloadState()
	{
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
