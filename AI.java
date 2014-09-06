import java.util.LinkedList;
import java.util.ListIterator;

public class AI
{
	// AI types
	public static final int NONE = 0;
	public static final int WAIT = 1;
	public static final int WALK = 2;
	public static final int JUMP = 3;

	private class AIAction
	{
		private int type;
		private int timer;
		private int origTime;

		// Variables used for action
		private float var1;
		private float var2;
		private float var3;

		public AIAction(int type)
		{
			this.type = type;
		}

		public AIAction(int type, int time)
		{
			this.type = type;
			this.origTime = time;
			this.timer = origTime;
		}

		public int getType()
		{
			return this.type;
		}

		public void setType(int value)
		{
			this.type = value;
		}

		public void doTimer()
		{
			if(this.timer > 0)
				timer--;
		}

		public int getTimer()
		{
			return this.timer;
		}

		public void resetTimer()
		{
			this.timer = origTime;
		}

		public float getVar(int var)
		{
			switch(var)
			{
				case 0:
					return var1;
				case 1:
					return var2;
				case 2:
					return var3;

				default:
					return 0f;
			}
		}

		public void setVar(int var, float value)
		{
			switch(var)
			{
				case 0:
					this.var1 = value;
				break;
				case 1:
					this.var2 = value;
				break;
				case 2:
					this.var3 = value;
				break;

				default:
				break;
			}
		}

	}

	private LinkedList<AIAction> actionList;
	protected ListIterator<AIAction> actionListIterator;
	private AIAction curAction;

	public AI()
	{
		actionList = new LinkedList<AIAction>();
	}

	public boolean hasActions()
	{
		if(this.curAction == null)
			return false;
		return true;
	}

	public void addAction(int type)
	{
		actionList.push(new AIAction(type));
		this.resetActions();
	}

	public void addAction(int type, int time)
	{
		actionList.push(new AIAction(type, time));
		this.resetActions();
	}

	public void resetActions()
	{
		actionListIterator = actionList.listIterator();
		curAction = actionList.getFirst();
	}

	public void setNextAction()
	{
		curAction.resetTimer();
		if(actionListIterator.hasNext())
			curAction = actionListIterator.next();
		else
		{
			this.resetActions();
		}
	}

	public int getType()
	{
		return this.curAction.getType();
	}

	public void doTimer()
	{
		this.curAction.doTimer();
	}

	public int getTimer()
	{
		return this.curAction.getTimer();
	}

	public float getVar(int var)
	{
		return this.curAction.getVar(var);
	}

	public void setVar(int var, float value)
	{
		this.curAction.setVar(var, value);
	}
}
