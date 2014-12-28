import sdljava.*;
import sdljava.video.*;
import java.util.ArrayList;

// LevelLayer keeps data related to a single map layer
public class LevelLayer
{
	public static final int TILE_SIZE = 16;

	private int id;
	private Tiles tiles;
	private SDLSurface img;
	private int imgSize;
	private int imgRowW;
	private SDLRect[] imgClip;
	private int tileW;
	private int tileH;

	protected class Tiles
	{
		private ArrayList<ArrayList<Integer>> t;

		protected Tiles()
		{
			t = new ArrayList<ArrayList<Integer>>();
		}

		protected void addRow()
		{
			t.add(new ArrayList<Integer>());
		}
		protected void removeLastRow()
		{
			t.remove(getNumOfRows() - 1);
		}
		protected void putElement(int row, int element)
		{
			t.get(row).add(element);
		}
		protected int getElement(int row, int element)
		{
			return t.get(row).get(element);
		}
		protected void setElement(int row, int element, int value)
		{
			t.get(row).set(element, value);
		}
		protected int getNumOfRows()
		{
			return t.size();
		}
		protected int getNumOfElements(int row) // technically all rows should have the same number of elements, but let's force passing a row
		{
			return t.get(row).size();
		}
	}

	public LevelLayer()
	{
		tiles = new Tiles();
	}

	public int getTile(int x, int y)
	{
		if(x < 0)
			return -1;
		if(y < 0)
			return -1;
		if(x > getWidth() - 1)
			return -1;
		if(y > getHeight() - 1)
			return -1;

		return tiles.getElement(y, x);
	}

	public void setTile(int x, int y, int value)
	{
		if(x < 0)
			return;
		if(y < 0)
			return;
		if(x > getWidth() - 1)
			return;
		if(y > getHeight() - 1)
			return;

		tiles.setElement(y, x, value);
	}

	public int getWidth()
	{
		return tiles.getNumOfElements(0);
	}

	public int getHeight()
	{
		return tiles.getNumOfRows();
	}

	public int getId()
	{
		return this.id;
	}

	public void load(int id)
	{
		this.id = id;
	}

	public void load(String fileName, int w, int h, int rowW, int size)
	{
		int i;
		int j;
		int x;
		int y;

		this.img = Sdl.loadImage(fileName);
		this.imgSize = size;
		this.imgRowW = rowW;
		this.tileW = w;
		this.tileH = h;
		this.imgClip = new SDLRect[this.imgSize];

		for(i = 0, y = -tileH; i < imgSize;)
		{
			for(j = 0, x = 0, y += tileH; j < imgRowW; j++, x += tileW, i++)
			{
				imgClip[i] = new SDLRect();
				imgClip[i].x = x;
				imgClip[i].y = y;
				imgClip[i].width = tileW;
				imgClip[i].height = tileH;
			}
		}
	}

	public void load(FileIO fp)
	{
		String line;
		String [] words;
		int token;

		while(fp.hasNext())
		{
			line = fp.getLine();
			words = line.split("\\s");
			token = -1;

			if(words[0].equals("END"))
				return;

			if(words.length > 0 && !(words[0].equals("END")))
				tiles.addRow();

			while(token < words.length - 1)
			{
				token++;
				tiles.putElement(tiles.getNumOfRows() - 1, Integer.parseInt(words[token]));
			}
		}
	}

	public void draw(Camera camera) // draws layer
	{
		int i;
		int j;
		int x;
		int y;
		int tileNum;

		for(i = camera.getY()/LevelLayer.TILE_SIZE; i < camera.getY()/LevelLayer.TILE_SIZE + 15 + 1; i++)
		{
			if(i >= tiles.getNumOfRows())
				break;

			for(j = camera.getX()/LevelLayer.TILE_SIZE; j < camera.getX()/LevelLayer.TILE_SIZE + 20 + 1; j++)
			{
				if(j >= tiles.getNumOfElements(i))
					break;

				tileNum = tiles.getElement(i, j);

				if(tileNum != 0) // don't bother drawing tile #0 (transparent)
				{
					SDLRect r = new SDLRect();
					r.x = j * tileW - camera.getX();
					r.y = i * tileH - camera.getY();

					try
					{
						this.img.blitSurface(imgClip[tileNum], Sdl.screen, r);
					}
					catch (SDLException e)
					{
						// todo
					}
				}
			}
		}
	}
}
