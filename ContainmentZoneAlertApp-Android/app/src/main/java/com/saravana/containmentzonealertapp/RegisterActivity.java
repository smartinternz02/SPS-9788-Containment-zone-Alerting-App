package com.saravana.containmentzonealertapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.saravana.containmentzonealertapp.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout emailTextLayout,nameTextLayout,passwordTextLayout;
    private Button registerBtn,loginBtn;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initWidgets();
    }

    private void initWidgets() {
         emailTextLayout = findViewById(R.id.emailTextField);
         nameTextLayout = findViewById(R.id.usernameTextField);
         passwordTextLayout = findViewById(R.id.passwordTextField);
         registerBtn = findViewById(R.id.goToRegisterBtn);
         loginBtn = findViewById(R.id.goToLoginBtn);
         progressBar = findViewById(R.id.progressBar);

         loginBtn.setOnClickListener(view->goToLoginActivity());
         registerBtn.setOnClickListener(view->registeUser());
    }

    private void registeUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email = emailTextLayout.getEditText().getText().toString().trim();
        String name = nameTextLayout.getEditText().getText().toString().trim();
        String password = passwordTextLayout.getEditText().getText().toString().trim();
        Log.d("TESTING", "loginUser: email:"+email +",password:"+password+",name:"+name);

        if(email.isEmpty()){
             emailTextLayout.setError("This Field cannot be empty");
             progressBar.setVisibility(View.INVISIBLE);
             return;
        }else if(name.isEmpty()){
            nameTextLayout.setError("This Field cannot be empty");
            progressBar.setVisibility(View.INVISIBLE);
             return;
        }else if(password.isEmpty()){
            passwordTextLayout.setError("This Field cannot be empty");
            progressBar.setVisibility(View.INVISIBLE);
           return;
        }

         User newUser = new User(name,email,password);
        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().createUser(newUser);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               if(response.isSuccessful()){
                   Toast.makeText(RegisterActivity.this,"Registration Successfull.Please Login",Toast.LENGTH_LONG).show();
                   goToLoginActivity();
               }else{
                   Toast.makeText(RegisterActivity.this,"Registration Failed: Email Already exists",Toast.LENGTH_SHORT).show();
               }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void goToLoginActivity(){
         finish();
    }
}