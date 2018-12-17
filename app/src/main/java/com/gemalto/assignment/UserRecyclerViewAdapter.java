package com.gemalto.assignment;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gemalto.gemaltoapi.data.User;

import java.util.List;

/**
 * Created by jacksondeng on 15/12/18.
 */

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {
    private List<User> users;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView gender;
        public TextView email;
        private User user;

        public ViewHolder(ConstraintLayout constraintLayout) {
            super(constraintLayout);
            username = constraintLayout.findViewById(R.id.username);
            gender = constraintLayout.findViewById(R.id.gender);
            email = constraintLayout.findViewById(R.id.email);
            constraintLayout.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(),UserDashboardActivity.class);
                intent.putExtra("user",user);
                view.getContext().startActivity(intent);
            });
        }
    }

    public UserRecyclerViewAdapter(List<User> users) {
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);

        ViewHolder vh = new ViewHolder(constraintLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.username.setText(users.get(position).getrealUsername());
        holder.gender.setText(users.get(position).getGender());
        holder.email.setText(users.get(position).getEmail());
        holder.user = users.get(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}