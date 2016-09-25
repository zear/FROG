import sdljava.event.*;

public class GameStateCredits implements GameState
{
	private Font font;
	private boolean[] keys;
	private int scroll;
	private boolean doScroll;
	private int scrollTimer;
	private int lineHeight;

	public void loadState()
	{
		scroll = 0;
		scrollTimer = 0;
		lineHeight = 0;
		font = new Font("./data/gfx/font1.bmp", 7, 10, 1, 4);
		keys = new boolean[6]; // left, right, up, down, accept, back
	}

	public void unloadState()
	{
	}

	public void input()
	{
		keys[Input.KEY_LEFT] = Sdl.getInput(SDLKey.SDLK_LEFT);
		keys[Input.KEY_RIGHT] = Sdl.getInput(SDLKey.SDLK_RIGHT);
		keys[Input.KEY_UP] = Sdl.getInput(SDLKey.SDLK_UP);
		keys[Input.KEY_DOWN] = Sdl.getInput(SDLKey.SDLK_DOWN);
//		keys[Input.KEY_JUMP] = Sdl.getInput(SDLKey.SDLK_x);
//		keys[Input.KEY_ATTACK] = Sdl.getInput(SDLKey.SDLK_z);
		keys[Input.KEY_JUMP] = Sdl.getInput(SDLKey.SDLK_LCTRL);
		keys[Input.KEY_ATTACK] = Sdl.getInput(SDLKey.SDLK_LALT);
	}

	public void logic()
	{
		input();

		doScroll = true;

		if (keys[Input.KEY_JUMP])
		{
			keys[Input.KEY_JUMP] = false;
			Sdl.putInput(SDLKey.SDLK_LCTRL, keys[Input.KEY_JUMP]);

			Program.game.changeState(GameStateEnum.STATE_MENU);
		}
		else if (keys[Input.KEY_UP])
		{
			doScroll = false;
			scroll--;
		}
		else if (keys[Input.KEY_DOWN])
		{
			doScroll = false;
			scroll++;
		}
	}

	private int getLineHeight()
	{
		lineHeight += font.getH() + font.getLeading();

		return lineHeight;
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

		lineHeight = 0;
		font.drawFloatingCentered("** Programming **", Sdl.SCREEN_HEIGHT - scroll, 1, 3);
		getLineHeight();
		font.drawCentered("Artur Rojek", Sdl.SCREEN_HEIGHT - scroll + getLineHeight());
		getLineHeight();
		getLineHeight();
		font.drawFloatingCentered("** Graphics **", Sdl.SCREEN_HEIGHT - scroll + getLineHeight(), 1, 3);
		getLineHeight();
		font.drawCentered("Daniel Garcia", Sdl.SCREEN_HEIGHT - scroll + getLineHeight());
		getLineHeight();
		getLineHeight();
		font.drawFloatingCentered("** Level design **", Sdl.SCREEN_HEIGHT - scroll + getLineHeight(), 1, 3);
		getLineHeight();
		font.drawCentered("Artur Rojek", Sdl.SCREEN_HEIGHT - scroll + getLineHeight());
		getLineHeight();
		getLineHeight();
		font.drawFloatingCentered("** Special Thanks **", Sdl.SCREEN_HEIGHT - scroll + getLineHeight(), 1, 3);
		getLineHeight();
		font.drawCentered("Paul Cercueil", Sdl.SCREEN_HEIGHT - scroll + getLineHeight());
		font.drawCentered("Andreas Bjerkeholt", Sdl.SCREEN_HEIGHT - scroll + getLineHeight());
		font.drawCentered("Nebuleon", Sdl.SCREEN_HEIGHT - scroll + getLineHeight());
		font.drawCentered("Tomas Vymazal", Sdl.SCREEN_HEIGHT - scroll + getLineHeight());
		lineHeight += Sdl.SCREEN_HEIGHT;
		font.drawFloatingCentered("Thank YOU for playing!", Sdl.SCREEN_HEIGHT - scroll + getLineHeight(), 1, 3);

		if (doScroll && scrollTimer-- <= 0)
		{
			scrollTimer = 3;
			scroll++;
		}

		if (scroll > lineHeight + font.getH() + font.getLeading() + Sdl.SCREEN_HEIGHT)
		{
			scroll = 0;
		}
		else if (scroll < 0)
		{
			scroll = lineHeight + font.getH() + font.getLeading() + Sdl.SCREEN_HEIGHT;
		}

		Sdl.flip(Sdl.screen);
	}

}
