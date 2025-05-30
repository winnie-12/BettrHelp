package com.example.bettrhelp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.List;

public class Mood2Fragment extends Fragment {

    public Mood2Fragment() {
        super(R.layout.fragment_mood2);
    }

    GridAdapter emotionAdapter, eventAdapter, peopleAdapter, schoolAdapter, healthAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button next = view.findViewById(R.id.buttonNextMood3);
        next.setOnClickListener(v -> {
            // save selected labels to MoodRecord
            MoodRecord.selectedEmotions = emotionAdapter.getSelectedLabels();
            MoodRecord.selectedEvents = eventAdapter.getSelectedLabels();
            MoodRecord.selectedPeople = peopleAdapter.getSelectedLabels();
            MoodRecord.selectedSchool = schoolAdapter.getSelectedLabels();
            MoodRecord.selectedHealth = healthAdapter.getSelectedLabels();

            Fragment parent = getParentFragment();
            if (parent instanceof MoodFragment) {
                ((MoodFragment) parent).navigateToFragment(new Mood3Fragment());
            }
        });

        //Back button
        ImageButton buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> {
                    Fragment parent = getParentFragment();
                    if (parent instanceof MoodFragment) {
                        ((MoodFragment) parent).navigateToFragment(new Mood1Fragment());
                    }
        });

        // Update button text for edit mode
        if (MoodRecord.isEditMode) {
            next.setText(R.string.continue_editing);
        }

        ExpandableHeightGridView gridViewEmotions = view.findViewById(R.id.gridEmotions);
        ExpandableHeightGridView gridViewEvents = view.findViewById(R.id.gridEvents);
        ExpandableHeightGridView gridViewPeople = view.findViewById(R.id.gridPeople);
        ExpandableHeightGridView gridViewSchool = view.findViewById(R.id.gridSchool);
        ExpandableHeightGridView gridViewHealth = view.findViewById(R.id.gridHealth);

        gridViewEmotions.setExpanded(true);
        gridViewEvents.setExpanded(true);
        gridViewPeople.setExpanded(true);
        gridViewSchool.setExpanded(true);
        gridViewHealth.setExpanded(true);

        int[] emotionImages = {
                R.drawable.emo_happy, R.drawable.emo_relaxed, R.drawable.emo_excited, R.drawable.emo_enthusiastic,
                R.drawable.emo_calm, R.drawable.emo_grateful, R.drawable.emo_hopeful, R.drawable.emo_proud,
                R.drawable.emo_sad, R.drawable.emo_anxious, R.drawable.emo_pressured, R.drawable.emo_stressed,
                R.drawable.emo_angry, R.drawable.emo_lonely, R.drawable.emo_tired, R.drawable.emo_annoyed
        };

        int[] emotionSelected = {
                R.drawable.emo_happy_selected, R.drawable.emo_relaxed_selected, R.drawable.emo_excited_selected, R.drawable.emo_enthusiastic_selected,
                R.drawable.emo_calm_selected, R.drawable.emo_grateful_selected, R.drawable.emo_hopeful_selected, R.drawable.emo_proud_selected,
                R.drawable.emo_sad_selected, R.drawable.emo_anxious_selected, R.drawable.emo_pressured_selected, R.drawable.emo_stressed_selected,
                R.drawable.emo_angry_selected, R.drawable.emo_lonely_selected, R.drawable.emo_tired_selected, R.drawable.emo_annoyed_selected
        };

        int[] eventsImages = {
                R.drawable.eve_cafe, R.drawable.eve_cinema, R.drawable.eve_home, R.drawable.eve_party,
                R.drawable.eve_restaurant, R.drawable.eve_school, R.drawable.eve_shopping, R.drawable.eve_travel
        };

        int[] eventsSelected = {
                R.drawable.eve_cafe_selected, R.drawable.eve_cinema_selected, R.drawable.eve_home_selected, R.drawable.eve_party_selected,
                R.drawable.eve_restaurant_selected, R.drawable.eve_school_selected, R.drawable.eve_shopping_selected, R.drawable.eve_travel_selected
        };

        int[] peopleImages = {
                R.drawable.pp_family, R.drawable.pp_friends, R.drawable.pp_partner, R.drawable.pp_none
        };

        int[] peopleSelected = {
                R.drawable.pp_family_selected, R.drawable.pp_friends_selected, R.drawable.pp_partner_selected, R.drawable.pp_none_selected
        };

        int[] schoolImages = {
                R.drawable.sc_class, R.drawable.sc_exam, R.drawable.sc_homework, R.drawable.sc_study
        };

        int[] schoolSelected = {
                R.drawable.sc_class_selected, R.drawable.sc_exam_selected, R.drawable.sc_homework_selected, R.drawable.sc_study_selected
        };

        int[] healthImages = {
                R.drawable.health_checkup, R.drawable.health_hospital, R.drawable.health_sick, R.drawable.health_medicine
        };

        int[] healthSelected = {
                R.drawable.health_checkup_selected, R.drawable.health_hospital_selected, R.drawable.health_sick_selected, R.drawable.health_medicine_selected
        };

        String[] emotionLabels = {
                "Happy", "Relaxed", "Excited", "Enthusiastic",
                "Calm", "Grateful", "Hopeful", "Proud",
                "Sad", "Anxious", "Pressured", "Stressed",
                "Angry", "Lonely", "Tired", "Annoyed"
        };

        String[] eventsLabels = {
                "Cafe", "Cinema", "Home", "Party",
                "Restaurant", "School", "Shopping", "Travel"
        };

        String[] peopleLabels = {
                "Family", "Friends", "Partner", "None"
        };

        String[] schoolLabels = {
                "Class", "Exam", "Homework", "Study"
        };

        String[] healthLabels = {
                "Check Up", "Hospital", "Sick", "Medicine"
        };

        emotionAdapter = new GridAdapter(getContext(), emotionImages, emotionLabels, emotionSelected);
        eventAdapter = new GridAdapter(getContext(), eventsImages, eventsLabels, eventsSelected);
        peopleAdapter = new GridAdapter(getContext(), peopleImages, peopleLabels, peopleSelected);
        schoolAdapter = new GridAdapter(getContext(), schoolImages, schoolLabels, schoolSelected);
        healthAdapter = new GridAdapter(getContext(), healthImages, healthLabels, healthSelected);

        gridViewEmotions.setAdapter(emotionAdapter);
        gridViewEvents.setAdapter(eventAdapter);
        gridViewPeople.setAdapter(peopleAdapter);
        gridViewSchool.setAdapter(schoolAdapter);
        gridViewHealth.setAdapter(healthAdapter);

        if(MoodRecord.isEditMode){
            preSelectItems();
        }
    }

