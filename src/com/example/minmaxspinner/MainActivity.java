package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.minmaxspinner.MinMaxSpinner.OnMinMaxSpinnerListener;

public class MainActivity extends Activity implements OnMinMaxSpinnerListener {
	private static final String logTag = "MainActivity";
	private static List<String> strokecountList;
	private static final int MAX_STROKECOUNT = 24;
	private static MinMaxSpinner minMaxSpinner;
	
	public class ThemeInfo {
		private String name; private int resId;
		private ThemeInfo (String name, int resId) {
			this.name = name; this.resId = resId;
		}
		public String toString() {return name; }
	}
	public final ArrayList<ThemeInfo> themeList = new ArrayList<ThemeInfo>(2) {
		{
			add(new ThemeInfo("Holo Dark", android.R.style.Theme_Holo));
			add(new ThemeInfo("Holo Light", android.R.style.Theme_Holo_Light));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		View minMaxLayout = findViewById(R.id.activity_minmax);
		strokecountList = new ArrayList<String>();
		for(int i = 1; i < MAX_STROKECOUNT; i++) {
			strokecountList.add(String.valueOf(i));
		}
		if (null == minMaxSpinner) {
			minMaxSpinner = new MinMaxSpinner(minMaxLayout
					, R.id.min_strokecount, R.id.max_strokecount
					, strokecountList)
			.setTitle("Activity min/max spinner: ");
		}
		minMaxSpinner.update(minMaxLayout);
		Log.i(logTag, "reset listener");
		minMaxSpinner.setOnSelectListener(this);

		Fragment minMaxFrag = new MinMaxSpinnerFrag();
		FragmentTransaction transaction = getFragmentManager()
			.beginTransaction();
		transaction.replace(R.id.frag_placeholder, minMaxFrag)
			.commit();

	}

	@Override
	public void onMinMaxSelect(int id) {
		ArrayList<String> minMax = minMaxSpinner.getMinMax();
		Log.i(logTag, "activity onMinMaxSelect: min = " + minMax.get(0)
				+ ", max = " + minMax.get(1));
	}
}
