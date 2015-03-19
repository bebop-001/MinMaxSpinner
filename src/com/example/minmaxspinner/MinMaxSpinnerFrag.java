package com.example.minmaxspinner;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.minmaxspinner.MinMaxSpinner.OnMinMaxSpinnerListener;

public class MinMaxSpinnerFrag extends Fragment implements OnMinMaxSpinnerListener{
	private static final String logTag = "MinMaxFragment";
	private static List<String> list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(logTag, "onCreate");
		list = new ArrayList<String>();
		for (int i = 1; i <= 20; i++)
			list.add("" + i);
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onPause() {
    	minMaxSelectedSaved = minMaxSpinner.getMinMaxPositions();
		super.onPause();
	}

	private static MinMaxSpinner minMaxSpinner;
	private static int[] minMaxSelectedSaved;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(logTag, "onCreateView");
		View minMaxFrag = inflater.inflate(R.layout.minmax_spinner_frag, container, false);
		View minmaxView = minMaxFrag.findViewById(R.id.fragment_minmax);
		minMaxSpinner = new MinMaxSpinner(minmaxView
			, R.id.min_strokecount, R.id.max_strokecount
			, list)
		.setTitle("Fragment min/max spinner: ")
		.setOnSelectListener(this)
		.setMinMaxPositions(minMaxSelectedSaved);
		return minMaxFrag;
	}

	@Override
	public void onMinMaxSelect(int id) {
		ArrayList<String> minMax = minMaxSpinner.getMinMax();
		Log.i(logTag, "onMinMaxSelect: min = " + minMax.get(0)
				+ ", max = " + minMax.get(1));
	}

}
