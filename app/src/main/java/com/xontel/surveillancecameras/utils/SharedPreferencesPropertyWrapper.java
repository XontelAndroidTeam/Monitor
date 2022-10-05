package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.xontel.surveillancecameras.R;

public class SharedPreferencesPropertyWrapper {
    public static final int DEFAULT_SLIDE_SHOW_INTERVAL = 5 ; //SECONDS



    @BindingAdapter("android:text")
    public static void setChoice(AutoCompleteTextView view, String newValue) {
        String currentValue = view.getText().toString();
        // Important to break potential infinite loops.
        if (currentValue != newValue) {
            view.setText(newValue ,false);
        }
    }



    @InverseBindingAdapter(attribute = "android:text", event =   "android:textAttrChanged")
    public static String getChoice(AutoCompleteTextView view) {
        return view.getText().toString();

    }



    @BindingAdapter("android:textAttrChanged")
    public static void setListeners(AutoCompleteTextView view, final InverseBindingListener attrChange) {
        // Set a listener for click, focus, touch, etc.
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                attrChange.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private static String getWithSecondsLabel(Context context, Integer intervalSeconds) {
        return intervalSeconds+  " "+ context.getString(R.string.seconds);
    }

    private static int getSecondsOnly(String value) {
        return !value.isEmpty() && value.contains(" ")? Integer.parseInt(value.split(" ")[0]): 0;
    }

}
