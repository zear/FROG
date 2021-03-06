import sdljava.*;
import sdljava.video.*;

public class Gui
{
	private SDLSurface hpBar;
	private SDLRect[] hpBarClip;
	private Player player;
	private Level level;
	private Font font;

	private int timer = 240;
	private int timer2 = 40;
	private int dist = 0;

	private int playTimeEffect = 0;
	private boolean playTimeCalculated = false;

	public Gui()
	{
		this.hpBar = Sdl.loadImage("./data/gfx/health.bmp");
		this.hpBarClip = new SDLRect[2];

		for (int i = 0; i < 2; i++)
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

	public void setLevel(Level level)
	{
		this.level = level;
	}

	public void setFont(Font font)
	{
		this.font = font;
	}

	public boolean isPlayTimeCalculated()
	{
		return this.playTimeCalculated;
	}

	public void draw() // draws game object
	{
		SDLRect r = new SDLRect();

		if (this.player == null)
			return;

		try
		{
			r.x = 5;
			r.y = 2;

			if (this.player.getHp() > 0)
				this.hpBar.blitSurface(this.hpBarClip[1], Sdl.screen, r);
			else
				this.hpBar.blitSurface(this.hpBarClip[0], Sdl.screen, r);
			r.x = r.x + 13;
			if (this.player.getHp() > 1)
				this.hpBar.blitSurface(this.hpBarClip[1], Sdl.screen, r);
			else
				this.hpBar.blitSurface(this.hpBarClip[0], Sdl.screen, r);
			r.x = r.x + 13;
			if (this.player.getHp() > 2)
				this.hpBar.blitSurface(this.hpBarClip[1], Sdl.screen, r);
			else
				this.hpBar.blitSurface(this.hpBarClip[0], Sdl.screen, r);

				font.draw("".format("%6dp", this.player.getScore()), 255, 1);

			if (Game.drawFps)
			{
				font.draw("" + (Sdl.fpsIsCapped ? "" : "!") + Sdl.fpsCalculated, 290, 14);
			}

			Sdl.fpsIsCapped = false;

			if (timer2 > 0)
			{
				if (timer > 0)
				{
					timer--;
//					font.draw("Programming - Artur \"Zear\" Rojek\nGraphics - Daniel \"Dnilo\" Garcia", 20, 90);
				}
				else
				{
					timer2--;
					dist++;
				}

//				font.draw("** Fantastic Rescue Of Greeny v0.1 **", 5, 30 - dist);
//				//font.draw("** FROG v0.1 **", 80, 30);
//				//font.draw("Programming - Artur \"Zear\" Rojek\nGraphics - Daniel \"Dnilo\" Garcia", 20, 90);
//				font.draw("(c) 2014-2015, Licensed under LGPLv2.1+", 20, 220 + dist);
			}

			if (this.level.isComplete())
			{
				int time = playTimeEffect/Sdl.framesPerSecond;
				font.drawCentered("Level complete!", Sdl.SCREEN_HEIGHT/2 - 36);
				font.drawCentered("Time: " + String.valueOf(time/60) + ":" + "".format("%02d", time%60), Sdl.SCREEN_HEIGHT/2 - 24); 

				if (playTimeEffect < this.level.getPlayTime())
				{
					playTimeEffect+=Sdl.framesPerSecond/4;
				}
				else
				{
					playTimeEffect = this.level.getPlayTime();
					playTimeCalculated = true;
				}
			}
			else if (this.player.isDead())
			{
				font.drawFloatingCentered((player.getLives() > 0 ? "Woops" : "Game Over"), Sdl.SCREEN_HEIGHT/2, 4, 5);
			}
		}
		catch (SDLException e)
		{
			// todo
		}
	}
}
