
package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private MinMaxAdapter minAdapter, maxAdapter;
    private int minResId, maxResId;
    private ArrayList<String> inList;
    private View contentView;
    private String title;

    public MinMaxSpinner(View contentView
            , int minResId, int maxResId, List<String> list) {
        super(contentView.getContext());
        Context context = contentView.getContext();
        this.minResId = minResId; this.maxResId = maxResId;
        this.contentView = contentView;
        spinnerId = idBase++;

        inList = new ArrayList<String>(list);

        minAdapter = new MinMaxAdapter(context
            , android.R.layout.simple_spinner_item
            , new ArrayList<String>(inList)
            , getResources().getString(R.string.min_hint));

        minAdapter.currentIndex = inList.size();

        maxAdapter = new MinMaxAdapter(context
            , android.R.layout.simple_spinner_item
            , new ArrayList<String>(inList)
            , getResources().getString(R.string.max_hint));
        maxAdapter.currentIndex = inList.size();
    }
    public void update(View contentView) {
        this.contentView = contentView;
        // This is used during restore from orientation change.
        if (null != title)
            ((TextView)contentView.findViewById(R.id.minmax_title))
                .setText(title);
        minAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
        maxAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);

        minSpinner = (Spinner)contentView.findViewById(minResId);
        minSpinner.setAdapter(minAdapter);

        maxSpinner = (Spinner)contentView.findViewById(maxResId);
        maxSpinner.setAdapter(maxAdapter);

        // use the on-selected callbacks from the spinners to update
        // the min-max spinner callback.
        minSpinner.setOnItemSelectedListener(onItemSelectedListener());
        maxSpinner.setOnItemSelectedListener(onItemSelectedListener());

        // Reset the spinner to the current index.  This
        // is used during startup to init and during
        // orientation change to restore previous
        // values.
        maxSpinner.setSelection(maxAdapter.currentIndex);
        minSpinner.setSelection(minAdapter.currentIndex);
    }
    public MinMaxSpinner setTitle(String title) {
        this.title = title;
        TextView tv = (TextView)contentView.findViewById(R.id.minmax_title);
        tv.setText(title);
        return this;
    }
    private class MinMaxAdapter extends ArrayAdapter<String> {
        private int currentIndex = -1;
        private List<String> list;
        // viewed is primarily a semaphore to eliminate calls
        // to the user callback until the user has at least 
        // used the drop-down menu.  It's false until the
        // user has viewed the drop-down menu.
        private boolean viewed;
        private String hint;
        private MinMaxAdapter(Context context,
                int resId, List<String> list, String hint) {
            super(context, resId, new ArrayList<String>(list));
            this.hint = new String(hint);
            super.add(this.hint);
        }
        @Override
        public int getCount() {
            int count = super.getCount();
            // this masks the hint until the user has viewed
            // the dropdown menu.
            if (viewed)
                count -= 1;
            return count;
        }
        @Override
        public String getItem(int position) {
            currentIndex = position;
            return super.getItem(position);
        }
        @Override
        public View getDropDownView(int position, View convertView,
                ViewGroup parent) {
            viewed = true;
            return super.getDropDownView(position, convertView, parent);
        }
        private boolean viewed() { return viewed; }
    }
    public ArrayList<String> getMinMax() {
        // return the min/max value string if we are looking
        // at the hint.
        String min = (String)minSpinner.getSelectedItem();
        if (min.equals(minAdapter.hint))
            min = inList.get(0);
        String max = (String)maxSpinner.getSelectedItem();
        if (max.equals(maxAdapter.hint))
            max = inList.get(inList.size() - 1);

        ArrayList<String> rv = new ArrayList<String>(2);
        rv.add(0, min);
        rv.add(1, max);
        return rv;
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
                        if (max.equals(maxAdapter.hint))
                            maxSpinner.setSelection(maxSpinner.getCount() - 2);
                        // if min is greater than max, set max to min.
                        else if (position > maxSpinner.getSelectedItemPosition())
                            maxSpinner.setSelection(position);

                        parent.post(performSelect);
                    }
                    else if (maxAdapter.viewed()) {
                        // if min is still displaying its hint, set it to its
                        // min value.
                        String min = (String)minSpinner.getSelectedItem();
                        if (min.equals(minAdapter.hint))
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
