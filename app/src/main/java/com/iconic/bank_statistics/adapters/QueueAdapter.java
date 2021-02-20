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
import com.iconic.services.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueViewHolder> {
    List<Issue> issues;
    Context context;

    public QueueAdapter(List<Issue> issues, Context context) {
        this.issues = issues;
        this.context = context;
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item,parent,false);
        QueueViewHolder viewHolder = new QueueViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder holder, int position) {
        Issue issue = issues.get(position);
        holder.bind_issue(issue);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    static class QueueViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.queue_product) TextView mProductName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.queue_number) TextView mAccountNumber;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.queue_status) TextView mQueueStatus;

        public QueueViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        private void bind_issue(final Issue issue){
            StatsInterface client = StatsClient.getClient();
            Call<List<Product>> call =  client.get_product(issue.getProductCode());
            call.enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                    List<Product> products = response.body();
                    Product product = products.get(0);
                    mProductName.setText(product.getProductName());
                    mAccountNumber.setText(issue.getAccountName());
                    mQueueStatus.setText(issue.getStatus());
                }

                @Override
                public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {

                }
            });
        }
    }
}
