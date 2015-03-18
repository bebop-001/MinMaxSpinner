package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MinMaxSpinner extends Spinner {
    private static final String logTag = "MinMaxSpinner";

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

    private static int idBase = 100;
    public int spinnerId;
    private Spinner minSpinner, maxSpinner;
    private MinMaxAdapter minAdapter, maxAdapter;
    private int minResId, maxResId;
    private ArrayList<String> inList;

    public MinMaxSpinner(View contentView
            , int minResId, int maxResId, List<String> list) {
        super(contentView.getContext());
        Context context = contentView.getContext();
        this.minResId = minResId; this.maxResId = maxResId;
        spinnerId = idBase++;

        inList = new ArrayList<String>(list);

        minAdapter = new MinMaxAdapter(context
                , android.R.layout.simple_spinner_item
                , new ArrayList<String>(inList));
        minAdapter.currentIndex = 0;

        maxAdapter = new MinMaxAdapter(context
                , android.R.layout.simple_spinner_item
                , new ArrayList<String>(inList));
        maxAdapter.currentIndex = inList.size() - 1;
    }
    public void update(View contentView) {
        minAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
        maxAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);

        minSpinner = (Spinner)contentView.findViewById(minResId);
        minSpinner.setAdapter(minAdapter);

        maxSpinner = (Spinner)contentView.findViewById(maxResId);
        maxSpinner.setAdapter(maxAdapter);

        minSpinner.setOnItemSelectedListener(onItemSelectedListener());
        maxSpinner.setOnItemSelectedListener(onItemSelectedListener());

        maxSpinner.setSelection(maxAdapter.currentIndex);
        minSpinner.setSelection(minAdapter.currentIndex);
    }
    private class MinMaxAdapter extends ArrayAdapter<String> {
        private int currentIndex = -1;
        private List<String> list;
        private boolean visited;
        private MinMaxAdapter(Context context,
                int resId, List<String> list) {
            super(context, resId, list);
            this.list = list;
        }
        @Override
        public int getCount() {
            int count = super.getCount();
            return count;
        }
        @Override
        public String getItem(int position) {
            currentIndex = position;
            currentIndex = position;
            return super.getItem(position);
        }
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			visited = true;
			return super.getDropDownView(position, convertView, parent);
		}
		private boolean visited() { return visited; }
    }
    public String getMin() {
        String rv = (String)minSpinner.getSelectedItem();
        return rv;
    }
    public String getMax() {
        String rv = (String)maxSpinner.getSelectedItem();
        return rv;
    }
    private OnItemSelectedListener onItemSelectedListener() {
        return new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                int spinnerId = parent.getId();
                if (spinnerId == minResId || spinnerId == maxResId) {
                    if (spinnerId == minResId && minAdapter.visited()) {
                        // If user sets min value, catch the current max
                        // value, truncate the max values to be valid for
                        // the new min, then try to set the max value back
                        // to what it was before.  the change.
                        String currentMin
                        	= (String)minSpinner.getSelectedItem();
                        String currentMax
                        	= (String)maxSpinner.getSelectedItem();
                        List<String> l = new ArrayList<String>();
                        for(int i = minSpinner.getSelectedItemPosition();
                                    i < inList.size(); i++) {
                            l.add(inList.get(i));
                        }
                        maxAdapter.clear();
                        maxAdapter.addAll(l);
                        maxAdapter.notifyDataSetChanged();
                        if (l.contains(currentMax))
	                        maxSpinner.setSelection(
	                                maxAdapter.getPosition(currentMax));
                        else
                        	maxSpinner.setSelection(
                        			maxAdapter.getPosition(currentMin));
                        parent.post(performSelect);
                    }
                    else if (maxAdapter.visited()) {
                        parent.post(performSelect);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }
}
