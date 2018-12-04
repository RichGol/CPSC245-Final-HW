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
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/*Model class for objects to be thrown at the target in the game*/
class UserThrownObject {
	private boolean isThrown;	//Monitor which instances have been 'Thrown' by the user
	private double length;	//Store it's length in centimeters for calculating weight
	private double weight;	//Store it's weight in ounces for calculating drop
	private int pointsEarned;	//Store the point zone it landed in on the target after being thrown
	
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
			length = 2.54*len;	//Converts user-entered inches into centimeters for consistency
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
			this.weight = 1.5*(length/2.54);	//Throwing knives typically weigh 1.5 ounces per inch
		}
	}
	public int getPointsEarned() {
		return pointsEarned;
	}
	public void setPointsEarned(int points) {
		pointsEarned = points;
	}
	
	public UserThrownObject(double len, double wt) {	//Allows the user to set a custom length and weight for throwing knives,
		setIsThrown(false);									//Their settings are still bound by the set functions
		setLength(len);
		setWeight(wt);
		setPointsEarned(0);
	}
	public UserThrownObject(double len, double wt, int points, boolean thrown) {
		setIsThrown(thrown);
		setLength(len);
		setWeight(wt);
		setPointsEarned(points);
	}
	public UserThrownObject() {
		this(7.8125,0);	//Sets an average throwing knife length in inches, sets the weight according to the length
	}
	
	public int[] throwObject(int x, int y, int throwStrength, double scale) {
		Random rnd = new Random();
		double[] strDropVals = new double[10];
		
		for (int i = 0; i < 10; i++) {
			strDropVals[i] = (1.16 - ((1.16-.39077)/9)*i);
		}
		
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
		
		int endX = rnd.nextInt(20)-10 + x;	//The object will land within 20px of either side from what the user targets
		if (throwStrength > 9) {
			throwStrength = 9;
		} else if (throwStrength < 0) {
			throwStrength = 0;
		}
		int endY = (int)(y*strDropVals[throwStrength]/scale);
		int[] retArray = {endX, endY};
/*PRINT	System.out.println(endX);	*/
/*PRINT	System.out.println(endY);	*/
		return retArray;
	}
	
	public String toString() {
		//Formatted to save the game state as plaintext easily using PrintWriter
		return String.format("%07.4f %07.4f %d %b",length,weight,pointsEarned,isThrown);
	}
}
/*Model class for the target which the user will throw objects at in the game*/
class ThrownObjectTarget {
	private double distance;	//Distance from player to target in meters, for easier calculations
	private int leftX;
	private int topY;	
	private double scale;
	
	public double getDistance() {
		return distance;
	}
	public void setDistance(double dist) {
		if (dist > 2) {	//Minimum safe distance to stand from the target when throwing knives
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
		if (scale == 0.5 || scale == 1.0 || scale == 2.0) {
			scale = scaleVal;
		} else {
			scale = 1;
		}
	}
	public ThrownObjectTarget(int x, int y, double scale) {
		setDistance(2);	//Sets the distance to a reasonable 2 meters (~6.56 ft) from the player to the target
		setLeftX(x);
		setTopY(y);
		setScale(scale);
	}
	public int getPointZone(int[] hitLoc, double scale) {
		int x = hitLoc[0];
		int y = hitLoc[1];
		int centerX = leftX+40;
		int centerY = topY+40;
		int ringWidth = (int)(4*scale);
/*PRINT	System.out.println("Drop (" + x + " " + y + ")");
		System.out.println("CENTER (" + centerX + " " + centerY + ")");
		System.out.println("Top Left (" + leftX + " " + topY + ") -> Bottom Right (" + (leftX + 80) + " " + (topY + 80) + ")");		*/
		
		int score = 10;
		for (int i = ringWidth; i < 40*scale; i+= ringWidth) {
			if ((x>=centerX-i && x<=centerX+i) && (y>=centerY-i && y<=centerY+i)) {
				return score;
			} else {
				score -= 1;
			}
		}
		return 0;
		/*	OLD SCORING ZONE SCORE
		if ((x>=centerX-4 && x<=centerX+4) && (y>=centerY-4 && y<=centerY+4)){
			return 10;
		} else if ((x>=centerX-8 && x<=centerX+8) && (y>=centerY-8 && y<=centerY+8)){
			return 9;
		} else if ((x>=centerX-12 && x<=centerX+12) && (y>=centerY-12 && y<=centerY+12)){
			return 8;
		} else if ((x>=centerX-16 && x<=centerX+16) && (y>=centerY-16 && y<=centerY+16)){
			return 7;
		} else if ((x>=centerX-20 && x<=centerX+20) && (y>=centerY-20 && y<=centerY+20)){
			return 6;
		} else if ((x>=centerX-24 && x<=centerX+24) && (y>=centerY-24 && y<=centerY+24)){
			return 5;
		} else if ((x>=centerX-28 && x<=centerX+28) && (y>=centerY-28 && y<=centerY+28)){
			return 4;
		} else if ((x>=centerX-32 && x<=centerX+32) && (y>=centerY-32 && y<=centerY+32)){
			return 3;
		} else if ((x>=centerX-36 && x<=centerX+36) && (y>=centerY-36 && y<=centerY+36)){
			return 2;
		} else if ((x>=centerX-40 && x<=centerX+40) && (y>=centerY-40 && y<=centerY+40)) {
			return 1;
		} else {
			return 0;
		}
		*/
	}
	public String toString() {
		//Formatted to save the game state as plain-text easily using PrintWriter
		return String.format("%.1f",scale);
	}
}
/*Controller class for interactions between ObjectTargetWindowController (Which represents the View Classes) and Files*/
class ObjectFileController {
	public boolean writeGameToTextFile(ArrayList<UserThrownObject> throwObjects, ThrownObjectTarget tar, File f) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			for (UserThrownObject thrObj : throwObjects) {
				pw.println(thrObj);
			}
			pw.println(tar);
			pw.close();
			return true;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,"File Save Error -- Throws, Aborting!");
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
				if (line.contains(" ")) {
					parts = line.split(" ");
					throwObjects.add(new UserThrownObject(Double.parseDouble(parts[0]),Double.parseDouble(parts[1]),Integer.parseInt(parts[2]),Boolean.parseBoolean(parts[3])));
				}
			}
			sc.close();
			return throwObjects;
		} catch (Exception ex) {
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
	
}
/*Controller class for interactions between UserThrownObject, ThrownObjectTarget, and the MainFrame*/
class ObjectTargetWindowController implements MouseListener,ActionListener {
	private ArrayList<UserThrownObject> throwObjects;
	private int objectsThrown;
	private ThrownObjectTarget targetObject;
	private MainFrame mf;
	private Timer tim;
	private int clickStr;
	
