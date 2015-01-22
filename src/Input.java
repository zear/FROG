// Input class checks all input changes reported by SDL and stores them
public class Input
{
	public static final int KEY_LEFT = 0;
	public static final int KEY_RIGHT = 1;
	public static final int KEY_UP = 2;
	public static final int KEY_DOWN = 3;
	public static final int KEY_JUMP = 4;
	public static final int KEY_ATTACK = 5;
	public static final int KEY_MENU = 6;

	static void getInput()
	{
		Sdl.input();
	}
}
