public class GameStateMenu implements GameState
{
	private final int MENU_EPISODES = 0;
	private final int MENU_OPTIONS = 1;
	private final int MENU_EXIT = 2;
	private final int MENU_SEPARATOR = 3;
	private final int MENU_BACK = 4;

	private int [] curMenu;
	private int curSelection = 0;

	private int [] menuMain =
	{
		MENU_EPISODES,
		MENU_OPTIONS,
		MENU_EXIT
	};
	private int [] menuEpisodes =
	{
		MENU_SEPARATOR,
		MENU_BACK
	};
	private int [] menuOptions =
	{
		MENU_BACK
	};

	private Font font;

	public void loadState()
	{
		font = new Font("./data/gfx/font1.bmp", 7, 10, 1, 4);
		curMenu = menuMain;
	}
	public void unloadState()
	{
	}

	public void logic()
	{
		switch(curMenu[curSelection])
		{
			case MENU_EPISODES:
				Program.game.changeState(GameStateEnum.STATE_GAME);
			break;
			case MENU_OPTIONS:
				// TODO
			break;
			case MENU_EXIT:
				Program.game.changeState(GameStateEnum.STATE_EXIT);
			break;

			default:
			break;
		}
	}
	public void draw()
	{
		if(curMenu != null)
		{
			font.draw("->", 110, 130 + curSelection * 12);

			for(int i = 0; i < curMenu.length; i++)
			{
				String word = null;

				switch(curMenu[i])
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

					default:
					break;
				}

				if(word != null)
				{
					font.draw(word, 130, 130 + i * 12);
				}

				font.draw("** Fantastic Rescue Of Greeny v0.1 **", 5, 30);
				font.draw("Programming - Artur \"Zear\" Rojek\nGraphics - Daniel \"Dnilo\" Garcia", 20, 60);
				font.draw("(c) 2014, Licensed under GPLv2+", 20, 220);
			}
		}

		Sdl.flip(Sdl.screen);
	}
}
