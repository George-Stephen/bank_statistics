package com.iconic.bank_statistics.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iconic.bank_statistics.R;
import com.iconic.services.StatsClient;
import com.iconic.services.StatsInterface;
import com.iconic.services.models.Order;
import com.iconic.services.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

    private List<Order> orders;
    private Context context;

    public ViewAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_view,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind_order(order);

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.order_product) TextView mOrderProduct;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.order_date) TextView mOrderDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.order_status) TextView mOrderStatus;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.order_quantity) TextView mOrderQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        private void bind_order(final Order order){
            StatsInterface client = StatsClient.getClient();
            Call<List<Product>> call = client.get_product(order.getProductCode());
            call.enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                    List<Product> products = response.body();
                    assert products != null;
                    String product_name = products.get(0).getProductName();
                    mOrderProduct.setText(product_name);
                    mOrderDate.setText(order.getDateIssued());
                    mOrderQuantity.setText(String.valueOf(order.getOrderQty()));
                    mOrderStatus.setText(order.getStatus());
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {

                }
            });

        }

    }
}
