package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.minmaxspinner.MinMaxSpinner.OnMinMaxSpinnerListener;

public class MainActivity extends Activity implements OnMinMaxSpinnerListener {
	private static final String logTag = "MainActivity";
	private static List<String> strokecountList;
	private static final int MAX_STROKECOUNT = 24;
	private static MinMaxSpinner minMaxSpinner;
	private static Spinner noHintSpinner;
	private static DropDownAdapter<String> noHintAdapter;
    private static SharedPreferences prefs;
    private static int[] minMaxSelectedSaved;
    
    private List<String> noHintList = new ArrayList<String>() {
    	{
    		add("1"); add("2"); add("3"); add("4"); add("5");
    		add("6"); add("7"); add("8"); add("9"); add("10");
    	}
    };
	
	public static class ThemeInfo {
		private String name; private int themeId;
		private ThemeInfo (String name, int themeId) {
			this.name = name; this.themeId = themeId;
		}
		public String toString() {return name; }
	}
	static int selectedTheme;
	public static final ArrayList<ThemeInfo> themeList = new ArrayList<ThemeInfo>(2) {
		{
			add(0, new ThemeInfo("Holo Dark", android.R.style.Theme_Holo));
			add(1, new ThemeInfo("Holo Light", android.R.style.Theme_Holo_Light));
		}
	};
	public void resetButton(View v) {
		minMaxSpinner.reset();
		noHintSpinner.setSelection(0);
		MinMaxSpinnerFrag.reset();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        /****************** Theme setup ********************************/
    	// Restore user's previous selected theme.
    	prefs = getSharedPreferences("user_prefs.txt", Context.MODE_PRIVATE);
    	selectedTheme = prefs.getInt("selectedTheme", 0);
    	if (selectedTheme > themeList.size() - 1) {
    		selectedTheme = 0;
    	}

        setTheme(themeList.get(selectedTheme).themeId);
		setContentView(R.layout.activity_main);
		View minMaxLayout = findViewById(R.id.activity_minmax);
		strokecountList = new ArrayList<String>();
		for(int i = 1; i <= MAX_STROKECOUNT; i++) {
			strokecountList.add(String.valueOf(i));
		}
		
		noHintSpinner = (Spinner) findViewById(R.id.nohint_spinner);
		noHintAdapter = new DropDownAdapter<String>(
			this, android.R.layout.simple_spinner_item, noHintList);
		noHintSpinner.setAdapter(noHintAdapter);
		TextView noHintTv = (TextView)findViewById(R.id.nohint_spinner_text);
		noHintTv.setText("No hint, 1..10:");
		
		minMaxSpinner = new MinMaxSpinner(minMaxLayout
				, R.id.min_strokecount, R.id.max_strokecount
				, strokecountList)
		.setTitle("Activity min/max spinner, 1..24: ")
		.setOnSelectListener(this)
		.setMinMaxPositions(minMaxSelectedSaved);

		Fragment minMaxFrag = new MinMaxSpinnerFrag();
		FragmentTransaction transaction = getFragmentManager()
			.beginTransaction();
		transaction.replace(R.id.frag_placeholder, minMaxFrag)
			.commit();

	}
	@Override
	protected void onPause() {
    	minMaxSelectedSaved = minMaxSpinner.getMinMaxPositions();
		super.onPause();
	}
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		int[] minMaxSelected = minMaxSpinner.getMinMaxPositions();
		savedInstanceState.putIntArray("minMaxSelections", minMaxSelected);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onMinMaxSelect(int id) {
		ArrayList<String> minMax = minMaxSpinner.getMinMax();
		Log.i(logTag, "activity onMinMaxSelect: min = " + minMax.get(0)
				+ ", max = " + minMax.get(1));
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
	    boolean rv = false;
	    if (itemId == R.id.select_theme) {
		    selectedTheme++;
		    if (selectedTheme > themeList.size() - 1)
		    	selectedTheme = 0;
        	Editor e = prefs.edit();
            e.putInt("selectedTheme", selectedTheme);
            e.commit();
            recreate();
            rv = true;
	    }
	    if (false == rv)
	    	rv = super.onOptionsItemSelected(item);
	    return rv;
	}

}
