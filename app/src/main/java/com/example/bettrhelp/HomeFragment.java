package com.example.bettrhelp;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView textFeeling, textStress, textEnergy, textMoodTags, textNote, textToggle, editButton, deleteRecordButton;
    private ImageView imageFeeling, imageView1, imageView2, imageView3;
    private LinearLayout moreContent, layoutChooseFeeling, layoutMoodData;

    // Store current data for editing
    private Integer currentFeeling;
    private Integer currentStress;
    private Integer currentEnergy;
    private List<String> currentEmotions;
    private List<String> currentEvents;
    private List<String> currentPeople;
    private List<String> currentSchool;
    private List<String> currentHealth;
    private String currentNote;
    private List<String> currentPhotos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        textFeeling = view.findViewById(R.id.textFeeling);
        textStress = view.findViewById(R.id.textStress);
        textEnergy = view.findViewById(R.id.textEnergy);
        imageFeeling = view.findViewById(R.id.imageFeeling);
        textMoodTags = view.findViewById(R.id.textMoodTags);
        textNote = view.findViewById(R.id.textNote);
        imageView1 = view.findViewById(R.id.imageView1);
        imageView2 = view.findViewById(R.id.imageView2);
        imageView3 = view.findViewById(R.id.imageView3);
        layoutChooseFeeling = view.findViewById(R.id.linearChooseFeeling);
        layoutMoodData = view.findViewById(R.id.layoutMoodData);
        TextView textDate = view.findViewById(R.id.textDate);
        textToggle = view.findViewById(R.id.textToggle);
        moreContent = view.findViewById(R.id.moreContent);
        editButton = view.findViewById(R.id.editButton);
        deleteRecordButton = view.findViewById(R.id.deleteRecordButton);

        textDate.setText(getTodayDate());

        textToggle.setOnClickListener(new View.OnClickListener() {
            boolean isExpanded = false;

            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    moreContent.setVisibility(View.GONE);
                    textToggle.setText(getString(R.string.read_more));
                } else {
                    moreContent.setVisibility(View.VISIBLE);
                    textToggle.setText(getString(R.string.read_less));
                }
                isExpanded = !isExpanded;
            }
        });

        layoutChooseFeeling.setOnClickListener(v -> {
            MoodRecord.clearData(); // Clear data for new entry
            // Navigate to MoodFragment - adjust container ID to match your MainActivity layout
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToMoodFragment();
            }
        });

        editButton.setOnClickListener(v -> editCurrentMoodRecord());

        deleteRecordButton.setOnClickListener(v -> confirmAndDeleteMood());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMoodData();
    }

