enum OnCollision
{
	IGNORE,
	PERISH,
	BOUNCE
}

public class Projectile extends Creature
{
	private int ttl;			// Time to live counter.
	private OnCollision onCollision;	// Behaviour on collision with level tiles (and not other objects!).

	public Projectile(LevelLayer lay, Collision col, Level lev)
	{
		super(lay, col, lev);
	}

	public int getTtl()
	{
		return this.ttl;
	}

	public void setTtl(int ttl)
	{
		this.ttl = ttl;
	}

	public void ttlCountdown()
	{
		if (this.ttl > 0)
			this.ttl--;
	}
}
