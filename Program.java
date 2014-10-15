import sdljava.*;

public class Program
{
	public static Game game;
	public static String levelName = null;
	public static void main(String [] args)
	{
		game = new Game();

		for(int i = 0; i < args.length; i++)
		{
			if(args[i].equals("-level") || args[i].equals("-l"))
			{
				if(i + 1 < args.length)
				{
					Program.levelName = args[i+1];
					Program.game.changeState(GameStateEnum.STATE_GAME);
				}
				else
				{
					System.out.printf("Not enough arguments for %s (need 1).\nSee -help for more details.\n", args[i]);
					return;
				}
			}
			if(args[i].equals("-help") || args[i].equals("-h") || args[i].equals("--h"))
			{
				System.out.printf("(c) 2013-2014 Artur Rojek, Daniel Garcia.\n\nList of commandline parameters:\n-level [name.lvl]\tlaunches a specified level\n-nojoy\t\t\tdisable joystick support\n-help\t\t\tthis screen\n");
				return;
			}
			if(args[i].equals("-nojoy"))
			{
				Sdl.enableJoystick = false;
			}
		}

		Sdl.initSDL();

		while(!game.mainLoop()) {}

		Sdl.unloadSDL();
	}
}
