public class Projectile extends Creature
{
	private int ttl; // time to live counter

	public Projectile(LevelLayer lay, Collision col)
	{
		super(lay, col);
	}

	public int getTtl()
	{
		return this.ttl;
	}

	public void setTtl(int ttl)
	{
		this.ttl = ttl;
	}
}
