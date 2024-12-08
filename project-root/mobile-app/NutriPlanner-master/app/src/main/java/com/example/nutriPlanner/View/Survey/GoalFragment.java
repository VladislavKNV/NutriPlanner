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

public class GoalFragment extends Fragment {
    private OnSurveyInteractionListener mListener;
    private MaterialButton btnGoal1, btnGoal2, btnGoal3;
    private String goal = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goal, container, false);

        btnGoal1 = rootView.findViewById(R.id.btnGoal1);
        btnGoal2 = rootView.findViewById(R.id.btnGoal2);
        btnGoal3 = rootView.findViewById(R.id.btnGoal3);
        sendCorrectData(false);

        btnGoal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goal = "WEIGHT_LOSS";
                btnGoal1.setTextColor(getResources().getColor(R.color.white));
                btnGoal1.setBackgroundColor(getResources().getColor(R.color.main_color));
                btnGoal2.setTextColor(getResources().getColor(R.color.main_color));
                btnGoal2.setBackgroundColor(getResources().getColor(R.color.white));
                btnGoal3.setTextColor(getResources().getColor(R.color.main_color));
                btnGoal3.setBackgroundColor(getResources().getColor(R.color.white));
                sendCorrectData(true);
            }
        });

        btnGoal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal = "MAINTENANCE";
                btnGoal2.setTextColor(getResources().getColor(R.color.white));
                btnGoal2.setBackgroundColor(getResources().getColor(R.color.main_color));
                btnGoal1.setTextColor(getResources().getColor(R.color.main_color));
                btnGoal1.setBackgroundColor(getResources().getColor(R.color.white));
                btnGoal3.setTextColor(getResources().getColor(R.color.main_color));
                btnGoal3.setBackgroundColor(getResources().getColor(R.color.white));
                sendCorrectData(true);
            }
        });

        btnGoal3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal = "WEIGHT_GAIN";
                btnGoal3.setTextColor(getResources().getColor(R.color.white));
                btnGoal3.setBackgroundColor(getResources().getColor(R.color.main_color));
                btnGoal1.setTextColor(getResources().getColor(R.color.main_color));
                btnGoal1.setBackgroundColor(getResources().getColor(R.color.white));
                btnGoal2.setTextColor(getResources().getColor(R.color.main_color));
                btnGoal2.setBackgroundColor(getResources().getColor(R.color.white));
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

    public void notifyGoalSelected() {
        if (mListener != null) {
            if (goal != null) {
                mListener.onGoalSelected(goal);
            }else {
                Toast.makeText(getActivity(), "Введите корректные данные", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
