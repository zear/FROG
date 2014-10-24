import sdljava.*;
import sdljava.video.*;

public class Font
{
	private SDLSurface img;
	private SDLRect[] imgClip;
	private int w;
	private int h;
	private int kerning;	// horizontal distance between characters
	private int leading;	// vertical distance between verses

//	public Font(String fileName, int w, int h)
//	{
//		this.Font(fileName, w, h, 1, 4); // init with default kerning (1px) and leading (4px)
//	}

	public Font(String fileName, int w, int h, int kerning, int leading)
	{
		int i;
		int j;
		int x;
		int y;
		this.img = Sdl.loadImage(fileName);

		this.w = w;
		this.h = h;
		this.imgClip = new SDLRect[256];

		for(i = 0, y = -this.h; i < this.imgClip.length;)
		{
			for(j = 0, x = 0, y += this.h; j < 16; j++, x += this.w, i++)
			{
				this.imgClip[i] = new SDLRect();
				this.imgClip[i].x = x;
				this.imgClip[i].y = y;
				this.imgClip[i].width = this.w;
				this.imgClip[i].height = this.h;
			}
		}

		this.kerning = kerning;
		this.leading = leading;

	}

	public int getW()
	{
		return this.w;
	}

	public int getH()
	{
		return this.h;
	}

	public void draw(String text, int x, int y)
	{
		SDLRect r = new SDLRect();
		int letterNum = 0;
		char letterCh;
		int origX = x;
		int origY = y;

		r.x = x;
		r.y = y;

		if(text == null)
			return;

		for(int i = 0; i < text.length(); i++)
		{
			letterCh = text.charAt(letterNum);
			if(letterCh == '\n') // line break
			{
				r.x = origX - this.w;
				y += this.h + this.leading;
			}

			r.x += this.kerning + this.w;
			r.y = y;

			try
			{
				this.img.blitSurface(this.imgClip[letterCh], Sdl.screen, r);
			}
			catch (SDLException e)
			{
				// todo
			}

			letterNum++;
		}
	}

	public void drawCentered(String text, int y)
	{
		int width = 0;

		for(int i = 0; i < text.length(); i++)
		{
			width += this.w;
			if(i < text.length() - 1)
				width += this.kerning;
		}

		draw(text, 320/2 - width/2, y);
	}

}
