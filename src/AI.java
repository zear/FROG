import java.util.ArrayList;
import java.util.ListIterator;

public class AI
{
	// AI types
	public static final int NONE		= 0;
	public static final int WAIT		= 1;
	public static final int WALK		= 2;
	public static final int JUMP		= 3;
	public static final int FLY		= 4;
	public static final int SPAWN_OBJ	= 5;
	public static final int TURN		= 6;

	// AI action var names
	public static final int WALK_VX		= 0;
	public static final int WALK_DROP	= 1;
	public static final int JUMP_VX		= 0;
	public static final int JUMP_VY		= 1;
	public static final int FLY_VX		= 0;
	public static final int FLY_AMPLITUDE	= 1;
	public static final int FLY_PERIOD	= 2;
	public static final int SPAWN_OBJ_OBJVX	= 1;
	public static final int SPAWN_OBJ_OBJVY	= 2;

	private class AIAction
	{
		private int type;
		private int timer;
		private int origTime;

		// Variables used for sine wave
		private int sinePeriod;

		// Variables used for action
		private float var1;
		private float var2;
		private float var3;
		private String objName;

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

		public int getSinePeriod()
		{
			return this.sinePeriod;
		}

		public void decreaseSinePeriod(int step)
		{
			this.sinePeriod-= step;

			if(this.sinePeriod < 0)
			{
				this.sinePeriod = SineTable.STEPS - 1;
			}
		}

		public void increaseSinePeriod(int step)
		{
			this.sinePeriod+= step;

			if(this.sinePeriod >= SineTable.STEPS)
			{
				this.sinePeriod = 0;
			}
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

		public void setVar(String objName)
		{
			this.objName = objName;
		}

		public String getObjName()
		{
			return this.objName;
		}
	}

	private ArrayList<AIAction> actionList;
	protected ListIterator<AIAction> actionListIterator;
	private AIAction curAction;

	public AI()
	{
		actionList = new ArrayList<AIAction>();
	}

	public boolean hasActions()
	{
		if(this.curAction == null)
			return false;
		return true;
	}

	public void addAction(int type)
	{
		actionList.add(new AIAction(type));
		this.resetActions();
	}

	public void addAction(int type, int time)
	{
		actionList.add(new AIAction(type, time));
		this.resetActions();
	}

	public void resetActions()
	{
		actionListIterator = actionList.listIterator();
		curAction = actionList.get(0);
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

	public int getSinePeriod()
	{
		return this.curAction.getSinePeriod();
	}

	public void decreaseSinePeriod(int step)
	{
		this.curAction.decreaseSinePeriod(step);
	}

	public void increaseSinePeriod(int step)
	{
		this.curAction.increaseSinePeriod(step);
	}

	public float getVar(int var)
	{
		return this.curAction.getVar(var);
	}

	public String getObjName()
	{
		return this.curAction.getObjName();
	}

	public void setVar(int var, float value)
	{
		this.curAction.setVar(var, value);
	}

	public void setVar(String objName)
	{
		this.curAction.setVar(objName);
	}
}
