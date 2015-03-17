package com.codepath.petbnbcodepath.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.andexert.calendarlistview.library.DayPickerView;
import com.andexert.calendarlistview.library.SimpleMonthAdapter;

import com.codepath.petbnbcodepath.R;

/**
 * Created by vibhalaljani on 3/16/15.
 */
public class DatePickerDialog extends DialogFragment implements
        com.andexert.calendarlistview.library.DatePickerController {

    private DayPickerView pickerView;

    public DatePickerDialog() {

    }

    public static DatePickerDialog newInstance() {
        DatePickerDialog datePickerDialog = new DatePickerDialog();
        return datePickerDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker, container);

        pickerView = (DayPickerView) view.findViewById(R.id.pickerView);
        pickerView.setmController(this);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    @Override
    public int getMaxYear()
    {
        return 2015;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day)
    {
        Log.e("Day Selected", day + " / " + month + " / " + year);
    }


    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays)
    {

        Log.e("Date range selected", selectedDays.getFirst().toString() + " --> " + selectedDays.getLast().toString());
    }
}
