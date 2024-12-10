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


public class WeightFactorFragment extends Fragment {

    private TextView textViewWeightFactor;
    private TextView textViewWeightFactorCount;
    private OnSurveyInteractionListener mListener;
    private double weightFactor = 0.5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weight_factor, container, false);

        textViewWeightFactor = rootView.findViewById(R.id.tvWfRec);
        textViewWeightFactorCount = rootView.findViewById(R.id.tvWfCount);
        SeekBar seekBarWeightFactor = rootView.findViewById(R.id.WFSeekBar);

        seekBarWeightFactor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // изменение текста в зависимости от выбранного значения в SeekBar
                updateWeightFactorText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        updateWeightFactorText(seekBarWeightFactor.getProgress());

        return rootView;
    }

    private void updateWeightFactorText(int progress) {
        String weightFactorText;
        String weightFactorCountText;
        switch (progress) {
            case 0:
                weightFactorText = "Низкий темп";
                weightFactorCountText = "0,3 кг/неделя";
                weightFactor = 0.3;
                break;
            case 1:
                weightFactorText = "Рекомендуемый темп для долгосрочного\n успеха";
                weightFactorCountText = "0,4 кг/неделя";
                weightFactor = 0.4;
                break;
            case 2:
                weightFactorText = "Рекомендуемый темп для долгосрочного\n успеха";
                weightFactorCountText = "0,5 кг/неделя";
                weightFactor = 0.5;
                break;
            case 3:
                weightFactorText = "Рекомендуемый темп для долгосрочного\n успеха";
                weightFactorCountText = "0,6 кг/неделя";
                weightFactor = 0.6;
                break;
            case 4:
                weightFactorText = "Высокий темп";
                weightFactorCountText = "0,7 кг/неделя";
                weightFactor = 0.7;
                break;
            default:
                weightFactorText = "Рекомендуемый темп для долгосрочного\n успеха";
                weightFactorCountText = "0,5 кг/неделя";
                weightFactor = 0.5;
                break;
        }
        textViewWeightFactor.setText(weightFactorText);
        textViewWeightFactorCount.setText(weightFactorCountText);
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

    public void notifyWFactorSelected() {
        if (mListener != null) {
            mListener.onWFactorSelected(weightFactor);
        }
    }
}