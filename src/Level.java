import java.util.ArrayList;
import java.util.ListIterator;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

// Level class keeps all that is necessary to have a playable level, such as the list of objects on the map (including player(s))
public class Level
{
	private ArrayList<GameObjectTemplate> objTemp;	// templates for object types // <- deprecated, to remove
	private ArrayList<GameObject> objTemp2;	// game object templates
	private ArrayList<GameObject> objs;		// game objects on the map
	private ArrayList<GameObject> newObjs;		// newly added game objects, to move to "objs" in the next iteration
	private ArrayList<LevelLayer> layers;		// level background layers
	private Collision collision;
	private Camera camera;
	private Gui gui;
	private Font font0;
	private boolean complete;
	private int playTime;

	private Player playerObj = null;

	public Level(String fileName) // constructor
	{
		objTemp = new ArrayList<GameObjectTemplate>();
		objTemp2 = new ArrayList<GameObject>();
		objs = new ArrayList<GameObject>();
		newObjs = new ArrayList<GameObject>();
		layers = new ArrayList<LevelLayer>();
		load(fileName);
		gui = new Gui();
		gui.setLevel(this);
		complete = false;
		playTime = 0;
	}

	public void setFont(Font font)
	{
		this.font0 = font;
		gui.setFont(font0);
	}

	public ArrayList<GameObject> getNewObjs()
	{
		return this.newObjs;
	}

	public boolean isComplete()
	{
		return this.complete;
	}

	public int getPlayTime()
	{
		return this.playTime;
	}

	public boolean isPlayTimeCalculated()
	{
		return gui.isPlayTimeCalculated();
	}

	public Player getPlayer()
	{
		if (playerObj == null)
		{
			for (GameObject curObj : objs)
			{
				if (curObj instanceof Player)
				{
					playerObj = (Player)curObj;
					break; // this assumes there is only one player on the level
				}
			}
		}

		return playerObj;
	}

	private void load(String fileName)
	{
		File file;
		file = new File((Program.levelName != null ? "" : "./data/level/") + fileName);

		FileIO fp = new FileIO(file);


		if (fp != null)
		{
			LevelLayer curElem = null;

			while (fp.hasNext())
			{
				String next = fp.getNext();

				if (next.equals("COLLISION"))
				{
					collision = new Collision(fp.getNext(), Integer.parseInt(fp.getNext()));
				}
				else if (next.equals("LAYER"))
				{
					curElem = new LevelLayer();
					layers.add(curElem);
					System.out.printf("New layer\n");
					curElem.load(Integer.parseInt(fp.getNext()));
				}
				else if (next.equals("IMG"))
				{
					curElem.load("./data/gfx/" + fp.getNext(), Integer.parseInt(fp.getNext()), Integer.parseInt(fp.getNext()), Integer.parseInt(fp.getNext()), Integer.parseInt(fp.getNext()));
					curElem.load(fp); // load the tileset
				}
				else if (next.equals("OBJECTS"))
				{
					loadObjects(fp);
				}
			}
		}

		fp.close();

//		System.out.printf("\nLayer 0: %d, %d\n", layers.get(0).getWidth(), layers.get(0).getHeight());
//		System.out.printf("Layer 1: %d, %d\n", layers.get(1).getWidth(), layers.get(1).getHeight());
//		System.out.printf("Layer 2: %d, %d\n", layers.get(2).getWidth(), layers.get(2).getHeight());
	}

	private void loadObjects(FileIO fp)
	{
		String line;
		String [] words;

		while (fp.hasNext())
		{
			line = fp.getLine();
			words = line.split("\\s");
			GameObject curElem = null;

			if (words[0].equals("END"))
				return;

			try
			{
				loadSingleObject(words[0]);
			}
			catch (Exception e)
			{
				System.out.printf("Failed to load object file: %s\n", words[0]);
				return;
				// todo
			}

			curElem = objs.get(objs.size() - 1);
			curElem.putX(Integer.parseInt(words[1]));
			curElem.putY(Integer.parseInt(words[2]));
			curElem.putDirection(Integer.parseInt(words[3]));
		}
	}

	public void loadSingleObject(String fileName)
	{
		loadSingleObject(fileName, this.objs);
	}

