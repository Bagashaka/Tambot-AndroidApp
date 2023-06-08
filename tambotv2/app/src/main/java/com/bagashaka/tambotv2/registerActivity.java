 package com.bagashaka.tambotv2;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;

 import android.content.Intent;
 import android.os.Bundle;
 import android.text.TextUtils;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ImageButton;
 import android.widget.Toast;

 import com.google.android.gms.tasks.OnFailureListener;
 import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.firebase.auth.AuthResult;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.firestore.DocumentReference;
 import com.google.firebase.firestore.FirebaseFirestore;

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Map;

 public class registerActivity extends AppCompatActivity {
     ArrayList<userTambot> Arrusr=new ArrayList<>();
      EditText Username2;
      EditText Password2;
      EditText Nama;

      String strUsername,strPassword, strNama;

      String userPattern = "[a-zA0-9._-]+@[a-z]+\\.+[a-z]+";

      FirebaseAuth mAuth;
      FirebaseFirestore db;

     private Button btnRegister;
     private ImageButton btnLogIn;


     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_register);
         Username2 = (EditText) findViewById(R.id.idUsername);
         Password2 = (EditText) findViewById(R.id.idPassword);
         Nama = (EditText) findViewById(R.id.idNama);
         btnRegister = (Button) findViewById(R.id.btnRegister);
         btnLogIn = (ImageButton) findViewById(R.id.idLogIn);

         mAuth = FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();

         btnLogIn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(registerActivity.this, loginActivity.class);
                 startActivity(intent);
             }
         });

         btnRegister.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 strUsername = Username2.getText().toString().trim();
                 strPassword = Password2.getText().toString().trim();
                 strNama = Nama.getText().toString().trim();

                 if(isValidate()){
                     SingUp();
                 }
             }
         });
     }

     private void SingUp(){
        btnRegister.setVisibility(View.INVISIBLE);
        mAuth.createUserWithEmailAndPassword(strUsername,strPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Map<String, Object> user = new HashMap<>();
                user.put("Username", strUsername);
                user.put("Password", strPassword);
                user.put("Name", strNama);

                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(registerActivity.this, "Data Stored Successfully !", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(registerActivity.this, loginActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(registerActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                btnRegister.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(registerActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                btnRegister.setVisibility(View.VISIBLE);
            }
        });
     }

     private boolean isValidate(){
         if(TextUtils.isEmpty(strUsername)){
             Username2.setError("Username can't be empty !");
             return false;
         }
         if(!strUsername.matches(userPattern)){
             Username2.setError("Enter a valid Email ID !");
             return false;
         }
         if(TextUtils.isEmpty(strPassword)){
             Password2.setError("Password can't be empty !");
             return false;
         }
         if(TextUtils.isEmpty(strNama)){
             Nama.setError("Name can't be empty !");
             return false;
         }
         return true;
     }

//     private void Save(String Username, String Password, String Nama){
//         Arrusr.add(new userTambot(Username, Password, Nama));
//     }
//
//     public void GoToRegister(View view) {
//         String inputUsername = Username2.getText().toString();
//         String inputPassword = Password2.getText().toString();
//         String inputNama = Nama.getText().toString();
//
//         Save(inputUsername,inputPassword, inputNama);
//         Intent intent = new Intent(registerActivity.this,loginActivity.class);
//         intent.putParcelableArrayListExtra("myData",Arrusr);
//         startActivity(intent);
//     }
//
//     @Override
//     public void onClick(View v) {
//         if (v.getId() == R.id.idLogIn){
//             Intent intent = new Intent(this,loginActivity.class);
//             startActivity(intent);
//         }
//     }
 }