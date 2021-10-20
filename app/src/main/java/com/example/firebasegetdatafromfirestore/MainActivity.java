package com.example.firebasegetdatafromfirestore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<UserModel> userList;
    UserAdapter userAdapter;
    FirebaseFirestore db;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Fetching data from firebase...");
        dialog.show();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<UserModel>();
        userAdapter = new UserAdapter(userList,MainActivity.this);

        recyclerView.setAdapter(userAdapter);
        // get data from the firestore using the method below
        EventChangeListener();


    }

    private void EventChangeListener() {
        db.collection("users").orderBy("firstname", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            if(dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Toast.makeText(MainActivity.this,"An error has occurred"+ error.getMessage(),Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if(dc.getType() == DocumentChange.Type.ADDED) {
                                userList.add(dc.getDocument().toObject(UserModel.class));
                            }
                            userAdapter.notifyDataSetChanged();
                            if(dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }
                });
    }
}