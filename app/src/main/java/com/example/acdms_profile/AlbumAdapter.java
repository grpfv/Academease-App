package com.example.acdms_profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AlbumAdapter extends BaseAdapter {

    private ArrayList<DataClass> dataList;
    private Context context;

    public AlbumAdapter(Context context, ArrayList<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item, viewGroup, false);
        }

        ImageView gridImage = view.findViewById(R.id.gridImage);
        TextView gridCaption = view.findViewById(R.id.gridCaption);

        DataClass item = dataList.get(i);
        gridCaption.setText(item.getCaption());

        Glide.with(context).load(item.getImageURL()).into(gridImage);

        // Set single click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullImage.class);
                intent.putExtra("imageUrl", item.getImageURL());
                context.startActivity(intent);
            }
        });

        // Set long click listener
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Show delete confirmation dialog
                showDeleteConfirmationDialog(item);
                return true;
            }
        });

        // Enable clickable property
        view.setClickable(true);

        return view;
    }

    // Method to show delete confirmation dialog
    private void showDeleteConfirmationDialog(final DataClass itemToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete this item?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform deletion action
                deleteItem(itemToDelete);
            }
        });

        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void deleteItem(DataClass itemToDelete) {
        // Delete item from Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Album");
        String documentId = itemToDelete.getId(); // Assuming getId() returns the document ID
        collectionReference.document(documentId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Deletion from Firestore successful
                        // Remove the item from the UI dataList and notify the adapter
                        dataList.remove(itemToDelete);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                        Log.e("AlbumAdapter", "Error deleting item from Firestore", e);
                    }
                });
    }
}