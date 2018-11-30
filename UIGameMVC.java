import java.util.ArrayList;
import java.util.Random;

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
			strDropVals[i] = (.39077 + ((1.16-.39077)/9)*(i));
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
	public ThrownObjectTarget() {
		setDistance(2);	//Sets the distance to a reasonable 2 meters (~6.56 ft) from the player to the target
	}
	public int getPointZone(int[] hitLoc) {
		int x = hitLoc[0];
		int y = hitLoc[1];
		if ((x>=46 && x<=54) && (y>=46 && y<=54)) {	//in the 10-point range
			return 10;
		}
		else if ((x>=42 && x<=58) && (y>=42 && y<=58)) {	//in the 9-point range
			return 9;
		}
		else if ((x>=38 && x<=62) && (y>=38 && y<=62)) {	//in the 8-point range
			return 8;
		}
		else if ((x>=34 && x<=66) && (y>=34 && y<=66)) {	//in the 7-point range
			return 7;
		}
		else if ((x>=30 && x<=70) && (y>=30 && y<=70)) {	//in the 6-point range
			return 6;
		}
		else if ((x>=26 && x<=74) && (y>=26 && y<=74)) {	//in the 5-point range
			return 5;
		}
		else if ((x>=22 && x<=78) && (y>=22 && y<=78)) {	//in the 4-point range
			return 4;
		}
		else if ((x>=18 && x<=82) && (y>=18 && y<=82)) {	//in the 3-point range
			return 3;
		}
		else if ((x>=14 && x<=86) && ((y>=14 && y<=86))) {	//in the 2-point range
			return 2;
		}
		else if ((x>=10 && x<=90) && ((y>=10 && y<=90))) {	//in the 1-point range
			return 1;
		}
		else {	//Out of target
			return 0;
		}
	}
}
class ObjectTargetWindowController {
	private ArrayList<UserThrownObject> throwObjects;
	private int objectsThrown;
	private ThrownObjectTarget targetObject;
	
	public ObjectTargetWindowController(ArrayList<UserThrownObject> throwObjects, ThrownObjectTarget targetObject) {
		this.throwObjects = throwObjects;
		this.targetObject = targetObject;
		objectsThrown = 0;
	}
	public void performThrow(int x, int y, int throwStrength) {
		int[] finalPoint = throwObjects.get(objectsThrown).throwObject(x, y, throwStrength);
		throwObjects.get(objectsThrown).setPointsEarned(targetObject.getPointZone(finalPoint));
		throwObjects.get(objectsThrown).setIsThrown(true);
		System.out.println(throwObjects.get(objectsThrown));
		System.out.println(throwObjects.get(objectsThrown).getPointsEarned());
		objectsThrown += 1;
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
} 
public class UIGameMVC {
	public static void main(String[] args) {
		ArrayList<UserThrownObject> throwObjectsHeld = new ArrayList<UserThrownObject>();
		throwObjectsHeld.add(new UserThrownObject());
		throwObjectsHeld.add(new UserThrownObject(6,0));
		ThrownObjectTarget tarObj = new ThrownObjectTarget();
		ObjectTargetWindowController con1 = new ObjectTargetWindowController(throwObjectsHeld,tarObj);
		for (int i = 0; i < throwObjectsHeld.size(); i++) {
			con1.performThrow(20,50,3);
		}
		System.out.println(con1.tallyPointsAsPercentage());
	}
}