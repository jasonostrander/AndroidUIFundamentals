package com.example;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener mListener;
    private int year = -1;
    private int month = -1;
    private int day = -1;
    
    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener,
            long milliTime) {
        DatePickerFragment frag = new DatePickerFragment();
        frag.mListener = listener;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliTime);
        frag.year = cal.get(Calendar.YEAR);
        frag.month = cal.get(Calendar.MONTH);
        frag.day = cal.get(Calendar.DAY_OF_MONTH);
        return frag;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), mListener, year, month, day);
    }
}
