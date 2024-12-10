package com.example.nutriPlanner.View.Survey;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nutriPlanner.R;

public class ActivityLevelFragment extends Fragment {

    private TextView textViewActivityLevel;
    private OnSurveyInteractionListener mListener;
    private String AL = "MODERATE";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activity_level, container, false);

        textViewActivityLevel = rootView.findViewById(R.id.tvwAL);
        SeekBar seekBarActivityLevel = rootView.findViewById(R.id.activityLevelSeekBar);

        seekBarActivityLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateActivityLevelText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        updateActivityLevelText(seekBarActivityLevel.getProgress());

        return rootView;
    }

    // Метод для обновления текста в соответствии с выбранным уровнем активности
    private void updateActivityLevelText(int progress) {
        String activityLevelText;
        switch (progress) {
            case 0:
                activityLevelText = "Очень низкий уровень активности:\n Менее 5 000 шагов в день";
                AL = "SEDENTARY";
                break;
            case 1:
                activityLevelText = "Низкий уровень активности:\n От 5 000 до 7 500 шагов в день";
                AL = "LIGHT";
                break;
            case 2:
                activityLevelText = "Средний уровень активности:\n От 7 500 до 10 000 шагов в день";
                AL = "MODERATE";
                break;
            case 3:
                activityLevelText = "Высокий уровень активности:\n От 10 000 до 12 500 шагов в день";
                AL = "ACTIVE";
                break;
            case 4:
                activityLevelText = "Очень высокий уровень активности:\n Более 12 500 шагов в день";
                AL = "VERY_ACTIVE";
                break;
            default:
                activityLevelText = "Средний уровень активности:\n От 7 500 до 10 000 шагов в день";
                AL = "MODERATE";
                break;
        }
        textViewActivityLevel.setText(activityLevelText);
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

    public void notifyALevelSelected() {
        if (mListener != null) {
                mListener.onALevelSelected(AL);
        }
    }
}