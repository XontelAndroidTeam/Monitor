package com.xontel.surveillancecameras.Bindings;



import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.xontel.surveillancecameras.R;

import java.util.ArrayList;


public class Binding {

    @BindingAdapter("items")
    public static void selectionFragmentAdapter(AutoCompleteTextView autoCompleteTextView, String[] items) {
//        ArrayAdapter mediaDirsDropDownAdapter = new ArrayAdapter<String>(
//                autoCompleteTextView.getContext(),
//                android.R.layout.simple_spinner_dropdown_item, items);
//        ArrayAdapter intervalsDirsDropDownAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.intervals));
//        binding.mediaFilter.setAdapter(mediaDirsDropDownAdapter);
//        binding.slideShowFilter.setAdapter(intervalsDirsDropDownAdapter);
//        SelectionFragmentAdapter adapter = (SelectionFragmentAdapter) recyclerView.getAdapter();
//        assert adapter != null;
//        if (data == null) {
//            adapter.setItemList(new ArrayList<>());
//        } else {
//            adapter.setItemList(data);}
    }
}