package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class DropDownAdapter<T> extends ArrayAdapter<T> {
	private static final String logTag = "DropDownAdapter";
    // viewed is primarily a semaphore to eliminate calls
    // to the user callback until the user has at least 
    // used the drop-down menu.  It's false until the
    // user has viewed the drop-down menu.
    private boolean viewed;
    private String hint;
    // constructor with hint.
    public DropDownAdapter(Context context, int resId, List<T> list, T hintObj) {
        super(context, resId, 0, new ArrayList<T>(list));
        Log.i(logTag, "instantiate:" + ((hintObj != null) ? hintObj.toString() : "noHint") + ":start");
    	if (null != hintObj) {
    		super.add(hintObj);
    		hint = hintObj.toString();
    	}

        this.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
        Log.i(logTag, "instantiate:" + ((hintObj != null) ? hintObj.toString() : "noHint") + ":start");
    }
    // no-hint constructor.
    public DropDownAdapter(Context context, int resId, List<T> list) {
    	this(context,resId,list,null);
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        // this masks the hint until the user has viewed
        // the dropdown menu.
        if (null != hint)
        	count -= 1;
        return count;
    }
    @Override
    public T getItem(int position) {
        return super.getItem(position);
    }
    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
        viewed = true;
        return super.getDropDownView(position, convertView, parent);
    }
    public boolean viewed() { return viewed; }
    public String getHint() {return hint;}
    public DropDownAdapter<T> setHint(T hintObj) {
    	if (null == hint) {
	    	hint = hintObj.toString();
	    	super.add(hintObj);
    	}
    	return reset();
    }
    public DropDownAdapter<T> reset() {
    	viewed = false;
    	return this;
    }
}
