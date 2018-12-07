/*	Author: Rich Goluszka
 * 	Project Name: Target Practice v1.0
 * 	For CPSC24500-1 -- Object-Oriented Programming with Dr. Klump
 * 
 *  	This was a solo project meant to test my ability to integrate physics
 *  and collision detection into programming within a GUI environment, as well
 *  as test the abilities of the javax.swing.* and java.awt.* libraries. Throwing
 *  knives/hatchets is a hobby of mine so it seemed like a logical start, the archery 
 *  target inspiration came from my involvement with the archery club at Lewis. 
 *  Power-scaling throws and scaling the target was included to make the game more engaging 
 *  and less dull, and came as an afterthought, while throwing-knife drop was an idea from the 
 *  start.
 *  
 *  	When you run the program, it generates the game window and loads into challenge mode.
 *  This mode gives you three chances to score, after which you can add your score to the local
 *  high-score list and export the list to a .bin file before closing the game under the Score menu.
 *  Loading a score is also done through the score menu, the top score in the list can be viewed in the
 *  score menu.
 *  Throwing is simple, just click and hold to build power, and release to throw. Drop & Power are used
 *  to calculate the final x and y location of the knife. You can save your game as a .txt file before 
 *  using all three throws in the Game menu, and load it at any time.
 * 		There are two modes to play in, practice mode and challenge mode. Challenge mode has scoring
 * 	enabled and limits the user to three throws before having to start a new game in the Game menu.
 * 	Practice mode has scoring disabled and gives the user infinite throws. Each game type can be saved
 * 	and loaded into its respective mode. Loading practice saves into challenge mode will import the last
 * 	three throws, while loading challenge saves into practice mode will load all throws.
 *		The target is based on a real-life archery competition target, it has ten circles, the center is worth
 *	10 points and each circle out is worth one less than the circle it encapsulates. The last circle is worth
 *	1 point, anything that isn't the target is worth 0 points. It can be scaled closer or further away through
 *	the scale menu, which treats it like moving closer to or further from the target and affects drop. Powerful
 *	throws close-up will go over the target, weak throws far away will go under the target.
 *		If the user tries to do something disallowed, a message will pop up in place of the mode indicator explaining
 *	the problem to the user, clicking the OK button will return it to the previous message.
 *
 *		I'd like to add score scaling with the current target scaling, the groundwork is already there in the
 *	code but I'd have to update the UserThrownObject model class and update how data is saved/read in from .txt
 *	files to do so. Another improvement would be a background to display behind the target, either a user-selected
 *	image or a static image/color. XML saving support for the UserThrownObject and ThrownObjectTarget classes
 *	would make loading, saving, and making custom game files easier. A separate display for viewing the high score
 *	list, which currently only displays the top score would also be interesting to add. 
 *
 *	Menu Summary:
 *	File
 *		Exit: quits the game
 *	Game
 *		New Game: starts a new game, clearing old points and resetting score/throw counters
 *		Save Game (.txt): saves the state of the current game in a .txt file
 *		Load Game (.txt): loads the state of a saved game, should be loaded in the same mode as the save
 *	Scores
 *		Add My Score: adds the user's current score to the local scoreList unless it is lower than their previously saved score
 *		Show Top Score: displays the highest score in the list 
 *		Save Scores List (.bin): copies the local scoresList into a .bin file
 *		Load Scores List (.bin): sets scoresList as the list from the .bin file to compete against
 *	Scale
 *		Large: the maximum size of the target, makes it seem closer (~ 1 Meter)
 *		Medium: the default size of the target, easy with some practice (~ 2 Meters)
 *		Small: the minimum size of the target, makes it seem distant (~ 3 Meters)
 *	Mode
 *		Challenge Mode: default gamemode, starts a new game with three throws for scoring competition
 *		Practice Mode: alternate gamemode, starts a new game with infinite throws without scoring
 *  
 *	Further information about the game can be found in the README.md file which contains this information and more.
 *  
 *  Model Classes:
 *  	UserThrownObject: Stores information about the throwing knives, stored in the .txt game-save
 *  	ThrownObjectTarget: Stores information about the target, stored in the .txt game-save
 *  	Score: Stores information about the scores in the list, stored as an ArrayList of scores in the .bin score-save
 *  
 *  View Classes:
 *  	MainFrame: Main window of the program, contains InfoPanel, ScorePanel, and TargetPanel
 *  	EntryFrame: Score-entry window of the program
 *  	InfoPanel: Displays game mode or warning message
 *  	ScorePanel: Displays the score counters and throw counter on the window
 *  	TargetPanel: Displays the target and the hit locations on the window
 * 	
 * 	Controller Classes:
 * 		ObjectTargetWindowController: Main controller of the program, manages interactions 
 * 			between models, updates the views, manages saving & loading into model instances
 * 			from the other controller, acts as a mouseListener for the TargetPanel, abstracts
 * 			the process of throwing a knife, etc.
 * 		ObjectFileController: Manages reading and writing of files (.bin for scores, .txt for game
 * 			saves), used by the ObjectTargetWindowController to load model values
 * 		UIGameMVC: Public class, instantiates ObjectTargetWindowController, the three 
 * 			UserThrownObject instances, the ObjectFileController, and passes control to the 
 * 			ObjectTargetWindowController	
 *  
 *  Serialization:
 *  	Text Serialization -- Game State Saving/Loading
 *  	Binary Serialization -- High Score Saving/Loading (behaves more like
 *  		exporting/importing)
 *  
 *  Program Layout:
 *  	1. Imports
 *  	2. Model Classes
 *  		-UserThrownObject (Line 153)
 *  		-ThrownObjectTarget	(Line 272)
 *  		-Score (Line 351)
 *  	3. Controller Classes
 *  		-ObjectFileController (Line 395)
 *  		-ObjectTargetWindowController (Line 476)
 *  	4. View Classes
 *  		-ScorePanel (Line 696)
 *  		-TargetPanel (Line 778)
 *  		-InfoPanel (Line 852)
 *  		-EntryFrame (Line 892)
 *  		-MainFrame (Line 931)
 *  	5. Public Class (Line 1116)
 *  
 *  Class Layout:
 *  	1. private data variable declarations
 *  	1. constructor(s)
 *  	2. getters & setters
 *  	3. methods
 */

//imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


/*Model class for objects to be thrown at the target in the game*/
class UserThrownObject {
	private boolean isThrown;	//Monitor which instances have been 'Thrown' by the user
	private double length;		//Store it's length in centimeters for calculating weight
	private double weight;		//Store it's weight in ounces for calculating drop
	private int pointsEarned;	//Store the point zone it landed in on the target after being thrown
	private int xFin;			//Store the final x-value for game-state loading support
	private int yFin;			//Store the final y-value for game-state loading support
	
	public UserThrownObject(double len, double wt) {	//Allows the user to set a custom length and weight for throwing knives, done through save-modification
		this(len,wt,0,false);							//Their settings are still bound by the set functions							
	}
	public UserThrownObject(double len, double wt, int points, boolean thrown) {	//Meant for creating UserThrownObjects from a loaded save
		setIsThrown(thrown);
		setLength(len);
		setWeight(wt);
		setPointsEarned(points);
		xFin = -10;
		yFin = -10;
	}
	public UserThrownObject() {	//Quick creation of standard knives 
		this(7.8125,0);			//Sets an average throwing knife length in inches, sets the weight according to the length
	}
	
	public boolean getIsThrown() {
		return isThrown;
	}
	public void setIsThrown(boolean state) {
		isThrown = state;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double len) {
		if (len > 5.9 && len < 12.1) {	//Throwing knives are typically between 6" and 12"
			length = 2.54*len;			//Converts user-entered inches into centimeters for consistency
		} else {
			length = 6.0;
		}
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		if (weight > 0) {
			this.weight = weight;
		} else {
			this.weight = 1.5*(length/2.54);	//Throwing knives typically weigh 1.5 ounces per inch, length is stored as centimeters
		}
	}
	public int getPointsEarned() {
		return pointsEarned;
	}
	public void setPointsEarned(int points) {
		pointsEarned = points;
	}
	public int getXFin() {
		return xFin;
	}
	public void setXFin(int xVal) {
		if (xVal >= 0) {
			xFin = xVal;
		} else {
			xFin = -10;
		}
	}
	public int getYFin() {
		return yFin;
	}
	public void setYFin(int yVal) {
		if (yVal >=0) {
			yFin = yVal;
		} else {
			yFin = -10;
		}
	}
	public int[] getFinPoint() {		//Shortcut for getXFin() and getYFin() to use for saving
		int[] tmpArray = {xFin,yFin};
		return tmpArray;
	}
	public void setFinPoint(int[] point) {	//Shortcut for setXFin() and setYFin() to use for loading
		setXFin(point[0]);
		setYFin(point[1]);
	}
	
	public int[] throwObject(int x, int y, int throwStrength, double scale) {
		/*	This list of values will be used to determine how much/little drop the throwing knife will
		 * 		have in the game. The index of one is pulled based on how long the user holds the mouse
		 * 		button down.
		 * 	The list is sorted strongest power to weakest power, the knife will fly up for higher powers and
		 * 		down for weak powers (as if it were influenced by gravity).
		 * 	Numbers were chosen based on a translation matrix which would move the starting point of the knife
		 * 		to the top of the target (index 10), and another transition matrix which would move the starting
		 * 		point of the knife to the bottom of the target (index 0). Then the range was used to determine
		 * 		
		 */
		Random rnd = new Random();
		double[] strDropVals = new double[10];
		for (int i = 0; i < 10; i++) {
			strDropVals[i] = (1.16 - ((1.16-.39077)/9)*i);
		}
		int endX = rnd.nextInt(20)-10 + x;	//The object will land within 20px of either side from what the user targets
		if (throwStrength > 9) {			//Limit max throw strength through a check
			throwStrength = 9;
		} else if (throwStrength < 0) {
			throwStrength = 0;
		}
		int endY = (int)(y*strDropVals[throwStrength]/scale);	//Scale drop based on throwStrength and transformation matrix
		int[] retArray = {endX, endY};
		return retArray;										//return the final point
	}
	@Override
	public String toString() {
		//Formatted to save the game state as plain-text easily using a PrintWriter
		return String.format("%07.4f %07.4f %d %b %d %d",length,weight,pointsEarned,isThrown,xFin,yFin);
	}
}


