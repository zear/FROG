import java.util.ArrayList;
import java.io.File;

import sdljava.*;
import sdljava.event.*;
import sdljava.video.*;

public class GameStateMenu implements GameState
{
	private static final int MENU_EPISODES = 0;
	private static final int MENU_OPTIONS = 1;
	private static final int MENU_EXIT = 2;
	private static final int MENU_LAUNCH_EPISODE = 3;
	private static final int MENU_FPS = 4;
	private static final int MENU_DEBUG = 5;
	private static final int MENU_SEPARATOR = 6;
	private static final int MENU_BACK = 7;

	private int[] curMenu;
	private int[] parentMenu;
	private int curSelection;
	private boolean selected = false;
	private boolean[] keys;

	private int[] menuMain =
	{
		MENU_EPISODES,
		MENU_OPTIONS,
		MENU_EXIT
	};
	private int[] menuEpisodes;

	private int[] menuOptions =
	{
		MENU_FPS,
		MENU_DEBUG,
		MENU_SEPARATOR,
		MENU_BACK
	};

	private ArrayList<Episode> episodeList = null;
	private Episode selectedEpisode = null;

	private Font font;

	private SDLSurface disk;
	private boolean drawLoading = false;

	private int fadeStep;
	private int fadeTotalSteps;
	private int fadeTo;

	private void loadEpisodes()
	{
		if (episodeList != null)
			return;

		int i = 0;
		episodeList = new ArrayList<Episode>();
		File folder = new File("./data/level/");
		File[] fileList = folder.listFiles();

		for (File curFile : fileList)
		{
			if (curFile.isFile() && curFile.getName().endsWith(".ep"))
			{
				episodeList.add(new Episode(curFile.getName()));
			}
		}

		menuEpisodes = new int[episodeList.size() + 2];

		for (; i < episodeList.size(); ++i)
		{
			menuEpisodes[i] = MENU_LAUNCH_EPISODE;
		}
		menuEpisodes[i] = MENU_SEPARATOR;
		menuEpisodes[i+1] = MENU_BACK;
	}

	public Episode getSelectedEpisode()
	{
		return selectedEpisode;
	}

	public void loadState()
	{
		font = new Font("./data/gfx/font1.bmp", 7, 10, 1, 4);
		disk = Sdl.loadImage("./data/gfx/disk.bmp");
		keys = new boolean[6]; // left, right, up, down, accept, back
		curMenu = menuMain;
		parentMenu = null;
		curSelection = 0;

		fadeStep = 0;
		fadeTotalSteps = 25;
		fadeTo = 0;
	}
	public void unloadState()
	{
	}

	public void input()
	{
		keys[0] = Sdl.getInput(SDLKey.SDLK_LEFT);
		keys[1] = Sdl.getInput(SDLKey.SDLK_RIGHT);
		keys[2] = Sdl.getInput(SDLKey.SDLK_UP);
		keys[3] = Sdl.getInput(SDLKey.SDLK_DOWN);
//		keys[4] = Sdl.getInput(SDLKey.SDLK_x);
//		keys[5] = Sdl.getInput(SDLKey.SDLK_z);
		keys[4] = Sdl.getInput(SDLKey.SDLK_LCTRL);
		keys[5] = Sdl.getInput(SDLKey.SDLK_LALT);
	}