	public void resetGame() {
		objectsThrown = 0;
		for (UserThrownObject uto : throwObjects) {
			uto.setIsThrown(false);
			uto.setPointsEarned(0);
		}
		mf.getScorePanel().resetAll();
		mf.getTargetPanel().resetAll();
		
	}
	public void loadGame(ArrayList<UserThrownObject> loadedObjects, ThrownObjectTarget loadedTarget) {
		setThrowObjects(loadedObjects);
		setTargetObject(loadedTarget);
		mf.getTargetPanel().resetAll();
		mf.getTargetPanel().setScale(loadedTarget.getScale());
		mf.getScorePanel().setLastScore(0);
		mf.getScorePanel().setRunningScore(0);
		mf.getScorePanel().setRemThrows(3);
		for (UserThrownObject uto : throwObjects) {
			if (uto.getIsThrown()) {
				mf.getScorePanel().updateScores(uto.getPointsEarned());
			}
		}
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
	
	public ObjectTargetWindowController(ArrayList<UserThrownObject> throwObjects, ObjectFileController txtGameSaver) {
		this.throwObjects = throwObjects;
		mf = new MainFrame(this,txtGameSaver);
		mf.setVisible(true);
		targetObject = new ThrownObjectTarget(mf.getTargetPanel().getLeftX(),mf.getTargetPanel().getTopY(),mf.getTargetPanel().getScale());
		
		//Gets the correct number of objects left to throw, for importing a saved game
		objectsThrown = 0;
		for (UserThrownObject tmpObj : throwObjects) {
			if (tmpObj.getIsThrown()) {
				objectsThrown += 1;
			}
		}
		clickStr = 0;
		tim = new Timer(200,this);
		/*TESTING POINT HITS
		int[] testArray = {192,156};
		System.out.println(targetObject.getPointZone(testArray)); //10
		testArray[0] = 184;
		System.out.println(targetObject.getPointZone(testArray)); //9
		testArray[0] = 180;
		System.out.println(targetObject.getPointZone(testArray)); //8
		testArray[0] = 176;
		System.out.println(targetObject.getPointZone(testArray)); //7
		testArray[0] = 172;
		System.out.println(targetObject.getPointZone(testArray)); //6
		testArray[0] = 168;
		System.out.println(targetObject.getPointZone(testArray)); //5
		testArray[0] = 164;
		System.out.println(targetObject.getPointZone(testArray)); //4
		testArray[0] = 160;
		System.out.println(targetObject.getPointZone(testArray)); //3
		testArray[0] = 156;
		System.out.println(targetObject.getPointZone(testArray)); //2
		testArray[0] = 152;
		System.out.println(targetObject.getPointZone(testArray)); //1
		testArray[0] = 148;
		System.out.println(targetObject.getPointZone(testArray)); //0
		*/
	}
	public void performThrow(int x, int y, int throwStrength) {
		//Resets the top-left corner of the targetObject in the model class
			//in case the window has been resized
		targetObject.setLeftX(mf.getTargetPanel().getLeftX());
		targetObject.setTopY(mf.getTargetPanel().getTopY());
		targetObject.setScale(mf.getTargetPanel().getScale());	//Keeps the TargetPanel scale & the ThrownObjectTarget scale synced
		//Gets the user-thrown object, throws it, and calculates points earned
		UserThrownObject tmpObj = throwObjects.get(objectsThrown);
		int[] finalPoint = tmpObj.throwObject(x, y, throwStrength,targetObject.getScale());
		tmpObj.setPointsEarned(targetObject.getPointZone(finalPoint,targetObject.getScale()));
		tmpObj.setIsThrown(true);
		mf.getTargetPanel().addHit(finalPoint);
		
/*PRINT	System.out.println(tmpObj);		*/
/*PRINT	System.out.println("Points " + tmpObj.getPointsEarned());	*/
		objectsThrown += 1;
		//Updates the scoring Panel
		mf.getScorePanel().updateScores(tmpObj.getPointsEarned());
	}
	public float tallyPointsAsPercentage() {
		int pointCount = 0;
		int totalPoints = 0;
		for (UserThrownObject ut : throwObjects) {
			if (ut.getIsThrown()) {
				pointCount += ut.getPointsEarned();
				totalPoints += 10;
			}
		}
		return (float)((double)(pointCount)/(double)(totalPoints));
	}
	public int tallyPointsRaw() {
		int pointCount = 0;
		for (UserThrownObject ut : throwObjects) {
			if (ut.getIsThrown()) {
				pointCount += ut.getPointsEarned();
			}
		}
		return pointCount;
	}
	
	//MouseListener Functions
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		if (!tim.isRunning()) {
			clickStr = 0;
			tim.start();
/*PRINT		System.out.println("Click");	*/
		}
	}
	public void mouseReleased(MouseEvent e) {
		if (tim.isRunning()) {
			tim.stop();
/*PRINT		System.out.println("Release" + " " + clickStr);		*/
			if (!(objectsThrown > (throwObjects.size()-1))) {
				performThrow(e.getX(),e.getY(),clickStr);
/*PRINT			System.out.println("THROW!");	*/
			} else {
				JOptionPane.showMessageDialog(null,"Out of Throwing Knives!");	//Use to show final score & total, percentage
			}
			
		}
	}
	//ActionListener Functions
	public void actionPerformed(ActionEvent e) {
		clickStr += 1;
	}
	public String toString() {
		//Takes care of saving the entire game-state in text form through delegation
		String str = "";
		for (UserThrownObject uto : throwObjects) {
			str += uto.toString() + "/n";
		}
		str += targetObject.toString();
		return str;
	}
} 
/*View Class for Scores*/
class ScorePanel extends JPanel {
	private JLabel lastScoreDisp;
	private JLabel runningScoreDisp;
	private JLabel remThrowsDisp;
	private int runningScore;
	private int lastScore;
	private int remThrows;
	
