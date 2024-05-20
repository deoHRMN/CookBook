package com.example.cookbook3245.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cookbook3245.R;
import com.example.cookbook3245.databinding.FragmentCreateBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Create extends Fragment {

    FragmentCreateBinding binding;

    FirebaseAuth auth;
    Uri filepath;

    public Create() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        selectImage();
        super.onViewCreated(view, savedInstanceState);
    }

    private void selectImage() {
        binding.view2.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select your Image"), 101);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data!= null && data.getData() != null) {
            filepath = data.getData();
            binding.imageView.setVisibility(View.VISIBLE);
            binding.imageView.setImageURI(filepath);
            binding.view2.setVisibility(View.INVISIBLE);
            binding.textView2.setVisibility(View.INVISIBLE);

            uploadData(filepath);
        }

    }

    private void uploadData(Uri filepath) {
        binding.btnCreate.setOnClickListener(v -> {
            String title = binding.editRecipeName.getText().toString();
            String desc = binding.editRecipeDescription.getText().toString();
            auth = FirebaseAuth.getInstance();
            String author = Objects.requireNonNull(auth.getCurrentUser()).getEmail();

            if (TextUtils.isEmpty(title)) {
                binding.editRecipeName.setError("Recipe name cannot be empty");
                binding.editRecipeName.requestFocus();
            } else if (TextUtils.isEmpty(desc)) {
                binding.editRecipeDescription.setError("Description cannot be empty");
                binding.editRecipeDescription.requestFocus();
            } else if (filepath == null) {
                Toast.makeText(getContext(), "Please upload a picture", Toast.LENGTH_SHORT).show();
            } else {

                String date = (String) DateFormat.format("dd", new Date());
                String month = (String) DateFormat.format("MMM", new Date());
                String finalDate = date + " " + month;

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference reference = storage.getReference().child("images/" + filepath.toString() + ".jpg");
                reference.putFile(filepath).addOnSuccessListener((taskSnapshot -> {
                    reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String file_url = task.getResult().toString();

                            HashMap<String, String> map = new HashMap<>();
                            map.put("title", title);
                            map.put("desc", desc);
                            map.put("author_name", author);
                            map.put("date", finalDate);
                            map.put("img", file_url);
                            map.put("timestamp", String.valueOf(System.currentTimeMillis()));
                            map.put("like_count", "0");
                            map.put("liked", "false");

                            FirebaseFirestore.getInstance().collection("Recipes").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Recipe Uploaded", Toast.LENGTH_SHORT).show();
                                        binding.imageView.setVisibility(View.INVISIBLE);
                                        binding.view2.setVisibility(View.VISIBLE);
                                        binding.textView2.setVisibility(View.VISIBLE);
                                        binding.editRecipeName.setText("");
                                        binding.editRecipeDescription.setText("");
                                    }
                                }
                            });
                        }
                    });
                }));
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}