	public void logic()
	{
		input();

		if (keys[2]) // up
		{
			keys[2] = false;
			curSelection--;

			if (curSelection >= 0 && curMenu[curSelection] == MENU_SEPARATOR)
				curSelection--;
		}
		else if (keys[3]) // down
		{
			keys[3] = false;
			curSelection++;

			if (curSelection < curMenu.length && curMenu[curSelection] == MENU_SEPARATOR)
				curSelection++;
		}
		else if (keys[4]) // accept
		{
			keys[4] = false;
			selected = true;
		}

		// temp
		Sdl.putInput(SDLKey.SDLK_LEFT, keys[0]);
		Sdl.putInput(SDLKey.SDLK_RIGHT, keys[1]);
		Sdl.putInput(SDLKey.SDLK_UP, keys[2]);
		Sdl.putInput(SDLKey.SDLK_DOWN, keys[3]);
//		Sdl.putInput(SDLKey.SDLK_x, keys[4]);
//		Sdl.putInput(SDLKey.SDLK_z, keys[5]);
		Sdl.putInput(SDLKey.SDLK_LCTRL, keys[4]);
		Sdl.putInput(SDLKey.SDLK_LALT, keys[5]);

		if (curSelection < 0)
			curSelection = curMenu.length - 1;
		else if (curSelection >= curMenu.length)
			curSelection = 0;

		if (selected)
		{
			switch (curMenu[curSelection])
			{
				case MENU_EPISODES:
					loadEpisodes();
					parentMenu = curMenu;
					curMenu = menuEpisodes;
					curSelection = 0;
				break;
				case MENU_OPTIONS:
					parentMenu = curMenu;
					curMenu = menuOptions;
					curSelection = 0;
				break;
				case MENU_EXIT:
					Program.game.changeState(GameStateEnum.STATE_EXIT);
				break;
				case MENU_LAUNCH_EPISODE:
					if(curSelection < episodeList.size())
					{
						for (int i = 0; i < episodeList.size(); ++i)
						{
							if (episodeList.get(i).getId() == curSelection)
							{
								selectedEpisode = episodeList.get(i);
								Program.game.changeState(GameStateEnum.STATE_GAME);
								drawLoading = true;
							}
						}
					}
				break;
				case MENU_FPS:
					Game.drawFps = !Game.drawFps;
				break;
				case MENU_DEBUG:
					Game.debugMode = !Game.debugMode;
				break;
				case MENU_BACK:
					if (parentMenu != null)
					{
						curMenu = parentMenu;
						parentMenu = null;
						curSelection = 0;
					}
				break;

				default:
				break;
			}

			selected = false;
		}
	}
	public void draw()
	{
		// Draw uniform background
		try
		{
			Sdl.screen.fillRect(100);
		}
		catch (Exception e)
		{
			//todo
		}

		if (drawLoading)
		{
			drawLoading = false;
			try
			{
				disk.blitSurface(null, Sdl.screen, new SDLRect(140, 80));
			}
			catch (SDLException e)
			{
				// todo
			}

			font.drawCentered("Just a moment...", 130);
		}
		else if (curMenu != null)
		{
			for (int i = 0; i < curMenu.length; i++)
			{
				String word = null;

				switch (curMenu[i])
				{
					case MENU_EPISODES:
						word = "Play";
					break;
					case MENU_OPTIONS:
						word = "Options";
					break;
					case MENU_EXIT:
						word = "Quit";
					break;
					case MENU_LAUNCH_EPISODE:
						for (int j = 0; j < episodeList.size(); ++j)
						{
							if (episodeList.get(j).getId() == i)
							{
								word = String.valueOf(i+1) + ": " + episodeList.get(j).getTitle();
								break;
							}
						}
					break;
					case MENU_FPS:
						word = "show fps" + (Game.drawFps ? ": ON" : ": OFF");
					break;
					case MENU_DEBUG:
						word = "debug mode" + (Game.debugMode ? ": ON" : ": OFF");
					break;
					case MENU_SEPARATOR:
						word = "";
					break;
					case MENU_BACK:
						word = "back";
					break;

					default:
					break;
				}

				if (word != null)
				{
					if (i == curSelection)
					{
						word = "> " + word + " <";
					}

					font.drawCentered(word, 130 + i * 12);
				}

				if (curMenu == menuEpisodes)
				{
					font.drawCentered("Select episode:", 110);
				}
				else
				{
					font.drawCentered("** Fantastic Rescue Of Greeny v0.1 **", 30);
					font.drawCentered("Programming - Artur \"Zear\" Rojek\nGraphics - Daniel \"Dnilo\" Garcia", 60);
					font.drawCentered("(c) 2014-2015, Licensed under LGPLv2.1+", 220);
				}
			}
		}

		if (fadeStep < fadeTotalSteps)
		{
			Sdl.fade(Sdl.screen, 255, fadeTo, fadeStep, fadeTotalSteps);
			fadeStep++;
		}
		Sdl.flip(Sdl.screen);
	}
}
