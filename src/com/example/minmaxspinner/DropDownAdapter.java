package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class DropDownAdapter<T> extends ArrayAdapter<T> {
    // viewed is primarily a semaphore to eliminate calls
    // to the user callback until the user has at least 
    // used the drop-down menu.  It's false until the
    // user has viewed the drop-down menu.
    private boolean viewed;
    private String hint;
    public DropDownAdapter(Context context,
            int resId, List<T> list, boolean hasHint) {
        super(context, resId, 0, new ArrayList<T>(list));
        if (hasHint)
        	hint = new String (list.get(list.size() - 1).toString());
        this.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
    }
    public DropDownAdapter(Context context, int resId, List<T> list) {
    	this(context, resId, list, false);
    }
    @Override
    public int getCount() {
        int count = super.getCount() - 1;
        // this masks the hint until the user has viewed
        // the dropdown menu.
        if (null != hint && viewed)
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
    public String hint() {return hint;}
    public void reset() {
    	viewed = false;
    }
}
