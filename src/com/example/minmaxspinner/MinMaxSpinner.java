package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MinMaxSpinner extends Spinner implements OnItemSelectedListener {
	private static final String logTag = "MinMaxSpinner";
    private static int idBase = 100;
    public int spinnerId = idBase++;
	private Spinner minSpinner, maxSpinner;
	List<String> list;
	MinMaxAdapter minAdapter, maxAdapter;
	private int minResId, maxResId;
	private String minString, maxString;
	
	
    // Use a Runnable and post this rather than calling
    // performClick directly. This lets other visual state
    // of the view update before click actions start.
    public OnMinMaxSpinnerListener onMinMaxSpinnerListener;
    public interface OnMinMaxSpinnerListener {
        public void onMinMaxSelect(int id);
    }
    public void setOnSelectListener(OnMinMaxSpinnerListener l) {
        onMinMaxSpinnerListener = l;
    }
    private PerformSelect performSelect = new PerformSelect();
    private final class PerformSelect implements Runnable {
        public void run() {
        	if (null != onMinMaxSpinnerListener)
        		onMinMaxSpinnerListener.onMinMaxSelect(spinnerId);
        }
    }

	public MinMaxSpinner(View contentView, int minResId, int maxResId, List<String> list) {
		super(contentView.getContext());
		Context context = contentView.getContext();
		this.minResId = minResId; this.maxResId = maxResId;
		maxString = new String(context.getResources().getString(R.string.max_spinner_string));
		minString = new String(context.getResources().getString(R.string.min_spinner_string));

		List<String> l;
		l = new ArrayList<String>(list);
		l.add(minString);
		minSpinner = (Spinner)contentView.findViewById(minResId);
		minAdapter = new MinMaxAdapter(context
				, android.R.layout.simple_spinner_item, l);
		minAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		minSpinner.setAdapter(minAdapter);
		
		minSpinner.setOnItemSelectedListener(this);
		minSpinner.setSelection(minSpinner.getCount());
		
		l = new ArrayList<String>(list);
		l.add(maxString);
		maxSpinner = (Spinner)contentView.findViewById(maxResId);
		maxAdapter = new MinMaxAdapter(context
				, android.R.layout.simple_spinner_item, l);
		maxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		maxSpinner.setAdapter(maxAdapter);
		
		maxSpinner.setOnItemSelectedListener(this);
		maxSpinner.setSelection(maxSpinner.getCount());
	}
    private class MinMaxAdapter extends ArrayAdapter<String> {
    	private List<String> list;
    	private MinMaxAdapter(Context context, int resId, List<String> list) {
    		super(context, resId, list);
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
		String rv = (String)minSpinner.getSelectedItem();
		return rv;
	}
	public String getMax() {
		String rv = (String)maxSpinner.getSelectedItem();
		return rv;
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// ignore the hint position.
		if (position < parent.getCount()) {
			int spinnerId = parent.getId();
			if (spinnerId == minResId || spinnerId == maxResId) {
				if (spinnerId == minResId) {
					// If user sets min value, catch the current max value,
					// truncate the max values to be valid for the new min,
					// then try to set the max value back to what it was
					// before.  the change.
					String currentMax = (String)maxSpinner.getSelectedItem();
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
				else {
					// if min hasn't been selected, set it to min value.
					String val = (String)minSpinner.getSelectedItem();
					if (val.equals(minString))
						minSpinner.setSelection(0);
				}
				// call any onSelect listeners.
				post(performSelect);
			}
		}
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {}
}