/*Model class for the target which the user will throw objects at in the game*/
class ThrownObjectTarget {
	private double distance;	//Distance from player to target in meters, for easier calculations
	private int leftX;			//Identifies the left edge of the target in the TargetPanel
	private int topY;			//Identifies the top edge of the target in the TargetPanel
	private double scale;		//Identifies the scale of the target in the TargetPanel
	
	public ThrownObjectTarget(int x, int y, double scale) {
		setDistance(2);										//Sets the distance to a reasonable 2 meters (~6.56 ft) from the player to the target
		setLeftX(x);
		setTopY(y);
		setScale(scale);
	}
	
	public double getDistance() {
		return distance;
	}
	public void setDistance(double dist) {
		if (dist > 2) {						//Minimum safe distance to stand from the target when throwing knives
			distance = dist;
		} else {
			distance = 2;
		}
	}
	public int getLeftX() {
		return leftX;
	}
	public void setLeftX(int x) {
		if (x > 0) {
			leftX = x;
		} else {
			leftX = 0;
		}
	}
	public int getTopY() {
		return topY;
	}
	public void setTopY(int y) {
		if (y > 0) {
			topY = y;
		} else {
			topY = 0;
		}
	}
	public double getScale() {
		return scale;
	}
	public void setScale(double scaleVal) {
		if (scale == 0.5 || scale == 1.0 || scale == 2.0) {	//Possible scale options for the ThrownObjectTarget
			scale = scaleVal;
		} else {
			scale = 1;
		}
	}
	
	public int getPointZone(int[] hitLoc, double scale) {
		int x = hitLoc[0];
		int y = hitLoc[1];
		int centerX = leftX+40;								//Locates the centerX value of the target in the TargetPanel
		int centerY = topY+40;								//Locates the centerY value of the target in the TargetPanel
		int ringWidth = (int)(4*scale);						//Initializes the ring-width based on the scale of the TargetPanel
		int score = 10;
		for (int i = ringWidth; i < 40*scale; i+= ringWidth) {						//Step through the rings and check if the point is in them
			if ((x>=centerX-i && x<=centerX+i) && (y>=centerY-i && y<=centerY+i)) {
				return score;														//Calculate and return the score based on the ring it lands in
			} else {
				score -= 1;
			}
		}
		return 0;
	}
	@Override
	public String toString() {
		//Formatted to save the game state as plain-text easily using PrintWriter
		return String.format("%.1f",scale);
	}
}


/*Model class for scores to store in the ObjectTargetWindowController.scoreList ArrayList, saved in the .bin files*/
class Score implements Serializable {
	private String name;			//Hold the name of the person with the score
	private double pointsEarned;	//Hold the points they earned
	private double totalPoints;		//Hold the points they could have earned
	
	public Score(String name, double ptsEarned, double totalPts) {
		setName(name);
		setPointsEarned(ptsEarned);
		setTotalPoints(totalPts);
	}
	public Score(String name) {
		this(name,0,0);			//Create a default score of 0,0 to be changed with setTotalPoints() and setPointsEarned()
	}
	public Score(int ptsEarned, int totalPts) {
		this("Unknown",ptsEarned,totalPts);		//Create a default score 
	}
	
	public String getName() {
		return name;
	}
	public void setName(String newName) {
		name = newName;
	}
	public double getPointsEarned() {
		return pointsEarned;
	}
	public void setPointsEarned(double pts) {
		pointsEarned = pts;
	}
	public double getTotalPoints() {
		return totalPoints;
	}
	public void setTotalPoints(double pts) {
		totalPoints = pts;
	}
	
	@Override
	public String toString() {
		return String.format("%s %.2f %.2f %.2f",name,pointsEarned,totalPoints,(double)(pointsEarned)/(double)(totalPoints));
	}
}


