package com.example.cookbook3245.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.cookbook3245.Adapter;
import com.example.cookbook3245.Model;
import com.example.cookbook3245.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Home extends Fragment {

    FragmentHomeBinding binding;
    ArrayList<Model> list;
    Adapter adapter;
    Model model;

    public Home() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupRv();
        setupSearchView();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void filter(String newText) {
        ArrayList<Model> filtered_list = new ArrayList<>();
        for(Model item:list) {
            if(item.getTitle().toString().toLowerCase().contains(newText)) {
                filtered_list.add(item);
            }
        }

        if (filtered_list.isEmpty()) {
            //
        } else {
            adapter.filter_list(filtered_list);
        }
    }

    private void setupRv() {
        list = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Recipes").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                list.clear();
                assert value != null;
                for (DocumentSnapshot snapshot:value.getDocuments()) {
                    model = snapshot.toObject(Model.class);
                    assert model != null;
                    model.setId(snapshot.getId());
                    list.add(model);
                }
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new Adapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        binding.rvRecipes.setLayoutManager(linearLayoutManager);
        binding.rvRecipes.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}