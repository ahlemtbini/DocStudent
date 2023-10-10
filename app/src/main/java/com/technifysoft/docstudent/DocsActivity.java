package com.technifysoft.docstudent;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technifysoft.docstudent.databinding.FragmentRowElementsBinding;

import java.util.ArrayList;

public class DocsActivity extends Fragment {

    //view binding
    private FragmentRowElementsBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //ArrayList to store category
    private ArrayList<ModelDoc> DocArrayList;

    //adapter
    private AdapterDoc adapterDoc;

    public DocsActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRowElementsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadDocs();

        //Search : on Text change
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterDoc.getFilter().filter(s);
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void loadDocs() {

        //Init arrayList
        DocArrayList = new ArrayList<>();

        //Get all categories from firebase > categories
        DatabaseReference ref = FirebaseDatabase.getInstance("https://docstudent-firebase-default-rtdb.firebaseio.com/").getReference("Docs");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Clear arrayList before adding data into it
                DocArrayList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    ModelDoc model = ds.getValue(ModelDoc.class);

                    //add to arrayList
                    DocArrayList.add(model);
                }
                //Setup Adapter
                adapterDoc = new AdapterDoc(getActivity(), DocArrayList);
                adapterDoc = new AdapterDoc(getActivity(), DocArrayList);

                //set adapter to recyclerView
                binding.docsRv.setAdapter(adapterDoc);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}