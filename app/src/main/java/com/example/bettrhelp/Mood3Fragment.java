package com.example.bettrhelp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class Mood3Fragment extends Fragment {

    private LinearLayout buttonAddPhoto;
    private LinearLayout photoContainer;
    private EditText editNotes;

    private static final int MAX_PHOTO_COUNT = 3;
    private final List<Uri> selectedPhotos = new ArrayList<>();
    private final List<String> existingPhotosBase64 = new ArrayList<>(); // For edit mode
    private ActivityResultLauncher<Intent> resultLauncher;
    private ActivityResultLauncher<Intent> speechInputLauncher;

    public Mood3Fragment() {
        super(R.layout.fragment_mood3);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonAddPhoto = view.findViewById(R.id.buttonAddPhoto);
        photoContainer = view.findViewById(R.id.photoContainer);
        ImageButton buttonVoice = view.findViewById(R.id.buttonVoice);
        editNotes = view.findViewById(R.id.editNotes);
        Button buttonSaveMood = view.findViewById(R.id.buttonSaveMood);

        buttonSaveMood.setOnClickListener(v -> uploadMoodRecordToFirebase());

        //Back button
        ImageButton buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> {
            Fragment parent = getParentFragment();
            if (parent instanceof MoodFragment) {
                ((MoodFragment) parent).navigateToFragment(new Mood2Fragment());
            }
        });

        // Update button text for edit mode
        if (MoodRecord.isEditMode) {
            buttonSaveMood.setText(R.string.update_mood);
        }

        // Register photo picker and speech input launchers
        registerPhotoPickerResult();
        registerSpeechInputResult();

        buttonAddPhoto.setOnClickListener(v -> {
            int totalPhotos = selectedPhotos.size() + existingPhotosBase64.size();
            if (totalPhotos < MAX_PHOTO_COUNT) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                resultLauncher.launch(intent);
            } else {
                Toast.makeText(getContext(), "You can only have up to 3 photos", Toast.LENGTH_LONG).show();
                buttonAddPhoto.setVisibility(View.GONE);
            }
        });

        buttonVoice.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

            try {
                speechInputLauncher.launch(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Speech input not supported", Toast.LENGTH_SHORT).show();
                Log.e("Mood3Fragment", "Speech input error", e);
            }
        });

        // Load existing data if in edit mode
        if (MoodRecord.isEditMode) {
            loadEditModeData();
        }
    }

    private void loadEditModeData() {
        // Load existing note
        if (MoodRecord.inputNote != null && !MoodRecord.inputNote.isEmpty()) {
            editNotes.setText(MoodRecord.inputNote);
        }

        // Load existing photos
        existingPhotosBase64.clear();
        if (MoodRecord.photoBase64List != null) {
            existingPhotosBase64.addAll(MoodRecord.photoBase64List);

            // Display existing photos
            for (int i = 0; i < existingPhotosBase64.size(); i++) {
                String base64 = existingPhotosBase64.get(i);
                addExistingPhotoToContainer(base64);
            }
        }

        // Update add photo button visibility
        updateAddPhotoButtonVisibility();
    }

    private void addExistingPhotoToContainer(String base64) {
        try {
            // Convert base64 to bitmap
            byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            FrameLayout frame = new FrameLayout(requireContext());
            frame.setLayoutParams(new ViewGroup.LayoutParams(800, 800));
            frame.setPadding(60, 60, 60, 60);

            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(bitmap);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ImageButton deleteBtn = new ImageButton(requireContext());
            deleteBtn.setImageResource(R.drawable.icon_delete);
            deleteBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
            deleteBtn.setBackgroundColor(Color.TRANSPARENT);

            FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(
                    130, 130, Gravity.END | Gravity.TOP
            );
            deleteBtn.setLayoutParams(deleteParams);

            deleteBtn.setOnClickListener(v -> {
                existingPhotosBase64.remove(base64);
                photoContainer.removeView(frame);
                updateAddPhotoButtonVisibility();
            });

            frame.addView(imageView);
            frame.addView(deleteBtn);
            photoContainer.addView(frame);

        } catch (Exception e) {
            Log.e("Mood3Fragment", "Error displaying existing photo", e);
        }
    }

    private void updateAddPhotoButtonVisibility() {
        int totalPhotos = selectedPhotos.size() + existingPhotosBase64.size();
        if (totalPhotos >= MAX_PHOTO_COUNT) {
            buttonAddPhoto.setVisibility(View.GONE);
        } else {
            buttonAddPhoto.setVisibility(View.VISIBLE);
        }
    }

    private void registerPhotoPickerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                int totalPhotos = selectedPhotos.size() + existingPhotosBase64.size();
                                if (totalPhotos >= MAX_PHOTO_COUNT) {
                                    Toast.makeText(getContext(), "You can only have up to 3 photos", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                Uri uri = data.getClipData().getItemAt(i).getUri();
                                if (!selectedPhotos.contains(uri)) {
                                    addPhotoToContainer(uri);
                                }
                            }
                        } else if (data.getData() != null) {
                            int totalPhotos = selectedPhotos.size() + existingPhotosBase64.size();
                            if (totalPhotos < MAX_PHOTO_COUNT) {
                                Uri uri = data.getData();
                                if (!selectedPhotos.contains(uri)) {
                                    addPhotoToContainer(uri);
                                }
                            } else {
                                Toast.makeText(getContext(), "You can only have up to 3 photos", Toast.LENGTH_LONG).show();
                            }
                        }

                        updateAddPhotoButtonVisibility();
                    }
                }
        );
    }

    private void registerSpeechInputResult() {
        speechInputLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (results != null && !results.isEmpty()) {
                            editNotes.setText(results.get(0));
                        }
                    }
                }
        );
    }

    private void addPhotoToContainer(Uri uri) {
        selectedPhotos.add(uri);

        FrameLayout frame = new FrameLayout(requireContext());
        frame.setLayoutParams(new ViewGroup.LayoutParams(800, 800));
        frame.setPadding(60, 60, 60, 60);

        ImageView imageView = new ImageView(getContext());
        imageView.setImageURI(uri);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ImageButton deleteBtn = new ImageButton(requireContext());
        deleteBtn.setImageResource(R.drawable.icon_delete);
        deleteBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        deleteBtn.setBackgroundColor(Color.TRANSPARENT);

        FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(
                130, 130, Gravity.END | Gravity.TOP
        );
        deleteBtn.setLayoutParams(deleteParams);

        deleteBtn.setOnClickListener(v -> {
            selectedPhotos.remove(uri);
            photoContainer.removeView(frame);
            updateAddPhotoButtonVisibility();
        });

        frame.addView(imageView);
        frame.addView(deleteBtn);
        photoContainer.addView(frame);
    }

    private String convertUriToBase64(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] bytes = baos.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("Mood3Fragment", "Error converting URI to Base64", e);
            return null;
        }
    }

    private void uploadMoodRecordToFirebase() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        MoodRecord.inputNote = editNotes.getText().toString().trim();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();

        String dbUrl = "https://bettrhelp-bci3283-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(dbUrl);
        DatabaseReference ref = database.getReference("moods").child(userId);

        Map<String, Object> moodRecord = new HashMap<>();
        moodRecord.put("feeling", MoodRecord.selectedFeeling);
        moodRecord.put("stressLevel", MoodRecord.stressLevel);
        moodRecord.put("energyLevel", MoodRecord.energyLevel);
        moodRecord.put("emotions", MoodRecord.selectedEmotions);
        moodRecord.put("events", MoodRecord.selectedEvents);
        moodRecord.put("people", MoodRecord.selectedPeople);
        moodRecord.put("school", MoodRecord.selectedSchool);
        moodRecord.put("health", MoodRecord.selectedHealth);
        moodRecord.put("inputNote", MoodRecord.inputNote);

        // Combine existing photos and new photos
        List<String> photoBase64List = new ArrayList<>(existingPhotosBase64);// Keep existing photos

        // Add new photos
        for (Uri uri : selectedPhotos) {
            String base64 = convertUriToBase64(uri);
            if (base64 != null) {
                photoBase64List.add(base64);
            }
        }
        moodRecord.put("photos", photoBase64List);

        String successMessage = MoodRecord.isEditMode ? "Record updated" : "Record saved";

        ref.child(today).setValue(moodRecord)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();

                    // Clear the MoodRecord data
                    MoodRecord.clearData();

                    // Navigate back to HomeFragment
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigateToHomeFragment();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save record: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Firebase", "Failed to save record", e);
                });
    }
}