/*Controller class for interactions between ObjectTargetWindowController (Which represents the View Classes) and Files, called by MainFrame JMenuBar*/
class ObjectFileController {
	public boolean writeGameToTextFile(ArrayList<UserThrownObject> throwObjects, ThrownObjectTarget tar, File f) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			for (UserThrownObject thrObj : throwObjects) {
				pw.println(thrObj);														//Save Each UserThrownObject in the ArrayList throwObjects
			}
			pw.println(tar);															//Save the ThrownObjectTarget too
			pw.close();
			return true;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,"File Save Error -- Throws, Aborting!");	//Pop-up an error if saving doesn't work for any reason
			return false;
		}
	}
	public ArrayList<UserThrownObject> loadThrowsFromTextFile(File f) {
		try {
			Scanner sc = new Scanner(f);
			ArrayList<UserThrownObject> throwObjects = new ArrayList<UserThrownObject>();
			String line;
			String[] parts;
			while (sc.hasNextLine()) {
				line = sc.nextLine().trim();
				if (line.contains(" ")) {		//Check if the line is properly formatted
					parts = line.split(" ");	//Create a new UserThrownObject and set their XFin and YFin values according to the save
					throwObjects.add(new UserThrownObject(Double.parseDouble(parts[0]),Double.parseDouble(parts[1]),Integer.parseInt(parts[2]),Boolean.parseBoolean(parts[3])));
					throwObjects.get(throwObjects.size()-1).setXFin(Integer.parseInt(parts[4]));
					throwObjects.get(throwObjects.size()-1).setYFin(Integer.parseInt(parts[5]));
				}
			}
			sc.close();
			return throwObjects;
		} catch (Exception ex) {				//Pop-up an error if loading doesn't work for any reason
			JOptionPane.showMessageDialog(null,"File Read Error -- Throws, Aborting!");
			return null;
		}
	}
	public ThrownObjectTarget loadTargetFromTextFile(File f, MainFrame mf) {
		try {
			Scanner sc = new Scanner(f);
			ThrownObjectTarget tar = new ThrownObjectTarget(mf.getTargetPanel().getLeftX(),mf.getTargetPanel().getTopY(),1);
			String line;
			while (sc.hasNextLine()) {
				line = sc.nextLine().trim();
				if (!line.contains(" ")) {
					tar.setScale(Double.parseDouble(line));
				}
			}
			sc.close();
			return tar;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "File Read Error -- Target, Aborting!");
			return null;
		}
	}
	public boolean writeScoreListToFile(ObjectTargetWindowController ctrl, File f) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(ctrl.getScoreList());		//Save the scoreList ArrayList of Score Objects
			oos.close();
			return true;
		} catch (Exception ex) {						//Pop-up an error if saving doesn't work for any reason
			JOptionPane.showMessageDialog(null,"File Save Error -- Scores, Aborting!");
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public ArrayList<Score> loadScoreListFromFile(File f) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			ArrayList<Score> loadedScoreList = (ArrayList<Score>)(ois.readObject());
			ois.close();
			return loadedScoreList;
		} catch (Exception ex) {
			return null;
		}
	}
}


/*Controller class for interactions between UserThrownObject, ThrownObjectTarget, and the MainFrame*/
class ObjectTargetWindowController implements MouseListener,ActionListener {
	private ArrayList<UserThrownObject> throwObjects;	//Hold the throwing-knife objects
	private ArrayList<Score> scoreList;					//Hold a list of scores locally
	private int objectsThrown;							//Track the number of objects thrown
	private ThrownObjectTarget targetObject;			//Hold the targetObject
	private MainFrame mf;								//hold the mainframe, used to access all view classes
	private Timer tim;									//Used for implementing power through held-clicks
	private int clickStr;								//Track power according to the Timer
	private String mode;								//Track the game-mode
	
	public ObjectTargetWindowController(ArrayList<UserThrownObject> throwObjects, ObjectFileController txtGameSaver) {
		this.throwObjects = throwObjects;
		scoreList = new ArrayList<Score>();
		mf = new MainFrame(this,txtGameSaver);
		mf.setVisible(true);
		targetObject = new ThrownObjectTarget(mf.getTargetPanel().getLeftX(),mf.getTargetPanel().getTopY(),mf.getTargetPanel().getScale());	//keep the target in-sync with TargetPanel
		objectsThrown = 0;
		clickStr = 0;
		tim = new Timer(200,this);
		setMode("");
	}
	
	public ArrayList<Score> getScoreList() {
		return scoreList;
	}
	public void setScoreList(ArrayList<Score> loadedScores) {
		scoreList = loadedScores;
	}
	public ArrayList<UserThrownObject> getThrowObjects() {
		return throwObjects;
	}
	public void setThrowObjects(ArrayList<UserThrownObject> loadedObjects) {
		objectsThrown = 0;
		for (int i = 0; i < loadedObjects.size(); i++) {
			if (i < throwObjects.size()) {
				throwObjects.set(i,loadedObjects.get(i));
			} else {
				throwObjects.add(loadedObjects.get(i));
			}
			if (loadedObjects.get(i).getIsThrown()) {
				objectsThrown += 1;
			}
		}
	}
	public ThrownObjectTarget getTargetObject() {
		return targetObject;
	}
	public void setTargetObject(ThrownObjectTarget loadedTar) {
		targetObject.setScale(loadedTar.getScale());
	}
	public MainFrame getMainFrame() {
		return mf;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String modeStr) {
		if (modeStr.equalsIgnoreCase("p")) {
			mode = "p";
			mf.getScorePanel().configurePracticeMode();
		} else if (modeStr.equalsIgnoreCase("c")) {
			mode = "c";
			mf.getScorePanel().configureChallengeMode();
		} else {
			mode = "c";
		}
	}
	
