public class Item extends GameObject
{
	private int pointsValue;
	private int powerValue;
	private int livesValue;
	private int heartsValue;

	public int getPointsValue()
	{
		return this.pointsValue;
	}

	public void setPointsValue(int points)
	{
		this.pointsValue = points;
	}

	public int getPowerValue()
	{
		return this.powerValue;
	}

	public void setPowerValue(int power)
	{
		this.powerValue = power;
	}

	public int getLivesValue()
	{
		return this.livesValue;
	}

	public void setLivesValue(int lives)
	{
		this.livesValue = lives;
	}

	public int getHeartsValue()
	{
		return this.heartsValue;
	}

	public void setHeartsValue(int hearts)
	{
		this.heartsValue = hearts;
	}
}
