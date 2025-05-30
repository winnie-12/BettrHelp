package com.example.bettrhelp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;



public class MoodFragment extends Fragment {
    public MoodFragment() {
        super(R.layout.fragment_mood);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load Mood1Fragment by default
        getChildFragmentManager().beginTransaction()
                .replace(R.id.mood_inner_fragment_container, new Mood1Fragment())
                .commit();
    }

    public void navigateToFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.mood_inner_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
