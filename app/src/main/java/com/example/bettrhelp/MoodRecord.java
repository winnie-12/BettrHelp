package com.example.bettrhelp;

import java.util.ArrayList;
import java.util.List;

public class MoodRecord {
    public static int selectedFeeling = -1; // 0~4
    public static int stressLevel = 0;
    public static int energyLevel = 0;

    public static List<String> selectedEmotions = new ArrayList<>();
    public static List<String> selectedEvents = new ArrayList<>();
    public static List<String> selectedPeople = new ArrayList<>();
    public static List<String> selectedSchool = new ArrayList<>();
    public static List<String> selectedHealth = new ArrayList<>();

    public static String inputNote = "";

    // Add these new fields for edit functionality
    public static boolean isEditMode = false;
    public static List<String> photoBase64List = new ArrayList<>();

    // Method to clear all data (call when starting fresh mood entry)
    public static void clearData() {
        selectedFeeling = -1;
        stressLevel = 0;
        energyLevel = 0;
        selectedEmotions.clear();
        selectedEvents.clear();
        selectedPeople.clear();
        selectedSchool.clear();
        selectedHealth.clear();
        inputNote = "";
        isEditMode = false;
        photoBase64List.clear();
    }

    // Method to populate data for editing (call from HomeFragment)
    public static void populateForEdit(int feeling, int stress, int energy,
                                       List<String> emotions, List<String> events,
                                       List<String> people, List<String> school,
                                       List<String> health, String note,
                                       List<String> photos) {
        isEditMode = true;
        selectedFeeling = feeling;
        stressLevel = stress;
        energyLevel = energy;

        selectedEmotions.clear();
        selectedEvents.clear();
        selectedPeople.clear();
        selectedSchool.clear();
        selectedHealth.clear();
        photoBase64List.clear();

        if (emotions != null) selectedEmotions.addAll(emotions);
        if (events != null) selectedEvents.addAll(events);
        if (people != null) selectedPeople.addAll(people);
        if (school != null) selectedSchool.addAll(school);
        if (health != null) selectedHealth.addAll(health);
        if (note != null) inputNote = note;
        if (photos != null) photoBase64List.addAll(photos);
    }
}