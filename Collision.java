import java.io.File;
import java.io.FileNotFoundException;

public class Collision
{
	public static final int COLLISION_NONE		= 0;
	public static final int COLLISION_SOLID		= (1 << 0);
	public static final int COLLISION_PLATFORM	= (1 << 1);
	public static final int COLLISION_DAMAGE	= (1 << 2);
	public static final int COLLISION_DESTRUCTIBLE	= (1 << 3);
	public static final int COLLISION_CLIMB		= (1 << 4);

	private int[] map; // collision map

	public Collision(String fileName, int size)
	{
		map = new int[size];
		load(fileName);
	}

	private void load(String fileName)
	{
		FileIO fp = null;
		File file = new File("./data/level/" + fileName);

		try
		{
			fp = new FileIO(file);
		}
		catch (Exception e)
		{
			System.out.printf("Failed to load collision map: %s\n", fileName);
			// todo
		}

		if(fp != null)
		{
			int index = 0;

			while(fp.hasNext())
			{
				String next = fp.getNext();

				if (next.equals("EOF"))
					break;
				map[index] = Integer.parseInt(next);

				index++;
			}
		}
	}

	public int getCollision(int index)
	{
		if(index == -1)
			return COLLISION_SOLID;

		switch(map[index])
		{
			case 1:	// solid
				return COLLISION_SOLID;
			case 2:	// platform
				return COLLISION_PLATFORM;
			case 3:	// damage
				return COLLISION_DAMAGE;
			case 4:	// destructible
				return COLLISION_DESTRUCTIBLE;
			case 5:	// ladder
				return COLLISION_CLIMB;
			case 6:	// top of the ladder
				return COLLISION_CLIMB | COLLISION_PLATFORM;

			default:
				return COLLISION_NONE;
		}
	}
}
