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
			this.weight = 1.5*length;	//Throwing knives typically weigh 1.5 ounces per inch
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
		this(7.8125,0);	//Sets an average throwing knife length, sets the weight according to the length
	}

/*
	public int[] throwObject(int x, int y) {
		Random rnd = new Random();
		double throwStrength = rnd.nextDouble()+.51;	//Generates a random strength for the throw as a percentage in the range [0.5-1.5]
		
		int endX = rnd.nextInt(20)-10 + x;	//The object will land within 20px of where it is targeted to land
		
		int endY = 
	}
	
*/
	public String toString() {
		return String.format("Length: %07.4f in\nWeight: %07.4f ounces\n",length,weight);
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
		setDistance(3);	//Sets the distance to a reasonable 3 meters (~9.85 ft) from the player to the target
	}
	public int getPointZone(int x, int y) {
		if (x <= -8 && x >= 8) {
			return 10;
		} else if (x <= -12 && x >= 12) {
			return 9;
		} else if (x <= -16 && x >= 16) {
			return 8;
		} else if (x <= -20 && x >= 20) {
			return 7;
		} else if (x <= -24 && x >= 24) {
			return 6;
		} else if (x <= -28 && x >= 28) {
			return 5;
		} else if (x <= -32 && x >= 32) {
			return 4;
		} else if (x <= -36 && x >= 36) {
			return 3;
		} else if (x <= -40 && x >= 40) {
			return 2;
		} else if (x <= -44 && x >= 44) {
			return 1;
		} else {
			return 0;
		}
	}
}

public class UIGameMVC {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		UserThrownObject obj1 = new UserThrownObject();
		UserThrownObject obj2 = new UserThrownObject(6,0);
		System.out.println(obj1);
		System.out.println(obj2);
	}
}