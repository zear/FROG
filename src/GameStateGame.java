import sdljava.*;
import sdljava.video.*;

public class GameStateGame implements GameState
{
	public static boolean leaveGame = false;

	private Episode episode = null;
	private Level level = null;
	private int levelNum;
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
		// Load fonts
		font0 = new Font("./data/gfx/font1.bmp", 7, 10, 1, 4);

		// Load level
		levelNum = 0;
		loadLevel();

		fadeStep = 0;
		fadeTotalSteps = 25;
		fadeTo = 255;
	}
	public void unloadState()
	{
		Sdl.frameTime = 1000/Sdl.framesPerSecond; // reset the frameTime
		level = null;
		levelNum = 0;
		leaveGame = false;
	}

	private void loadLevel()
	{
		intro = true;
		introTimer = 120;

		Player oldPlayer = null;

		if (level != null)
		{
			oldPlayer = level.getPlayer();
		}

		if (Program.levelName != null)
		{
			level = new Level("", Program.levelName);
			intro = false;
		}
		else if (episode != null)
		{
			level = new Level(episode.getDirectory(), episode.getLevel(levelNum));
		}

		if (oldPlayer != null)
		{
			Player newPlayer = level.getPlayer();
			newPlayer.setLives(oldPlayer.getLives());
			newPlayer.setScore(oldPlayer.getScore());
		}
		else
		{
			Player newPlayer = level.getPlayer();
			newPlayer.setLives(3);
			newPlayer.setScore(0);
		}

		level.setFont(font0);
	}

	public void logic()
	{
		if (level != null)
		{
			if (leaveGame)
			{
				Player player = level.getPlayer();

				if (fadeStep >= fadeTotalSteps)
				{
					if (player.getLives() > 0)
					{
						if (level.isComplete())
						{
							if (Program.levelName == null)
							{
								levelNum++;
							}
						}

						if (Program.levelName != null || levelNum >= episode.getLevelNum())
						{
							Program.game.changeState(GameStateEnum.STATE_MENU);
						}
						else
						{
							fadeStep = 0;
							fadeTotalSteps = 25;
							fadeTo = 255;

							leaveGame = false;
							loadLevel();
						}
					}
					else
					{
						Program.game.changeState(GameStateEnum.STATE_MENU);
					}
				}
			}

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

				Player player = level.getPlayer();

				font0.drawCentered("Get ready!", 60);
				if (episode != null)
				{
					font0.drawCentered(episode.getTitle() + " " + (levelNum+1), 100);
				}
				font0.drawCentered("  x " + player.getLives(), 140);
				player.draw(Sdl.SCREEN_WIDTH/2 - player.getW() - 12, 145 - player.getH()/2, "IDLE");

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
		}

		Sdl.flip(Sdl.screen);
	}
}
