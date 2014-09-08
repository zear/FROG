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
		this.targetX = (int)target.getX();
		this.targetY = (int)target.getY();
	}

	public void setTarget(int x, int y)
	{
		this.target = null;
		this.targetX = x;
		this.targetY = y;
	}

	public void setCamera(GameObject obj)
	{
		this.x = (int)obj.x - 320/2;
		this.y = (int)obj.y - 240/2;
	}

	public void setCamera(int x, int y)
	{
		this.x = x - 320/2;
		this.y = y - 240/2;
	}

	public void track(LevelLayer layer, boolean canLeaveScreen)
	{
		if(target != null)
		{
			if(this.trackX)
				this.targetX = (int)target.getX();
			if(this.trackY)
				this.targetY = (int)target.getY();

			if(!canLeaveScreen && this.target instanceof Creature)
			{
				Creature t = (Creature)this.target;

				if(this.targetX < this.x)
				{
					this.x = targetX;
				}
				else if(this.targetX + t.w - 1 > this.x + 320)
				{
					this.x = targetX + t.w - 1 - 320;
				}

				if(this.targetY < this.y + MUST_FOLLOW_DIST)
				{
					this.y = targetY - MUST_FOLLOW_DIST;
				}
				else if(this.targetY + t.h - 1 > this.y + 240 - MUST_FOLLOW_DIST)
				{
					this.y = targetY + t.h - 1 - 240 + MUST_FOLLOW_DIST;
				}
			}
		}

		if(target != null)
		{
			Creature t = (Creature)this.target;

			if(this.targetX < this.x + 320/2 - 16)
			{
				this.x -= this.x + 320/2 - 16 - this.targetX;
			}
			else if(this.targetX + t.w - 1 > this.x + 320/2 + 16)
			{
				this.x += (this.targetX + t.w - 1) - (this.x + 320/2 + 16);
			}
			this.trackX = false;

			if(t.isOnGround)
			{
				if(this.targetY != this.y + 240/2)
					this.trackY = true;
				else
					this.trackY = false;
			}
			else if(this.targetY > this.y + 240/2)
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
			if(this.x > this.targetX - 320/2)
				this.x-= 1;
			if(this.x < this.targetX - 320/2)
				this.x+= 1;
		}
		if(this.trackY)
		{
			if(this.y > this.targetY - 240/2)
				this.y-= 1;
			if(this.y < this.targetY - 240/2)
				this.y+= 1;
		}

		if(this.x < 0)
			this.x = 0;
		if(this.y < 0)
			this.y = 0;

		if(this.x + 320 > (layer.getWidth()) * 16)
		{
			this.x = (layer.getWidth()) * 16 - 320;
		}
		if(this.y + 240 > (layer.getHeight()) * 16)
		{
			this.y = (layer.getHeight()) * 16 - 240;
		}
	}

	public void track(LevelLayer layer)
	{
		if(target != null)
		{
			this.targetX = (int)target.getX();
			this.targetY = (int)target.getY();
		}

		if(this.x > this.targetX - 320/2)
			this.x--;
		if(this.x < this.targetX - 320/2)
			this.x++;
		if(this.y > this.targetY - 240/2)
			this.y--;
		if(this.y < this.targetY - 240/2)
			this.y++;

		if(this.x < 0)
			this.x = 0;
		if(this.y < 0)
			this.y = 0;

		if(this.x + 320 > (layer.getWidth()) * 16)
		{
			this.x = (layer.getWidth()) * 16 - 320;
		}
		if(this.y + 240 > (layer.getHeight()) * 16)
		{
			this.y = (layer.getHeight()) * 16 - 240;
		}
	}
}
