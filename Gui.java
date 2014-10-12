import sdljava.*;
import sdljava.video.*;

public class Gui
{
	private SDLSurface hpBar;
	private SDLRect[] hpBarClip;
	private Player player;
	private Font font;

	private int timer = 240;
	private int timer2 = 40;
	private int dist = 0;

	public Gui()
	{
		this.hpBar = Sdl.loadImage("./data/gfx/health.bmp");
		this.hpBarClip = new SDLRect[2];

		for(int i = 0; i < 2; i++)
		{
			this.hpBarClip[i] = new SDLRect();
			this.hpBarClip[i].x = i * 12;
			this.hpBarClip[i].y = 0;
			this.hpBarClip[i].width = 12;
			this.hpBarClip[i].height = 11;
		}
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}

	public void setFont(Font font)
	{
		this.font = font;
	}

	public void draw() // draws game object
	{
		SDLRect r = new SDLRect();

		if(this.player == null)
			return;

		try
		{
			r.x = 5;
			r.y = 2;

			if(this.player.getHp() > 0)
				this.hpBar.blitSurface(this.hpBarClip[1], Sdl.screen, r);
			else
				this.hpBar.blitSurface(this.hpBarClip[0], Sdl.screen, r);
			r.x = r.x + 13;
			if(this.player.getHp() > 1)
				this.hpBar.blitSurface(this.hpBarClip[1], Sdl.screen, r);
			else
				this.hpBar.blitSurface(this.hpBarClip[0], Sdl.screen, r);
			r.x = r.x + 13;
			if(this.player.getHp() > 2)
				this.hpBar.blitSurface(this.hpBarClip[1], Sdl.screen, r);
			else
				this.hpBar.blitSurface(this.hpBarClip[0], Sdl.screen, r);

			font.draw("".format("%6dp", this.player.getScore()), 255, 1);
			font.draw("" + (Sdl.fpsIsCapped ? "" : "!") + Sdl.fpsCalculated, 290, 14);
			Sdl.fpsIsCapped = false;

			if(timer2 > 0)
			{
				if(timer > 0)
				{
					timer--;
					font.draw("Programming - Artur \"Zear\" Rojek\nGraphics - Daniel \"Dnilo\" Garcia", 20, 90);
				}
				else
				{
					timer2--;
					dist++;
				}

				font.draw("** Fantastic Rescue Of Greeny v0.1 **", 5, 30 - dist);
				//font.draw("** FROG v0.1 **", 80, 30);
				//font.draw("Programming - Artur \"Zear\" Rojek\nGraphics - Daniel \"Dnilo\" Garcia", 20, 90);
				font.draw("(c) 2014, Licensed under GPLv2+", 20, 220 + dist);
			}

			if(this.player.isDead())
			{
				font.draw("Game Over", 320/2 - 36, 240/2);
			}
		}
		catch (SDLException e)
		{
			// todo
		}
	}
}
