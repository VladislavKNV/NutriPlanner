package com.example.nutriPlanner.View.Survey;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.nutriPlanner.R;
import com.example.nutriPlanner.View.SurveyActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class WeightFragment extends Fragment {

    private OnSurveyInteractionListener mListener;
    private TextInputEditText edtSurveyCurrentWeight;
    private TextInputEditText edtSurveyDesiredWeight;
    private TextInputLayout edtSurveyCurrentWeight1;
    private TextInputLayout edtSurveyDesiredWeight1;
    private  double current;
    private  double desired;
    private String goal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weight, container, false);

        SurveyActivity activity = (SurveyActivity) getActivity();
        goal = activity.goal;

        edtSurveyCurrentWeight = rootView.findViewById(R.id.edtSurveyCurrentWeight);
        edtSurveyDesiredWeight = rootView.findViewById(R.id.edtSurveyDesiredWeight);
        edtSurveyCurrentWeight1 = rootView.findViewById(R.id.edtSurveyCurrentWeight1);
        edtSurveyDesiredWeight1 = rootView.findViewById(R.id.edtSurveyDesiredWeight1);
        sendCorrectData(false);

        if (goal.equals("MAINTENANCE")) {
            edtSurveyDesiredWeight.setVisibility(View.GONE);
            edtSurveyDesiredWeight1.setVisibility(View.GONE);
        }

        edtSurveyCurrentWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    validateWeightInput();
                } catch (NumberFormatException e) {
                    sendCorrectData(false);
                }
            }
        });

        edtSurveyDesiredWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    validateWeightInput();
                } catch (NumberFormatException e) {
                    sendCorrectData(false);
                }
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

    public boolean validateWeightInput() {
        String currentStr = edtSurveyCurrentWeight.getText().toString();
        String desiredStr = edtSurveyDesiredWeight.getText().toString();

        if (goal.equals("MAINTENANCE")) {
            desiredStr = currentStr;
        }

        if (currentStr.isEmpty() || desiredStr.isEmpty()) {
            return false;
        }

        try {
            current = Double.parseDouble(currentStr);
            desired = Double.parseDouble(desiredStr);

            // Проверка диапазона
            if (current < 20 || current > 300 || desired < 20 || desired > 300) {
                // установка ошибок
                edtSurveyCurrentWeight1.setError("Введите корректные данные");
                edtSurveyCurrentWeight.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                edtSurveyDesiredWeight1.setError("Введите корректные данные");
                edtSurveyDesiredWeight.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                return false;
            } else {
                // очистка ошибок и установка цвета текста
                edtSurveyCurrentWeight1.setError(null);
                edtSurveyCurrentWeight.setTextColor(getResources().getColor(R.color.main_color));
                edtSurveyDesiredWeight1.setError(null);
                edtSurveyDesiredWeight.setTextColor(getResources().getColor(R.color.main_color));
            }

            // округление значений до двух знаков после запятой
            current = Math.round(current * 100.0) / 100.0;
            desired = Math.round(desired * 100.0) / 100.0;

            if (checkGoalAchievement(goal, current, desired)){
                // отправка правильных данных
                sendCorrectData(true);
                return true;
            } else {
                sendCorrectData(false);
                Toast.makeText(getContext(), "Данные не соответствуют цели", Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void sendCorrectData(boolean isCorrectData)
    {
        SurveyActivity activity = (SurveyActivity) getActivity();
        if (activity != null) {
            activity.setCorrectData(isCorrectData);
        }
    }

    public boolean checkGoalAchievement(String goal, double currentWeight, double desiredWeight){
        switch (goal) {
            case "WEIGHT_LOSS":
                return currentWeight > desiredWeight;
            case "MAINTENANCE":
                return currentWeight == desiredWeight;
            case "WEIGHT_GAIN":
                return currentWeight < desiredWeight;
        }
        return false;
    }

    public void notifyWeightSelected() {
        if (mListener != null) {
            mListener.onWeightSelected(current, desired);
        }
    }
}