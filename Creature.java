import java.util.LinkedList;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;

// Creature class extends GameObject class by moving creatures specific features
public class Creature extends GameObject
{
	protected float vx;
	protected float vy;

	private AI ai;

	protected boolean isOnGround;
	protected boolean isCrouching;
	protected boolean isClimbing;
	protected boolean canClimb;
	protected boolean affectedByGravity = true;

	protected int hp;

	protected LevelLayer levelLayer;	// the middle-layer of the level
	protected Collision collision;		// reference to collision map passed by the Level object

	public Creature(LevelLayer lay, Collision col)
	{
		this.collision = col;
		this.levelLayer = lay;
	}

	public void load(String fileName, int w, int h, int rowW, int size, LinkedList <GameObjectTemplate> tempList)
	{
		super.load(fileName, w, h, rowW, size, tempList);
		this.ai = new AI();

		// Let's hardcode the AI behaviour for now.
		if(super.getName().equals("jumper"))
		{
			ai.addAction(AI.JUMP);
			ai.setVar(0, 1.5f);
			ai.setVar(1, 4.0f);
		}
		else if(super.getName().equals("badass"))
		{
			ai.addAction(AI.WALK);
			ai.setVar(0, 0.5f);
		}
	}

	public float getVx()
	{
		return vx;
	}
	public float getVy()
	{
		return vy;
	}

	public void setVx(float vx)
	{
		this.vx = vx;
	}
	public void setVy(float vy)
	{
		this.vy = vy;
	}

	public int getHp()
	{
		return this.hp;
	}

	public void setHp(int value)
	{
		this.hp = value;
	}

	public void tryCrouch()
	{
		this.isCrouching = true;
	}

	public void tryClimb()
	{
		this.isClimbing = true;
	}

	public void climb(float vy)
	{
		if(this.isClimbing)
			this.vy = vy;
	}

	public void jump(float vy)
	{
		if(this.isOnGround)
			this.vy = vy;
		if(this.isClimbing)
		{
			this.isClimbing = false;
			this.vy = vy; //vy/2;
		}
	}

	public void walk(float vx)
	{
		this.vx = vx;
	}

	public void doAi()
	{
		if(!this.ai.hasActions())	// Don't bother if there are no AI actions for this object
			return;

		switch(this.ai.getType())
		{
			case AI.WAIT:
				this.ai.doTimer();

				if(this.ai.getTimer() <= 0)
					this.ai.setNextAction();
			break;
			case AI.WALK:
				if(!this.direction)	// left
					this.walk(-this.ai.getVar(0));
				else			// right
					this.walk(this.ai.getVar(0));
				this.ai.setNextAction();
			break;
			case AI.JUMP:
				if(this.isOnGround)
				{
					this.jump(-this.ai.getVar(1));
					if(!this.direction)	// left
					{
						this.walk(-this.ai.getVar(0));
					}
					else			// right
					{
						this.walk(this.ai.getVar(0));
					}
					this.ai.setNextAction();
				}
			break;

			default:
			break;
		}
	}