	public void resetGame(String mode) {	//Used for the New Game menu option
		objectsThrown = 0;
		for (UserThrownObject uto : throwObjects) {
			uto.setIsThrown(false);
			uto.setPointsEarned(0);
		}
		while (throwObjects.size() > 3) {
			throwObjects.remove(throwObjects.size()-1);
		}
		mf.getScorePanel().resetAll();
		mf.getTargetPanel().resetAll();
		if (mode.equalsIgnoreCase("c")) {
			mf.getInfoPanel().setMsg("Challenge Mode");
		} else if (mode.equalsIgnoreCase("p")) {
			mf.getInfoPanel().setMsg("Practice Mode");
		}
		
	}
	public void loadGame(ArrayList<UserThrownObject> loadedObjects, ThrownObjectTarget loadedTarget) {	//Used for the Load Game menu option
		mf.getTargetPanel().resetAll();
		setThrowObjects(loadedObjects);
		setTargetObject(loadedTarget);
		mf.getTargetPanel().setScale(loadedTarget.getScale());
		mf.getScorePanel().setLastScore(0);
		mf.getScorePanel().setRunningScore(0);
		mf.getScorePanel().setRemThrows(3);
		if (mode.equalsIgnoreCase("c")) {	//Prevent people from importing practice saves into challenge mode & glitching a score
			mf.getInfoPanel().setMsg("Challenge Mode");
			if (throwObjects.size() > 3) {
				mf.getInfoPanel().setMsg("Practice Save in Challenge Mode - last 3 throws only");	//Warning message for user if modes dont match
			}
			while (throwObjects.size() > 3) {	//We're allowing them to bring over the last three throws from their practice save.
				throwObjects.remove(0);
			}
		} else {
			mf.getInfoPanel().setMsg("Practice Mode");
		}
		for (UserThrownObject uto : throwObjects) {	//Step through UserThrownObjects loaded, update points and show the hit like it was thrown
			if (uto.getIsThrown()) {
				mf.getScorePanel().updateScores(uto.getPointsEarned());
				mf.getTargetPanel().addHit(uto.getFinPoint());
			}
		}
	}
	public void addScore(String name) {					//Add a score to the local scoreList
		boolean addScore = false;
		if (scoreList.size() > 0) {
			for (Score s : scoreList) {					//If their score is better than their last score
				if ((s.getPointsEarned() < tallyPointsRaw()) && (s.getTotalPoints() >= throwObjects.size()*10)) {
					addScore = true;
				}
			}
		} else {										//If scoreList.size() == 0, no scores, let them add one
			addScore = true;
		}
		if (addScore && mode.equalsIgnoreCase("c")) {	//Only add scores in challenge mode
			scoreList.add(new Score(name,tallyPointsRaw(),throwObjects.size()*10));
		}
	}
	public void checkScoreList() {	//Find the maximum score, send to InfoPanel
		double maxScore = 0;
		int maxScoreIndex = scoreList.size()-1;
		if (scoreList.size() == 0) {
			mf.getInfoPanel().setMsg("Add a score or import a scoreList first!");
		} else {
			for (int i = 0; i < scoreList.size(); i++) {
				if (scoreList.get(i).getPointsEarned() > maxScore) {
					maxScore = scoreList.get(i).getPointsEarned();
					maxScoreIndex = i;
				}
			}
			mf.getInfoPanel().setMsg(scoreList.get(maxScoreIndex).toString());
		}
	}
	public void performThrow(int x, int y, int throwStrength) {	//Handle throwing the knife and class interactions through one function
		targetObject.setLeftX(mf.getTargetPanel().getLeftX());	//Resets the top-left corner of the targetObject in the model class
		targetObject.setTopY(mf.getTargetPanel().getTopY());		//in case the window has been resized
		targetObject.setScale(mf.getTargetPanel().getScale());	//Keeps the TargetPanel scale & the ThrownObjectTarget scale in-sync
			
		//Gets the user-thrown object, throws it, and calculates points earned
		UserThrownObject tmpObj = throwObjects.get(objectsThrown);
		int[] finalPoint = tmpObj.throwObject(x, y, throwStrength,targetObject.getScale());
		tmpObj.setFinPoint(finalPoint);
		tmpObj.setPointsEarned(targetObject.getPointZone(finalPoint,targetObject.getScale()));
		tmpObj.setIsThrown(true);
		mf.getTargetPanel().addHit(finalPoint);
		objectsThrown += 1;
		mf.getScorePanel().updateScores(tmpObj.getPointsEarned());	//Updates the ScorePanel
	}
	public int tallyPointsRaw() {	//Figure out how many points they've earned
		int pointCount = 0;
		for (UserThrownObject ut : throwObjects) {
			if (ut.getIsThrown()) {
				pointCount += ut.getPointsEarned();
			}
		}
		return pointCount;
	}
	public void closeEntryFrame() {	//Used for managing score additions through a dialog window (which is another JFrame)
		if (mf.getScoreEntryFrame().wantsToSave() && mode.equalsIgnoreCase("c")) {
			addScore(mf.getScoreEntryFrame().getNameField());
		}
		mf.getScoreEntryFrame().setVisible(false);
	}
	
