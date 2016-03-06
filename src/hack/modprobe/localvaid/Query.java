package hack.modprobe.localvaid;

public class Query {
    
    //private variables
    int _id,_sid;
    String _symptoms,_diagnosis,_prescription;
    Subuser _parent;
     
    // Empty constructor
    public Query(){
         
    }
    // constructor
    public Query(int id, String symptoms, String diagnosis, String prescription, Subuser parent,int sid){
        this._id = id;
        this._symptoms = symptoms;
        this._diagnosis = diagnosis;
        this._prescription = prescription;
        this._parent = parent;
        this._sid = sid;
    }
     
    // constructor
    public Query(String symptoms, String diagnosis, String prescription, Subuser parent){
        this._symptoms = symptoms;
        this._diagnosis = diagnosis;
        this._prescription = prescription;
        this._parent = parent;
    }
    
    @Override
	public String toString() {
		return String.valueOf(_id) + " - " + "28/12/15";
	}
    
}