	private void collisionCheck(float totalVx, float totalVy)
	{
		int col;
		float curVx;
		float curVy;
		float remVx = 0; // remaining vx
		float remVy = 0; // remaining vy
		int tile;
		int x1;
		int x2;
		int y1;
		int y2;

		// If the total velocity is greater than 1px/frame, calculate only 1px movement
		// and leave the remaining part for recursive call to collisionCheck().
		// This is done to accurately calculate collision of fast moving objects.
		if(abs(totalVx) > 1f)
		{
			if(totalVx > 0)
			{
				curVx = 1f;
				remVx = totalVx - 1f; 
			}
			else
			{
				curVx = -1f;
				remVx = totalVx + 1f;
			}
		}
		else
		{
			curVx = totalVx;
		}
		if(abs(totalVy) > 1f)
		{
			if(totalVy > 0)
			{
				curVy = 1f;
				remVy = totalVy - 1f; 
			}
			else
			{
				curVy = -1f;
				remVy = totalVy + 1f;
			}
		}
		else
		{
			curVy = totalVy;
		}

		if(this.vx == 0)
			curVx = 0;
		if(this.vy == 0)
			curVy = 0;

		// check y
		if(curVy > 0)
		{
			// .---.
			// |   |
			// #####

			x1 = (int)(super.x);
			x2 = (int)(super.x) + super.w - 1;
			y1 = (int)(curVy + super.y) + super.h - 1;

			if(x1 < 0)
				x1 = -16;

			col = Collision.COLLISION_NONE;
			for(int i = x1/16; i <= x2/16; i++)
			{
				tile = levelLayer.getTile(i, y1/16);
				col = col | collision.getCollision(tile);
			}

			if((col & Collision.COLLISION_SOLID) > 0)
			{
				this.vy = 0;
				this.y = (float)ceil(this.y) - 0.2f;
				this.isOnGround = true;
			}
			else if((col & Collision.COLLISION_PLATFORM) > 0)
			{
				if(((int)this.y + super.h - 1)/16*16 < (y1)/16*16)
				{
					this.vy = 0;
					this.y = (float)ceil(this.y) - 0.2f;
					this.isOnGround = true;
				}
				else
					super.y += curVy;
			}
			else if((col & Collision.COLLISION_CLIMB) > 0)
			{
				this.canClimb = true;
				super.y += curVy;
			}
			else
			{
				super.y += curVy;
			}

			// Is not
			if(!((col & Collision.COLLISION_CLIMB) > 0))
			{
				this.canClimb = false;
			}
		}
		else if(curVy < 0)
		{
			// #####
			// |   |
			// .---.

			x1 = (int)(super.x);
			x2 = (int)(super.x) + super.w - 1;
			y1 = (int)(curVy + super.y);

			if(x1 < 0)
				x1 = -16;
			if(y1 < 0)
				y1 = -16;

			col = Collision.COLLISION_NONE;
			for(int i = x1/16; i <= x2/16; i++)
			{
				tile = levelLayer.getTile(i, y1/16);
				col = col | collision.getCollision(tile);
			}

			if((col & Collision.COLLISION_SOLID) > 0)
			{
				this.vy = 0;
			}
			else if((col & Collision.COLLISION_CLIMB) > 0)
			{
				this.canClimb = true;
				super.y += curVy;
			}
			else
			{
				super.y += curVy;
			}

			// Is not
			if(!((col & Collision.COLLISION_CLIMB) > 0))
			{
				if(canClimb)
				{
					super.y -= curVy;
					//this.vy = 0;
				}
					//super.y -= curVy;
				//this.canClimb = false;
			}
		}

		// check x
		if(curVx > 0)
		{
			// .---#
			// |   #
			// .---#

			x1 = (int)(curVx + super.x) + super.w - 1;
			y1 = (int)(super.y);
			y2 = (int)(super.y) + super.h - 1;

			if(y1 < 0)
				y1 = -16;
			if(y2 < 0)
				y2 = -16;

			col = Collision.COLLISION_NONE;
			for(int i = y1/16; i <= y2/16; i++)
			{
				tile = levelLayer.getTile(x1/16, i);
				col = col | collision.getCollision(tile);
			}

			if((col & Collision.COLLISION_SOLID) > 0)
			{
				this.vx = 0;
				if(!(this instanceof Player))	// TODO: Shouldn't be here. Player class should have separate collision check that overloads this one.
					this.direction = !this.direction;
			}
			else
			{
				super.x += curVx;
			}
		}
		else if(curVx < 0)
		{
			// #---.
			// #   |
			// #---.

			x1 = (int)(curVx + super.x);
			y1 = (int)(super.y);
			y2 = (int)(super.y) + super.h - 1;

			if(x1 < 0)
				x1 = -16;
			if(y1 < 0)
				y1 = -16;
			if(y2 < 0)
				y2 = -16;

			col = Collision.COLLISION_NONE;
			for(int i = y1/16; i <= y2/16; i++)
			{
				tile = levelLayer.getTile(x1/16, i);
				col = col | collision.getCollision(tile);
			}

			if((col & Collision.COLLISION_SOLID) > 0)
			{
				this.vx = 0;
				if(!(this instanceof Player))	// TODO: Shouldn't be here. Player class should have separate collision check that overloads this one.
					this.direction = !this.direction;
			}
			else
			{
				super.x += curVx;
			}
		}

		if(remVx != 0 || remVy != 0)
		{
			collisionCheck(remVx, remVy);
		}
	}