	public void loadSingleObject(String fileName, ArrayList<GameObject> list)
	{
		FileIO fp;
		String line;
		String [] words;
		GameObject newObj = null;

		for (GameObject curObjTemp : objTemp2)
		{
			if (fileName.equals(curObjTemp.getFileName()))
			{
				loadObjectFromTemplate(curObjTemp, list);
				return;
			}
		}

		fp = new FileIO(new File("./data/obj/" + fileName + ".obj"));

		while (fp.hasNext())
		{
			line = fp.getLine();
			if (line == null)
			{
				return;
			}

			words = line.split("\\s");

			if (words[0].equals("END"))
			{
				return;
			}

			if (words[0].equals("TYPE"))
			{
				ObjType type = null;

				if (words[1].equals("OBJECT"))
				{
					objTemp2.add(new GameObject());
					type = ObjType.OBJECT;
					System.out.printf("new object\n");
				}
				else if (words[1].equals("ITEM"))
				{
					objTemp2.add(new Item());
					type = ObjType.ITEM;
					System.out.printf("new item\n");
				}
				else if (words[1].equals("TRIGGER"))
				{
					objTemp2.add(new Trigger());
					type = ObjType.TRIGGER;
					System.out.printf("new trigger\n");
				}
				else if (words[1].equals("CREATURE"))
				{
					System.out.printf("List: %d\n", layers.size());
					objTemp2.add(new Creature(layers.get(1), collision, this)); // this is assuming layer #1 is the middle layer
					type = ObjType.CREATURE;
					System.out.printf("new creature\n");
				}
				else if (words[1].equals("PROJECTILE"))
				{
					objTemp2.add(new Projectile(layers.get(1), collision, this));
					type = ObjType.PROJECTILE;
					System.out.printf("new projectile\n");
				}
				else if (words[1].equals("PLAYER"))
				{
					objTemp2.add(new Player(layers.get(1), collision, this));
					type = ObjType.PLAYER;
					System.out.printf("new player\n");
				}

				newObj = objTemp2.get(objTemp2.size() - 1);
				newObj.setType(type);
				newObj.setFileName(fileName);
			}
			else if (newObj != null)
			{
				if (words[0].equals("NAME"))
				{
					newObj.setName(words[1]);
				}
				else if (words[0].equals("WIDTH"))
				{
					newObj.putW(Integer.parseInt(words[1]));
				}
				else if (words[0].equals("HEIGHT"))
				{
					newObj.putH(Integer.parseInt(words[1]));
				}
				else if (words[0].equals("IMG"))
				{
					newObj.load("./data/gfx/obj/" + words[1], Integer.parseInt(words[2]), Integer.parseInt(words[3]), Integer.parseInt(words[4]), Integer.parseInt(words[5]), objTemp);
				}
				else if (words[0].equals("HP"))
				{
					if (newObj instanceof Creature)
						((Creature)newObj).setHp(Integer.parseInt(words[1]));
				}
				else if (words[0].equals("VULN"))
				{
					newObj.setVulnerability(Integer.parseInt(words[1]) != 0);
				}
				else if (words[0].equals("WALK_V"))
				{
					if (newObj instanceof Player)
					{
						((Player)newObj).setWalkV(Float.parseFloat(words[1]));
					}
				}
				else if (words[0].equals("JUMP_V"))
				{
					if (newObj instanceof Player)
					{
						((Player)newObj).setJumpV(Float.parseFloat(words[1]));
					}
				}
				else if (words[0].equals("TTL"))
				{
					if (newObj instanceof Projectile)
					{
						((Projectile)newObj).setTtl(Integer.parseInt(words[1]));
					}
				}
				else if (words[0].equals("POINTS"))
				{
					if (newObj instanceof Item)
					{
						((Item)newObj).setPoints(Integer.parseInt(words[1]));
					}
				}
				else if (words[0].equals("TRIGGER_TYPE"))
				{
					if (newObj instanceof Trigger)
					{
						((Trigger)newObj).setTriggerType(words[1]);
					}
				}
				else if (words[0].equals("ANIMATION"))
				{
					newObj.addAnimation(fp);
					loadObjectFromTemplate(newObj, list);
				}
			}
		}
	}

