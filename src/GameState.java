public interface GameState
{
	public abstract void loadState();
	public abstract void unloadState();
	public abstract void logic();
	public abstract void draw();
}