	// Simplified version of the collision check without the recursion step. Left for reference.
//	private void collisionCheck()
//	{
//		int tile;
//		int tile2;
//		int x1;
//		int x2;
//		int y1;
//		int y2;

//		// level boundaries check
//		if(super.x + this.vx < 0)
//		{
//			this.vx = 0;
//			super.x = 0;
//		}
//		if(((int)(super.x + this.vx) + super.w - 1)/16 >= levelLayer.getWidth())
//		{
//			this.vx = 0;
//			super.x = levelLayer.getWidth() * 16 - super.w;
//		}
//		if(super.y + this.vy < 0)
//		{
//			this.vy = 0;
//			super.y = 0;
//		}
//		if(((int)(super.y + this.vy) + super.h)/16 >= levelLayer.getHeight())
//		{
//			this.vy = 0;
//			super.y = levelLayer.getHeight() * 16 - super.h;
//		}

//		// check y
//		if(this.vy > 0)
//		{
//			// .---.
//			// |   |
//			// #####

//			x1 = (int)(super.x);
//			x2 = (int)(super.x) + super.w - 1;
//			y1 = (int)(this.vy + super.y) + super.h - 1;

//			tile = levelLayer.getTile(x1/16, y1/16);
//			tile2 = levelLayer.getTile(x2/16, y1/16);

//			if(collision.getCollision(tile) == 0 && collision.getCollision(tile2) == 0)
//			{
//				super.y += this.vy;
//			}
//			else
//			{
//				this.vy = 0;
//			}
//		}
//		else if(this.vy < 0)
//		{
//			// #####
//			// |   |
//			// .---.

//			x1 = (int)(super.x);
//			x2 = (int)(super.x) + super.w - 1;
//			y1 = (int)(this.vy + super.y);

//			tile = levelLayer.getTile(x1/16, y1/16);
//			tile2 = levelLayer.getTile(x2/16, y1/16);

//			if(collision.getCollision(tile) == 0 && collision.getCollision(tile2) == 0)
//			{
//				super.y += this.vy;
//			}
//			else
//			{
//				this.vy = 0;
//			}
//		}

//		// check x
//		if(this.vx > 0)
//		{
//			// .---#
//			// |   #
//			// .---#

//			x1 = (int)(this.vx + super.x) + super.w - 1;
//			y1 = (int)(super.y);
//			y2 = (int)(super.y) + super.h - 1;

//			tile = levelLayer.getTile(x1/16, y1/16);
//			tile2 = levelLayer.getTile(x1/16, y2/16);

//			if(collision.getCollision(tile) == 0 && collision.getCollision(tile2) == 0)
//			{
//				super.x += this.vx;
//			}
//			else
//			{
//				this.vx = 0;
//			}

//		}
//		else if(this.vx < 0)
//		{
//			// #---.
//			// #   |
//			// #---.

//			x1 = (int)(this.vx + super.x);
//			y1 = (int)(super.y);
//			y2 = (int)(super.y) + super.h - 1;

//			tile = levelLayer.getTile(x1/16, y1/16);
//			tile2 = levelLayer.getTile(x1/16, y2/16);

//			if(collision.getCollision(tile) == 0 && collision.getCollision(tile2) == 0)
//			{
//				super.x += this.vx;
//			}
//			else
//			{
//				this.vx = 0;
//			}
//		}
//	}

