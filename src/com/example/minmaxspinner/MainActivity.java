package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.minmaxspinner.MinMaxSpinner.OnMinMaxSpinnerListener;

public class MainActivity extends Activity implements OnMinMaxSpinnerListener {
	private static final String logTag = "MainActivity";
	private static List<String> strokecountList;
	private static final int MAX_STROKECOUNT = 24;
	private static MinMaxSpinner minMaxSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		View minMaxLayout = findViewById(R.id.min_max_layout);
		strokecountList = new ArrayList<String>();
		for(int i = 1; i < MAX_STROKECOUNT; i++) {
			strokecountList.add(String.valueOf(i));
		}
		minMaxSpinner = new MinMaxSpinner(minMaxLayout
			, R.id.min_strokecount, R.id.max_strokecount, strokecountList);
		minMaxSpinner.setOnSelectListener(this);
	}

	@Override
	public void onMinMaxSelect(int id) {
		Log.i(logTag, "onMinMaxSelect: min = " + minMaxSpinner.getMin()
				+ ", max = " + minMaxSpinner.getMax());
	}
}
