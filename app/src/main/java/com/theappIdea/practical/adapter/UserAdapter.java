package com.theappIdea.practical.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.theappIdea.practical.R;
import com.theappIdea.practical.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = UserAdapter.class.getSimpleName();
    private Context context;
    private List<User> userList = new ArrayList<>();
    private OnUserItemClick onUserItemClick;

    public UserAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.txtName.setText("" + user.getFirst_name() + " " + user.getLast_name());
        holder.txtEmail.setText("" + user.getEmail());
        holder.txtMobileNo.setText("" + user.getMobile_number());

        if (user.getImage_path() != null && !user.getImage_path().isEmpty()) {
            Uri photoUri = Uri.fromFile(new File(user.getImage_path()));
            Glide.with(context).load(photoUri).into(holder.imgUser);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser;
        TextView txtName;
        TextView txtEmail;
        TextView txtMobileNo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtMobileNo = itemView.findViewById(R.id.txtMobileNo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onUserItemClick != null) {
                        onUserItemClick.onUserItemClick(getAdapterPosition());
                    }
                }
            });

        }
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public interface OnUserItemClick {
        void onUserItemClick(int position);
    }

    public void setOnUserItemClick(OnUserItemClick onUserItemClick) {
        this.onUserItemClick = onUserItemClick;
    }
}
