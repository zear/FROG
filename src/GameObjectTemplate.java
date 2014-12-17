import sdljava.*;
import sdljava.video.*;
import java.util.ArrayList;

public class GameObjectTemplate
{
	private String templateName;
	private ArrayList <Animation> animation;
	private SDLSurface img;
	private int imgSize;
	private int imgRowW;
	private SDLRect[] imgClip;
	private int tileW;
	private int tileH;

	public GameObjectTemplate()
	{
		this.animation = new ArrayList<Animation>();
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

	public String getName()
	{
		return this.templateName;
	}

	public void setName(String name)
	{
		this.templateName = name;
	}

	public SDLSurface getImg()
	{
		return this.img;
	}

	public SDLRect[] getImgClip()
	{
		return this.imgClip;
	}

	public Animation addAnimation(FileIO fp)
	{
		String word;
		Animation tmp = null;

		while(fp.hasNext())
		{
			word = fp.getNext();
			if(word.equals("END"))
			{
				return tmp;
			}
			else
			{
				animation.add(tmp = new Animation(fp, word));
			}
		}

		return tmp;
	}

	public ArrayList <Animation> getAnimation()
	{
		return this.animation;
	}
}