//=========================EDIT========================
    private void preSelectItems() {
        // Pre-select emotions
        if (MoodRecord.selectedEmotions != null && emotionAdapter != null) {
            preSelectInAdapter(emotionAdapter, MoodRecord.selectedEmotions,
                    new String[]{"Happy", "Relaxed", "Excited", "Enthusiastic",
                            "Calm", "Grateful", "Hopeful", "Proud",
                            "Sad", "Anxious", "Pressured", "Stressed",
                            "Angry", "Lonely", "Tired", "Annoyed"});
        }

        // Pre-select events
        if (MoodRecord.selectedEvents != null && eventAdapter != null) {
            preSelectInAdapter(eventAdapter, MoodRecord.selectedEvents,
                    new String[]{"Cafe", "Cinema", "Home", "Party",
                            "Restaurant", "School", "Shopping", "Travel"});
        }

        // Pre-select people
        if (MoodRecord.selectedPeople != null && peopleAdapter != null) {
            preSelectInAdapter(peopleAdapter, MoodRecord.selectedPeople,
                    new String[]{"Family", "Friends", "Partner", "None"});
        }

        // Pre-select school
        if (MoodRecord.selectedSchool != null && schoolAdapter != null) {
            preSelectInAdapter(schoolAdapter, MoodRecord.selectedSchool,
                    new String[]{"Class", "Exam", "Homework", "Study"});
        }

        // Pre-select health
        if (MoodRecord.selectedHealth != null && healthAdapter != null) {
            preSelectInAdapter(healthAdapter, MoodRecord.selectedHealth,
                    new String[]{"Check Up", "Hospital", "Sick", "Medicine"});
        }
    }

    private void preSelectInAdapter(GridAdapter adapter, List<String> selectedItems, String[] allLabels) {
        for (String selectedItem : selectedItems) {
            for (int i = 0; i < allLabels.length; i++) {
                if (allLabels[i].equals(selectedItem)) {
                    adapter.setItemSelected(i, true);
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

}