	//MouseListener Functions
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {	//Start the timer for power-leveling throws
		if (!tim.isRunning()) {
			clickStr = 0;
			tim.start();
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {	//Stop the timer for power-leveling throws, do the throw
		if (tim.isRunning()) {
			tim.stop();
			if (!(objectsThrown > (throwObjects.size()-1))) {
				performThrow(e.getX(),e.getY(),clickStr);
			} else if (objectsThrown == throwObjects.size() && mode.equalsIgnoreCase("p")) {
				throwObjects.add(new UserThrownObject());
				performThrow(e.getX(),e.getY(),clickStr);
			} else {
				mf.getInfoPanel().setMsg("Out of Throwing Knives!");	//Display a warning when the user runs out of throwing knives
			}
			
		}
	}
	//ActionListener Functions
	@Override
	public void actionPerformed(ActionEvent e) {
		clickStr += 1;
	}
	@Override
	public String toString() {			//Takes care of saving the entire game-state in text form through delegation
		String str = "";
		for (UserThrownObject uto : throwObjects) {
			str += uto.toString() + "/n";
		}
		str += targetObject.toString();
		return str;
	}
} 


/*View Class for the local scores*/
class ScorePanel extends JPanel {
	private JLabel lastScoreDisp;		//Hold onto the JLabel for the score counter
	private JLabel runningScoreDisp;	//Hold onto the JLabel for the running sore counter
	private JLabel remThrowsDisp;		//Hold onto the JLabel for the throw counter
	private int runningScore;			//Track the running score for the display
	private int lastScore;				//Track the last score for the display
	private int remThrows;				//Track the throws for the display
	
	public ScorePanel() {
		lastScoreDisp = new JLabel("Last Score: N/A");
		runningScoreDisp = new JLabel("Running Score: N/A");
		remThrowsDisp = new JLabel("Remaining Throws: N/A");
		add(lastScoreDisp);
		add(runningScoreDisp);
		add(remThrowsDisp);
		remThrows = 3;
		setRemThrowsDisp();
	}
	
	public void setLastScoreDisp() {
		if (lastScore >= 0) {
			lastScoreDisp.setText(String.format("Last Score: %d",lastScore));
		} else {
			lastScoreDisp.setText("Last Score: N/A");
		}
	}
	public void setLastScore(int score) {
		lastScore = score;
		setLastScoreDisp();
	}
	public void setRunningScoreDisp() {
		if (runningScore >= 0) {
			runningScoreDisp.setText(String.format("Running Score: %d",runningScore));
		} else {
			runningScoreDisp.setText("Running Score: N/A");
		}
	}
	public void setRunningScore(int score) {
		runningScore = score;
		setRunningScoreDisp();
	}
	public void setRemThrowsDisp() {
		if (remThrows >= 0) {
			remThrowsDisp.setText(String.format("Remaining Throws: %d",remThrows));
		} else {
			remThrowsDisp.setText("Remaining Throws: None");
		}
	}
	public void setRemThrows(int attempts) {
		remThrows = attempts;
		setRemThrowsDisp();
	}
	
	public void resetAll() {			//Used by resetGame() in the controller to clear itself
		setLastScore(0);
		setRunningScore(0);
		setRemThrows(3);
		repaint();
	}
	public void updateScores(int score) {	//Auto-handle updating the score every time through one function
		setLastScore(score);
		setRunningScore(runningScore + score);
		setRemThrows(remThrows - 1);
	}
	public void configurePracticeMode() {	//Disable the throw counter for practice mode
		remThrowsDisp.setVisible(false);
		repaint();
	}
	public void configureChallengeMode() {	//Enable the throw counter for challenge mode
		remThrowsDisp.setVisible(true);
		repaint();
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setRunningScoreDisp();
		setLastScoreDisp();
	}
}


/*View Class for The Target Model*/
class TargetPanel extends JPanel {
	private int centerX;					//Track the center x value of the target drawn to the panel
	private int centerY;					//Track the center y value of the target drawn to the panel
	private double scale;
	private ArrayList<Integer> hitLocs;		//Track the knife hits drawn to the panel 

	public TargetPanel(ObjectTargetWindowController ctrl) {
		addMouseListener(ctrl);
		hitLocs = new ArrayList<Integer>();
		scale = 1;
	}
	
	public int getLeftX() {					//Helps syncing the ThrownObjectTarget model with the view
		return centerX-40;
	}
	public int getTopY() {					//Helps syncing the ThrownObjectTarget model with the view
		return centerY-40;
	}
	public double getScale() {				//Helps syncing the ThrownObjectTarget model with the view
		return scale;
	}
	public void setScale(double scaleVal) {	//Helps syncing the ThrownObjectTarget model with the view
		scale = scaleVal;
		repaint();
	}
	
	public void resetAll() {				//Used by the resetGame() function to clear itself
		hitLocs.clear();
		repaint();
	}
	public void addHit(int[] point) {
		hitLocs.add(point[0] - (centerX-40));	//Makes X and Y relative to the target, so it scales
		hitLocs.add(point[1] - (centerY-40));	//Target top-left is 0,0 relative to each hit
		repaint();
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		centerX = getWidth()/2;
		centerY = getHeight()/2;
		int width = (int)(80*scale);
		int ringWidth = (int)(8*scale);
		g.fillRect(centerX-(width/2),centerY-(width/2),width,width);	//Draw the border of the target
			//Draw each ring and color it appropriately
		for (int i = 0; i < 11; i++) {		
			if (i < 2) {
				g.setColor(Color.WHITE);
			} else if (i < 4) {
				g.setColor(Color.BLACK);
			} else if (i < 6) {
				g.setColor(Color.CYAN);
			} else if (i < 8) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.YELLOW);
			}
			g.fillOval((centerX-(width/2))+(ringWidth/2)*i,(centerY-(width/2))+(ringWidth/2)*i,width-(ringWidth*i),width-(ringWidth*i));
			if (i == 3) {
				g.setColor(Color.LIGHT_GRAY);
			} else {
				g.setColor(Color.BLACK);	
			}
			g.drawOval((centerX-(width/2))+(ringWidth/2)*i,(centerY-(width/2))+(ringWidth/2)*i,width-(ringWidth*i),width-(ringWidth*i));
		}
			//Draw the hit locations
		for (int i = 0; i < hitLocs.size(); i+=2) {
			g.setColor(new Color(0,150,0));
			g.fillRect(((centerX-40)+hitLocs.get(i))-3,((centerY-40)+hitLocs.get(i+1))-3,6,6);
		}
	}
}


