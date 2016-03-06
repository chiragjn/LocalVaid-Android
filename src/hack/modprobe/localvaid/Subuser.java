package hack.modprobe.localvaid;

public class Subuser {

	// private variables
	int _id, _sid;
	String _name;
	int _gender;
	int _age;
	int _heightFt, _heightIn;
	int _weight;

	// Empty constructor
	public Subuser() {

	}

	// constructor
	public Subuser(int id, String name, int gender, int age, int heightFt,
			int heightIn, int weight, int sid) {
		this._id = id;
		this._name = name;
		this._gender = gender;
		this._age = age;
		this._heightFt = heightFt;
		this._heightIn = heightIn;
		this._weight = weight;
		this._sid = sid;
	}

	// constructor
	public Subuser(String name, int gender, int age, int heightFt,
			int heightIn, int weight) {
		this._name = name;
		this._gender = gender;
		this._age = age;
		this._heightFt = heightFt;
		this._heightIn = heightIn;
		this._weight = weight;
	}

	@Override
	public String toString() {
		return this._name;
	}

}