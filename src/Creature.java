import java.util.ArrayList;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;

/**
 * This class extends GameObject class to provide additional logic for mobile creatures.
 */
public class Creature extends GameObject
{
	protected float vx;
	protected float vy;

	private AI ai;

	protected boolean isOnGround;
	protected boolean isCrouching;
	protected boolean isClimbing;
	protected boolean canClimb;
	private boolean canWalk;
	private int climbX;
	private int climbY;
	protected boolean affectedByGravity = true;

	protected int hp;

	protected boolean hurt = false;
	protected boolean hurtOnGround = false;

	protected LevelLayer levelLayer;	// the middle-layer of the level
	protected Collision collision;		// reference to collision map passed by the Level object
	protected Level level;			// Current level. We use that to be able to spawn new objects within the current object. Ugly!

	public Creature(LevelLayer lay, Collision col, Level lev)
	{
		this.collision = col;
		this.levelLayer = lay;
		this.level = lev;

		this.canWalk = true;
	}

	/**
	 * Overrides the load() method from the GameObject superclass.
	 * This method invokes load() from the superclass to set up a new game object, then loads specific creature features.
	 */
	public void load(String fileName, int w, int h, int rowW, int size, ArrayList <GameObjectTemplate> tempList)
	{
		super.load(fileName, w, h, rowW, size, tempList);
		loadAI();
	}

