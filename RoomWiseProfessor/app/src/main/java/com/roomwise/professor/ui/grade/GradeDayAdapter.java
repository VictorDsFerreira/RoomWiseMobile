package com.roomwise.professor.ui.grade;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roomwise.professor.databinding.ItemGradeDayBinding;

import java.util.ArrayList;
import java.util.List;

public class GradeDayAdapter extends RecyclerView.Adapter<GradeDayAdapter.ViewHolder> {
    private final List<GradeDayUi> items = new ArrayList<>();

    public void submitList(List<GradeDayUi> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemGradeDayBinding binding = ItemGradeDayBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemGradeDayBinding binding;

        ViewHolder(ItemGradeDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(GradeDayUi dayUi) {
            binding.tvDayTitle.setText(dayUi.getTitulo());
            binding.tvDayContent.setText(dayUi.getConteudo());
        }
    }
}
