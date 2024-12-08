package com.example.nutriPlanner.View.Survey;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.fragment.app.Fragment;

import com.example.nutriPlanner.R;
import com.example.nutriPlanner.View.SurveyActivity;

public class HeightFragment extends Fragment {


    private OnSurveyInteractionListener mListener;
    private NumberPicker heightPicker1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sendCorrectData();
        View rootView = inflater.inflate(R.layout.fragment_height, container, false);
        heightPicker1 = rootView.findViewById(R.id.heightPicker);
        bindingHeightPickerCm();

        return rootView;
    }

    private void bindingHeightPickerCm(){
        heightPicker1.setMinValue(100);
        heightPicker1.setMaxValue(250);
        heightPicker1.setValue(175);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSurveyInteractionListener) {
            mListener = (OnSurveyInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUnitSelectedListener");
        }
    }

    private void sendCorrectData()
    {
        SurveyActivity activity = (SurveyActivity) getActivity();
        if (activity != null) {
            activity.setCorrectData(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void notifyHeightSelected() {
        if (mListener != null) {
            mListener.onHeightSelected(heightPicker1.getValue());
        }
    }
}
