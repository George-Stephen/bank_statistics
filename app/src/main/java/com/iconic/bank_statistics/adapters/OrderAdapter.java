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
import com.iconic.services.models.Issue;
import com.iconic.services.models.Order;
import com.iconic.services.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
    Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);
        OrderHolder holder = new OrderHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind_order(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.display_name) TextView mDisplayName;
        @BindView(R.id.display_quantity) TextView mDisplayQuantity;
        @BindView(R.id.display_rem) TextView mDisplayRemaining;
        @BindView(R.id.display_iss) TextView mDisplayIssued;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        @SuppressLint("SetTextI18n")
        private void bind_order(final Order order){
            StatsInterface client = StatsClient.getClient();
            Call<List<Product>> call = client.get_product(order.getProductCode());
            call.enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                    List<Product> products = response.body();
                    assert products != null;
                    mDisplayName.setText(products.get(0).getProductName());
                    mDisplayQuantity.setText(Integer.toString(order.getOrderQty()));
                }

                @Override
                public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {

                }
            });
            StatsInterface client_1 = StatsClient.getClient();
            Call<List<Issue>> call_1 = client_1.get_issue(order.getOrderRef());
            call_1.enqueue(new Callback<List<Issue>>() {
                @Override
                public void onResponse(@NotNull Call<List<Issue>> call, @NotNull Response<List<Issue>> response) {
                    List<Issue> issues = response.body();
                    assert issues != null;
                    int issued = issues.size();
                    int remaining = order.getOrderQty() - issued;
                    mDisplayIssued.setText(Integer.toString(issued));
                    mDisplayRemaining.setText(Integer.toString(remaining));
                }

                @Override
                public void onFailure(@NotNull Call<List<Issue>> call, @NotNull Throwable t) {

                }
            });
        }
    }
}
