package com.example.shoppingcomplex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shoppingcomplex.Model.Users;
import com.example.shoppingcomplex.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowBtn,loginBtn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinNowBtn = (Button) findViewById(R.id.main_join_now_btn);
        loginBtn = (Button) findViewById(R.id.main_login_btn);
        loadingBar=new ProgressDialog(this);


        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);


        if(UserPasswordKey !="" && UserPasswordKey !="" )
        {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKey, UserPasswordKey);

                loadingBar.setTitle("Already Logged In");
                loadingBar.setMessage("Please Wait.....");
                loadingBar.setCanceledOnTouchOutside(false);

                loadingBar.show();
            }
        }

    }

    private void AllowAccess(final String UserPhoneKey, final String UserPasswordKey)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("Users").child(UserPhoneKey).exists())
                {
                    Users userData = dataSnapshot.child("Users").child(UserPhoneKey).getValue(Users.class);

                    if (userData.getPhone().equals(UserPhoneKey))
                    {
                        if (userData.getPassword().equals(UserPasswordKey))
                        {
                            Toast.makeText(MainActivity.this, "You are already logged in...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent=new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser=userData;
                            startActivity(intent);

                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this, "Account with this "+UserPhoneKey+" number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    //Toast.makeText(LoginActivity.this, "You need to create a new Account.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
