package com.example.bettrhelp;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class Mood1Fragment extends Fragment {
    public Mood1Fragment() {
        super(R.layout.fragment_mood1);
    }

    SeekBar seekBarStressLevel, seekBarEnergyLevel;
    TextView textStressLevelValue, textEnergyLevelValue;
    ImageButton[] emojiButtons;
    int[] defaultIcons = {
            R.drawable.emoji_great,
            R.drawable.emoji_good,
            R.drawable.emoji_okay,
            R.drawable.emoji_not_okay,
            R.drawable.emoji_bad
    };
    int[] selectedIcons = {
            R.drawable.emoji_great_selected,
            R.drawable.emoji_good_selected,
            R.drawable.emoji_okay_selected,
            R.drawable.emoji_not_okay_selected,
            R.drawable.emoji_bad_selected
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Next Button
        Button next = view.findViewById(R.id.buttonNextMood2);
        next.setOnClickListener(v -> {
            // 使用 parent Fragment 进行切换
            Fragment parent = getParentFragment();
            if (parent instanceof MoodFragment) {
                ((MoodFragment) parent).navigateToFragment(new Mood2Fragment());
            }
        });

        //Back button
        ImageButton buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToHomeFragment();
            }
        });


        //emoji Button
        emojiButtons = new ImageButton[5];
        emojiButtons[0] = view.findViewById(R.id.imageButtonEmojiGreat);
        emojiButtons[1] = view.findViewById(R.id.imageButtonEmojiGood);
        emojiButtons[2] = view.findViewById(R.id.imageButtonEmojiOkay);
        emojiButtons[3] = view.findViewById(R.id.imageButtonEmojiNotOkay);
        emojiButtons[4] = view.findViewById(R.id.imageButtonEmojiBad);

        for (int i = 0; i < emojiButtons.length; i++) {
            final int index = i;
            emojiButtons[i].setOnClickListener(v -> selectEmoji(index));
        }

        //SeekBar stress level
        seekBarStressLevel = view.findViewById(R.id.seekBarStressLevel);
        textStressLevelValue = view.findViewById(R.id.textStressLevelValue);

        seekBarStressLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textStressLevelValue.setVisibility(View.VISIBLE);
                textStressLevelValue.setText(
                        getString(R.string.stress_level_format, progress)
                );
                MoodRecord.stressLevel = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //SeekBar energy level
        seekBarEnergyLevel = view.findViewById(R.id.seekBarEnergyLevel);
        textEnergyLevelValue = view.findViewById(R.id.textEnergyLevelValue);

        seekBarEnergyLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textEnergyLevelValue.setVisibility(View.VISIBLE);
                textEnergyLevelValue.setText(
                        getString(R.string.energy_level_format, progress)
                );
                MoodRecord.energyLevel = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Check if we're in edit mode and populate the UI
        if (MoodRecord.isEditMode) {
            populateEditData();
        }
    }

//=============EDIT=================================
    private void selectEmoji(int index) {
        // reset to origin
        for (int j = 0; j < emojiButtons.length; j++) {
            emojiButtons[j].setImageResource(defaultIcons[j]);
            emojiButtons[j].setScaleX(1f);
            emojiButtons[j].setScaleY(1f);
        }

        // Selected emoji will be blue
        emojiButtons[index].setImageResource(selectedIcons[index]);
        emojiButtons[index].animate().scaleX(1.2f).scaleY(1.2f).setDuration(150).start();

        MoodRecord.selectedFeeling = index; //save selected emoji index
    }

    private void populateEditData() {
        // Set the feeling emoji if available
        if (MoodRecord.selectedFeeling >= 0 && MoodRecord.selectedFeeling < 5) {
            selectEmoji(MoodRecord.selectedFeeling);
        }

        // Set stress level
        seekBarStressLevel.setProgress(MoodRecord.stressLevel);
        textStressLevelValue.setVisibility(View.VISIBLE);
        textStressLevelValue.setText(
                getString(R.string.stress_level_format, MoodRecord.stressLevel)
        );

        // Set energy level
        seekBarEnergyLevel.setProgress(MoodRecord.energyLevel);
        textEnergyLevelValue.setVisibility(View.VISIBLE);
        textEnergyLevelValue.setText(
                getString(R.string.energy_level_format, MoodRecord.energyLevel)
        );
    }
}