	public void loadObjectFromTemplate(GameObject template, ArrayList<GameObject> list)
	{
		GameObject newObj = null;

		if (template != null)
		{
			switch (template.getType())
			{
				case OBJECT:
					list.add(new GameObject());
					newObj = list.get(list.size() - 1);
				break;
				case ITEM:
					list.add(new Item());
					newObj = list.get(list.size() - 1);
				break;
				case TRIGGER:
					list.add(new Trigger());
					newObj = list.get(list.size() - 1);
				break;
				case CREATURE:
					list.add(new Creature(layers.get(1), collision, this)); // this is assuming layer #1 is the middle layer
					newObj = list.get(list.size() - 1);
				break;
				case PROJECTILE:
					list.add(new Projectile(layers.get(1), collision, this)); // this is assuming layer #1 is the middle layer
					newObj = list.get(list.size() - 1);
				break;
				case PLAYER:
					list.add(new Player(layers.get(1), collision, this));
					newObj = list.get(list.size() - 1);
				break;

				default:
				return;
			}
		}

		if (newObj != null)
		{
			newObj.setFileName(template.getFileName());
			newObj.setName(template.getName());
			newObj.setType(template.getType());
			newObj.putW(template.getW());
			newObj.putH(template.getH());
			newObj.setTemplate(template.getTemplate());
			newObj.setVulnerability(template.isVulnerable());
			newObj.setAnimation(template.getAnimation());
			if (newObj instanceof Creature)
			{
				((Creature)newObj).setHp(((Creature)template).getHp());
				((Creature)newObj).loadAI();
			}
			if (newObj instanceof Player)
			{
				((Player)newObj).setWalkV(((Player)template).getWalkV());
				((Player)newObj).setJumpV(((Player)template).getJumpV());
			}
			if (newObj instanceof Projectile)
			{
				((Projectile)newObj).setTtl(((Projectile)template).getTtl());
			}
			if (newObj instanceof Item)
			{
				((Item)newObj).setPoints(((Item)template).getPoints());
			}
			if (newObj instanceof Trigger)
			{
				((Trigger)newObj).setTriggerType(((Trigger)template).getTriggerType());
			}
		}
	}