//========================EDIT======================
    private void editCurrentMoodRecord() {
        // Populate MoodRecord with current data for editing
        MoodRecord.populateForEdit(
                currentFeeling != null ? currentFeeling : -1,
                currentStress != null ? currentStress : 0,
                currentEnergy != null ? currentEnergy : 0,
                currentEmotions,
                currentEvents,
                currentPeople,
                currentSchool,
                currentHealth,
                currentNote,
                currentPhotos
        );

        // Navigate to MoodFragment - adjust to match your MainActivity layout
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToMoodFragment();
        }
    }

    //===================VIEW==============================
    private void loadMoodData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String dbUrl = "https://bettrhelp-bci3283-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(dbUrl);
        DatabaseReference ref = database.getReference("moods").child(userId);

        ref.child(today()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d("FirebaseDebug", "No data for today.");
                    Toast.makeText(getContext(), "No mood record for today", Toast.LENGTH_SHORT).show();
                    layoutChooseFeeling.setVisibility(View.VISIBLE);
                    layoutMoodData.setVisibility(View.GONE);
                    editButton.setVisibility(View.GONE);
                    deleteRecordButton.setVisibility(View.GONE);
                    return;
                }

                layoutChooseFeeling.setVisibility(View.GONE);
                layoutMoodData.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
                deleteRecordButton.setVisibility(View.VISIBLE);

                Integer feeling = snapshot.child("feeling").getValue(Integer.class);
                Integer stress = snapshot.child("stressLevel").getValue(Integer.class);
                Integer energy = snapshot.child("energyLevel").getValue(Integer.class);

                String feelingStr = "Unknown";
                int drawableRes = R.drawable.emoji_unknown;

                if (feeling != null) {
                    switch (feeling) {
                        case 0: feelingStr = "Great"; drawableRes = R.drawable.emoji_great; break;
                        case 1: feelingStr = "Good"; drawableRes = R.drawable.emoji_good; break;
                        case 2: feelingStr = "Okay"; drawableRes = R.drawable.emoji_okay; break;
                        case 3: feelingStr = "Not Okay"; drawableRes = R.drawable.emoji_not_okay; break;
                        case 4: feelingStr = "Bad"; drawableRes = R.drawable.emoji_bad; break;
                    }
                }

                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<>() {};
                List<String> emotions = snapshot.child("emotions").getValue(t);
                List<String> events = snapshot.child("events").getValue(t);
                List<String> people = snapshot.child("people").getValue(t);
                List<String> school = snapshot.child("school").getValue(t);
                List<String> health = snapshot.child("health").getValue(t);
                List<String> photoBase64List = snapshot.child("photos").getValue(t);

                String moodTags = combineMoodTags(Arrays.asList(emotions, events, people, school, health));
                moodTags = moodTags.isEmpty() ? "-" : moodTags;

                String inputNote = snapshot.child("inputNote").getValue(String.class);

                // Store current data for editing
                currentFeeling = feeling;
                currentStress = stress;
                currentEnergy = energy;
                currentEmotions = emotions;
                currentEvents = events;
                currentPeople = people;
                currentSchool = school;
                currentHealth = health;
                currentNote = inputNote;
                currentPhotos = photoBase64List;

                textFeeling.setText(feelingStr);
                textStress.setText(getString(R.string.stress_level_label, stress));
                textEnergy.setText(getString(R.string.energy_level_label, energy));
                imageFeeling.setImageResource(drawableRes);
                textMoodTags.setText(moodTags);

                if (inputNote != null && !inputNote.trim().isEmpty()) {
                    textNote.setText(getString(R.string.notes_label, inputNote));
                    textNote.setVisibility(View.VISIBLE);
                } else {
                    textNote.setVisibility(View.GONE);
                }

                if (photoBase64List != null && !photoBase64List.isEmpty()) {
                    List<ImageView> imageViews = Arrays.asList(imageView1, imageView2, imageView3);
                    int count = Math.min(photoBase64List.size(), imageViews.size());

                    for (int i = 0; i < count; i++) {
                        try {
                            String base64 = photoBase64List.get(i);
                            if (base64 != null && !base64.trim().isEmpty()) {
                                byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                imageViews.get(i).setImageBitmap(bitmap);
                                imageViews.get(i).setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.e("BitmapError", "Error decoding image: " + e.getMessage());
                        }
                    }

                    for (int i = count; i < imageViews.size(); i++) {
                        imageViews.get(i).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String combineMoodTags(List<List<String>> tagLists) {
        StringBuilder sb = new StringBuilder();
        for (List<String> tags : tagLists) {
            if (tags != null) {
                for (String tag : tags) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(tag);
                }
            }
        }
        return sb.toString();
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH);
        return dateFormat.format(calendar.getTime());
    }

    private String today() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return sdf.format(date);
    }

//===================DELETE==============================
    private void confirmAndDeleteMood() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Mood Record")
                .setMessage("Are you sure you want to delete today's mood record?")
                .setPositiveButton("Delete", (dialog, which) -> deleteMoodRecord())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMoodRecord() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String dbUrl = "https://bettrhelp-bci3283-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(dbUrl);
        DatabaseReference ref = database.getReference("moods").child(userId).child(today());

        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Record deleted", Toast.LENGTH_SHORT).show();
                // Clear current data variables
                currentFeeling = null;
                currentStress = null;
                currentEnergy = null;
                currentEmotions = null;
                currentEvents = null;
                currentPeople = null;
                currentSchool = null;
                currentHealth = null;
                currentNote = null;
                currentPhotos = null;

                // Update UI: show choose feeling layout, hide mood data layout and buttons
                layoutChooseFeeling.setVisibility(View.VISIBLE);
                layoutMoodData.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                deleteRecordButton.setVisibility(View.GONE);

                // Also clear MoodRecord static data to ensure a fresh start
                MoodRecord.clearData();
            } else {
                Toast.makeText(getContext(), "Failed to delete mood record.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}