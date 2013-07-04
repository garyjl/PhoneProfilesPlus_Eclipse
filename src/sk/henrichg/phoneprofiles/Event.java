package sk.henrichg.phoneprofiles;

public class Event {
	
	//private variables
	public long _id;
	public String _name;
	public int _type;
	public long _fkProfile;
	public long _fkParams;
	public long _fkParamsEdit;
	
	
	// Empty constructorn
	public Event(){
		
	}
	
	// constructor
	public Event(long id, 
		         String name,
		         int type,
		         long fkProfile,
		         long fkParams,
		         long fkParamsEdit)
	{
		this._id = id;
		this._name = name;
        this._type = type;
        this._fkProfile = fkProfile;
        this._fkParams = fkParams;
        this._fkParamsEdit = fkParamsEdit;
	}
	
	// constructor
	public Event(String name,
	         	 int type,
	         	 long fkProfile,
	         	 long fkParams,
	         	 long fkParamsEdit)
	{
		this._name = name;
	    this._type = type;
	    this._fkProfile = fkProfile;
	    this._fkParams = fkParams;
	    this._fkParamsEdit = fkParamsEdit;
	}
	
}