/*View class for displaying the mode, warnings to the user*/
class InfoPanel extends JPanel {
	private String msg;			//Holds the message displayed by the JLabel
	private JLabel msgDisp;		//Holds the JLabel drawn to the screen
	private JButton btnOK;		//Holds the OK button to dismiss warning messages
	private String prevMsg;		//Holds the previous message displayed for dismissing the message
	
	public InfoPanel() {
		msg = "Challenge Mode";							//Display the mode by default
		msgDisp = new JLabel(msg);
		btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {	//Clicking the button dismisses the warning,
			public void actionPerformed(ActionEvent e) {//returning to the previous message
				setMsg(prevMsg);
				btnOK.setVisible(false);
			}
		});
		add(msgDisp);
		add(btnOK);
		btnOK.setVisible(false);
	}
	
	public void setMsg(String str) {
		prevMsg = msg;
		msg = str;
		setMsgDisp();
		if (!(msg.equalsIgnoreCase("Challenge Mode") || msg.equalsIgnoreCase("Practice Mode"))) {
			setBtnOKFlag();			//Tell the button to become visible for non-regular messages
		}
	}
	public void setMsgDisp() {
		msgDisp.setText(msg);
		repaint();
	}
	public void setBtnOKFlag() {	//Set the button to be visible for non-regular messages
		btnOK.setVisible(true);
	}
}


/*View Class for entering high-scores into the local list*/
class EntryFrame extends JFrame {
	private JTextField nameField;	//Holds the JTextField displayed for the user to enter their name
	
	public EntryFrame(ObjectTargetWindowController ctrl) {
		setDefaultCloseOperation(HIDE_ON_CLOSE);			//Hide the window, but allow the program to continue
		setTitle("Add Score");
		setLayout(new BorderLayout());
		setBounds(200,200,250,90);
		nameField = new JTextField("Name");
		nameField.setToolTipText("Enter your name to save your score (and save the score-list before you quit!)");
		add(nameField,BorderLayout.CENTER);
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {		//Make the button close the frame through ObjectTargetWindowController
			public void actionPerformed(ActionEvent e) {
				ctrl.closeEntryFrame();
			}
		});
		add(btnOK,BorderLayout.SOUTH);
		setVisible(true);
	}
	
	public String getNameField() {
		return nameField.getText();
	}
	
	public boolean wantsToSave() {	//Make sure the user wants to save their score, prevent no-name scores 
		if (nameField.getText().equals("")) {
			return false;
		} else {
			if (nameField.getText().equalsIgnoreCase("Name")) {	//Prevent default-name scores
				return false;
			}
			return true;
		}
	} 
}


/*View Class for displaying the UI window*/
class MainFrame extends JFrame {
	private TargetPanel tarPan;				//Hold onto the TargetPanel for access to target-drawing functions
	private ScorePanel scorePan;			//Hold onto the ScorePanel for access to score-changing functions
	private InfoPanel infoPan;				//Hold onto the InfoPanel for message-displaying functions
	private EntryFrame scoreEntryFrame;		//Hold onto the EntryFrame for score-adding functions
	
	public MainFrame(ObjectTargetWindowController ctrl, ObjectFileController ofc) {
		//basics
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100,100,400,400);
		setTitle("Target Practice v1.0");
		
		//BorderLayout Configuration
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
			//BorderLayout.SOUTH
		scorePan = new ScorePanel();
		c.add(scorePan,BorderLayout.SOUTH);
		
			//BorderLayout.CENTER
		tarPan = new TargetPanel(ctrl);
		c.add(tarPan,BorderLayout.CENTER);
		
			//BorderLayout.NORTH
		infoPan = new InfoPanel();
		c.add(infoPan,BorderLayout.NORTH);
		
		//Menu Configuration
		JMenuBar bar = new JMenuBar();
		
			//File menu
		JMenu mnuFile = new JMenu("File");
		JMenuItem miExit = new JMenuItem("Exit");	//Exit: closes the program
		miExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnuFile.add(miExit);
		bar.add(mnuFile);
		
		/*Setup JFileChooser for Saving/Loading Menus*/
		JFileChooser jfc = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Text","txt");
		jfc.addChoosableFileFilter(filter);
		filter = new FileNameExtensionFilter("Binary","bin");
		jfc.addChoosableFileFilter(filter);
		
