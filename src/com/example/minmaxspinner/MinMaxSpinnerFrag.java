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

	private static MinMaxSpinner minMaxSpinner;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(logTag, "onCreateView");
		View minMaxFrag = inflater.inflate(R.layout.minmax_spinner_frag, container, false);
		View minmaxView = minMaxFrag.findViewById(R.id.fragment_minmax);
		if (null == minMaxSpinner) {
			minMaxSpinner = new MinMaxSpinner(minmaxView
				, R.id.min_strokecount, R.id.max_strokecount
				, list);
		}
		minMaxSpinner.update(minmaxView);
		minMaxSpinner.setOnSelectListener(this);
		return minMaxFrag;
	}

	@Override
	public void onMinMaxSelect(int id) {
		Log.i(logTag, "onMinMaxSelect: min = " + minMaxSpinner.getMin()
				+ ", max = " + minMaxSpinner.getMax());
	}

}