	public void logic() // logic related to the map and all it's objects
	{
		// Update the objs list with:
		// 1) objects to delete
		ListIterator<GameObject> objsli = objs.listIterator();
		while (objsli.hasNext())
		{
			GameObject curObj = objsli.next();

			if (curObj.getRemoval())
			{
				if (curObj.getName().equals("swoosh") && playerObj.getAttackObjs() != null)
					playerObj.getAttackObjs().remove(curObj);
				objsli.remove();
			}
		}
		// 2) newly added objects
		ListIterator<GameObject> newObjsli = newObjs.listIterator();
		if (newObjsli.hasNext())
		{
			do
			{
				GameObject curObj = newObjsli.next();

				if (curObj.getName().equals("swoosh") && playerObj.getAttackObjs() != null)
					playerObj.getAttackObjs().add(curObj);
				objs.add(curObj);
			}
			while (newObjsli.hasNext());
			newObjs = new ArrayList<GameObject>();
		}

		if (playerObj == null)
		{
			getPlayer();
		}

		if (playerObj != null)
		{
			if (camera == null)
			{
				camera = playerObj.viewport;
				camera.setCamera(playerObj);
			}
			else if (playerObj.hp > 0)
			{
				camera = playerObj.viewport;
			}

			gui.setPlayer(playerObj);
			// check input
			playerObj.updateKeys();

			if (!playerObj.isDead())
			{
				if (playerObj.acceptInput())
				{
					if (playerObj.getAction(0))		// left
						playerObj.walk(-playerObj.getWalkV());
					if (playerObj.getAction(1))		// right
						playerObj.walk(playerObj.getWalkV());
					if (playerObj.getAction(2))		// up
					{
						if (playerObj.isClimbing)
						{
							playerObj.climb(-1f);
						}
						else
						{
							playerObj.tryClimb();
						}
					}
					if (playerObj.getAction(3))		// down
					{
						if (playerObj.isClimbing)
						{
							playerObj.climb(1f);
						}
						else	// crouch
						{
							playerObj.tryCrouch();
						}
					}
					if (playerObj.getAction(4))		// jump
					{
						playerObj.setAction(2, false);
						playerObj.setAction(4, false);
						playerObj.jump(-playerObj.getJumpV());
					}
					if (playerObj.getAction(5))		// attack
					{
						int x;

						//playerObj.setAction(5, false);
						playerObj.setAcceptInput(false);

						if (!playerObj.direction)	// left
						{
							x = (int)playerObj.x - 15;
						}
						else			// right
						{
							x = (int)playerObj.x + playerObj.w;
						}

						try
						{
							loadSingleObject("swoosh", newObjs);
						}
						catch (Exception e)
						{
							System.out.printf("Failed to load object file\n");
							return;
							// todo
						}

						Projectile swoosh = (Projectile)newObjs.get(newObjs.size() - 1);

						swoosh.putX(x);
						if (playerObj.isCrouching)
							swoosh.putY((int)playerObj.y + 14);
						else
							swoosh.putY((int)playerObj.y + 7);
						swoosh.putDirection(playerObj.direction ? 1 : 0);
						swoosh.affectedByGravity = false;
					}
				}
				else	// TODO: This should be solved in a cleaner way.
				{
					playerObj.setAction(5, false);
				}
			}

			playerObj.doAi();
			playerObj.move();
		}

		Player player = playerObj;

		for (GameObject tmpObj : objs)
		{
			// Check triggers
			if (tmpObj instanceof Trigger)
			{
				Trigger tmpTrigger = (Trigger)tmpObj;
				if (tmpTrigger.isTriggered())
				{
					switch (tmpTrigger.getTriggerType())
					{
						case EXITPOINT:
							camera.setTarget((int)tmpTrigger.getX(), (int)tmpTrigger.getY());
							player.setVulnerability(false);
							this.complete = true;
							//GameStateGame.leaveGame = true;
						break;

						default:
						break;
					}
				}
			}

			// Activity zone around the player - only objects within this zone have the logic computed
			int zoneX = this.camera.getX() - 20;
			int zoneY = this.camera.getY() - 20;
			int zoneW = Sdl.SCREEN_WIDTH + 40;
			int zoneH = Sdl.SCREEN_HEIGHT + 40;
			int ox = (int)tmpObj.x;
			int oy = (int)tmpObj.y;

			if ((ox + tmpObj.w - 1 > zoneX && ox < zoneX + zoneW) && (oy + tmpObj.h - 1 > zoneY && oy < zoneY + zoneH))
			{
				if (tmpObj instanceof Creature && tmpObj != player)
				{
					Creature tmpCreature = (Creature)tmpObj;

					int px = (int)player.x;
					int py = (int)player.y;
					int cx = (int)tmpCreature.x;
					int cy = (int)tmpCreature.y;

					tmpCreature.doAi();
					tmpCreature.move();

					if (player.isVulnerable() && !player.isDead() && !tmpCreature.getName().equals("swoosh"))
					{
						if ((px >= cx && px <= cx + tmpCreature.w - 1) || (px + player.w - 1 >= cx && px + player.w - 1 <= cx + tmpCreature.w - 1) || (px < cx && px + player.w - 1 > cx + tmpCreature.w - 1))
						{
							if ((py >= cy && py <= cy + tmpCreature.h - 1) || (py + player.h - 1 >= cy && py + player.h - 1 <= cy + tmpCreature.h - 1) || (py < cy && py + player.h - 1 > cy + tmpCreature.h - 1))
							{
								// Pushes the player away from the creature.
								if (px + (player.w - 1)/2 > cx + (tmpCreature.w - 1)/2)
								{
									player.vx += 1;
								}
								else
								{
									player.vx -= 1;
								}

								player.hp--;
								player.setInvincibility(90);
								player.setBlinking(90);

								if (player.hp <= 0)
								{
									camera.setTarget(tmpCreature);
								}
								//player.setAcceptInput(false);

								// Pushes the creature away from the player.
//								if (px + (player.w - 1)/2 > cx + (tmpCreature.w - 1)/2)
//								{
//									tmpCreature.vx -= 2;
//								}
//								else
//								{
//									tmpCreature.vx += 2;
//								}
							}
						}
					}
				}
				else if (tmpObj instanceof Item && tmpObj != player)
				{
					Item tmpItem = (Item)tmpObj;

					int px = (int)player.x;
					int py = (int)player.y;
					int ix = (int)tmpItem.x;
					int iy = (int)tmpItem.y;

					if ((px >= ix && px <= ix + tmpItem.w - 1) || (px + player.w - 1 >= ix && px + player.w - 1 <= ix + tmpItem.w - 1) || (px < ix && px + player.w - 1 > ix + tmpItem.w - 1))
					{
						if ((py >= iy && py <= iy + tmpItem.h - 1) || (py + player.h - 1 >= iy && py + player.h - 1 <= iy + tmpItem.h - 1) || (py < iy && py + player.h - 1 > iy + tmpItem.h - 1))
						{
							player.addScore(tmpItem.getPoints());
							tmpItem.setRemoval(true);
						}
					}
				}
				else if (tmpObj instanceof Trigger && tmpObj != player)
				{
					Trigger tmpTrigger = (Trigger)tmpObj;

					int px = (int)player.x;
					int py = (int)player.y;
					int ix = (int)tmpTrigger.x;
					int iy = (int)tmpTrigger.y;

					if ((px >= ix && px <= ix + tmpTrigger.w - 1) || (px + player.w - 1 >= ix && px + player.w - 1 <= ix + tmpTrigger.w - 1) || (px < ix && px + player.w - 1 > ix + tmpTrigger.w - 1))
					{
						if ((py >= iy && py <= iy + tmpTrigger.h - 1) || (py + player.h - 1 >= iy && py + player.h - 1 <= iy + tmpTrigger.h - 1) || (py < iy && py + player.h - 1 > iy + tmpTrigger.h - 1))
						{
							tmpTrigger.setTriggered(true);
						}
					}
				}
			}
		}

		// check collision with other objects
		for (GameObject curObj : objs)
		{
			curObj.logic();

			if (curObj.isVulnerable() && curObj != playerObj) // check if can get damage
			{
				ArrayList<GameObject> attackObjs = playerObj.getAttackObjs();
				for (GameObject tmpObj : attackObjs)
				{
					if (tmpObj instanceof Creature && !(curObj instanceof Item) && !(curObj instanceof Trigger) && tmpObj != curObj && tmpObj.getName().equals("swoosh"))
					{
						Creature tmpCreature = (Creature)tmpObj;

						int ox = (int)curObj.x;
						int oy = (int)curObj.y;
						int cx = (int)tmpCreature.x;
						int cy = (int)tmpCreature.y;

						if ((ox >= cx && ox <= cx + tmpCreature.w - 1) || (ox + curObj.w - 1 >= cx && ox + curObj.w - 1 <= cx + tmpCreature.w - 1) || (ox < cx && ox + curObj.w - 1 > cx + tmpCreature.w - 1))
						{
							if ((oy >= cy && oy <= cy + tmpCreature.h - 1) || (oy + curObj.h - 1 >= cy && oy + curObj.h - 1 <= cy + tmpCreature.h - 1) || (oy < cy && oy + curObj.h - 1 > cy + tmpCreature.h - 1))
							{
								curObj.setRemoval(true);
								tmpCreature.setRemoval(true);
							}
						}
					}
				}
			}
		}

		if (!playerObj.isDead() && !this.complete)
			playTime++;
	}

	public void draw() // draws map layers, objects and all the other map related stuff
	{
		if (playerObj.hp > 0 && !this.complete)
		{
			camera.setTarget(playerObj);
		}
		else
		{
			camera.setTarget(camera.getTarget());
		}

		camera.track(layers.get(1), false); // update the camera

		//Draw uniform background.
		try
		{
			//Sdl.screen.fillRect(SDLVideo.mapRGB(Sdl.screen.getFormat(), 0, 191, 243));
			Sdl.screen.fillRect(1534);
		}
		catch (Exception e)
		{
			//todo
		}

		// Draw level layers.
		for (LevelLayer curLayer : layers)
		{
			if (curLayer.getId() == 2) // Draw game objects before drawing the foreground layer.
			{
				for (GameObject curObj : objs)
				{
					curObj.draw(camera);
				}
			}

			curLayer.draw(camera);
		}

		// Draw gui.
		this.gui.draw();
	}
}
