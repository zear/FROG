public class Item extends GameObject
{
	private int points;

//	public void load(String fileName, int w, int h, int rowW, int size, LinkedList <GameObjectTemplate> tempList)
//	{
//		super.load(fileName, w, h, rowW, size, tempList);
//	}

	public int getPoints()
	{
		return this.points;
	}

	public void setPoints(int points)
	{
		this.points = points;
	}
}
