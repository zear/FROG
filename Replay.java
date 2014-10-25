import java.util.ArrayList;
import java.util.ListIterator;
import sdljava.*;

public class Replay
{
	private ArrayList<Action> actions = null;
	float startTime = 0;

	boolean record = false;
	boolean play = false;

	protected class Action
	{
		float time;
		int key;
		boolean value;
	}

	public Replay()
	{
		startTime = SDLTimer.getTicks();
		actions = new ArrayList<Action>();
	}

	public void record(int key, boolean value)
	{
		if(this.record)
		{
			Action elem = new Action();
			elem.time = SDLTimer.getTicks() - startTime;
			elem.key = key;
			elem.value = value;

			this.actions.add(elem);
		}
	}

	public void play()
	{
		if(this.play)
		{
			ListIterator<Action> actli = this.actions.listIterator();

			do
			{
				Action curAction = actli.next();
				System.out.printf("time: %f\nkey: %d\nvalue: %d\n", curAction.time, curAction.key, curAction.value != false ? 1 : 0);

			}
			while(actli.hasNext());
		}
	}
}
