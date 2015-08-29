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
	private boolean swordSwing;
	private boolean dead = false;	// temporary
	private int power;
	private int lives;
	private int score;
	private ArrayList<GameObject> attackObjs = null;

	public Player(LevelLayer lay, Collision col, Level lev)
	{
		super(lay, col, lev);
		viewport = new Camera();
		keys = new boolean[6]; // left, right, up, down, jump, attack
		attackObjs = new ArrayList<GameObject>();
	}

	public void hurt(boolean direction)
	{
		this.hurt = true;
		this.acceptInput = false;
		this.hp--;
		this.vx = (direction ? 2 : -2);
		this.vy = -2;
		this.isOnGround = false;

		if (!this.affectedByGravity && hp <= 0)
		{
			this.affectedByGravity = true;
		}
	}

	public void walk(float vx)
	{
		this.vx += vx;

		if (this.vx < -this.walkV)
		{
			this.vx = -this.walkV;
		}
		else if (this.vx > this.walkV)
		{
			this.vx = this.walkV;
		}
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

	public int getLives()
	{
		return this.lives;
	}

	public void setLives(int lives)
	{
		this.lives = lives;
	}

	public void addLives(int lives)
	{
		this.lives += lives;
	}

	public int getPower()
	{
		return this.power;
	}

	public void setPower(int value)
	{
		this.power = value;
	}

	public void addPower(int value)
	{
		this.power += value;
		if (this.power > 2)
		{
			this.power = 2;
			this.addScore(1000);
		}
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
		Sdl.putInput(SDLKey.SDLK_LEFT, keys[Input.KEY_LEFT]);
		Sdl.putInput(SDLKey.SDLK_RIGHT, keys[Input.KEY_RIGHT]);
		Sdl.putInput(SDLKey.SDLK_UP, keys[Input.KEY_UP]);
		Sdl.putInput(SDLKey.SDLK_DOWN, keys[Input.KEY_DOWN]);
//		Sdl.putInput(SDLKey.SDLK_x, keys[Input.KEY_JUMP]);
//		Sdl.putInput(SDLKey.SDLK_z, keys[Input.KEY_ATTACK]);
		Sdl.putInput(SDLKey.SDLK_LCTRL, keys[Input.KEY_JUMP]);
		Sdl.putInput(SDLKey.SDLK_LALT, keys[Input.KEY_ATTACK]);
	}

	public void updateKeys()
	{
		keys[Input.KEY_LEFT] = Sdl.getInput(SDLKey.SDLK_LEFT);
		keys[Input.KEY_RIGHT] = Sdl.getInput(SDLKey.SDLK_RIGHT);
		keys[Input.KEY_UP] = Sdl.getInput(SDLKey.SDLK_UP);
		keys[Input.KEY_DOWN] = Sdl.getInput(SDLKey.SDLK_DOWN);
//		keys[Input.KEY_JUMP] = Sdl.getInput(SDLKey.SDLK_x);
//		keys[Input.KEY_ATTACK] = Sdl.getInput(SDLKey.SDLK_z);
		keys[Input.KEY_JUMP] = Sdl.getInput(SDLKey.SDLK_LCTRL);
		keys[Input.KEY_ATTACK] = Sdl.getInput(SDLKey.SDLK_LALT);

		// TODO: move this elsewhere
		if (keys[Input.KEY_JUMP] && (dead || (this.level.isComplete() && this.level.isPlayTimeCalculated())))
		{
			GameStateGame.leaveGame = true;
			keys[Input.KEY_JUMP] = false;
			Sdl.putInput(SDLKey.SDLK_LCTRL, keys[Input.KEY_JUMP]);
		}
		if (Sdl.getInput(SDLKey.SDLK_ESCAPE))
		{
			lives = 0;
			GameStateGame.leaveGame = true;
		}
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

		if (curAnim.getAnimName().equals("ATTACK") && curAnim.isOver() && !hurt)
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
			if (this instanceof Player && !(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()) && !newAnim.equals("ATTACK") && !hurt)
			{
				((Player)this).setAcceptInput(true);
			}

			this.changeAnimation(newAnim);
		}
	}

	public boolean isSwordSwing()
	{
		return this.swordSwing;
	}

	public void setSwordSwing(boolean value)
	{
		this.swordSwing = value;
	}

	public boolean isDead()
	{
		return this.dead;
	}

	public void setDead(boolean value)
	{
		this.dead = value;
		this.lives--;
	}

	public ArrayList<GameObject> getAttackObjs()
	{
		return this.attackObjs;
	}
}
