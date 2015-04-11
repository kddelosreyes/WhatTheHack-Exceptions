package com.feutech.whatthehack.utilities;

public class UserSingleton {

	private static UserSingleton mInstance = null;
	 
    private Object mObject;
 
    private UserSingleton(){
        mObject = null;
    }
 
    public static UserSingleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new UserSingleton();
        }
        return mInstance;
    }
 
    public Object getObject(){
        return this.mObject;
    }
 
    public void setObject(Object object){
        mObject = object;
    }
}
