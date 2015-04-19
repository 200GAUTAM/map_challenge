package com.suresh.mapchallenge.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.suresh.mapchallenge.R;
import com.suresh.mapchallenge.api.model.Place;

/**
 * Created by suresh on 19/4/15.
 */
public class CategoryAdapter extends BaseAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Place.Category[] categories = Place.Category.values();
    private boolean[] checked;
    private OnCategoryChangedListener listener;

    public CategoryAdapter(OnCategoryChangedListener listener) {
        //Initialising the checked boolean array
        checked = new boolean[categories.length];
        for (int i = 0; i < checked.length; i++) {
            checked[i] = true;
        }

        this.listener = listener;
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Object getItem(int position) {
        return categories[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
            holder = new ViewHolder();
            view.setTag(holder);
            view.setOnClickListener(this);

            holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            holder.checkBox.setOnCheckedChangeListener(this);
            holder.categoryColorSwatch = view.findViewById(R.id.categoryColorSwatch);
            holder.tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryName);
        }

        //Updating the data for this view
        Place.Category c = categories[position];
        holder.position = position;

        //Setting the checkbox state if required
        if (holder.checkBox.isChecked() != checked[position]) {
            holder.checkBox.setChecked(checked[position]);
        }

        //Setting the swatch color based on the marker hue
        int swatchColor = Color.HSVToColor(new float[]{ c.hue, 95, 85 });
        holder.categoryColorSwatch.setBackgroundColor(swatchColor);

        holder.tvCategoryName.setText(c.displayName);

        return view;
    }

    /**
     * Triggered when the user clicks anywhere on the row
     * @param v
     */
    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();

        boolean newState = !checked[holder.position];
        holder.checkBox.setChecked(newState);
        checked[holder.position] = newState;

        if (listener != null) listener.onCategoryOptionChanged(categories[holder.position], newState);
    }

    /**
     * Triggered when the user clicks directly on the checkbox
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.v("test", "onCheckedChanged() called");
        View parent = (View) buttonView.getParent();
        ViewHolder holder = (ViewHolder) parent.getTag();

        checked[holder.position] = isChecked;
        if (listener != null) listener.onCategoryOptionChanged(categories[holder.position], isChecked);
    }

    private static class ViewHolder {
        CheckBox checkBox;
        View categoryColorSwatch;
        TextView tvCategoryName;
        int position;
    }

    public static interface OnCategoryChangedListener {
        public void onCategoryOptionChanged(Place.Category category, boolean chosen);
    }
}
