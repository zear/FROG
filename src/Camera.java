import java.lang.Math;

public class Camera
{
	public static final int MUST_FOLLOW_DIST = 48;

	private int x = 0;
	private int y = 0;
	private LevelLayer layer;
	private GameObject target = null;
	private int targetX = 0;
	private int targetY = 0;
	private boolean trackX;
	private boolean trackY;

	public int getX()
	{
		return this.x;
	}
	public int getY()
	{
		return this.y;
	}

	public GameObject getTarget()
	{
		return this.target;
	}

	public void setTarget(GameObject obj)
	{
		this.target = obj;
		this.targetX = (int)Math.floor(target.getX());
		this.targetY = (int)Math.floor(target.getY());
	}

	public void setTarget(int x, int y)
	{
		this.target = null;
		this.targetX = x;
		this.targetY = y;
	}

	public void setCamera(GameObject obj)
	{
		this.x = (int)obj.x - Sdl.SCREEN_WIDTH/2;
		this.y = (int)obj.y - Sdl.SCREEN_HEIGHT/2;
	}

	public void setCamera(int x, int y)
	{
		this.x = x - Sdl.SCREEN_WIDTH/2;
		this.y = y - Sdl.SCREEN_HEIGHT/2;
	}

	public void track(LevelLayer layer, boolean canLeaveScreen)
	{
		if(target != null)
		{
			if(this.trackX)
				this.targetX = (int)Math.floor((int)target.getX());
			if(this.trackY)
				this.targetY = (int)Math.floor((int)target.getY());

			if(!canLeaveScreen && this.target instanceof Creature)
			{
				Creature t = (Creature)this.target;

				if(this.targetX < this.x)
				{
					this.x = targetX;
				}
				else if(this.targetX + t.w - 1 > this.x + Sdl.SCREEN_WIDTH)
				{
					this.x = targetX + t.w - 1 - Sdl.SCREEN_WIDTH;
				}

				if(this.targetY < this.y + MUST_FOLLOW_DIST)
				{
					this.y = targetY - MUST_FOLLOW_DIST;
				}
				else if(this.targetY + t.h - 1 > this.y + Sdl.SCREEN_HEIGHT - MUST_FOLLOW_DIST)
				{
					this.y = targetY + t.h - 1 - Sdl.SCREEN_HEIGHT + MUST_FOLLOW_DIST;
				}
			}
		}

		if(target != null)
		{
			Creature t = (Creature)this.target;

			if(this.targetX < this.x + Sdl.SCREEN_WIDTH/2 - 16)
			{
				this.x -= this.x + Sdl.SCREEN_WIDTH/2 - 16 - this.targetX;
			}
			else if(this.targetX + t.w - 1 > this.x + Sdl.SCREEN_WIDTH/2 + 16)
			{
				this.x += (this.targetX + t.w - 1) - (this.x + Sdl.SCREEN_WIDTH/2 + 16);
			}

			this.trackX = false;

			if(t.isOnGround)
			{
				if(this.targetY != this.y + Sdl.SCREEN_HEIGHT/2)
					this.trackY = true;
				else
					this.trackY = false;
			}
			else if(this.targetY > this.y + Sdl.SCREEN_HEIGHT/2)
			{
				this.trackY = true;
			}
		}
		else
		{
			this.trackX = true;
			this.trackY = true;
		}

		if(this.trackX)
		{
			if(this.x > this.targetX - Sdl.SCREEN_WIDTH/2)
				this.x-= 1;
			if(this.x < this.targetX - Sdl.SCREEN_WIDTH/2)
				this.x+= 1;
		}
		if(this.trackY)
		{
			if(this.y > this.targetY - Sdl.SCREEN_HEIGHT/2)
				this.y-= 1;
			if(this.y < this.targetY - Sdl.SCREEN_HEIGHT/2)
				this.y+= 1;
		}

		if(this.x < 0)
			this.x = 0;
		if(this.y < 0)
			this.y = 0;

		if(this.x + Sdl.SCREEN_WIDTH > (layer.getWidth()) * LevelLayer.TILE_SIZE)
		{
			this.x = (layer.getWidth()) * LevelLayer.TILE_SIZE - Sdl.SCREEN_WIDTH;
		}
		if(this.y + Sdl.SCREEN_HEIGHT > (layer.getHeight()) * LevelLayer.TILE_SIZE)
		{
			this.y = (layer.getHeight()) * LevelLayer.TILE_SIZE - Sdl.SCREEN_HEIGHT;
		}
	}

	public void track(LevelLayer layer)
	{
		if(target != null)
		{
			this.targetX = (int)target.getX();
			this.targetY = (int)target.getY();
		}

		if(this.x > this.targetX - Sdl.SCREEN_WIDTH/2)
			this.x--;
		if(this.x < this.targetX - Sdl.SCREEN_WIDTH/2)
			this.x++;
		if(this.y > this.targetY - Sdl.SCREEN_HEIGHT/2)
			this.y--;
		if(this.y < this.targetY - Sdl.SCREEN_HEIGHT/2)
			this.y++;

		if(this.x < 0)
			this.x = 0;
		if(this.y < 0)
			this.y = 0;

		if(this.x + Sdl.SCREEN_WIDTH > (layer.getWidth()) * LevelLayer.TILE_SIZE)
		{
			this.x = (layer.getWidth()) * LevelLayer.TILE_SIZE - Sdl.SCREEN_WIDTH;
		}
		if(this.y + Sdl.SCREEN_HEIGHT > (layer.getHeight()) * LevelLayer.TILE_SIZE)
		{
			this.y = (layer.getHeight()) * LevelLayer.TILE_SIZE - Sdl.SCREEN_HEIGHT;
		}
	}
}
