package com.example.chatho.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatho.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Contacts_Fragment extends Fragment {


    public Contacts_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_contacts, container, false);
        return v;
    }

}
