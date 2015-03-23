
package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

public class MinMaxSpinner extends Spinner {
    private static final String logTag = "MinMaxSpinner";

    // Use a Runnable and post this rather than calling
    // performClick directly. This lets other visual state
    // of the view update before click actions start.
    public OnMinMaxSpinnerListener onMinMaxSpinnerListener;
    public interface OnMinMaxSpinnerListener {
        public void onMinMaxSelect(int id);
    }
    public MinMaxSpinner setOnSelectListener(OnMinMaxSpinnerListener l) {
        onMinMaxSpinnerListener = l;
        return this;
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
    private DropDownAdapter<String> minAdapter, maxAdapter;
    private int minResId, maxResId;
    private ArrayList<String> inList;
    private View contentView;

    public MinMaxSpinner(View contentView
            , int minResId, int maxResId, List<String> list) {
        super(contentView.getContext());
        Context context = contentView.getContext();
        this.minResId = minResId; this.maxResId = maxResId;
        this.contentView = contentView;
        spinnerId = idBase++;

        minAdapter = new DropDownAdapter<String>(context
            , android.R.layout.simple_spinner_item, list
            , getResources().getString(R.string.min_hint));

        maxAdapter = new DropDownAdapter<String>(context
            , android.R.layout.simple_spinner_item, list
            , getResources().getString(R.string.max_hint));
        
        minSpinner = (Spinner)contentView.findViewById(minResId);
        minSpinner.setAdapter(minAdapter);

        maxSpinner = (Spinner)contentView.findViewById(maxResId);
        maxSpinner.setAdapter(maxAdapter);

        // use the on-selected callbacks from the spinners to update
        // the min-max spinner callback.
        minSpinner.setOnItemSelectedListener(onItemSelectedListener());
        maxSpinner.setOnItemSelectedListener(onItemSelectedListener());

        reset();
    }
    public void reset() {
    	minAdapter.reset();
    	maxAdapter.reset();
    	maxSpinner.setSelection(maxAdapter.getCount());
        minSpinner.setSelection(minAdapter.getCount());
    }
    public MinMaxSpinner setTitle(String title) {
        TextView tv = (TextView)contentView.findViewById(R.id.minmax_title);
        tv.setText(title);
        return this;
    }
    public MinMaxSpinner setTitle(int titleResId) {
    	return setTitle(getResources().getString(titleResId));
    }
    public ArrayList<String> getMinMax() {
        // return the min/max value string if we are looking
        // at the hint.
        String min = (String)minSpinner.getSelectedItem();
        if (min.equals(minAdapter.getHint()))
            min = inList.get(0);
        String max = (String)maxSpinner.getSelectedItem();
        if (max.equals(maxAdapter.getHint()))
            max = inList.get(inList.size() - 1);

        ArrayList<String> rv = new ArrayList<String>(2);
        rv.add(0, min);
        rv.add(1, max);
        return rv;
    }
    public int[] getMinMaxPositions() {
    	int [] rv = new int[2];
    	rv[0] = minSpinner.getSelectedItemPosition();
    	rv[1] = maxSpinner.getSelectedItemPosition();
    	return rv;
    }
    public MinMaxSpinner setMinMaxPositions(int[] values) {
    	if (null != values) {
	    	minSpinner.setSelection(values[0]);
	    	maxSpinner.setSelection(values[1]);
    	}
    	return this;
    }
    // internal spinner listener.  If spinner changes, make sure min and
    // max values are correct and call the user's callback.
    private OnItemSelectedListener onItemSelectedListener() {
        return new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                int spinnerId = parent.getId();
                if (spinnerId == minResId || spinnerId == maxResId) {
                    if (spinnerId == minResId && minAdapter.viewed()) {
                        // if max is still displaying its hint, set it to
                        // its max value.
                        String max = (String)maxSpinner.getSelectedItem();
                        if (max.equals(maxAdapter.getHint()))
                            maxSpinner.setSelection(maxSpinner.getCount() - 1);
                        // if min is greater than max, set max to min.
                        else if (position > maxSpinner.getSelectedItemPosition())
                            maxSpinner.setSelection(position);

                        parent.post(performSelect);
                    }
                    else if (maxAdapter.viewed()) {
                        // if min is still displaying its hint, set it to its
                        // min value.
                        String min = (String)minSpinner.getSelectedItem();
                        if (min.equals(minAdapter.getHint()))
                            minSpinner.setSelection(0);
                        // if max is less than min, set min to max.
                        else if (position < minSpinner.getSelectedItemPosition())
                            minSpinner.setSelection(position);

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
