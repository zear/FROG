import java.util.ArrayList;

public class Animation
{
	private String name;
	private int frameRate;
	private boolean loop;
	private Frames left;
	private Frames right;
	private boolean isOver;

	protected class Frames
	{
		private ArrayList<Integer> frames;
		private int offsetX;
		private int offsetY;

		Frames()
		{
			frames = new ArrayList<Integer>();
		}

		protected int getLength()
		{
			return frames.size();
		}

		protected int getFrame(int i)
		{
			return frames.get(i);
		}

		protected void addFrame(int i)
		{
			frames.add(i);
		}

		protected int getOffsetX()
		{
			return this.offsetX;
		}

		protected int getOffsetY()
		{
			return this.offsetY;
		}
	}

	public String getAnimName()
	{
		return this.name;
	}

	public int getLength(boolean direction)
	{
		if(!direction)	// left
			return this.left.getLength();
		else		// right
			return this.right.getLength();
	}

	public int getFrame(boolean direction, int i)
	{
		if(!direction)	// left
			return this.left.getFrame(i);
		else		// right
			return this.right.getFrame(i);
	}

	public int getFrameRate()
	{
		return this.frameRate;
	}

	public int getOffsetX(boolean direction)
	{
		if(!direction)	// left
			return this.left.getOffsetX();
		else		// right
			return this.right.getOffsetX();
	}

	public int getOffsetY(boolean direction)
	{
		if(!direction)	// left
			return this.left.getOffsetY();
		else		// right
			return this.right.getOffsetY();
	}

	public boolean isLooping()
	{
		return loop;
	}

	public boolean isOver()
	{
		return isOver;
	}

	public void setIsOver(boolean value)
	{
		this.isOver = value;
	}

	public Animation(FileIO fp, String name)
	{
		String line;
		String [] words;
		int token;

		//if(fp != null) System.out.printf("fp is not null. It is: %s\n", fp.getNext());
		// name, speed, loop
		this.name = name;
		this.frameRate = Integer.parseInt(fp.getNext());
		this.loop = (Integer.parseInt(fp.getNext()) != 0);

		left = new Frames();
		right = new Frames();

		fp.getNext();
		// left offsets
		this.left.offsetX = Integer.parseInt(fp.getNext());
		this.left.offsetY = Integer.parseInt(fp.getNext());
		// left frames
		{
			line = fp.getLine();
			words = line.split("\\s");
			token = 0; // skip the first word

			while(token < words.length - 1)
			{
				token++;
				this.left.addFrame(Integer.parseInt(words[token]));
			}
		}
		fp.getNext();
		// right offsets
		this.right.offsetX = Integer.parseInt(fp.getNext());
		this.right.offsetY = Integer.parseInt(fp.getNext());
		// right frames
		{
			line = fp.getLine();
			words = line.split("\\s");
			token = 0; // skip the first word

			while(token < words.length - 1)
			{
				token++;
				this.right.addFrame(Integer.parseInt(words[token]));
			}
		}

		System.out.printf("Name: %s\nFramerate: %s\nLEFT\n", this.name, this.frameRate);
		System.out.printf("Offx: %d\nOffy: %d\nLen: %d\nRIGHT\n", this.left.offsetX, this.left.offsetY, this.left.getLength());
		System.out.printf("Offx: %d\nOffy: %d\nLen: %d\n", this.right.offsetX, this.right.offsetY, this.right.getLength());
		System.out.printf("===\n");
	}

	public String getName()
	{
		return name;
	}
}