	public void logic()
	{
		super.logic();

		if(this.hp <= 0)
			this.setRemoval(true);
	}

	public void move()
	{
		String newAnim = null;

		if(this.affectedByGravity && !this.isClimbing)
		{
			vy += 0.2; // gravity
		}

		if(vx > 0)
		{
			if(this instanceof Player)
			{
				if(((Player)this).getAction(1))	// right
					super.direction = true;
			}
			else
				super.direction = true;
		}
		else if(vx < 0)
		{
			if(this instanceof Player)
			{
				if(((Player)this).getAction(0))	// left
					super.direction = false;
			}
			else
				super.direction = false;
		}


		Animation curAnim = super.getAnimation();

		if(this instanceof Player)
		{
			if(curAnim.getAnimName().equals("ATTACK") && curAnim.isOver())
			{
				((Player)this).setAcceptInput(true);
			}
		}

		if(vx != 0)
		{
			if(this instanceof Player && ((Player)this).getAction(5))
			{
				newAnim = "ATTACK";
			}
			else
			{
				if(this.isOnGround)
				{
					if(!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()))
						newAnim = "WALK";
				}
			}
		}
		else
		{
			if(this instanceof Player && ((Player)this).getAction(5))
			{
					newAnim = "ATTACK";
			}
			else
			{
				if(this.isOnGround)
				{
					if(!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()))
						newAnim = "IDLE";
				}
			}
		}

		if(vy < 0)
		{
			if(this.isOnGround)
			{
				if(!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()))
					newAnim = "JUMP_UP";
			}
		}
		else if(vy > 0)
		{
			if(!this.isOnGround)
			{
				if(!(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()))
					newAnim = "JUMP_DOWN";
			}
		}

		if(newAnim != null)
		{
			if(this instanceof Player && !(curAnim.getAnimName().equals("ATTACK") && !curAnim.isOver()) && !newAnim.equals("ATTACK"))
			{
				((Player)this).setAcceptInput(true);
			}

			super.changeAnimation(newAnim);
		}

		if(vx > 3)
			vx = 3;
		if(vx < -3)
			vx = -3;
		if(vy > 4)
			vy = 4;
		if(vy < -4)
			vy = -4;

		if(isClimbing)
			vx = 0;

		// reset the fields
		this.isOnGround	= false;
		// and call collision check
		collisionCheck(this.vx, this.vy);

		if(isClimbing)
		{
			if(!canClimb)
				isClimbing = false;
		}

		if(this.isOnGround)
		{
//			vx = 0;
			if(vx > 0)
				vx-= 0.1;
			else if(vx < 0)
				vx+= 0.1;
		}
//		else if(this.isClimbing)
//		{
//			vx = 0;
//		}
		else
		{
			if(vx > 0)
			{
				if(vx <= 0.03)
					vx = 0;
				else
					vx-= 0.03;
			}
			else if(vx < 0)
			{
				if(vx >= -0.03)
					vx = 0;
				else
					vx+= 0.03;
			}
		}

		if(this.isClimbing)
		{
			vy = 0;
		}
		else
		{
			if(vy > 0)
			{
				if(vy <= 0.03)
					vy = 0;
				else
					vy-= 0.03;
			}
			else if(vy < 0)
			{
				if(vy >= -0.03)
					vy = 0;
				else
					vy+= 0.03;
			}
		}

		if(abs(vx) < 0.1)
			vx = 0;
		if(abs(vy) < 0.1)
			vy = 0;

		// Ugly Hack! TODO: Move it somewhere else.
		if(this.getName().equals("swoosh") && vx == 0)
		{
			super.setRemoval(true);
		}
	}
}
