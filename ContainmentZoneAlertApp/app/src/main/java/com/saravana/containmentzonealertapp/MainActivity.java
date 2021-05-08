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
import com.saravana.containmentzonealertapp.models.AuthResponse;
import com.saravana.containmentzonealertapp.models.User;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static String apiUrl;
    private TextInputLayout  emailEditTextLayout,passwordEditTextLayout;
    private Button loginBtn, registerBtn;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiUrl = getResources().getString(R.string.api_url);
        Log.d("TESTING", "onCreate: apiUrl :"+apiUrl);
        setContentView(R.layout.activity_main);
        initWidgets();
        helloWorld();
    }

    private void goToHome() {
        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    //pinging with jwt
    private void helloWorld(){
         String jwtToken = AuthUtils.getAuthorizationToken(MainActivity.this);
         if(jwtToken.isEmpty()){
             Log.d("TESTING", "helloWorld: No Auth token");
             progressBar.setVisibility(View.GONE);
             return;
         }
        Log.d("TESTING", "helloWorld: "+jwtToken);
         Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().helloWorld(jwtToken);
         call.enqueue(new Callback<ResponseBody>() {
             @Override
             public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                 if(response.isSuccessful()){
                     try {
                         Log.d("TESTING", "onResponse: "+response.body().string());
                         goToHome();
                         progressBar.setVisibility(View.GONE);
                         return;
                     } catch (IOException ioException) {
                         ioException.printStackTrace();
                     }
                 }

                 progressBar.setVisibility(View.GONE);

             }
             @Override
             public void onFailure(Call<ResponseBody> call, Throwable t) {
                 Log.d("TESTING", "onResponse: "+t.getLocalizedMessage());
                 progressBar.setVisibility(View.GONE);

             }
         });

    }
    private void initWidgets() {
        emailEditTextLayout = findViewById(R.id.emailTextField);
        passwordEditTextLayout = findViewById(R.id.passwordTextField);
        progressBar = findViewById(R.id.loginProgressbar);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.goToRegisterBtn);

        registerBtn.setOnClickListener(view-> goToRegisterActivity());
        loginBtn.setOnClickListener(view-> loginUser()
        );

        progressBar.setVisibility(View.VISIBLE);
    }
    private void goToRegisterActivity(){
        Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }
    private void loginUser(){
        progressBar.setVisibility(View.VISIBLE);
        String email = emailEditTextLayout.getEditText().getText().toString().trim();
        String password = passwordEditTextLayout.getEditText().getText().toString().trim();

        if(email.isEmpty()) {
            emailEditTextLayout.setError("This field cannot be empty");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        if(password.isEmpty()) {
            passwordEditTextLayout.setError("This field cannot be empty");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }


        Call<AuthResponse> call =RetrofitClient.getInstance().getAPI().loginUser(new User(null,email,password));

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("TESTING", "onResponse: "+response.toString());
                if(response.isSuccessful()) {
                    String jwt = "";
                    Log.d("TESTING", "onResponse: "+response.body().getJwt());
                    jwt = response.body().getJwt();
                    if (jwt.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AuthUtils.storeAuthorizationToken(MainActivity.this, jwt);
                    goToHome();
                }else{
                    Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("TESTING", "onResponse: "+t.getLocalizedMessage());
                Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}