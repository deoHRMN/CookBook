package com.example.cookbook3245;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookbook3245.databinding.ActivityRecipeDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Objects;

public class RecipeDetail extends AppCompatActivity {

    ActivityRecipeDetailBinding binding;
    String id, title, desc, author_name, like_count, liked, message;
    int new_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        showdata();
    }

    private void showdata() {
        id = getIntent().getStringExtra("id");
        FirebaseFirestore.getInstance().collection("Recipes").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                Glide.with(getApplicationContext()).load(value.getString("img")).into(binding.imageView4);
                binding.recipeTitle.setText(value.getString("title"));
                binding.recipeDescription.setText(value.getString("desc"));
                binding.likeCount.setText("Likes: " + value.getString("like_count"));
                binding.authorName.setText(value.getString("author_name"));

                title = value.getString("title");
                desc = value.getString("desc");
                like_count = value.getString("like_count");
                author_name = value.getString("author_name");
                liked = value.getString("liked");




            }
        });
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int old_count = Integer.parseInt(like_count);
                if (Objects.equals(liked, "false")) {
                    new_count = old_count + 1;
                    liked = "true";
                    message = "Recipe liked";

                } else {
                    new_count = old_count - 1;
                    liked = "false";
                    message = "Like removed";

                }

                HashMap<String, Object> map = new HashMap<>();
                String count = String.valueOf(new_count);
                map.put("like_count", count);
                map.put("liked", liked);

                FirebaseFirestore.getInstance().collection("Recipes").document(id).update(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }


        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecipeDetail.this, DrawerActivity.class));
            }
        });




    }


}