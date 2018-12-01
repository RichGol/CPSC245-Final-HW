import java.util.ArrayList;
import java.util.Random;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Graphics;

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
	public UserThrownObject() {
		this(7.8125,0);	//Sets an average throwing knife length in inches, sets the weight according to the length
	}
	
	public int[] throwObject(int x, int y, int throwStrength) {
		Random rnd = new Random();
		double[] strDropVals = new double[10];
		for (int i = 0; i < 10; i++) {
			strDropVals[i] = (1.16 - ((1.16-.39077)/9)*(i));
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
		int endY = (int)(y*strDropVals[throwStrength]+2);
		int[] retArray = {endX, endY};
		return retArray;
	}
	
	public String toString() {
		return String.format("Length: %07.4f cm\nWeight: %07.4f ounces\n",length,weight);
	}
}
/*Model class for the target which the user will throw objects at in the game*/
class ThrownObjectTarget {
	private double distance;	//Distance from player to target in meters, for easier calculations
	private int leftX;
	private int topY;	
	
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
	
	public ThrownObjectTarget(int x, int y) {
		setDistance(2);	//Sets the distance to a reasonable 2 meters (~6.56 ft) from the player to the target
		setLeftX(x);
		setTopY(y);
	}
	public int getPointZone(int[] hitLoc) {
		int x = hitLoc[0];
		int y = hitLoc[1];
		int centerX = leftX+40;
		int centerY = topY+40;
/*PRINT	System.out.println("Drop (" + x + " " + y + ")");
		System.out.println("CENTER (" + centerX + " " + centerY + ")");
		System.out.println("Top Left (" + leftX + " " + topY + ") -> Bottom Right (" + (leftX + 80) + " " + (topY + 80) + ")");		*/
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
	}
}
/*Controller class for interactions between UserThrownObject and ThrownObjectTarget*/
class ObjectTargetWindowController implements MouseListener,ActionListener {
	private ArrayList<UserThrownObject> throwObjects;
	private int objectsThrown;
	private ThrownObjectTarget targetObject;
	private MainFrame mf;
	private Timer tim;
	private int clickStr;
	
	public ObjectTargetWindowController(ArrayList<UserThrownObject> throwObjects) {
		this.throwObjects = throwObjects;
		mf = new MainFrame(this);
		mf.setVisible(true);
		targetObject = new ThrownObjectTarget(mf.getTargetPanel().getLeftX(),mf.getTargetPanel().getTopY());
		objectsThrown = 0;
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
		//Gets the user-thrown object, throws it, and calculates points earned
		UserThrownObject tmpObj = throwObjects.get(objectsThrown);
		int[] finalPoint = tmpObj.throwObject(x, y, throwStrength);
		tmpObj.setPointsEarned(targetObject.getPointZone(finalPoint));
		tmpObj.setIsThrown(true);
		
		if (objectsThrown == 0) {
			mf.getTargetPanel().setHit1(finalPoint);
		} else if (objectsThrown == 1) {
			mf.getTargetPanel().setHit2(finalPoint);
		} else if (objectsThrown == 2) {
			mf.getTargetPanel().setHit3(finalPoint);
		}
		
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
/*				System.out.println("THROW!");	*/
			} else {
				JOptionPane.showMessageDialog(null,"Out of Throwing Knives!");	//Use to show final score & total, percentage
			}
			
		}
	}
	//ActionListener Functions
	public void actionPerformed(ActionEvent e) {
		clickStr += 1;
	}
} 
/*View Class for Scores*/
class ScorePanel extends JPanel {
	private ArrayList<Integer> scores;
	private JLabel lastScoreDisp;
	private JLabel runningScoreDisp;
	private JLabel remThrowsDisp;
	private int runningScore;
	private int lastScore;
	private int remThrows;
	
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
	public void addToRunningScore(int score) {
		runningScore += score;
		setRunningScoreDisp();
	}
	public void updateScores(int score) {
		setLastScore(score);
		addToRunningScore(score);
		removeThrow();
	}
	public void setRemThrowDisp() {
		if (remThrows >= 0) {
			remThrowsDisp.setText(String.format("Remaining Throws: %d",remThrows));
		} else {
			remThrowsDisp.setText("Remaining Throws: None");
		}
	}
	public void removeThrow() {
		remThrows -= 1;
		setRemThrowDisp();
	}
	
	public ScorePanel() {
		lastScoreDisp = new JLabel("Last Score: N/A");
		runningScoreDisp = new JLabel("Running Score: N/A");
		remThrowsDisp = new JLabel("Remaining Throws: N/A");
		add(lastScoreDisp);
		add(runningScoreDisp);
		add(remThrowsDisp);
		remThrows = 3;
		setRemThrowDisp();
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
	private int[] hit1;
	private boolean drawHit1;
	private int[] hit2;
	private boolean drawHit2;
	private int[] hit3;
	private boolean drawHit3;

	public int getLeftX() {
		return centerX-40;
	}
	public int getTopY() {
		return centerY-40;
	}
	public void setHit1(int[] point) {
		hit1[0] = point[0];
		hit1[1] = point[1];
		drawHit1 = true;
		repaint();
	}
	public void setHit2(int[] point) {
		hit2[0] = point[0];
		hit2[1] = point[1];
		drawHit2 = true;
		repaint();
	}
	public void setHit3(int[] point) {
		hit3[0] = point[0];
		hit3[1] = point[1];
		drawHit3 = true;
		repaint();
	}
	public TargetPanel(ObjectTargetWindowController ctrl) {
		addMouseListener(ctrl);
		hit1 = new int[2];
		hit2 = new int[2];
		hit3 = new int[2];
		drawHit1 = drawHit2 = drawHit3 = false;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		centerX = getWidth()/2;
		centerY = getHeight()/2;
		g.fillRect(centerX-40,centerY-40,80,80);
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
			g.fillOval((centerX-40)+4*i,(centerY-40)+4*i,80-(8*i),+80-(8*i));
			g.setColor(Color.BLACK);
			g.drawOval((centerX-40)+4*i,(centerY-40)+4*i,80-(8*i),+80-(8*i));
		}
		if (drawHit1) {
			g.setColor(Color.GREEN);
			g.fillRect(hit1[0]-3,hit1[1]-3,6,6);
		}
		if (drawHit2) {
			g.setColor(Color.GREEN);
			g.fillRect(hit2[0]-3,hit2[1]-3,6,6);
		}
		if (drawHit3) {
			g.setColor(Color.GREEN);
			g.fillRect(hit3[0]-3,hit3[1]-3,6,6);
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
	public MainFrame(ObjectTargetWindowController ctrl) {
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
		setJMenuBar(bar);
	}
}

public class UIGameMVC {
	public static void main(String[] args) {
		ArrayList<UserThrownObject> throwObjectsHeld = new ArrayList<UserThrownObject>();
		throwObjectsHeld.add(new UserThrownObject());
		throwObjectsHeld.add(new UserThrownObject(6,0));
		throwObjectsHeld.add(new UserThrownObject());
		ObjectTargetWindowController con1 = new ObjectTargetWindowController(throwObjectsHeld);
/*PRINT	System.out.println(con1.tallyPointsAsPercentage());		*/
	}
}