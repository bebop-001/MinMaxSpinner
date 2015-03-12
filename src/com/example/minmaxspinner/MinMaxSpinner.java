package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
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
    private String minHint, maxHint;
    private ArrayList<String> minList, maxList;

    public MinMaxSpinner(View contentView
            , int minResId, int maxResId, List<String> list) {
        super(contentView.getContext());
        Context context = contentView.getContext();
        this.minResId = minResId; this.maxResId = maxResId;
        spinnerId = idBase++;

        maxHint = new String(context.getResources()
            .getString(R.string.max_spinner_string));
        minHint = new String(context.getResources()
            .getString(R.string.min_spinner_string));

        minList = new ArrayList<String>(list);
        minList.add(minHint);
        maxList = new ArrayList<String>(list);
        maxList.add(maxHint);

        minAdapter = new MinMaxAdapter(context
                , android.R.layout.simple_spinner_item
                , new ArrayList<String>(minList), minHint);
        minAdapter.currentIndex = minList.size() - 1;

        maxAdapter = new MinMaxAdapter(context
                , android.R.layout.simple_spinner_item
                , new ArrayList<String>(maxList), maxHint);
        maxAdapter.currentIndex = maxList.size() - 1;

    }
    public void update(View contentView) {
        Log.i(logTag, minHint + ":" + maxHint + ":update "
                + "min current=" + minAdapter.currentIndex
                + ", max current = " + maxAdapter.currentIndex);

        minAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
        maxAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);

        minSpinner = (Spinner)contentView.findViewById(minResId);
        minSpinner.setAdapter(minAdapter);

        maxSpinner = (Spinner)contentView.findViewById(maxResId);
        maxSpinner.setAdapter(maxAdapter);

        minSpinner.setOnItemSelectedListener(onItemSelectedListener());
        minSpinner.setSelection(minAdapter.currentIndex);

        maxSpinner.setOnItemSelectedListener(onItemSelectedListener());
        maxSpinner.setSelection(maxAdapter.currentIndex);
    }
    private class MinMaxAdapter extends ArrayAdapter<String> {
        private int currentIndex = -1;
        private String name;
        private List<String> list;
        private MinMaxAdapter(Context context,
                int resId, List<String> list, String name) {
            super(context, resId, list);
            this.name = name;
            this.list = list;
            Log.i(logTag, "new instance:" + name + "\""
                + ", spinnerId=" + spinnerId);
        }
        @Override
        public int getCount() {
            // item at getCount is the hint.  Don't show...
            int count = super.getCount() - 1;
            Log.i(logTag, name + ":getCount:" + count
                + ", size=" + list.size() + ", spinnerId=" + spinnerId);
            return count;
        }
        @Override
        public String getItem(int position) {
            currentIndex = position;
            Log.i(logTag, name + ":getItem:" + position + ":"
                + currentIndex + "==>" + position
                + ":" + currentIndex);
            currentIndex = position;
            return super.getItem(position);
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
    private OnItemSelectedListener onItemSelectedListener() {
        return new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                // ignore the hint position.
                if (position < parent.getCount()) {
                    int spinnerId = parent.getId();
                    if (spinnerId == minResId || spinnerId == maxResId) {
                        if (spinnerId == minResId) {
                            // If user sets min value, catch the current max
                            // value, truncate the max values to be valid for
                            // the new min, then try to set the max value back
                            // to what it was before.  the change.
                            String currentMax
                                = (String)maxSpinner.getSelectedItem();
                            List<String> l = new ArrayList<String>();
                            for(int i = minSpinner.getSelectedItemPosition();
                                        i < maxList.size(); i++) {
                                l.add(maxList.get(i));
                            }
                            maxAdapter.clear();
                            maxAdapter.addAll(l);
                            maxAdapter.notifyDataSetChanged();
                            maxSpinner
                                .setSelection(
                                    maxAdapter.getPosition(currentMax));
                        }
                        else {
                            // if min hasn't been selected, set it to min value.
                            String val = (String)minSpinner.getSelectedItem();
                            if (val.equals(minHint))
                                minSpinner.setSelection(0);
                        }
                        // call any onSelect listeners.
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