	public void resetAll() {
		setLastScore(0);
		setRunningScore(0);
		setRemThrows(3);
		repaint();
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
	public void updateScores(int score) {
		setLastScore(score);
		setRunningScore(runningScore + score);
		setRemThrows(remThrows - 1);
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
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setRunningScoreDisp();
		setLastScoreDisp();
	}
}
/*View Class for The Target Model*/
class TargetPanel extends JPanel {
	private int centerX;
	private int centerY;
	private double scale;
	private ArrayList<Integer> hitLocs; 

	public void resetAll() {
		hitLocs.clear();
		repaint();
	}
	
	public int getLeftX() {
		return centerX-40;
	}
	public int getTopY() {
		return centerY-40;
	}
	public void addHit(int[] point) {
		hitLocs.add(point[0] - (centerX-40));	//Makes X and Y relative to the target, so it scales
		hitLocs.add(point[1] - (centerY-40));		//Target top-left is 0,0 to each hit
		repaint();
	}
	public double getScale() {
		return scale;
	}
	public void setScale(double scaleVal) {
		scale = scaleVal;
		repaint();
	}
	public TargetPanel(ObjectTargetWindowController ctrl) {
		addMouseListener(ctrl);
		hitLocs = new ArrayList<Integer>();
		scale = 1;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		centerX = getWidth()/2;
		centerY = getHeight()/2;
		int width = (int)(80*scale);
		int ringWidth = (int)(8*scale);
		g.fillRect(centerX-(width/2),centerY-(width/2),width,width);
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
			g.setColor(Color.BLACK);
			g.drawOval((centerX-(width/2))+(ringWidth/2)*i,(centerY-(width/2))+(ringWidth/2)*i,width-(ringWidth*i),width-(ringWidth*i));
		}
		for (int i = 0; i < hitLocs.size(); i+=2) {
			g.setColor(new Color(0,150,0));
/*PRINT		System.out.println(centerX + " " + hitLocs.get(i) + " " + centerY + " " + hitLocs.get(i+1));	*/
			g.fillRect(((centerX-40)+hitLocs.get(i))-3,((centerY-40)+hitLocs.get(i+1))-3,6,6);
		}
	}
}

