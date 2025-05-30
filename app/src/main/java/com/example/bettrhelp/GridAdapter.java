package com.example.bettrhelp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends BaseAdapter {
    private final Context context;
    private final int[] images;
    private final String[] labels;
    private final int[] selectedImages;
    private final boolean[] isSelected;

    public GridAdapter(Context context, int[] images, String[] labels, int[] selectedImages) {
        this.context = context;
        this.images = images;
        this.labels = labels;
        this.selectedImages = selectedImages;
        this.isSelected = new boolean[labels.length];
    }

    @Override
    public int getCount() {
        return labels.length;
    }

    @Override
    public Object getItem(int i) {
        return labels[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_grid, parent, false);
        }

        ImageButton imageButton = view.findViewById(R.id.itemImage);
        TextView label = view.findViewById(R.id.itemLabel);

        // Set icon based on selection state
        imageButton.setImageResource(isSelected[i] ? selectedImages[i] : images[i]);
        label.setText(labels[i]);

        imageButton.setOnClickListener(v -> {
            isSelected[i] = !isSelected[i]; // Toggle selection state
            notifyDataSetChanged();         // Refresh UI
        });

        return view;
    }

    public List<String> getSelectedLabels() {
        List<String> selectedLabels = new ArrayList<>();
        for (int i = 0; i < isSelected.length; i++) {
            if (isSelected[i]) {
                selectedLabels.add(labels[i]);
            }
        }
        return selectedLabels;
    }

    // Add this missing method for edit mode
    public void setItemSelected(int position, boolean selected) {
        if (position >= 0 && position < isSelected.length) {
            isSelected[position] = selected;
        }
    }
}
