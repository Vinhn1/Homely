package com.example.homely.ui.post;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.homely.R;

public class PostFragment extends Fragment {

    public PostFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_postFragment_to_addRoomFragment);
    }
}