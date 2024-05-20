package com.example.cookbook3245;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    ArrayList<Model> list;

    @SuppressLint("NotifyDataSetChanged")
    public Adapter(ArrayList<Model> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public void filter_list(ArrayList<Model> filter_list) {
        list = filter_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        Model model = list.get(position);
        holder.title.setText(model.getTitle());
        holder.author_name.setText(model.getAuthor_name());
        holder.desc.setText(model.getDesc());
        holder.date.setText(model.getDate());
        holder.like_count.setText("Likes: " + model.getLike_count());

        Glide.with(holder.author_name.getContext()).load(model.getImg()).into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.author_name.getContext(), RecipeDetail.class);
                intent.putExtra("id", model.getId());
                holder.author_name.getContext().startActivity(intent);
            }
        });

        if (Objects.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(), model.getAuthor_name())) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.author_name.getContext());
                    builder.setTitle("Choose an action");
                    builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Dialog update_dialog = new Dialog(holder.author_name.getContext());
                            update_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            update_dialog.setCancelable(false);
                            Objects.requireNonNull(update_dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                            update_dialog.setContentView(R.layout.update_dialog);
                            update_dialog.show();

                            EditText title = update_dialog.findViewById(R.id.editRecipeName);
                            EditText desc = update_dialog.findViewById(R.id.editRecipeDescription);

                            title.setText(model.getTitle());
                            desc.setText(model.getDesc());

                            TextView dialogbutton = update_dialog.findViewById(R.id.btn_update);
                            dialogbutton.setOnClickListener(v1 -> {
                                if (title.getText().toString().isEmpty()){
                                    title.setError("Recipe name cannot be empty");
                                } else if (desc.getText().toString().isEmpty()) {
                                    desc.setError("Description cannot be empty");
                                } else {

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("title", title.getText().toString());
                                    map.put("desc", desc.getText().toString());

                                    FirebaseFirestore.getInstance().collection("Recipes").document(model.getId()).update(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        update_dialog.dismiss();
                                                    }
                                                }
                                            });


                                }
                            });

                        }
                    });
                    builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            AlertDialog.Builder builders = new AlertDialog.Builder(holder.author_name.
                                    getContext());
                            builders.setTitle("Are you sure to Delete it??");
                            builders.setPositiveButton("Yes! I am Sure", new
                                    DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseFirestore.getInstance().collection("Recipes").
                                                    document(model.getId()).delete();
                                            dialog.dismiss();
                                        }
                                    });
                            builders.setNegativeButton("Cancel", new
                                    DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialogs = builders.create();
                            dialogs.show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView date, title, desc, like_count, author_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView4);
            date = itemView.findViewById(R.id.textView);
            title = itemView.findViewById(R.id.textView8);
            author_name = itemView.findViewById(R.id.textView9);
            desc = itemView.findViewById(R.id.textView10);
            like_count = itemView.findViewById(R.id.textView11);

        }
    }

}
