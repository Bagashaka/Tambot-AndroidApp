package com.bagashaka.tambotv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends Fragment {
    private EditText editTextEmail;
    private EditText editTextName;
    private EditText editTextPassword;
    private ImageButton buttonSave;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String loggedEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = view.findViewById(R.id.editEmail);
        editTextName = view.findViewById(R.id.editName);
        editTextPassword = view.findViewById(R.id.editPassword);
        buttonSave = view.findViewById(R.id.btnSave);

        if (mAuth.getCurrentUser() != null) {
            loggedEmail = mAuth.getCurrentUser().getEmail();
        } else {
            Toast.makeText(getActivity(), "Error: No user found!", Toast.LENGTH_SHORT).show();
        }

        // Ambil data saat ini dari Firebase Firestore
        DocumentReference docRef = db.collection("users").document(loggedEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("Username");
                        String password = documentSnapshot.getString("Password");
                        String name = documentSnapshot.getString("Name");

                        editTextEmail.setText(email);
                        editTextPassword.setText(password);
                        editTextName.setText(name);
                    }
                }
            }
        });

        // Tombol Simpan
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String name = editTextName.getText().toString().trim();

                // Simpan perubahan ke dalam objek Map
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("Username", email);
                updatedData.put("Password", password);
                updatedData.put("Name", name);

                // Update data di Firebase Firestore
                db.collection("users").document(loggedEmail)
                        .update(updatedData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                                    Fragment ProfileFrag = new ProfileFragment();
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.fragment_container, ProfileFrag).commit();
                                } else {
                                    Toast.makeText(getActivity(), "Error updating data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    Fragment ProfileFrag = new ProfileFragment();
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.fragment_container, ProfileFrag).commit();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error updating data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Fragment ProfileFrag = new ProfileFragment();
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_container, ProfileFrag).commit();
                            }
                        });
            }
        });

        return view;
    }
}
