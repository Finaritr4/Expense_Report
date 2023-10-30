package com.example.myapplication.tools;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailVH> {

    List<Detail> itemsDetail;

    public OnItemDeletedListener getItemListener() {
        return itemListener;
    }

    private OnItemDeletedListener itemListener;

    public DetailAdapter(List<Detail> itemsDetail) {
        this.itemsDetail = itemsDetail;
    }

    @NonNull
    @Override
    public DetailVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detail_item,viewGroup, false);
        return new DetailVH(view).linkAdapter(this);
    }

    public void setOnItemDeletedListener(OnItemDeletedListener itemListener){
        this.itemListener=itemListener;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailVH detailVH, int i) {
            detailVH.detail.setText(itemsDetail.get(i).getDetail()+"");
            detailVH.price.setText(itemsDetail.get(i).getPrice()+" Ar");
    }

    @Override
    public int getItemCount() {
        return itemsDetail.size();
    }

    public interface OnItemDeletedListener{
        void onItemDeleted(int position);
    }
}

class DetailVH extends RecyclerView.ViewHolder{

    TextView detail,price;
    ImageButton edit;

    DetailAdapter adapter;
    public DetailVH(@NonNull View itemView) {
        super(itemView);
        detail=itemView.findViewById(R.id.textDetail);
        price=itemView.findViewById(R.id.textPrice);
        edit=itemView.findViewById(R.id.edit_DetailReport);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.itemsDetail.remove(getAdapterPosition());
                if(adapter.getItemListener()!= null){
                    adapter.getItemListener().onItemDeleted(getAdapterPosition());
                }
                adapter.notifyItemRemoved(getAdapterPosition());
                Log.i("DEBUG","Size: "+adapter.itemsDetail.size());
            }
        });

    }

    public void setDetail(TextView detail) {
        this.detail = detail;
    }

    public void setPrice(TextView price) {
        this.price = price;
    }

    public DetailVH linkAdapter(DetailAdapter adapter){
        this.adapter= adapter;
        return this;
    }
}
