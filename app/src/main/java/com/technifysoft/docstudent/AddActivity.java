package com.technifysoft.docstudent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class AddActivity extends Fragment {

    //view binding
    private DashboardActivity binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;
    private static final int PICK_PDF_REQUEST = 1;

    private Button uploadBtn, selectFileBtn;
    private Uri filePath;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //configure progress dialog
        progressDialog = new ProgressDialog(container.getContext());
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        Button uploadbtn = view.findViewById(R.id.upload_button);
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile(getView());
            }
        });
        //handle click , begin upload docs
        Button btn = view.findViewById(R.id.submitBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData(getView());
            }
        });


        return view;
    }

    private String title = "";

    private void validateData(View view) {
            addFirebase(getView());

    }

    private void addFirebase(View view) {

        String title = ((TextView) getView().findViewById(R.id.titleEt)).getText().toString().trim();
        String pdf = ((TextView) getView().findViewById(R.id.file_name_textview)).getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(view.getContext(), "Please Enter title add", Toast.LENGTH_SHORT).show();
        }else if(pdf.equals("No file selected")) {
            Toast.makeText(view.getContext(), "Please Enter file", Toast.LENGTH_SHORT).show();
        }
        else{
            //show progress bar
            progressDialog.setMessage("Adding doc .. ");
            progressDialog.show();

            if (filePath != null) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference pdfRef = storageRef.child("uploads/" + UUID.randomUUID().toString() + ".pdf");

                pdfRef.putFile(filePath)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return pdfRef.getDownloadUrl();
                        })
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String pdfUrl = task.getResult().toString();
                                //get timestamp
                                long timestamp = System.currentTimeMillis();
                                //Setup info to add in FireBase db
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("id", "" + timestamp); // cuz time in millis is unique
                                hashMap.put("title", "" + title);
                                hashMap.put("pdf", pdfUrl);
                                hashMap.put("timestamp", timestamp);
                                hashMap.put("uid", "" + firebaseAuth.getUid());

                                //add to firebase db ... database root > Categories > categoryId > category info
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Docs");
                                ref.child("" + timestamp)
                                        .setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                //success
                                                progressDialog.dismiss();
                                                Toast.makeText(view.getContext(), "Doc Added Successfully", Toast.LENGTH_SHORT).show();
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                        new DocsActivity()).commit();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                //fail
                                                progressDialog.dismiss();
                                                Toast.makeText(view.getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();


                                            }
                                        });
                            } else {
                                // Handle failures
                                Toast.makeText(getContext(), "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
            }
        }
    }

    public void selectFile (View view){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }
    @Override
    public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == -1 && data != null && data.getData() != null) {
            TextView select= getView().findViewById(R.id.file_name_textview);
            select.setText("PDF selected");
            filePath = data.getData();
            Toast.makeText(getContext(), "File selected", Toast.LENGTH_LONG).show();

        }
    }
}

