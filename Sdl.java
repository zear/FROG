import sdljava.*;
import sdljava.event.*;
import sdljava.joystick.*;
import sdljava.video.*;

// Class Sdl handles libSDL specific features
public class Sdl
{
	public static long startFrameTime; // = SDLTimer.getTicks();
	public static long newFrameTime;
	public static long counterLastFull;			// for FPS counter
	public final static double framesPerSecond = 60.0;
	public static double accumulator = 0;
	public static double frameTime = 1000.0/framesPerSecond;
	public static long deltaTime;
	public static int fpsCalculated;			// Calculated FPS value to display on screen
	public static boolean fpsIsCapped = false;
	public static boolean[] input = new boolean[2048];	// correct size?
	private static SDLEvent event;
	private static SDLJoystick joy;
	public static SDLSurface screen;
	private static String windowTitle = "F.R.O.G.";
	public static boolean enableJoystick = true;

	public Sdl()
	{
		//startFrameTime = System.currentTimeMillis();
		startFrameTime = SDLTimer.getTicks();
		counterLastFull = startFrameTime;
	}

	public static boolean getInput(int key)
	{
		return input[key];
	}

	public static void putInput(int key, boolean value)
	{
		input[key] = value;
	}

	public static void toggleFullscreen()
	{
		if((screen.getFlags() & SDLVideo.SDL_FULLSCREEN) > 0)
		{
			try
			{
				screen = SDLVideo.setVideoMode(320, 240, 16, SDLVideo.SDL_HWSURFACE | SDLVideo.SDL_DOUBLEBUF);
			}
			catch (SDLException e)
			{
				// todo
			}
		}
		else
		{
			try
			{
				screen = SDLVideo.setVideoMode(320, 240, 16, SDLVideo.SDL_HWSURFACE | SDLVideo.SDL_DOUBLEBUF | SDLVideo.SDL_FULLSCREEN);
			}
			catch (SDLException e)
			{
				// todo
			}
		}
	}

	public static int initSDL()
	{
		try
		{
			SDLMain.init(SDLMain.SDL_INIT_VIDEO | SDLMain.SDL_INIT_JOYSTICK);
		}
		catch (SDLException e)
		{
			// todo
		}

		SDLEvent.showCursor(SDLVideo.SDL_DISABLE);
		SDLVideo.wmSetCaption(windowTitle, null);

		System.out.printf("Joysticks found: %d\n", SDLJoystick.numJoysticks());

		if(SDLJoystick.numJoysticks() > 0)
		{
			try
			{
				joy = SDLJoystick.joystickOpen(0);
			}
			catch (SDLException e)
			{
				// todo
			}
		}

		try
		{
			screen = SDLVideo.setVideoMode(320, 240, 16, SDLVideo.SDL_HWSURFACE | SDLVideo.SDL_DOUBLEBUF);
		}
		catch (SDLException e)
		{
			// todo
		}

		return 0;
	}

	public static void unloadSDL()
	{
		if(joy != null)
		{
			joy.joystickClose();
		}

		SDLMain.quit();
	}

	public static boolean fps()
	{
		//newFrameTime = System.currentTimeMillis();
		newFrameTime = SDLTimer.getTicks();
		deltaTime = newFrameTime - startFrameTime;
		startFrameTime = newFrameTime;
		accumulator += deltaTime;

		if(newFrameTime - counterLastFull > 1000)
		{
			fpsCalculated = (int)((newFrameTime - counterLastFull)/frameTime);
			counterLastFull = newFrameTime;
		}

		if (accumulator >= frameTime)
		{
			accumulator -= frameTime;
			return true;
		}
		else
		{
			fpsIsCapped = true;
			// sleep
			try
			{
				Thread.sleep(1);	// Less overhead than calling a native method
				//SDLTimer.delay(1);
			}
			catch (InterruptedException e)
			{
				// todo
			}
			return false;
		}
	}

