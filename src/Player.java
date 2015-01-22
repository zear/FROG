import sdljava.event.*;
import java.util.ArrayList;

// Player class extends Creature class by player specific features
public class Player extends Creature
{
	private float walkV;
	private float jumpV;
	public Camera viewport;
	private boolean[] keys;
	private boolean acceptInput = true;
	private boolean dead = false;	// temporary
	private int score;
	private ArrayList<GameObject> attackObjs = null;

	public Player(LevelLayer lay, Collision col, Level lev)
	{
		super(lay, col, lev);
		viewport = new Camera();
		keys = new boolean[6]; // left, right, up, down, jump, attack
		attackObjs = new ArrayList<GameObject>();
	}

	public float getWalkV()
	{
		return this.walkV;
	}

	public void setWalkV(float velocity)
	{
		this.walkV = velocity;
	}

	public float getJumpV()
	{
		return this.jumpV;
	}

	public void setJumpV(float velocity)
	{
		this.jumpV = velocity;
	}

	public boolean acceptInput()
	{
		return this.acceptInput;
	}
	public void setAcceptInput(boolean value)
	{
		this.acceptInput = value;
	}

	public int getScore()
	{
		return this.score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public void addScore(int score)
	{
		this.score += score;
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
		if (keys[4] && (dead || (this.level.isComplete() && this.level.isPlayTimeCalculated())))
		{
			GameStateGame.leaveGame = true;
			keys[4] = false;
		}
		if (Sdl.getInput(SDLKey.SDLK_ESCAPE))
			GameStateGame.leaveGame = true;
		if (Sdl.getInput(SDLKey.SDLK_f))
			Sdl.toggleFullscreen();
		if (Sdl.getInput(SDLKey.SDLK_c))
			this.viewport.setTarget(0,0);
		// debug
		if (Sdl.getInput(SDLKey.SDLK_0))
		{
			Sdl.putInput(SDLKey.SDLK_0, false);

			if (Sdl.frameTime == 1000/Sdl.framesPerSecond)
				Sdl.frameTime *= 10;
			else
				Sdl.frameTime = 1000/Sdl.framesPerSecond;
		}
		if (Sdl.getInput(SDLKey.SDLK_d))
		{
			Sdl.putInput(SDLKey.SDLK_d, false);

			Game.debugMode = !Game.debugMode;
		}
	}

	/**
	 * Updates player animation.
	 */
	public void updateAnimation()
	{
		String newAnim = null;
		Animation curAnim = this.getAnimation();

		if (curAnim.getAnimName().equals("ATTACK") && curAnim.isOver())
		{
			((Player)this).setAcceptInput(true);
		}

		if (vx != 0)
		{
			if (this.getAction(5))
			{
				this.setAction(5, false);
				newAnim = "ATTACK";
			}
			else
			{
				if (this.isOnGround)
				{
					if (!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()))
						newAnim = "WALK";
				}
			}
		}
		else
		{
			if (this.getAction(5))
			{
					this.setAction(5, false);
					newAnim = "ATTACK";
			}
			else
			{
				if (this.isOnGround)
				{
					if (!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()))
						newAnim = "IDLE";
				}
			}
		}

		if (vy < 0)
		{
			if (this.isOnGround)
			{
				if (!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()) && newAnim != "ATTACK")
					newAnim = "JUMP_UP";
			}

			if (this.isClimbing)
			{
				if (!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()) && newAnim != "ATTACK")
					newAnim = "CLIMB";
			}
		}
		else if (vy > 0)
		{
			if (!this.isOnGround)
			{
				if (!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()) && newAnim != "ATTACK")
					newAnim = "JUMP_DOWN";
			}

			if (this.isClimbing)
			{
				if (!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()) && newAnim != "ATTACK")
					newAnim = "CLIMB";
			}
		}
		else
		{
			if (this.isClimbing)
			{
				if (!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()) && newAnim != "ATTACK")
					newAnim = "CLIMB";
			}
		}

		if (newAnim != null)
		{
			if (this instanceof Player && !(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()) && !newAnim.equals("ATTACK"))
			{
				((Player)this).setAcceptInput(true);
			}

			this.changeAnimation(newAnim);
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

	public ArrayList<GameObject> getAttackObjs()
	{
		return this.attackObjs;
	}
}
