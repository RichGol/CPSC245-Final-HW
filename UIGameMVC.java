class UserThrownObject {
	private boolean isThrown;	//Monitor which instances have been 'Thrown' by the user
	private double length;	//Store it's length in inches for calculating weight
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
			length = len;
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
	
	public String toString() {
		return String.format("Length: %07.4f in\nWeight: %07.4f ounces\n",length,weight);
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