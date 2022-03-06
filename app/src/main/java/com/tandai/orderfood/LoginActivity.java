package com.tandai.orderfood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tandai.orderfood.Model.Common;
import com.tandai.orderfood.Model.User;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    DatabaseReference mData;
    FirebaseAuth mAuthencation;
    Button btnLog;
    EditText email;
    EditText pass;

    CheckBox Remember;
    TextView forgotPass;

    AlertDialog waiting;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.layout_login);
        AnhXa();
        waiting =  new SpotsDialog.Builder().setContext(this).setMessage("Please wait...").setCancelable(false).build();
        mAuthencation = FirebaseAuth.getInstance();

        Paper.init(this);

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DangNhap();
            }
        });

       forgotPass.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              startActivity(new Intent(LoginActivity.this,ForgotPassActivity.class));
           }
       });
    }

    private void AnhXa(){
        btnLog=(Button) findViewById(R.id.btnLog);
        email= (EditText) findViewById(R.id.edtEmailLog);
        pass=(EditText) findViewById((R.id.edtPassLog));
        Remember =(CheckBox) findViewById(R.id.ckbRemember);
        forgotPass = (TextView) findViewById(R.id.forgotPass);
    }

    private void DangNhap(){
        final String Email = email.getText().toString().trim();
        final String Pass = pass.getText().toString().trim();
        if (Email.isEmpty() || Pass.isEmpty() ) {
            Toast.makeText(this, "Please enter full information ", Toast.LENGTH_SHORT).show();
        }
        else {
            if(isNetworkAvailable()) {
                waiting.show();
                if (Remember.isChecked()) {
                    Paper.book().write(Common.USER_KEY, Email);
                    Paper.book().write(Common.PWD_KEY, Pass);
                }
                mAuthencation.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
                            String userID = USER.getUid();


                            mData = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            mData.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    waiting.dismiss();
                                    User user = dataSnapshot.getValue(User.class);

                                    if (user.getUserType().equals("admin")) {
                                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                    } else if (user.getUserType().equals("restaurant")) {
                                        startActivity(new Intent(LoginActivity.this, RestaurantActivity.class));
                                    } else if (user.getUserType().equals("customer")) {

                                        if (USER.isEmailVerified()) {
                                            startActivity(new Intent(LoginActivity.this, CustomersActivity.class));
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please verify your Email to login ", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            // write me down in the database if you get used to it after laying it out

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataSnapshot.child("pass").getRef().setValue(Pass);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            mDatabase.addListenerForSingleValueEvent(eventListener);

                        } else {
                            waiting.dismiss();
                            Toast.makeText(LoginActivity.this, "Invalid account or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(this, "You are not connected to the Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // check internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
