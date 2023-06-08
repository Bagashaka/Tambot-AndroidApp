package com.bagashaka.tambotv2;

import static androidx.constraintlayout.motion.widget.TransitionBuilder.validate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

//rules_version = '2';
//
//        service cloud.firestore {
//        match /databases/{database}/documents {
//        match /{document=**} {
//        allow read, write: if false;
//        }
//        }
//        }

public class loginActivity extends AppCompatActivity{

    ArrayList<userTambot> Arrusr;
    Button btnLogin;
    ImageButton btnRegis;
    TextView Info;
    EditText Username;
    EditText Password;

    String strUsername, strPassword;
    FirebaseAuth mAuth;
    String userPattern = "[a-zA0-9._-]+@[a-z]+\\.+[a-z]+";
    private int counter = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = (Button) findViewById(R.id.idLogin);
        btnRegis = (ImageButton) findViewById(R.id.idRegister);
        Username = (EditText) findViewById(R.id.IdUsername);
        Password = (EditText) findViewById(R.id.IdPasswd);
        Info = (TextView) findViewById(R.id.IdInfo);

        mAuth = FirebaseAuth.getInstance();

//        Intent intent =getIntent();
//        Arrusr= intent.getParcelableArrayListExtra("myData");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               strUsername = Username.getText().toString().trim();
               strPassword = Password.getText().toString();

               if(isValidate()){
                   SingIn();
               }
            }
        });

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SingIn(){
        btnLogin.setVisibility(View.INVISIBLE);

        mAuth.signInWithEmailAndPassword(strUsername,strPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent(loginActivity.this,naviActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(loginActivity.this,"Error - " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        btnLogin.setVisibility(View.VISIBLE);
                    }
                });
    }

    private boolean isValidate(){
        if(TextUtils.isEmpty(strUsername)){
            Username.setError("Username can't be empty !");
            return false;
        }
        if(!strUsername.matches(userPattern)){
            Username.setError("Enter a valid Email ID !");
            return false;
        }
        if(TextUtils.isEmpty(strPassword)) {
            Password.setError("Password can't be empty !");
            return false;
        }
        return true;
    }


//    @Override
//    public void onClick(View v) {
//        if(v.getId() == R.id.idLogin){
//            validate(Username.getText().toString(), Password.getText().toString());
//        }
//
//        if (v.getId() == R.id.idRegister){
//            Intent intent = new Intent(this,registerActivity.class);
//            startActivity(intent);
//        }
//    }
//
//    private void validate(String valUsername, String valPassword) {
//        for (int idx = 0; idx < Arrusr.size();idx++){
//            String UserPassword = Arrusr.get(idx).getPassword();
//            String UserName = Arrusr.get(idx).getUsername();
//            if (valUsername.equals(UserName) && valPassword.equals(UserPassword)) {
//                Intent intent = new Intent(this,naviActivity.class);
//                startActivity(intent);
//            } else {
//                counter--;
//                Info.setText("Wrong pass :" + String.valueOf(counter));
//
//                if (counter == 0) {
//                    btnLogin.setEnabled(false);
//
//                }
//            }
//        }
//    }

}
