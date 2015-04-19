package com.suresh.mapchallenge.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.suresh.mapchallenge.R;
import com.suresh.mapchallenge.api.model.Place;

/**
 * Created by suresh on 19/4/15.
 */
public class CategoryAdapter extends BaseAdapter {

    private Place.Category[] categories = Place.Category.values();
    private boolean[] checked;

    public CategoryAdapter() {
        checked = new boolean[categories.length];

        for (int i = 0; i < checked.length; i++) {
            checked[i] = true;
        }
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

            holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            holder.categoryColorSwatch = view.findViewById(R.id.categoryColorSwatch);
            holder.tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryName);
        }

        //Setting the checkbox state
        holder.checkBox.setChecked(checked[position]);

        //Setting the swatch color based on the marker hue
        int swatchColor = Color.HSVToColor(new float[]{ categories[position].hue, 95, 90 });
        holder.categoryColorSwatch.setBackgroundColor(swatchColor);

        holder.tvCategoryName.setText(categories[position].displayName);

        return view;
    }

    private static class ViewHolder {
        CheckBox checkBox;
        View categoryColorSwatch;
        TextView tvCategoryName;
    }
}
