package com.example.nutriPlanner.View.Survey;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.nutriPlanner.R;
import com.example.nutriPlanner.View.SurveyActivity;
import com.google.android.material.button.MaterialButton;


public class GenderFragment extends Fragment {
    private OnSurveyInteractionListener mListener;
    private MaterialButton btnGenderWoman, btnGenderMan;
    private String gender = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gender, container, false);

        btnGenderWoman = rootView.findViewById(R.id.btnGenderWoman);
        btnGenderMan = rootView.findViewById(R.id.btnGenderMan);
        sendCorrectData(false);

        btnGenderWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Woman";
                btnGenderWoman.setTextColor(getResources().getColor(R.color.white));
                btnGenderWoman.setBackgroundColor(getResources().getColor(R.color.main_color));
                btnGenderMan.setTextColor(getResources().getColor(R.color.main_color));
                btnGenderMan.setBackgroundColor(getResources().getColor(R.color.white));
                sendCorrectData(true);
            }
        });

        btnGenderMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Man";
                btnGenderMan.setTextColor(getResources().getColor(R.color.white));
                btnGenderMan.setBackgroundColor(getResources().getColor(R.color.main_color));
                btnGenderWoman.setTextColor(getResources().getColor(R.color.main_color));
                btnGenderWoman.setBackgroundColor(getResources().getColor(R.color.white));
                sendCorrectData(true);
            }
        });

        return rootView;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void sendCorrectData(boolean isCorrectData)
    {
        SurveyActivity activity = (SurveyActivity) getActivity();
        if (activity != null) {
            activity.setCorrectData(isCorrectData);
        }
    }

    public void notifyGenderSelected() {
        if (mListener != null) {
            if (gender != null) {
                mListener.onGenderSelected(gender);
            }else {
                Toast.makeText(getActivity(), "Введите корректные данные", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
