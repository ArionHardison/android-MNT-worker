package com.dietmanager.chef.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dietmanager.chef.R;
import com.dietmanager.chef.model.orderrequest.OrderingredientItem;

import java.util.List;

public class IngredientsInvoiceAdapter extends RecyclerView.Adapter<IngredientsInvoiceAdapter.MyViewHolder> {

    private Context context;
    private List<OrderingredientItem> list;

    public IngredientsInvoiceAdapter(List<OrderingredientItem> list, Context con) {
        this.list = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredients_invoice, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        OrderingredientItem item = list.get(position);
        holder.ingredientName.setText(item.getFoodingredient().getIngredient().getName());
        if(item.getFoodingredient().getIngredient().getUnitType()!=null)
            holder.ingredient_quantity.setText(item.getFoodingredient().getQuantity()+" "+item.getFoodingredient().getIngredient().getUnitType().getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setList(List<OrderingredientItem> list) {
        this.list = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ingredientName,ingredient_quantity;

        public MyViewHolder(View view) {
            super(view);
            ingredientName = view.findViewById(R.id.ingredient_name);
            ingredient_quantity = view.findViewById(R.id.ingredient_quantity);
        }
    }
}
