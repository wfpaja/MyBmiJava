package com.example.mybmi;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybmi.roomdata.BmiData;

import java.text.DecimalFormat;
import java.util.List;

public class MemberBmiAdapter extends RecyclerView.Adapter<MemberBmiAdapter.ViewHolder> {
    private List<BmiData> data;
    private BmiViewModel viewModel;
    private Context context;

    public MemberBmiAdapter(BmiViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public MemberBmiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bmi_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberBmiAdapter.ViewHolder holder, int position) {
        BmiData itemData = data.get(position);
        holder.tvName.setText(itemData.getName());
        holder.tvHeight.setText(changeToString(itemData.getHeight()));
        holder.tvWeight.setText(changeToString(itemData.getWeight()));
        holder.tvBmi.setText(changeToString(itemData.getBmi()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSelectBackground(holder.itemView, viewModel.processingSelect(itemData.getId()));
            }
        });
        changeSelectBackground(holder.itemView, viewModel.checkSelected(itemData.getId()));
    }

    private String changeToString(double value) {
        String result;
        DecimalFormat df = new DecimalFormat("0.##");
        result = df.format(value);
        return result;
    }

    private void changeSelectBackground(View view, boolean selected) {
        if (selected) {
            view.setBackground(AppCompatResources.getDrawable(context, R.drawable.tv_border_selected));
        }
        else {
            view.setBackground(AppCompatResources.getDrawable(context, R.drawable.tv_border));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<BmiData> dataList) {
        data = dataList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvHeight, tvWeight, tvBmi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.inner_tv_name);
            tvHeight = itemView.findViewById(R.id.inner_tv_height);
            tvWeight = itemView.findViewById(R.id.inner_tv_weight);
            tvBmi = itemView.findViewById(R.id.inner_tv_bmi);
        }
    }
}