/*View Class for displaying the UI window*/
class MainFrame extends JFrame {
	private TargetPanel tarPan;
	private ScorePanel scorePan;
	
	public TargetPanel getTargetPanel() {
		return tarPan;
	}
	public ScorePanel getScorePanel() {
		return scorePan;
	}
	public MainFrame(ObjectTargetWindowController ctrl, ObjectFileController ofc) {
		//basics
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100,100,400,400);
		setTitle("Version beta 3 - GUI Version");
		
		//BorderLayout Configuration
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
			//BorderLayout.SOUTH
		scorePan = new ScorePanel();
		c.add(scorePan,BorderLayout.SOUTH);
		
			//BorderLayout.CENTER
		tarPan = new TargetPanel(ctrl);
		c.add(tarPan,BorderLayout.CENTER);
		
		//menu
		JMenuBar bar = new JMenuBar();
		
		JMenu mnuFile = new JMenu("File");
		JMenuItem miExit = new JMenuItem("Exit");
		miExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnuFile.add(miExit);
		bar.add(mnuFile);
		
		JFileChooser jfc = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Text","txt");
		jfc.addChoosableFileFilter(filter);
		filter = new FileNameExtensionFilter("Binary","bin");
		jfc.addChoosableFileFilter(filter);
		
		JMenu mnuGame = new JMenu("Game");
		JMenuItem miNewGame = new JMenuItem("New Game");
		miNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctrl.resetGame();
			}
		});
		mnuGame.add(miNewGame);
		
		JMenu mnuSave = new JMenu("Save Game");
		JMenuItem miSaveGame = new JMenuItem("Save Game (as .txt)");
		miSaveGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					ofc.writeGameToTextFile(ctrl.getThrowObjects(),ctrl.getTargetObject(),jfc.getSelectedFile());
				}
			}
		});
		mnuSave.add(miSaveGame);
		JMenuItem miSaveScoreList = new JMenuItem("Save High Scores (as .bin)");
		//Add ActionListener, add to mnuSave
		mnuGame.add(mnuSave);
		
		JMenu mnuLoad = new JMenu("Load Game");
		JMenuItem miLoadGame = new JMenuItem("Load Game");
		miLoadGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					ctrl.loadGame(ofc.loadThrowsFromTextFile(jfc.getSelectedFile()),ofc.loadTargetFromTextFile(jfc.getSelectedFile(),ctrl.getMainFrame()));
				}
			}
		});
		mnuLoad.add(miLoadGame);
		JMenuItem miLoadScoreList = new JMenuItem("Load High Scores");
		//Add ActionListener, add to mnuLoad
		
		mnuGame.add(mnuLoad);
		bar.add(mnuGame);
		
		JMenu mnuScale = new JMenu("Scale");
		JMenuItem miLarge = new JMenuItem("Large (1 Meter Away)");
		miLarge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tarPan.setScale(2);
			}
		});
		mnuScale.add(miLarge);
		JMenuItem miMedium = new JMenuItem("Medium (2 Meters Away)");
		miMedium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tarPan.setScale(1);
			}
		});
		mnuScale.add(miMedium);
		JMenuItem miSmall = new JMenuItem("Small (3 Meters Away)");
		miSmall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tarPan.setScale(0.5);
			}
		});
		mnuScale.add(miSmall);
		
		bar.add(mnuScale);
		setJMenuBar(bar);
	}
}

public class UIGameMVC {
	public static void main(String[] args) {
		ArrayList<UserThrownObject> throwObjectsHeld = new ArrayList<UserThrownObject>();
		throwObjectsHeld.add(new UserThrownObject());
		throwObjectsHeld.add(new UserThrownObject(6,0));
		throwObjectsHeld.add(new UserThrownObject());
		ObjectFileController ofc = new ObjectFileController();
		ObjectTargetWindowController con1 = new ObjectTargetWindowController(throwObjectsHeld,ofc);
/*PRINT	System.out.println(con1.tallyPointsAsPercentage());		*/
	}
}