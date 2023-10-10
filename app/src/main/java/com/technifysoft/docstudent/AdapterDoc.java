package com.technifysoft.docstudent;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.technifysoft.docstudent.databinding.ElementBinding;
import java.util.ArrayList;
import java.util.List;

public class AdapterDoc extends RecyclerView.Adapter<AdapterDoc.HolderDoc> implements Filterable {

    private android.content.Context  context;
    public ArrayList<ModelDoc> DocArrayList,filterList;



    //view binding
    private ElementBinding  binding;

    //Instance of our filter class
    private FilterDoc filter;


    //Parameterized Constructor

    public AdapterDoc(Context context, ArrayList<ModelDoc> docArrayList) {
        this.context = context;
        this.DocArrayList = docArrayList;
        this.filterList = docArrayList;
    }


    //Methods

    @NonNull
    @Override
    public HolderDoc onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Bind element.xml
        binding = ElementBinding.inflate(LayoutInflater.from(context),parent,false);


        return new HolderDoc(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDoc.HolderDoc holder, int position) {
        // get data
        ModelDoc model = DocArrayList.get(position);
        String id = model.getId();
        String doc = model.gettitle();
        String pdf = model.getPdf();

        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        //Set data
        holder.DocTv.setText(doc);


        //Handle click, delete category
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Confirm delete dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this category ?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "Deleting ..", Toast.LENGTH_SHORT).show();
                                deleteDoc(model,holder);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        //Handle click, pdf
        holder.pdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF(model,pdf);
            }
        });





    }

    private void deleteDoc(ModelDoc model, HolderDoc holder) {
        //get ID of category to delete
        String id = model.getId();

        //Firebase DB > docss > docId
        DatabaseReference ref = FirebaseDatabase.getInstance("https://docstudent-firebase-default-rtdb.firebaseio.com/").getReference("Docs");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //success
                        Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void openPDF(ModelDoc model, String pdf) {
        //String url = pdf;
        String url = model.getPdf();

        if (url == null || url.isEmpty()) {
            Toast.makeText(context, "No URL found for PDF.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "application/pdf");

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (apps.isEmpty()) {
            Toast.makeText(context, "No app found to open PDF.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Unable to open PDF.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public int getItemCount() {
        return DocArrayList.size();
    }



    @Override
    public Filter getFilter() {

        if(filter == null){
            filter = new FilterDoc(filterList,this);
        }


        return filter;
    }


    //view holder class to hold UI for row_category.xml
    class HolderDoc extends RecyclerView.ViewHolder{

        //UI views of row_category.xml
        TextView DocTv;
        ImageButton deleteBtn;
        ImageButton pdfBtn;



        public HolderDoc(@NonNull View itemView) {
            super(itemView);

            //init UI views
            DocTv = binding.docTv;
            deleteBtn = binding.deleteBtn;
            pdfBtn = binding.pdfBtn;




        }
    }


}
