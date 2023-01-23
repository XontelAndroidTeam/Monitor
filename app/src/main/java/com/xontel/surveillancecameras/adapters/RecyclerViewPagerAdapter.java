package com.xontel.surveillancecameras.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.ItemAddCamBinding;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewPagerAdapter  extends RecyclerView.Adapter<RecyclerViewPagerAdapter.ViewHolder>{

private ArrayList<IpCam> dataList=new ArrayList<>();

  public RecyclerViewPagerAdapter(){}

static class ViewHolder extends RecyclerView.ViewHolder {
    private final ItemAddCamBinding binding;

    public ViewHolder(ItemAddCamBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void bind(IpCam item) {
        binding.setData(item);
        binding.executePendingBindings();
    }

}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @NonNull ItemAddCamBinding view = ItemAddCamBinding.inflate(LayoutInflater.from(parent.getContext()));
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.getRoot().setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IpCam item = dataList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public void setItemList(List<IpCam> data) {
        if (!data.isEmpty()) {this.dataList.clear();}
        this.dataList.addAll(data);
        notifyDataSetChanged();
    }

}
