public class GameStateGame implements GameState
{
	public static boolean leaveGame = false;

	private Episode episode = null;
	private Level level = null;
	private int fadeStep;
	private int fadeTotalSteps;
	private int fadeTo;
	private Font font0;
	private boolean intro;
	private int introTimer;

	public void setEpisode(Episode newEpisode)
	{
		this.episode = newEpisode;
	}

	public void loadState()
	{
		intro = true;
		introTimer = 120;

		// Load fonts
		font0 = new Font("./data/gfx/font1.bmp", 7, 10, 1, 4);

		// Load level
		if (Program.levelName != null)
		{
			level = new Level(Program.levelName);
			intro = false;
		}
		else if (episode != null)
		{
			level = new Level(episode.getLevel(0));
		}

		level.setFont(font0);

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
		{
			if (!intro)
			{
				level.logic();
			}
		}
	}

	public void draw()
	{
		if (level != null)
		{
			if (intro)
			{
				// Draw uniform background
				try
				{
					Sdl.screen.fillRect(100);
				}
				catch (Exception e)
				{
				}

				font0.drawCentered("Get ready!", 60);
				if (episode != null)
					font0.drawCentered(episode.getTitle() + " 1", 100);

				if (--introTimer == 0)
				{
					intro = false;
				}
			}
			else
			{
				level.draw();
			}
		}

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
