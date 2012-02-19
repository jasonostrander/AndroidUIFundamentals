package com.example;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class ToggleText extends LinearLayout implements OnCheckedChangeListener {
    EditText mTextView;

    public ToggleText(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.toggle_text, this);

        mTextView = (EditText) view.findViewById(R.id.edit_text);

        ToggleButton toggle = (ToggleButton) view.findViewById(R.id.toggle_button);
        toggle.setChecked(true);
        toggle.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mTextView.setEnabled(isChecked);
    }
}
