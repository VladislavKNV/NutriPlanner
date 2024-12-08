package com.example.nutriPlanner.View.Survey;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nutriPlanner.R;
import com.example.nutriPlanner.View.SurveyActivity;

import java.util.Calendar;

public class BirthdayFragment extends Fragment {

    private NumberPicker dayPicker;
    private NumberPicker monthPicker;
    private NumberPicker yearPicker;
    private OnSurveyInteractionListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_birthday, container, false);

        sendCorrectData(true);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        dayPicker = rootView.findViewById(R.id.dayPicker);
        monthPicker = rootView.findViewById(R.id.monthPicker);
        yearPicker = rootView.findViewById(R.id.yearPicker);

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        yearPicker.setMinValue(1940);
        yearPicker.setMaxValue(currentYear - 6);
        dayPicker.setValue(1);
        monthPicker.setValue(1);
        yearPicker.setValue(2000);

        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                checkDateAndNotify();
            }
        });

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                checkDateAndNotify();
            }
        });

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                checkDateAndNotify();
            }
        });

        return rootView;
    }

    private void sendCorrectData(boolean isCorrectData) {
        SurveyActivity activity = (SurveyActivity) getActivity();
        if (activity != null) {
            activity.setCorrectData(isCorrectData);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSurveyInteractionListener) {
            mListener = (OnSurveyInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDateSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void notifyDateSelected() {
        int day = dayPicker.getValue();
        int month = monthPicker.getValue();
        int year = yearPicker.getValue();

        if (mListener != null) {
            mListener.onDateSelected(day, month, year);
        }
    }

    private boolean isValidDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.set(year, month - 1, day);
        try {
            calendar.getTime();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void checkDateAndNotify() {
        int day = dayPicker.getValue();
        int month = monthPicker.getValue();
        int year = yearPicker.getValue();

        if (isValidDate(day, month, year)) {
            sendCorrectData(true);
        } else {
            Toast.makeText(getContext(), "Выбрана некорректная дата", Toast.LENGTH_SHORT).show();
            sendCorrectData(false);
        }
    }
}