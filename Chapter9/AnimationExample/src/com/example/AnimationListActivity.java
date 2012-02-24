package com.example;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AnimationListActivity extends ListActivity {
    String[] examples = {"Drawable Animation", "View Animation", "Property Animation", "View Property Animation"};
    Class[] activities = {DrawableAnimationActivity.class, ViewAnimationActivity.class, PropertyAnimationActivity.class, ViewPropertyAnimationActivity.class};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, examples);
        setListAdapter(adapter);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Class activity = activities[position];
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}
