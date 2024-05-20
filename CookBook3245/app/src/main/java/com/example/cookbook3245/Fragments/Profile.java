package com.example.cookbook3245.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.cookbook3245.MainActivity;
import com.example.cookbook3245.R;
import com.example.cookbook3245.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Profile extends Fragment {

    FragmentProfileBinding binding;
    GoogleSignInAccount account;
    GoogleSignInOptions signInOptions;
    GoogleSignInClient signInClient;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initvar();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initvar() {
        account = GoogleSignIn.getLastSignedInAccount(getContext());
        binding.username.setText(account.getDisplayName());
        binding.userEmail.setText((account.getEmail()));
        Glide.with(getContext()).load(account.getPhotoUrl()).into(binding.profileImage);

        logoutuser();
    }

    private void logoutuser() {
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("206523423940-bh5co5rn36cc5g8d9s14fk4rs5dnoika.apps.googleusercontent.com").requestEmail().build();
                signInClient = GoogleSignIn.getClient(getContext(), signInOptions);

                new AlertDialog.Builder(getActivity()).setTitle("Log Out?")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut(); // logout from firebase

                                signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() { //logout from google
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                                        getActivity().finish();
                                    }
                                });

                            }
                        }).show();

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}