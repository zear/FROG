public class GameStateGame implements GameState
{
	public static boolean leaveGame = false;

	private Episode episode = null;
	private Level level = null;
	private int fadeStep;
	private int fadeTotalSteps;
	private int fadeTo;

	public void setEpisode(Episode newEpisode)
	{
		this.episode = newEpisode;
	}

	public void loadState()
	{
		if (Program.levelName != null)
		{
			level = new Level(Program.levelName);
		}
		else if (episode != null)
		{
			level = new Level(episode.getLevel(0));
		}

		fadeStep = 0;
		fadeTotalSteps = 25;
		fadeTo = 255;
	}
	public void unloadState()
	{
		Sdl.frameTime = 1000/Sdl.framesPerSecond; // reset the frameTime
		level = null;
		leaveGame = false;
	}

	public void logic()
	{
		if (level != null)
			level.logic();
	}
	public void draw()
	{
		if (level != null)
			level.draw();


		if (leaveGame)
		{
			Sdl.fade(Sdl.screen, 0, fadeTo, fadeStep, fadeTotalSteps);

			if (fadeStep < fadeTotalSteps)
			{
				fadeStep++;
			}
			else
			{
				Program.game.changeState(GameStateEnum.STATE_MENU);
			}
		}

		Sdl.flip(Sdl.screen);
	}
}
