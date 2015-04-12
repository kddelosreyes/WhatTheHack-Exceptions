package com.feutech.whatthehack.utilities;

public class PostSingleton {

	private static PostSingleton mInstance = null;
	 
    private Object mObject;
 
    private PostSingleton(){
        mObject = null;
    }
 
    public static PostSingleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new PostSingleton();
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
