import sdljava.*;
import sdljava.video.*;
import java.util.LinkedList;
import java.util.ListIterator;

enum ObjType
{
	OBJECT,
	CREATURE,
	PLAYER
}

// GameObject class keeps all information necessary for objects on the map
public class GameObject
{
	private String fileName;
	private String objName;
	private ObjType type;
	private boolean removal = false;	// if true, object is flagged for removal
	protected float x;
	protected float y;
	protected int w;
	protected int h;
	protected boolean direction;
	private boolean vulnerable = false;
	private int invincibilityTimer;
	private int invincibilityOrigTime;

	private GameObjectTemplate objTemplate = null;
	private Animation curAnim = null;
	private int frameNum;
	private int frameDelay;
	private boolean isBlinking;
	private int blinkTimer;
	private int blinkOrigTime;
	private boolean doDraw = true;

	public GameObject()
	{
		this.frameNum = 0;
		this.frameDelay = 0;
	}

	public GameObject(String name, int x, int y, boolean direction)
	{
		this.objName = name;
		this.x = (float)x;
		this.y = (float)y;
		this.direction = direction;
		this.frameNum = 0;
		this.frameDelay = 0;
	}

	public void load(String fileName, int w, int h, int rowW, int size, LinkedList <GameObjectTemplate> tempList)
	{
		String templateName = null;
		ListIterator<GameObjectTemplate> templi = tempList.listIterator();
		GameObjectTemplate curElement = null;

		if(templi.hasNext())
		{
			do
			{
				curElement = templi.next();
				templateName = curElement.getName();

				if(this.objName.equals(templateName))
				{
					objTemplate = curElement;
					return; // Template for this object already exists. Abort.
				}
			}
			while(templi.hasNext());
		}

		// Add new template to the list
		objTemplate = new GameObjectTemplate();
		tempList.push(objTemplate);
		objTemplate.setName(this.objName);
		objTemplate.load(fileName, w, h, rowW, size);
	}

	public String getFileName()
	{
		return this.fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getName()
	{
		return this.objName;
	}

	public void setName(String name)
	{
		this.objName = name;
	}

	public ObjType getType()
	{
		return this.type;
	}

	public void setType(ObjType type)
	{
		this.type = type;
	}

	public boolean getRemoval()
	{
		return this.removal;
	}

	public void setRemoval(boolean value)
	{
		this.removal = value;
	}

	public void putX(int x)
	{
		this.x = (float)x;
	}
	public void putY(int y)
	{
		this.y = (float)y;
	}
	public void putW(int w)
	{
		this.w = w;
	}
	public void putH(int h)
	{
		this.h = h;
	}
	public void putDirection(int direction)
	{
		if(direction > 0)
			this.direction = true;
		else
			this.direction = false;
	}
	public float getX()
	{
		return x;
	}
	public float getY()
	{
		return y;
	}
	public int getW()
	{
		return w;
	}
	public int getH()
	{
		return h;
	}
	public boolean getDirection()
	{
		return direction;
	}

	public boolean isVulnerable()
	{
		return this.vulnerable;
	}

	public void setVulnerability(boolean value)
	{
		this.vulnerable = value;
	}

	public void setInvincibility(int time)
	{
		this.vulnerable = false;
		this.invincibilityOrigTime = time;
		this.invincibilityTimer = this.invincibilityOrigTime;
	}

	private void invincibilityCountdown()
	{
		if(this.invincibilityTimer > 0)
			this.invincibilityTimer--;
		else
			this.vulnerable = true;
	}

	public void addAnimation(FileIO fp)
	{
		Animation tmp;

		tmp = this.objTemplate.addAnimation(fp);
		if(tmp != null && this.curAnim == null)
			this.curAnim = tmp;
	}

	public void setBlinking(int time)
	{
		this.isBlinking = true;
		this.blinkOrigTime = time;
		this.blinkTimer = this.blinkOrigTime;
	}

	private void blinkingCountdown()
	{
		if(this.blinkTimer > 0)
		{
			this.blinkTimer--;

			if(blinkTimer%5 == 0)
				this.doDraw = !this.doDraw;
		}
		else
		{
			this.isBlinking = false;
			this.doDraw = true;
		}
	}

	public GameObjectTemplate getTemplate()
	{
		return this.objTemplate;
	}

	public void setTemplate(GameObjectTemplate template)
	{
		this.objTemplate = template;
	}

	public void changeAnimation(String name)
	{
		ListIterator<Animation> animli = this.objTemplate.getAnimation().listIterator();

		if(curAnim == null)
		{
			return;
		}

		if(curAnim.getAnimName().equals(name)) // don't change the animation if it's already selected
		{
			return;
		}

		do
		{
			Animation curElement = animli.next();
			if(curElement.getAnimName().equals(name))
			{
				this.curAnim.setIsOver(false);
				this.curAnim = curElement;
				this.frameNum = this.curAnim.getLength(this.direction) - 1;
				this.frameDelay = curAnim.getFrameRate();				
				return;
			}
		}
		while(animli.hasNext());
	}

	public Animation getAnimation()
	{
		return this.curAnim;
	}

	public void setAnimation(Animation anim)
	{
		this.curAnim = anim;
	}

	public void logic()
	{
		this.invincibilityCountdown();
	}

	public void draw(Camera camera) // draws game object
	{
		SDLRect r = new SDLRect();

		if(curAnim == null)
			return;

		if(this instanceof Player)
		{
			if(((Player)this).isDead())
				return;
		}

		this.blinkingCountdown();

		if(!doDraw)
			return;

		r.x = (int)this.x - camera.getX();
		r.y = (int)this.y - camera.getY();

		r.x += this.curAnim.getOffsetX(direction);
		r.y += this.curAnim.getOffsetY(direction);

		if(r.x < -this.w + 1)
			return;
		if(r.x > 320)
			return;
		if(r.y < -this.h + 1)
			return;
		if(r.y > 240)
			return;

		try
		{
			if(this instanceof Player)
			{
				//System.out.printf("%s, Frame %d/%d: %d\n", this.curAnim.getName(), frameNum, this.curAnim.getLength(this.direction) - 1, this.curAnim.getFrame(this.direction, frameNum));
			}
			SDLRect[] imgClip = this.objTemplate.getImgClip();
			this.objTemplate.getImg().blitSurface(imgClip[this.curAnim.getFrame(this.direction, frameNum)], Sdl.screen, r);
			if(frameDelay == 0)
			{
				frameDelay = this.curAnim.getFrameRate();
				if(frameNum > 0)
					frameNum--;
				else
				{
					if(this.curAnim.isLooping())
						frameNum = this.curAnim.getLength(this.direction) - 1;
					else
						this.curAnim.setIsOver(true);
				}
			}
			else
			{
				frameDelay--;
			}
		}
		catch (SDLException e)
		{
			// todo
		}

		// Uncomment to debug camera
//		if(this instanceof Player)
//		{
//			r.x = 320/2 - 16;
//			r.y = 240/2 - 16;
//			r.width = 32;
//			r.height = 1;
//			try
//			{
//				Sdl.screen.fillRect(r, 10);
//				r.y = 240/2 + 8 + this.h - 1;
//				Sdl.screen.fillRect(r, 10);
//				r.y = 240/2 - 16;
//				r.width = 1;
//				r.height = 24 + this.h - 1;
//				Sdl.screen.fillRect(r, 10);
//				r.x = 320/2 + 16;
//				Sdl.screen.fillRect(r, 10);
//			}
//			catch (SDLException e)
//			{
//				// todo
//			}
//		}
	}
}
