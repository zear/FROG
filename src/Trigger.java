enum TriggerType
{
	NONE,
	CHECKPOINT,
	EXITPOINT
}

public class Trigger extends GameObject
{
	private TriggerType triggerType;
	private boolean triggered;

	public TriggerType getTriggerType()
	{
		return this.triggerType;
	}

	public void setTriggerType(TriggerType newType)
	{
		this.triggerType = newType;
	}

	public void setTriggerType(String typeString)
	{
		TriggerType type = TriggerType.NONE;

		if (typeString.equals("CHECKPOINT"))
		{
			type = TriggerType.CHECKPOINT;
		}
		else if (typeString.equals("EXITPOINT"))
		{
			type = TriggerType.EXITPOINT;
		}

		this.triggerType = type;
	}

	public boolean isTriggered()
	{
		return this.triggered;
	}

	public void setTriggered(boolean value)
	{
		this.triggered = value;
	}
}
