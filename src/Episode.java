import java.util.ArrayList;
import java.io.File;

public class Episode
{
	private String directory = "";
	private String title = "";
	private int id = -1;
	private ArrayList<String> levelList = null;

	public Episode(String directory, String fileName)
	{
		this.directory = directory;
		load(fileName);
	}

	public String getDirectory()
	{
		return this.directory;
	}

	public String getTitle()
	{
		return this.title;
	}

	public int getId()
	{
		return this.id;
	}

	public String getLevel(int levelNum)
	{
		if (levelList == null)
			return null;

		if (levelNum < levelList.size())
		{
			return levelList.get(levelNum);
		}
		else
		{
			return null;
		}
	}

	public int getLevelNum()
	{
		return (levelList != null ? levelList.size() : 0);
	}

	public void load(String fileName)
	{
		File file;

		file = new File(directory + fileName);
		FileIO fp = new FileIO(file);

		if (fp != null)
		{
			String line;
			String [] words;
			int token;
			boolean parseLevels = false;

			while (fp.hasNext())
			{
				line = fp.getLine();
				words = line.split("\\s");

				if (words[0].equals("END"))
					return;

				if (!parseLevels)
				{
					if (words[0].equals("TITLE:"))
					{
						for (int i = 1; i < words.length; i++)
						{
							title += words[i] + (i+1 >= words.length ? "" : " ");
						}
					}
					else if (words[0].equals("ID:"))
					{
						id = Integer.parseInt(words[1]);
					}
					else if (words[0].equals("LEVELS:"))
					{
						levelList = new ArrayList<String>();
						parseLevels = true;
					}
				}
				else
				{
					levelList.add(words[0]);
				}
			}
		}
	}
}
