 package com.bagashaka.tambotv2;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    ImageButton btnEditProf;
    TextView txtEmail,txtName,txtPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String rtvEmail,rtvName, rtvPassword, loggedEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnEditProf = view.findViewById(R.id.editProfButton);
        txtEmail = view.findViewById(R.id.rtvEmail);
        txtName = view.findViewById(R.id.rtvName);
        txtPassword = view.findViewById(R.id.rtvPassword);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null){
            loggedEmail = mAuth.getCurrentUser().getEmail();
        }else {
           Toast.makeText(getActivity(), "Error : No user Found !", Toast.LENGTH_SHORT).show();
        }

        db.collection("users")
                .document(loggedEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()){
                        rtvEmail = documentSnapshot.getString("Username");
                        rtvPassword = documentSnapshot.getString("Password");
                        rtvName = documentSnapshot.getString("Name");

                        txtEmail.setText(rtvEmail);
                        txtPassword.setText(rtvPassword);
                        txtName.setText(rtvName);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnEditProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment editProfileFrag = new EditProfile();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, editProfileFrag).commit();
            }
        });
        return view;
    }
}