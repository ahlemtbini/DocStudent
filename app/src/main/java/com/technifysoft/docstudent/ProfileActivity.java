package com.technifysoft.docstudent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class ProfileActivity extends Fragment{
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    private static final int REQUEST_CODE_PICK_IMAGE = 1;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        ImageView profilepic =view.findViewById(R.id.SeticonIv);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPic();
            }
        });




        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Retrieve the user data from the database
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("Email").getValue(String.class);
                    String picUrl = snapshot.child("profileImage").getValue(String.class);

                    TextView nameView = view.findViewById(R.id.SetnameEt);
                    TextView emailView = view.findViewById(R.id.SetemailEt);
                    ImageView profilImg = view.findViewById(R.id.SeticonIv);

                    if (isAdded()) {
                        if (name != null && nameView != null) {
                            nameView.setText(name);
                        }
                        if (email != null && emailView != null) {
                            emailView.setText(email);
                        }
                        if (picUrl != null && profilImg != null) {
                            Glide.with(getContext())
                                    .load(picUrl)
                                    .placeholder(R.drawable.placeholder_image)
                                    .error(R.drawable.default_image)
                                    .into(profilImg);
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });




        }

        Button updateButton = view.findViewById(R.id.updateBtn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(view);
            }
        });

        return view;
    }
    private void uploadPic() {
        // Create an intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            // Get the selected image URI
            Uri imageUri = data.getData();

            // Create a reference to the Firebase Storage service
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Create a reference to the user's profile picture in Firebase Storage
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference profilePicRef = storageRef.child("profile_pictures/" + uid);

            // Upload the image to Firebase Storage
            profilePicRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get the download URL of the uploaded image
                    profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            // Update the user's profile picture URL in Firebase Realtime Database
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                            if (getView() != null) {
                                ImageView pic = getView().findViewById(R.id.SeticonIv);
                                if (pic != null && imageUri != null) {
                                    Glide.with(getContext())
                                            .load(imageUri)
                                            .placeholder(R.drawable.placeholder_image)
                                            .error(R.drawable.default_image)
                                            .into(pic);
                                }

                            }
                            userRef.child("profileImage").setValue(downloadUri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {



                                    Toast.makeText(getContext(), "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to update profile picture URL in database", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to get download URL of uploaded image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void updateData(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        EditText newNameEditText = view.findViewById(R.id.SetnameEt);
        String newName = newNameEditText.getText().toString();
        EditText newEmailEditText = view.findViewById(R.id.SetemailEt);
        String newEmail = newEmailEditText.getText().toString();
        HashMap<String, Object> updateData = new HashMap<>();
        if(newName!=null) {
            updateData.put("name", newName);
        }
        if(newEmail!=null) {
            updateData.put("Email", newEmail);
        }
        DatabaseReference  ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(view.getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new HomeActivity()).commit();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
