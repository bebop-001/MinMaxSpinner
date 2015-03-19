package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.minmaxspinner.MinMaxSpinner.OnMinMaxSpinnerListener;

public class MainActivity extends Activity implements OnMinMaxSpinnerListener {
	private static final String logTag = "MainActivity";
	private static List<String> strokecountList;
	private static final int MAX_STROKECOUNT = 24;
	private static MinMaxSpinner minMaxSpinner;
    private static SharedPreferences prefs;
    private static int[] minMaxSelectedSaved;
	
	public static class ThemeInfo {
		private String name; private int themeId;
		private ThemeInfo (String name, int themeId) {
			this.name = name; this.themeId = themeId;
		}
		public String toString() {return name; }
	}
	static int selectedTheme;
	public static final SparseArray<ThemeInfo> themeList = new SparseArray<ThemeInfo>(2) {
		{
			append(R.id.dark_theme, new ThemeInfo("Holo Dark", android.R.style.Theme_Holo));
			append(R.id.light_theme, new ThemeInfo("Holo Light", android.R.style.Theme_Holo_Light));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        /****************** Theme setup ********************************/
    	// Restore user's previous selected theme.
    	prefs = getSharedPreferences("user_prefs.txt", Context.MODE_PRIVATE);
    	selectedTheme = prefs.getInt("selectedTheme", R.id.dark_theme);

        setTheme(themeList.get(selectedTheme).themeId);

		setContentView(R.layout.activity_main);
		View minMaxLayout = findViewById(R.id.activity_minmax);
		strokecountList = new ArrayList<String>();
		for(int i = 1; i < MAX_STROKECOUNT; i++) {
			strokecountList.add(String.valueOf(i));
		}

		minMaxSpinner = new MinMaxSpinner(minMaxLayout
				, R.id.min_strokecount, R.id.max_strokecount
				, strokecountList)
		.setTitle("Activity min/max spinner: ")
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    menu.findItem(selectedTheme).setChecked(true);
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
	    boolean rv = true;
	    ThemeInfo theme = themeList.get(itemId, null);
	    Log.i(logTag, String.format("onOptionsItemSelected: 0x%08x:", itemId));
	    if (null != theme) {
	    	Log.i(logTag, "selected " + theme.name);
	    	item.setChecked(true);
        	Log.i(logTag, "onNavigationItemSelected:" + itemId);
        	Editor e = prefs.edit();
            e.putInt("selectedTheme", itemId);
            e.commit();
            finish();
            startActivity(new Intent(this, this.getClass()));
	    }
	    else {
	    	rv = false;
	    }
	    if (false == rv)
	    	rv = super.onOptionsItemSelected(item);
	    return rv;
	}

}
