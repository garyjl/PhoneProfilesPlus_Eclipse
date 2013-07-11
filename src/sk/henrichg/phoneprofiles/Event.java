package sk.henrichg.phoneprofiles;

public class Event {
	
	//private variables
	public long _id;
	public String _name;
	public int _type;
	public long _fkProfile;
	public long _fkParams;
	public long _fkParamsEdit;
	public boolean _enabled;
	
	
	// Empty constructorn
	public Event(){
		
	}
	
	// constructor
	public Event(long id, 
		         String name,
		         int type,
		         long fkProfile,
		         long fkParams,
		         long fkParamsEdit,
		         boolean enabled)
	{
		this._id = id;
		this._name = name;
        this._type = type;
        this._fkProfile = fkProfile;
        this._fkParams = fkParams;
        this._fkParamsEdit = fkParamsEdit;
        this._enabled = enabled;
	}
	
	// constructor
	public Event(String name,
	         	 int type,
	         	 long fkProfile,
	         	 long fkParams,
	         	 long fkParamsEdit,
	         	 boolean enabled)
	{
		this._name = name;
	    this._type = type;
	    this._fkProfile = fkProfile;
	    this._fkParams = fkParams;
	    this._fkParamsEdit = fkParamsEdit;
	    this._enabled = enabled;
	}
	
}