	public static SDLSurface loadImage(String filename)
	{
		SDLSurface originalImg;
		SDLSurface optimizedImg;
		long colorkey = 0;

		if(filename == null)
		{
			return null;
		}

		try
		{
			originalImg = SDLVideo.loadBMP(filename);
		}
		catch (SDLException e)
		{
			System.out.printf("Failed to load image: %s\n", filename);
			return null;
		}

		try
		{
			optimizedImg = originalImg.displayFormat();
		}
		catch (SDLException e)
		{
			return null;
		}

		// set transparency
		try
		{
			colorkey = optimizedImg.mapRGB(255, 0, 255);
		}
		catch (SDLException e)
		{
			return null;
		}

		try
		{
			optimizedImg.setColorKey(SDLVideo.SDL_SRCCOLORKEY, colorkey);
		}
		catch (SDLException e)
		{
			return null;
		}

		return optimizedImg;
	}

	public static void flip(SDLSurface surface)
	{
		try
		{
			surface.flip();
		}
		catch (SDLException e)
		{
			// todo
		}
	}

	public static void input()
	{
		try
		{
			while((event = SDLEvent.pollEvent()) != null)
			{
				switch(event.getType())
				{
					case SDLEvent.SDL_QUIT:
						// exit program
						Game.setQuit(true);
						return;

					case SDLEvent.SDL_KEYDOWN:
					{
						SDLKeyboardEvent key = (SDLKeyboardEvent)event;
						putInput(key.getSym(), true);
					}
					break;

					case SDLEvent.SDL_KEYUP:
					{
						SDLKeyboardEvent key = (SDLKeyboardEvent)event;
						putInput(key.getSym(), false);
					}
					break;

					case SDLEvent.SDL_JOYBUTTONDOWN:
					{
						if(Sdl.enableJoystick)
						{
							SDLJoyButtonEvent but = (SDLJoyButtonEvent)event;
							switch(but.getButton())
							{
								case 0:
									//putInput(SDLKey.SDLK_z, true);
									putInput(SDLKey.SDLK_LCTRL, true);
								break;
								case 1:
									//putInput(SDLKey.SDLK_x, true);
									putInput(SDLKey.SDLK_LALT, true);
								break;

								default:
								break;
							}
						}
					}
					break;

					case SDLEvent.SDL_JOYBUTTONUP:
					{
						if(Sdl.enableJoystick)
						{
							SDLJoyButtonEvent but = (SDLJoyButtonEvent)event;
							switch(but.getButton())
							{
								case 0:
									//putInput(SDLKey.SDLK_z, false);
									putInput(SDLKey.SDLK_LCTRL, false);
								break;
								case 1:
									//putInput(SDLKey.SDLK_x, false);
									putInput(SDLKey.SDLK_LALT, false);
								break;

								default:
								break;
							}
						}
					}
					break;
					case SDLEvent.SDL_JOYAXISMOTION:
					{
						if(Sdl.enableJoystick)
						{
							SDLJoyAxisEvent axis = (SDLJoyAxisEvent)event;

							final int deadzone = 1000;
							int value = axis.getValue();

							switch(axis.getAxis())
							{
								case 0: // left-right
									if(value < -deadzone)
									{
										putInput(SDLKey.SDLK_LEFT, true);
										putInput(SDLKey.SDLK_RIGHT, false);
									}
									else if(value > deadzone)
									{
										putInput(SDLKey.SDLK_LEFT, false);
										putInput(SDLKey.SDLK_RIGHT, true);
									}
									else
									{
										putInput(SDLKey.SDLK_LEFT, false);
										putInput(SDLKey.SDLK_RIGHT, false);
									}
								break;
								case 1: // up-down
									if(value < -deadzone)
									{
										putInput(SDLKey.SDLK_UP, true);
										putInput(SDLKey.SDLK_DOWN, false);
									}
									else if(value > deadzone)
									{
										putInput(SDLKey.SDLK_UP, false);
										putInput(SDLKey.SDLK_DOWN, true);
									}
									else
									{
										putInput(SDLKey.SDLK_UP, false);
										putInput(SDLKey.SDLK_DOWN, false);
									}
								break;

								default:
								break;
							}
						}
					}
					break;

					default:
					break;
				}
			}
		}

		catch (SDLException e)
		{
			System.out.printf("Failed to get event\n");
			// todo
		}
	}
}
