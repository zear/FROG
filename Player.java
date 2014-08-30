import sdljava.event.*;

// Player class extends Creature class by player specific features
public class Player extends Creature
{
	public Camera viewport;
	private boolean[] keys;
	private boolean acceptInput = true;
	private Replay replay;
	private boolean dead = false;	// temporary

	public Player(LevelLayer lay, Collision col)
	{
		super(lay, col);
		viewport = new Camera();
		keys = new boolean[6]; // left, right, up, down, jump, attack
		replay = new Replay();
	}

	public boolean acceptInput()
	{
		return this.acceptInput;
	}
	public void setAcceptInput(boolean value)
	{
		this.acceptInput = value;
	}

	public boolean getAction(int key)
	{
		return keys[key];
	}

	public void setAction(int key, boolean value)
	{
		keys[key] = value;

		// temp
		Sdl.putInput(SDLKey.SDLK_LEFT, keys[0]);
		Sdl.putInput(SDLKey.SDLK_RIGHT, keys[1]);
		Sdl.putInput(SDLKey.SDLK_UP, keys[2]);
		Sdl.putInput(SDLKey.SDLK_DOWN, keys[3]);
//		Sdl.putInput(SDLKey.SDLK_x, keys[4]);
//		Sdl.putInput(SDLKey.SDLK_z, keys[5]);
		Sdl.putInput(SDLKey.SDLK_LCTRL, keys[4]);
		Sdl.putInput(SDLKey.SDLK_LALT, keys[5]);

		replay.play();
		replay.record(key, value);
	}

	public void updateKeys()
	{
		keys[0] = Sdl.getInput(SDLKey.SDLK_LEFT);
		keys[1] = Sdl.getInput(SDLKey.SDLK_RIGHT);
		keys[2] = Sdl.getInput(SDLKey.SDLK_UP);
		keys[3] = Sdl.getInput(SDLKey.SDLK_DOWN);
//		keys[4] = Sdl.getInput(SDLKey.SDLK_x);
//		keys[5] = Sdl.getInput(SDLKey.SDLK_z);
		keys[4] = Sdl.getInput(SDLKey.SDLK_LCTRL);
		keys[5] = Sdl.getInput(SDLKey.SDLK_LALT);

		// TODO: move this elsewhere
		if(Sdl.getInput(SDLKey.SDLK_ESCAPE))
			Game.setQuit(true);
		if(Sdl.getInput(SDLKey.SDLK_f))
			Sdl.toggleFullscreen();
		if(Sdl.getInput(SDLKey.SDLK_c))
			this.viewport.setTarget(0,0);
		if(Sdl.getInput(SDLKey.SDLK_r))
		{
			this.replay.record = !this.replay.record;
			System.out.printf("Recording: %d\n", this.replay.record != false ? 1 : 0);
		}
		if(Sdl.getInput(SDLKey.SDLK_p))
		{
			this.replay.play = !this.replay.play;
			System.out.printf("Playback: %d\n", !!this.replay.play != false ? 1 : 0);
		}
	}

	public boolean isDead()
	{
		return this.dead;
	}

	public void setDead(boolean value)
	{
		this.dead = value;
	}
}
