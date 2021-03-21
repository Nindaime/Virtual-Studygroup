package com.peeplotech.studygroup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.peeplotech.studygroup.ModuleDetail;
import com.peeplotech.studygroup.R;
import com.peeplotech.studygroup.interfaces.ItemClickListener;
import com.peeplotech.studygroup.models.Module;
import com.peeplotech.studygroup.util.Common;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>{

    private Activity activity;
    private Context ctx;
    private List<Module> moduleList;

    public ModuleAdapter(Activity activity, Context context, List<Module> moduleList) {
        this.activity = activity;
        this.ctx = context;
        this.moduleList = moduleList;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.module_item, parent, false);

        return new ModuleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {

        //get current module
        Module currentModule = moduleList.get(position);


        holder.moduleTitle.setText(currentModule.getModule_title());
        holder.moduleBrief.setText(currentModule.getModule_desc());

        //click
        holder.setItemClickListener((view, position1, isLongClick) -> {

            Intent courseIntent = new Intent(ctx, ModuleDetail.class);
            courseIntent.putExtra(Common.INTENT_MODULE, currentModule.getModule_id());
            ctx.startActivity(courseIntent);
            activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_out_left);

        });

    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    public class ModuleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //widgets
        private ItemClickListener itemClickListener;
        public TextView moduleTitle, moduleBrief;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            moduleTitle = itemView.findViewById(R.id.moduleTitle);
            moduleBrief = itemView.findViewById(R.id.moduleBrief);

            //click
            itemView.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