	/**
	 * Loads predefined AI patterns for the given creature type.
	 */
	public void loadAI()
	{
		this.ai = new AI();

		// Set the list of AI actions based on the creature type.
		// Combinations of multiple actions per creature are allowed.
		// addAction() - sets the AI action type.
		// setVar() - sets parameters for the current action (ie. walk speed, jump height).
		if (this.getName().equals("jumper"))
		{
			ai.addAction(AI.JUMP);
			ai.setVar(AI.JUMP_VX, 1.5f);
			ai.setVar(AI.JUMP_VY, 4.0f);
		}
		else if (this.getName().equals("badass"))
		{
			ai.addAction(AI.WALK);
			ai.setVar(AI.WALK_VX, 0.5f);
			ai.setVar(AI.WALK_DROP, 1f);
		}
		else if (this.getName().equals("swoosh"))
		{
			ai.addAction(AI.WALK);
			ai.setVar(AI.WALK_VX, 5f);
		}
		else if (this.getName().equals("dragonfly"))
		{
			ai.addAction(AI.FLY, 120);
			ai.setVar(AI.FLY_VX, 0.75f);
			ai.setVar(AI.FLY_AMPLITUDE, 1f);
			ai.setVar(AI.FLY_PERIOD, 5f);
			ai.addAction(AI.TURN);
		}
		else if (this.getName().equals("runner"))
		{
			ai.addAction(AI.SLEEP);
			ai.setVar(AI.RANGE_X, 88);
			ai.setVar(AI.RANGE_Y, 40);
			ai.addAction(AI.TURN);
			ai.setVar(AI.TURN_TOWARDS_PLAYER, 1f);
			ai.addAction(AI.WALK);
			ai.setVar(AI.WALK_VX, 1.25f);
			ai.setVar(AI.WALK_DROP, 0f);
			ai.addAction(AI.JUMP_IN_RANGE);
			ai.setVar(AI.RANGE_X, 48);
			ai.setVar(AI.RANGE_JUMP_VX, 2.0f);
			ai.setVar(AI.RANGE_JUMP_VY, 4.0f);
		}
		else if (this.getName().equals("flower"))
		{
			ai.addAction(AI.WAIT, 180);
			ai.addAction(AI.SPAWN_OBJ);
			ai.setVar("spikeball");
			ai.setVar(AI.SPAWN_OBJ_OBJVX, -1.25f);
			ai.setVar(AI.SPAWN_OBJ_OBJVY, -2.25f);
			ai.addAction(AI.WAIT, 30);
			ai.addAction(AI.SPAWN_OBJ);
			ai.setVar("spikeball");
			ai.setVar(AI.SPAWN_OBJ_OBJVX, 1.25f);
			ai.setVar(AI.SPAWN_OBJ_OBJVY, -2.25f);

			// Temporary hack.
			this.affectedByGravity = false;
		}

		ai.resetActions();
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

	public void addHp(int value)
	{
		this.hp += value;
	}

	public void hurt(boolean direction)
	{
		this.hurt = true;
		this.hp--;
		this.setVulnerability(false);
		this.vx = (direction ? 2 : -2);
		this.vy = -5;

		if (!this.affectedByGravity && hp <= 0)
		{
			this.affectedByGravity = true;
		}
	}

	public boolean isHurt()
	{
		return this.hurt;
	}

	/**
	 * Makes the creature crouch.
	 * If climbCheck() is true, this method will cause the creature to climb down.
	 */
	public void tryCrouch()
	{
		climbCheck(true);
		if (canClimb)
			this.isClimbing = true;
		else
		{
			if (this.isOnGround)
				this.isCrouching = true;
		}
	}

	/**
	 * Makes the creature climb.
	 */
	public void tryClimb()
	{
		climbCheck(false);
		if (canClimb)
			this.isClimbing = true;
	}

	/**
	 * Moves the creature vertically along a climbable tile (ie. a ladder).
	 */
	public void climb(float vy)
	{
		if (this.isClimbing)
			this.vy = vy;
	}

	/**
	 * Makes the creature jump.
	 * If the creature is currently climing, this method will cause the creature to drop from the climbable tile.
	 */
	public void jump(float vy)
	{
		if (this.isOnGround)
			this.vy = vy;
		if (this.isClimbing)
		{
			this.isClimbing = false;
			this.vy = vy; //vy/2;
		}
	}

	/**
	 * Moves the creature horizontally.
	 */
	public void walk(float vx)
	{
		this.vx = vx;
	}

	/**
	 * Movement equivalent for airborne creatures.
	 * This method allows an airborne creature to move in both horizontal and vertical plane.
	 */
	public void fly(float vx, float vy)
	{
		this.vx = vx;
		this.vy = vy;
	}

	/**
	 * Handles AI logic.
	 */
	public void doAi()
	{
		if (!this.ai.hasActions()) // No need to perform the rest of the method if the object has no AI actions.
			return;

		if (this.hurt)
		{
			return;
		}

		// Check AI action type and perform appropriate logic.
		switch (this.ai.getType())
		{
			// Idle.
			case AI.WAIT:
				this.ai.doTimer();

				if (this.ai.getTimer() <= 0)
					this.ai.setNextAction();
			break;
			// Walk.
			case AI.WALK:
				this.ai.doTimer();

				// Move forward.
				if (this.canWalk)
				{
					if (!this.direction)	// left
						this.walk(-this.ai.getVar(AI.WALK_VX));
					else			// right
						this.walk(this.ai.getVar(AI.WALK_VX));
				}
				else
				{
					// Check if there is a tile ahead to walk on.
					dropCheck(this.direction);
				}

				if (this.ai.getTimer() <= 0)
					this.ai.setNextAction();
			break;
			// Jump.
			case AI.JUMP:
				// If creature is on the ground, jump.
				if (this.isOnGround)
				{
					if (this.ai.isGoToNextAction())
					{
						this.ai.setNextAction();
						break;
					}

					this.jump(-this.ai.getVar(AI.JUMP_VY));
					if (!this.direction)	// left
					{
						this.walk(-this.ai.getVar(AI.JUMP_VX));
					}
					else			// right
					{
						this.walk(this.ai.getVar(AI.JUMP_VX));
					}

					this.ai.setGoToNextAction(true);
				}
			break;
			// Fly.
			case AI.FLY:
				this.ai.doTimer();

				// Creature is supposed to fly in a straight line - don't bother with calculating the sine position.
				if (this.ai.getVar(AI.FLY_AMPLITUDE) == 0)
				{
					this.fly((this.direction ? this.ai.getVar(AI.FLY_VX) : -this.ai.getVar(AI.FLY_VX)), 0f);
				}
				// Creature flies in a sine pattern.
				else
				{
					// Increase the sine period.
					this.ai.increaseSinePeriod((int)this.ai.getVar(AI.FLY_PERIOD));

					// Set the creature velocity.
					// The vertical velocity is set according to the sine table (SineTable.java), based on the current return value of getSinePeriod().
					this.fly((this.direction ? this.ai.getVar(AI.FLY_VX) : -this.ai.getVar(AI.FLY_VX)), SineTable.TABLE[this.ai.getSinePeriod()]*this.ai.getVar(AI.FLY_AMPLITUDE));
				}

				this.affectedByGravity = false;

				if (this.ai.getTimer() <= 0)
					this.ai.setNextAction();
			break;
			// Create new objects (ie. creature that shoots bullets)
			case AI.SPAWN_OBJ:
				// List of objects that will be added to the game in the next iteration of the logic() loop.
				ArrayList<GameObject> newObjs = this.level.getNewObjs();

				try
				{
					// Load the new game object.
					this.level.loadSingleObject(this.ai.getObjName(), newObjs);
				}
				catch (Exception e)
				{
					System.out.printf("Failed to load object file: %s.obj\n", this.ai.getObjName());
					return;
				}

				Creature obj = (Creature)newObjs.get(newObjs.size() - 1);

				// Place the newly created object in the vicinity of the creature which spawns it.
				obj.putX((int)(this.x + (this.w - 1)/2 - (obj.getW() - 1)/2));
				obj.putY((int)this.y);
				obj.setVx(this.ai.getVar(AI.SPAWN_OBJ_OBJVX));
				obj.setVy(this.ai.getVar(AI.SPAWN_OBJ_OBJVY));
//				obj.putDirection(this.direction ? 1 : 0);
				obj.putDirection(obj.getVx() != 0 ? (obj.getVx() > 0 ? 1 : 0) : (this.direction ? 1 : 0));
				obj.affectedByGravity = true;

				this.ai.setNextAction();
			break;
			// Change direction.
			case AI.TURN:
				if (this.ai.getVar(AI.TURN_TOWARDS_PLAYER) != 0)
				{
					Player playerObj = level.getPlayer();

					if ((this.x + this.w) < playerObj.x)
					{
						this.direction = true; // right
					}
					else if (this.x > (playerObj.x + playerObj.w))
					{
						this.direction = false; // left
					}
				}
				else
				{
					this.direction = !this.direction;
				}

				this.ai.setNextAction();
			break;
			// Sleep.
			case AI.SLEEP:
			{
				Player playerObj = level.getPlayer();

				int x1 = (int)(this.x + this.w/2 - this.ai.getVar(AI.RANGE_X));
				int x2 = (int)(this.x + this.w/2 + this.ai.getVar(AI.RANGE_X));
				int y1 = (int)(this.y + this.h/2 - this.ai.getVar(AI.RANGE_Y));
				int y2 = (int)(this.y + this.h);

				int pX = (int)(playerObj.x + playerObj.w/2);
				int pY = (int)(playerObj.y + playerObj.h/2);

				// Player is within range. Wake up!
				if (!playerObj.isDead() && pX >= x1 && pX <= x2 && pY >= y1 && pY <= y2)
				{
					this.ai.setNextAction();
				}
			}
			break;

			// Jump while in range.
			case AI.JUMP_IN_RANGE:
			{
				Player playerObj = level.getPlayer();
				int x1 = (int)(this.x + this.w/2 - this.ai.getVar(AI.RANGE_X));
				int x2 = (int)(this.x + this.w/2 + this.ai.getVar(AI.RANGE_X));

				int pX = (int)(playerObj.x + playerObj.w/2);

				// Player is within range. Attempt to jump.
				if (pX >= x1 && pX <= x2)
				{
					// If creature is on the ground, jump.
					if (this.isOnGround)
					{
						if (this.ai.isGoToNextAction())
						{
							this.ai.setNextAction();
							break;
						}

						this.jump(-this.ai.getVar(AI.RANGE_JUMP_VY));
						if (!this.direction)	// left
						{
							this.walk(-this.ai.getVar(AI.RANGE_JUMP_VX));
						}
						else			// right
						{
							this.walk(this.ai.getVar(AI.RANGE_JUMP_VX));
						}

						this.ai.setGoToNextAction(true);
					}
				}
				else
				{
					this.ai.setNextAction();
				}
			}
			break;

			default:
			break;
		}
	}

	/**
	 * Checks if a tile in front of the creature is walkable.
	 * This method is used to detect gaps in the tiles for walkable creatures that are expected to stay on a single horizontal set of tiles (and not jump down).
	 */
	private void dropCheck(boolean direction)
	{
		int col = Collision.COLLISION_NONE;
		int tile;

		int x;
		int y = (int)(this.y) + this.h;

		if (!this.isOnGround)
			return;

		if (direction)	// right
		{
			// .---.
			// |   |
			// .---.
			//      #

			x = (int)(this.x) + this.w;
		}
		else		// left
		{
			//  .---.
			//  |   |
			//  .---.
			// #

			x = (int)(this.x) - 1;
		}

		tile = levelLayer.getTile(x/LevelLayer.TILE_SIZE, y/LevelLayer.TILE_SIZE);
		col = col | collision.getCollision(tile);

		if (col == Collision.COLLISION_NONE)
		{
			this.vx = 0;

			// Check the opposite direction to see if there is space for turning and walking
			if (direction)
			{
				//  .---.
				//  |   |
				//  .---.
				// #

				x = (int)(this.x) - 1;
			}
			else
			{
				// .---.
				// |   |
				// .---.
				//      #

				x = (int)(this.x) + this.w;
			}

			tile = levelLayer.getTile(x/LevelLayer.TILE_SIZE, y/LevelLayer.TILE_SIZE);
			col = col | collision.getCollision(tile);

			if (col != Collision.COLLISION_NONE)
			{
				this.direction = !this.direction;
				this.canWalk = true;
			}
			else
			{
				this.canWalk = false;
			}
		}
		else
		{
			this.canWalk = true;
		}
	}

	/**
	 * Checks if a climbable surface (ie. a ladder) is located below or above the creature.
	 */
	private void climbCheck(boolean direction)
	{
		int col = Collision.COLLISION_NONE;
		int tile;

		int x1 = (int)(this.x);
		int x2 = (int)(this.x) + this.w - 1;
		int y1;

		if (direction) // down
		{
			// .---.
			// |   |
			// #####

			y1 = (int)(this.y) + 1 + this.h - 1;

			if (x1 < 0)
				x1 = -LevelLayer.TILE_SIZE;

			for (int i = (x1 + 6)/LevelLayer.TILE_SIZE; i <= (x2 - 6)/LevelLayer.TILE_SIZE; i++)
			{
				tile = levelLayer.getTile(i, y1/LevelLayer.TILE_SIZE);
				col = col | collision.getCollision(tile);

				if ((col & Collision.COLLISION_CLIMB) > 0)
				{
					{
						this.canClimb = true;
						this.climbX = i * LevelLayer.TILE_SIZE;
						break;
					}
				}
			}
		}
		else // up
		{
			// #####
			// |   |
			// .---.

			y1 = (int)(this.y) - 1;

			if (x1 < 0)
				x1 = -LevelLayer.TILE_SIZE;
			if (y1 < 0)
				y1 = -LevelLayer.TILE_SIZE;

			for (int i = (x1 + 6)/LevelLayer.TILE_SIZE; i <= (x2 - 6)/LevelLayer.TILE_SIZE; i++)
			{
				tile = levelLayer.getTile(i, y1/LevelLayer.TILE_SIZE);
				col = col | collision.getCollision(tile);

				if ((col & Collision.COLLISION_CLIMB) > 0)
				{
					{
						this.canClimb = true;
						this.climbX = i * LevelLayer.TILE_SIZE;
						break;
					}
				}
			}
		}
	}

	/**
	 * Performs a collision check against the level layer tiles.
	 * This method is called recursively for x or y velocities greater than 1 pixel/frame (< -1f or > 1f).
	 */
	private void collisionCheck(float totalVx, float totalVy)
	{
		int col;		// Holds the collision type bitfield.
		int tmpCol;		// Holds the collision type bitfield for a single tile check.
		boolean remTile;	// Tile removal information for destructible tile types.
		float curVx;		// Amount of vx used for calculation in the current iteration.
		float curVy;		// Amount of vy used for calculation in the current iteration.
		float remVx = 0;	// Amount of remaining vx for use in next iterations.
		float remVy = 0;	// Amount of remaining vy for use in next iterations.
		int tile;		// Tile type value.
		int x1;			// Helper variable used for calculating the change in creature collision box after velocity is applied.
		int x2;			// Helper variable used for calculating the change in creature collision box after velocity is applied.
		int y1;			// Helper variable used for calculating the change in creature collision box after velocity is applied.
		int y2;			// Helper variable used for calculating the change in creature collision box after velocity is applied.


		// If the total velocity is greater than 1px/frame, calculate only 1px movement
		// and leave the remaining part for a recursive call to collisionCheck().
		// This is done to accurately calculate collision of fast moving objects.
		if (abs(totalVx) > 1f)
		{
			if (totalVx > 0)
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
		if (abs(totalVy) > 1f)
		{
			if (totalVy > 0)
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

		if (this.vx == 0)
			curVx = 0;
		if (this.vy == 0)
			curVy = 0;

		// Check collision for y axis.
		if (curVy > 0) // If creature is moving downwards.
		{
			// .---.
			// |   |
			// #####

			// Set the collision check area according to the pictogram above (### line).
			x1 = (int)(this.x);
			x2 = (int)(this.x) + this.w - 1;
			y1 = (int)(curVy + this.y) + this.h - 1;

			if (x1 < 0)
				x1 = -LevelLayer.TILE_SIZE;

			col = Collision.COLLISION_NONE;
			remTile = true;

			// Perform collision check for all the level tiles located within the collision area.
			for (int i = x1/LevelLayer.TILE_SIZE; i <= x2/LevelLayer.TILE_SIZE; i++)
			{
				// Fetch the tile type.
				tile = levelLayer.getTile(i, y1/LevelLayer.TILE_SIZE);
				// Obtain collision type information from the tile type.
				tmpCol = collision.getCollision(tile);

				// If creature is a projectile...
				if (this.getName().equals("swoosh"))
				{
					// ...and the tile is destructible - remove the tile.
					if ((tmpCol & Collision.COLLISION_DESTRUCTIBLE) > 0 && remTile)
					{
						levelLayer.setTile(i, y1/LevelLayer.TILE_SIZE, 0);
						remTile = false;
					}
					// ...and the tile is hidden - replace it with an uncovered tile.
					if ((tmpCol & Collision.COLLISION_HIDDEN) > 0)
					{
						levelLayer.setTile(i, y1/LevelLayer.TILE_SIZE, 79);
					}
				}

				// Accumulate the collision information.
				col = col | tmpCol;
			}

			// Collision with a solid tile (wall, floor, ceiling, etc.).
			if ((col & Collision.COLLISION_SOLID) > 0)
			{
				this.vy = 0;
				this.y = (float)ceil(this.y) - 0.2f;
				this.isOnGround = true;

				if (this instanceof Projectile)
				{
					Projectile projectile = (Projectile)this;
					switch (projectile.getOnCollision())
					{
						case PERISH:
							projectile.setRemoval(true);
						break;

						default:
						break;
					}
				}
			}
			// Collision with a platform tile (walkable on the top part, but creature will go past if moving through it from the bottom).
			else if ((col & Collision.COLLISION_PLATFORM) > 0)
			{
				// Check if collision occurs with the top part.
				if (((int)this.y + this.h - 1)/LevelLayer.TILE_SIZE*LevelLayer.TILE_SIZE < (y1)/LevelLayer.TILE_SIZE*LevelLayer.TILE_SIZE && !((col & Collision.COLLISION_PLATFORM) > 0 && isClimbing))
				{
					this.vy = 0;
					this.y = (float)ceil(this.y) - 0.2f;
					this.isOnGround = true;

					if (this instanceof Projectile)
					{
						Projectile projectile = (Projectile)this;
						switch (projectile.getOnCollision())
						{
							case PERISH:
								projectile.setRemoval(true);
							break;

							default:
							break;
						}
					}
				}
				else
					this.y += curVy;
			}

			// If no collision occured, creature is free to continue its movement.
			else
			{
				this.y += curVy;
			}

			if ((col & Collision.COLLISION_DAMAGE) > 0)
			{
				if (this instanceof Player)
				{
					if (((int)this.y + this.h - 1) >= (y1)/LevelLayer.TILE_SIZE*LevelLayer.TILE_SIZE + LevelLayer.TILE_SIZE/2)
					{
						this.setHp(0);

						if (!this.hurt)
						{
							this.hurt((curVx != 0 ? (curVx > 0 ? false : true) : (this.direction ? true : false)));
						}
					}
				}

				// Code for doing regular damage.
//				if (this instanceof Player && !this.hurt)
//				{
//					if (((int)this.y + this.h - 1) >= (y1)/LevelLayer.TILE_SIZE*LevelLayer.TILE_SIZE + LevelLayer.TILE_SIZE/2)
//					{
//						this.hurt((curVx != 0 ? (curVx > 0 ? false : true) : (this.direction ? true : false)));
//					}
//				}
			}

			// Special collision check with a different collision area for climbable tiles (ie. ladder).
			col = Collision.COLLISION_NONE;
			for (int i = (x1 + 6)/LevelLayer.TILE_SIZE; i <= (x2 - 6)/LevelLayer.TILE_SIZE; i++)
			{
				tile = levelLayer.getTile(i, y1/LevelLayer.TILE_SIZE);
				col = col | collision.getCollision(tile);

				if ((col & Collision.COLLISION_CLIMB) > 0)
				{
					{
						this.canClimb = true;
						this.climbX = i * LevelLayer.TILE_SIZE;
						break;
					}
				}
			}
		}
		else if (curVy < 0) // If creature is moving upwards.
		{
			// #####
			// |   |
			// .---.

			// Set the collision check area according to the pictogram above (### line).
			x1 = (int)(this.x);
			x2 = (int)(this.x) + this.w - 1;
			y1 = (int)(curVy + this.y);

			if (x1 < 0)
				x1 = -LevelLayer.TILE_SIZE;
			if (y1 < 0)
				y1 = -LevelLayer.TILE_SIZE;

			col = Collision.COLLISION_NONE;
			remTile = true;

			// Perform collision check for all the level tiles located within the collision area.
			for (int i = x1/LevelLayer.TILE_SIZE; i <= x2/LevelLayer.TILE_SIZE; i++)
			{
				// Fetch the tile type.
				tile = levelLayer.getTile(i, y1/LevelLayer.TILE_SIZE);
				// Obtain collision type information from the tile type.
				tmpCol = collision.getCollision(tile);

				// If creature is a projectile...
				if (this.getName().equals("swoosh"))
				{
					// ...and the tile is destructible - remove the tile.
					if ((tmpCol & Collision.COLLISION_DESTRUCTIBLE) > 0 && remTile)
					{
						levelLayer.setTile(i, y1/LevelLayer.TILE_SIZE, 0);
						remTile = false;
					}
					// ...and the tile is hidden - replace it with an uncovered tile.
					if ((tmpCol & Collision.COLLISION_HIDDEN) > 0)
					{
						levelLayer.setTile(i, y1/LevelLayer.TILE_SIZE, 79);
					}
				}

				// Accumulate the collision information.
				col = col | tmpCol;
			}

			// Collision with a solid tile (wall, floor, ceiling, etc.).
			if ((col & Collision.COLLISION_SOLID) > 0)
			{
				this.vy = 0;

				if (this instanceof Projectile)
				{
					Projectile projectile = (Projectile)this;
					switch (projectile.getOnCollision())
					{
						case PERISH:
							projectile.setRemoval(true);
						break;

						default:
						break;
					}
				}
			}
			// If no collision occured, creature is free to continue its movement.
			else
			{
				this.y += curVy;
			}

			if ((col & Collision.COLLISION_DAMAGE) > 0)
			{
				if (this instanceof Player)
				{
					if (((int)this.y) <= (y1)/LevelLayer.TILE_SIZE*LevelLayer.TILE_SIZE + LevelLayer.TILE_SIZE)
					{
						this.setHp(0);

						if (!this.hurt)
						{
							this.hurt((curVx != 0 ? (curVx > 0 ? false : true) : (this.direction ? true : false)));
						}
					}
				}

				// Code for doing regular damage.
//				if (this instanceof Player && !this.hurt)
//				{
//					if (((int)this.y) <= (y1)/LevelLayer.TILE_SIZE*LevelLayer.TILE_SIZE + LevelLayer.TILE_SIZE)
//					{
//						this.hurt((curVx != 0 ? (curVx > 0 ? false : true) : (this.direction ? true : false)));
//					}
//				}
			}

			// Special collision check with a different collision area for climbable tiles (ie. ladder).
			col = Collision.COLLISION_NONE;
			for (int i = (x1 + 6)/LevelLayer.TILE_SIZE; i <= (x2 - 6)/LevelLayer.TILE_SIZE; i++)
			{
				tile = levelLayer.getTile(i, y1/LevelLayer.TILE_SIZE);
				col = col | collision.getCollision(tile);

				if ((col & Collision.COLLISION_CLIMB) > 0)
				{
					{
						this.canClimb = true;
						this.climbX = i * LevelLayer.TILE_SIZE;
						break;
					}
				}
			}

		}

		// Check collision for x axis.
		if (curVx > 0) // If creature is moving rightwards.
		{
			// .---#
			// |   #
			// .---#

			// Set the collision check area according to the pictogram above (### line).
			x1 = (int)(curVx + this.x) + this.w - 1;
			y1 = (int)(this.y);
			y2 = (int)(this.y) + this.h - 1;

			if (y1 < 0)
				y1 = -LevelLayer.TILE_SIZE;
			if (y2 < 0)
				y2 = -LevelLayer.TILE_SIZE;

			col = Collision.COLLISION_NONE;
			remTile = true;
			// Perform collision check for all the level tiles located within the collision area.
			for (int i = y1/LevelLayer.TILE_SIZE; i <= y2/LevelLayer.TILE_SIZE; i++)
			{
				// Fetch the tile type.
				tile = levelLayer.getTile(x1/LevelLayer.TILE_SIZE, i);
				// Obtain collision type information from the tile type.
				tmpCol = collision.getCollision(tile);

				// If creature is a projectile...
				if (this.getName().equals("swoosh"))
				{
					// ...and the tile is destructible - remove the tile.
					if ((tmpCol & Collision.COLLISION_DESTRUCTIBLE) > 0 && remTile)
					{
						levelLayer.setTile(x1/LevelLayer.TILE_SIZE, i, 0);
						remTile = false;
					}
					// ...and the tile is hidden - replace it with an uncovered tile.
					if ((tmpCol & Collision.COLLISION_HIDDEN) > 0)
					{
						levelLayer.setTile(x1/LevelLayer.TILE_SIZE, i, 79);
					}
				}

				// Accumulate the collision information.
				col = col | tmpCol;
			}

			// Collision with a solid tile (wall, floor, ceiling, etc.).
			if ((col & Collision.COLLISION_SOLID) > 0)
			{
				this.vx = 0;

				if (!(this instanceof Player) && !hurt)
				{
					this.direction = !this.direction;
				}

				if (this instanceof Projectile)
				{
					Projectile projectile = (Projectile)this;
					switch (projectile.getOnCollision())
					{
						case PERISH:
							projectile.setRemoval(true);
						break;

						default:
						break;
					}
				}
			}
			// If no collision occured, creature is free to continue its movement.
			else
			{
				this.x += curVx;
			}

			if ((col & Collision.COLLISION_DAMAGE) > 0)
			{
				if (this instanceof Player)
				{
					if (((int)this.y + this.h - 1) >= (y2)/LevelLayer.TILE_SIZE*LevelLayer.TILE_SIZE + LevelLayer.TILE_SIZE/2)
					{
						this.setHp(0);

						if (!this.hurt)
						{
							this.hurt((curVx != 0 ? (curVx > 0 ? false : true) : (this.direction ? true : false)));
						}
					}
				}

				// Code for doing regular damage.
//				if (this instanceof Player && !this.hurt)
//				{
//					if (((int)this.y + this.h - 1) >= (y2)/LevelLayer.TILE_SIZE*LevelLayer.TILE_SIZE + LevelLayer.TILE_SIZE/2)
//					{
//						this.hurt((curVx != 0 ? (curVx > 0 ? false : true) : (this.direction ? true : false)));
//					}
//				}
			}

			// For creatures with AI that turns on platform edges, perform additional check for walkable space.
			if (!(this instanceof Player) && !hurt && this.ai.getType() == AI.WALK && this.ai.getVar(AI.WALK_DROP) != 0f)
			{
				dropCheck(true);
			}
		}
		else if (curVx < 0) // If creature is moving leftwards.
		{
			// #---.
			// #   |
			// #---.

			// Set the collision check area according to the pictogram above (### line).
			x1 = (int)(curVx + this.x);
			y1 = (int)(this.y);
			y2 = (int)(this.y) + this.h - 1;

			if (x1 < 0)
				x1 = -LevelLayer.TILE_SIZE;
			if (y1 < 0)
				y1 = -LevelLayer.TILE_SIZE;
			if (y2 < 0)
				y2 = -LevelLayer.TILE_SIZE;

			col = Collision.COLLISION_NONE;
			remTile = true;
			// Perform collision check for all the level tiles located within the collision area.
			for (int i = y1/LevelLayer.TILE_SIZE; i <= y2/LevelLayer.TILE_SIZE; i++)
			{
				// Fetch the tile type.
				tile = levelLayer.getTile(x1/LevelLayer.TILE_SIZE, i);
				// Obtain collision type information from the tile type.
				tmpCol = collision.getCollision(tile);

				// If creature is a projectile...
				if (this.getName().equals("swoosh"))
				{
					// ...and the tile is destructible - remove the tile.
					if ((tmpCol & Collision.COLLISION_DESTRUCTIBLE) > 0 && remTile)
					{
						levelLayer.setTile(x1/LevelLayer.TILE_SIZE, i, 0);
						remTile = false;
					}
					// ...and the tile is hidden - replace it with an uncovered tile.
					if ((tmpCol & Collision.COLLISION_HIDDEN) > 0)
					{
						levelLayer.setTile(x1/LevelLayer.TILE_SIZE, i, 79);
					}
				}

				// Accumulate the collision information.
				col = col | tmpCol;
			}

			// Collision with a solid tile (wall, floor, ceiling, etc.).
			if ((col & Collision.COLLISION_SOLID) > 0)
			{
				this.vx = 0;

				if (!(this instanceof Player) && !hurt)
				{
					this.direction = !this.direction;
				}

				if (this instanceof Projectile)
				{
					Projectile projectile = (Projectile)this;
					switch (projectile.getOnCollision())
					{
						case PERISH:
							projectile.setRemoval(true);
						break;

						default:
						break;
					}
				}
			}
			// If no collision occured, creature is free to continue its movement.
			else
			{
				this.x += curVx;
			}

			if ((col & Collision.COLLISION_DAMAGE) > 0)
			{
				if (this instanceof Player)
				{
					if (((int)this.y + this.h - 1) >= (y2)/LevelLayer.TILE_SIZE*LevelLayer.TILE_SIZE + LevelLayer.TILE_SIZE/2)
					{
						this.setHp(0);

						if (!this.hurt)
						{
							this.hurt((curVx != 0 ? (curVx > 0 ? false : true) : (this.direction ? true : false)));
						}
					}
				}

				// Code for doing regular damage.
//				if (this instanceof Player && !this.hurt)
//				{
//					if (((int)this.y + this.h - 1) >= (y2)/LevelLayer.TILE_SIZE*LevelLayer.TILE_SIZE + LevelLayer.TILE_SIZE/2)
//					{
//						this.hurt((curVx != 0 ? (curVx > 0 ? false : true) : (this.direction ? true : false)));
//					}
//				}
			}

			// For creatures with AI that turns on platform edges, perform additional check for walkable space.
			if (!(this instanceof Player) && !hurt && this.ai.getType() == AI.WALK && this.ai.getVar(AI.WALK_DROP) != 0f)
			{
				dropCheck(false);
			}
		}

		// Recursively call the collisionCheck() if there is any remaining velocity to take into account.
		if (remVx != 0 || remVy != 0)
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
//		if (this.x + this.vx < 0)
//		{
//			this.vx = 0;
//			this.x = 0;
//		}
//		if (((int)(this.x + this.vx) + this.w - 1)/LevelLayer.TILE_SIZE >= levelLayer.getWidth())
//		{
//			this.vx = 0;
//			this.x = levelLayer.getWidth() * LevelLayer.TILE_SIZE - this.w;
//		}
//		if (this.y + this.vy < 0)
//		{
//			this.vy = 0;
//			this.y = 0;
//		}
//		if (((int)(this.y + this.vy) + this.h)/LevelLayer.TILE_SIZE >= levelLayer.getHeight())
//		{
//			this.vy = 0;
//			this.y = levelLayer.getHeight() * LevelLayer.TILE_SIZE - this.h;
//		}

//		// check y
//		if (this.vy > 0)
//		{
//			// .---.
//			// |   |
//			// #####

//			x1 = (int)(this.x);
//			x2 = (int)(this.x) + this.w - 1;
//			y1 = (int)(this.vy + this.y) + this.h - 1;

//			tile = levelLayer.getTile(x1/LevelLayer.TILE_SIZE, y1/LevelLayer.TILE_SIZE);
//			tile2 = levelLayer.getTile(x2/LevelLayer.TILE_SIZE, y1/LevelLayer.TILE_SIZE);

//			if (collision.getCollision(tile) == 0 && collision.getCollision(tile2) == 0)
//			{
//				this.y += this.vy;
//			}
//			else
//			{
//				this.vy = 0;
//			}
//		}
//		else if (this.vy < 0)
//		{
//			// #####
//			// |   |
//			// .---.

//			x1 = (int)(this.x);
//			x2 = (int)(this.x) + this.w - 1;
//			y1 = (int)(this.vy + this.y);

//			tile = levelLayer.getTile(x1/LevelLayer.TILE_SIZE, y1/LevelLayer.TILE_SIZE);
//			tile2 = levelLayer.getTile(x2/LevelLayer.TILE_SIZE, y1/LevelLayer.TILE_SIZE);

//			if (collision.getCollision(tile) == 0 && collision.getCollision(tile2) == 0)
//			{
//				this.y += this.vy;
//			}
//			else
//			{
//				this.vy = 0;
//			}
//		}

//		// check x
//		if (this.vx > 0)
//		{
//			// .---#
//			// |   #
//			// .---#

//			x1 = (int)(this.vx + this.x) + this.w - 1;
//			y1 = (int)(this.y);
//			y2 = (int)(this.y) + this.h - 1;

//			tile = levelLayer.getTile(x1/LevelLayer.TILE_SIZE, y1/LevelLayer.TILE_SIZE);
//			tile2 = levelLayer.getTile(x1/LevelLayer.TILE_SIZE, y2/LevelLayer.TILE_SIZE);

//			if (collision.getCollision(tile) == 0 && collision.getCollision(tile2) == 0)
//			{
//				this.x += this.vx;
//			}
//			else
//			{
//				this.vx = 0;
//			}

//		}
//		else if (this.vx < 0)
//		{
//			// #---.
//			// #   |
//			// #---.

//			x1 = (int)(this.vx + this.x);
//			y1 = (int)(this.y);
//			y2 = (int)(this.y) + this.h - 1;

//			tile = levelLayer.getTile(x1/LevelLayer.TILE_SIZE, y1/LevelLayer.TILE_SIZE);
//			tile2 = levelLayer.getTile(x1/LevelLayer.TILE_SIZE, y2/LevelLayer.TILE_SIZE);

//			if (collision.getCollision(tile) == 0 && collision.getCollision(tile2) == 0)
//			{
//				this.x += this.vx;
//			}
//			else
//			{
//				this.vx = 0;
//			}
//		}
//	}

	/**
	 * Handles creature related logic.
	 * This method overloads logic() from the GameObject superclass to perform actions for special types of creatures.
	 */
	public void logic()
	{
		super.logic();

		if (this instanceof Projectile)
		{
			if (((Projectile)this).getTtl() <= 0)
			{
				this.setRemoval(true);
			}
			((Projectile)this).ttlCountdown();
		}

		if (hurt)
		{
			if (isOnGround && !hurtOnGround)
			{
				hurtOnGround = true;

				if (this instanceof Player)
				{
					setInvincibility(90);
					setBlinking(90);
				}
				else
				{
					setInvincibility(30);
					setBlinking(30);
				}
			}

			if (hurtOnGround)
			{
				if (this instanceof Player)
				{
					if (hp <= 0)
					{
						if (!((Player)this).isDead())
							((Player)this).setDead(true);
					}
					else
					{
						((Player)this).setAcceptInput(true);
					}
				}

				if (!this.isBlinking())
				{
					this.hurt = false;
					this.hurtOnGround = false;
					this.setVulnerability(true);

					if (!(this instanceof Player) && hp <= 0)
					{
						this.setRemoval(true);
					}
				}
			}
		}
	}

	/**
	 * Updates creature animation.
	 */
	public void updateAnimation()
	{
		String newAnim = null;
		Animation curAnim = this.getAnimation();

		if (vx != 0)
		{
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

		if (hurt)
		{
			newAnim = "HURT";
		}

		if (newAnim != null)
		{
			this.changeAnimation(newAnim);
		}
	}

	/**
	 * Updates the creature position on the level.
	 */
	public void move()
	{
		if (this.affectedByGravity && !this.isClimbing)
		{
			vy += 0.2; // gravity
		}

		if (vx > 0)
		{
			if (this instanceof Player)
			{
				if (((Player)this).getAction(1) && ((Player)this).acceptInput()) // right
					this.direction = true;
			}
		}
		else if (vx < 0)
		{
			if (this instanceof Player)
			{
				if (((Player)this).getAction(0) && ((Player)this).acceptInput()) // left
					this.direction = false;
			}
		}

		updateAnimation();

		if (vx > 3)
			vx = 3;
		if (vx < -3)
			vx = -3;
		if (vy > 4)
			vy = 4;
		if (vy < -5)
			vy = -5;

		if (isClimbing)
		{
			vx = 0;
		}

		// reset the fields
		this.isOnGround	= false;
		if (vy != 0)
		{
			this.canClimb = false;
		}
		this.isCrouching = false;
		// and call collision check
		collisionCheck(this.vx, this.vy);

		if (isClimbing)
		{
			if (!canClimb)
			{
				y -= vy;
			}
		}

		if (this.isOnGround)
		{
			if (hurt && !this.isBlinking())
			{
				vx = 0;
			}
			else
			{
				if (vx > 0)
					vx-= 0.1;
				else if (vx < 0)
					vx+= 0.1;
			}

			if (this.isClimbing)
				this.isClimbing = false;
		}
		else
		{
			if (vx > 0)
			{
				if (vx <= 0.03)
					vx = 0;
				else
					vx-= 0.03;
			}
			else if (vx < 0)
			{
				if (vx >= -0.03)
					vx = 0;
				else
					vx+= 0.03;
			}
		}

		if (this.isClimbing)
		{
			vy = 0;
			x = climbX;
		}
		else
		{
			if (vy > 0)
			{
				if (vy <= 0.03)
					vy = 0;
				else
					vy-= 0.03;
			}
			else if (vy < 0)
			{
				if (vy >= -0.03)
					vy = 0;
				else
					vy+= 0.03;
			}
		}

		if (abs(vx) < 0.1)
			vx = 0;
		if (abs(vy) < 0.1)
			vy = 0;
	}
}
