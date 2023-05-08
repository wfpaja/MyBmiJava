package com.example.mybmi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybmi.roomdata.BmiData;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private BmiViewModel bmiViewModel;
    private List<CategoryData> data;


    public CategoryAdapter(BmiViewModel viewModel) {
        bmiViewModel = viewModel;
        data = bmiViewModel.getCategoryDataList();
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item_view, parent, false);

        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoryData itemData = data.get(position);
        MemberBmiAdapter adapter = new MemberBmiAdapter(bmiViewModel);
        holder.rvMember.setAdapter(adapter);
        bmiViewModel.getDataListByCategory(itemData.getCategory(), new BmiViewModel.GetDataCallback() {
            @Override
            public void result(List<BmiData> dataList) {
                String text = String.format(itemData.getTitleText(), dataList.size());
                holder.tvCategory.setText(text);
                adapter.setData(dataList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void updateData(Category category) {
        for (int i = 0; i< data.size(); i++ ) {
            CategoryData item = data.get(i);
            if (category == item.getCategory()) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategory;
        private final RecyclerView rvMember;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            rvMember = itemView.findViewById(R.id.rv_member_list);
        }
    }
}