package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MinMaxSpinner extends Spinner implements OnItemSelectedListener {
	private static final String logTag = "MinMaxSpinner";
	private Spinner minSpinner, maxSpinner;
	List<String> list;
	MinMaxAdapter minAdapter, maxAdapter;
	private int minResId, maxResId;
	private Activity activity;

	public MinMaxSpinner(Activity activity, int minResId, int maxResId, List<String> list) {
		super(activity);
		boolean showSpinnerHint = true;
		this.activity = activity; this.minResId = minResId; this.maxResId = maxResId;
		MinMaxAdapter adapter;
		List<String> l;
		l = new ArrayList<String>(list);
		l.add(activity.getResources().getString(R.string.min_spinner_string));
		minSpinner = (Spinner)activity.findViewById(minResId);
		minAdapter = new MinMaxAdapter(activity
				, android.R.layout.simple_spinner_item, l);
		minAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		minSpinner.setAdapter(minAdapter);
		
		minSpinner.setOnItemSelectedListener(this);
		minSpinner.setSelection(minSpinner.getCount());
		
		l = new ArrayList<String>(list);
		l.add(activity.getResources().getString(R.string.max_spinner_string));
		maxSpinner = (Spinner)activity.findViewById(maxResId);
		maxAdapter = new MinMaxAdapter(activity
				, android.R.layout.simple_spinner_item, l);
		maxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		maxSpinner.setAdapter(maxAdapter);
		
		maxSpinner.setOnItemSelectedListener(this);
		maxSpinner.setSelection(maxSpinner.getCount());
	}
    private class MinMaxAdapter extends ArrayAdapter<String> {
    	private List<String> list;
    	private MinMaxAdapter(Activity activity, int resId, List<String> list) {
    		super(activity, resId, list);
    		this.list = new ArrayList<String>(list);
    	}
    	private List<String>getAdapterList() { return list; }
		@Override
		public int getCount() {
			// item at getCount is the hint.  Don't show...
			int rv = super.getCount() - 1;
			return rv;
		}
    }
	public String getMin() {
		int selected = (int)minSpinner.getSelectedItemId();
		String rv = (String)minSpinner.getItemAtPosition(selected);
		return rv;
	}
	public String getMax() {
		int selected = (int)maxSpinner.getSelectedItemId();
		String rv = (String)maxSpinner.getItemAtPosition(selected);
		return rv;
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// ignore the hint position.
		if (position < parent.getCount()) {
			int spinnerId = parent.getId();
			if (spinnerId == minResId) {
				// If user sets min value, catch the current max value,
				// truncate the max values to be valid for the new min,
				// then try to set the max value back to what it was
				// before.  the change.
				String currentMax = (String)maxSpinner.getSelectedItem();
				int sel = (int) maxSpinner.getSelectedItemPosition();
				List<String> l = new ArrayList<String>();
				List<String> maxList = maxAdapter.getAdapterList();
				for(int i = minSpinner.getSelectedItemPosition(); i < maxList.size(); i++) {
					l.add(maxList.get(i));
				}
				maxAdapter.clear();
				maxAdapter.addAll(l);
				maxAdapter.notifyDataSetChanged();
				maxSpinner.setSelection(maxAdapter.getPosition(currentMax));
			}
		}
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
}