			//Game menu
		JMenu mnuGame = new JMenu("Game");
		JMenuItem miNewGame = new JMenuItem("New Game");	//New Game: calls resetGame() in ObjectTargetWindowController
		miNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctrl.resetGame(ctrl.getMode());
			}
		});
		mnuGame.add(miNewGame);		
		JMenuItem miSaveGame = new JMenuItem("Save Game (.txt)");	//Save Game: uses jfc to save the game in .txt format
		miSaveGame.setToolTipText("Save the game to resume later");
		miSaveGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					ofc.writeGameToTextFile(ctrl.getThrowObjects(),ctrl.getTargetObject(),jfc.getSelectedFile());
				}
			}
		});
		mnuGame.add(miSaveGame);		
		JMenuItem miLoadGame = new JMenuItem("Load Game (.txt)");	//Load Game: uses jfc to load a game with ObjectTargetFileController.loadGame()
		miLoadGame.setToolTipText("Load a game started earlier");
		miLoadGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					ctrl.loadGame(ofc.loadThrowsFromTextFile(jfc.getSelectedFile()),ofc.loadTargetFromTextFile(jfc.getSelectedFile(),ctrl.getMainFrame()));
				}
			}
		});
		mnuGame.add(miLoadGame);
		bar.add(mnuGame);
		
			//Scores Menu
		JMenu mnuScore = new JMenu("Scores");
		JMenuItem miAddScore = new JMenuItem("Add My Score");	//Add My Score: creates the EntryFrame for saving scores
		miAddScore.setToolTipText("Add your score to the list of scores, saved with 'Save Scores List'");
		miAddScore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ctrl.getMode().equalsIgnoreCase("c")) {
					scoreEntryFrame = new EntryFrame(ctrl);
				} else {
					infoPan.setMsg("Cannot add score in practice mode!");	//Warning message for wrong mode use
				}
			}
		});
		mnuScore.add(miAddScore);		
		JMenuItem miShowScore = new JMenuItem("Show Top Score");	//Show Top Score: shows the high-score in the imported/local scoreList
		miShowScore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctrl.checkScoreList();
			}
		});
		mnuScore.add(miShowScore);		
		JMenuItem miSaveScoreList = new JMenuItem("Save Scores List (.bin)");	//Save Scores List: uses jfc to 'export' the local scores list in .bin format
		miSaveScoreList.setToolTipText("Export the local scoreList");
		miSaveScoreList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					ofc.writeScoreListToFile(ctrl, jfc.getSelectedFile());
				}
			}
		});
		mnuScore.add(miSaveScoreList);		
		JMenuItem miLoadScoreList = new JMenuItem("Load Scores List (.bin)");	//Load Scores List: uses jfc to 'import' the .bin scores as the local scores
		miLoadScoreList.setToolTipText("Import a scoreList to compete against");
		miLoadScoreList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					ctrl.setScoreList(ofc.loadScoreListFromFile(jfc.getSelectedFile()));
				}
			}
		});
		mnuScore.add(miLoadScoreList);
		bar.add(mnuScore);

			//Scale menu
		JMenu mnuScale = new JMenu("Scale");
		JMenuItem miLarge = new JMenuItem("Large (1 Meter Away)");	//Large: double the default target size by modifying scale in TargetPanel, affects drop
		miLarge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tarPan.setScale(2);
			}
		});
		mnuScale.add(miLarge);
		JMenuItem miMedium = new JMenuItem("Medium (2 Meters Away)");	//Medium: default target size
		miMedium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tarPan.setScale(1);
			}
		});
		mnuScale.add(miMedium);
		JMenuItem miSmall = new JMenuItem("Small (3 Meters Away)");		//Small: half the default target size by modifying scale in TargetPanel, affects drop
		miSmall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tarPan.setScale(0.5);
			}
		});
		mnuScale.add(miSmall);
		bar.add(mnuScale);
		
			//Mode menu
		JMenu mnuMode = new JMenu("Mode");
		JMenuItem miChallenge = new JMenuItem("Challenge Mode");	//Challenge: 3 knife throws per game, scoring enabled
		miChallenge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctrl.resetGame("c");								//starts a new game upon clicking the button
				ctrl.setMode("c");
			}
		});
		mnuMode.add(miChallenge);
		JMenuItem miEndless = new JMenuItem ("Practice Mode");		//Practice: infinite knife throws per game, scoring disabled
		miEndless.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctrl.resetGame("p");								//starts a new game upon clicking the button
				ctrl.setMode("p");
			}
		});
		mnuMode.add(miEndless);
		bar.add(mnuMode);
		setJMenuBar(bar);
	}
	
	public TargetPanel getTargetPanel() {
		return tarPan;
	}
	public ScorePanel getScorePanel() {
		return scorePan;
	}
	public InfoPanel getInfoPanel() {
		return infoPan;
	}
	public EntryFrame getScoreEntryFrame() {
		return scoreEntryFrame;
	}
}

/*Public Class, initial controller*/
public class UIGameMVC {
	public static void main(String[] args) {
		ArrayList<UserThrownObject> throwObjectsHeld = new ArrayList<UserThrownObject>();	//Create the knife objects used in the game
		throwObjectsHeld.add(new UserThrownObject());
		throwObjectsHeld.add(new UserThrownObject(6,0));
		throwObjectsHeld.add(new UserThrownObject());
		ObjectFileController ofc = new ObjectFileController();	//Generates the other controller instances, "passes control to them"
		ObjectTargetWindowController con1 = new ObjectTargetWindowController(throwObjectsHeld,ofc);
	}
}