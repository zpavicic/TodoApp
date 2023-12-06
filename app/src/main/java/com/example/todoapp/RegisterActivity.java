package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRepeatPassword;
    private Button buttonRegister;
    private TextView textViewBack;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRepeatPassword = findViewById(R.id.editTextRepeatPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewBack = findViewById(R.id.textViewBack);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreUsuario = editTextUsername.getText().toString();
                String emailUsuario = editTextEmail.getText().toString();
                String contrasenaUsuario = editTextPassword.getText().toString();
                String contrasenaRepetirUsuario = editTextRepeatPassword.getText().toString();

                if (nombreUsuario.isEmpty() || emailUsuario.isEmpty() || contrasenaUsuario.isEmpty() || contrasenaRepetirUsuario.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        registerUser(nombreUsuario,emailUsuario,contrasenaUsuario);
                    } catch (Exception e){
                        e.getMessage();
                    }

                }
            }
        });

        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser(String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = auth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id",id);
                map.put("name",editTextUsername);
                map.put("email",editTextEmail);
                map.put("password", editTextPassword);

                firestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Error al guardar Usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void newUser() {

        User u = new User();

        u.setUuid(UUID.randomUUID().toString().trim());
        u.setUsername(editTextUsername.getText().toString().trim());
        u.setEmail(editTextEmail.getText().toString().trim());
        u.setPassword(editTextRepeatPassword.getText().toString().trim());

        if (u.getUsername().isEmpty() || u.getEmail().isEmpty() || u.getPassword().isEmpty()){
            Toast.makeText(RegisterActivity.this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            firestore.collection("user").add(u);
            // Database.child("Persona").child(p.getUid()).setValue(p);
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

            editTextUsername.setText("");
            editTextEmail.setText("");
            editTextRepeatPassword.setText("");
            editTextRepeatPassword.setText("");

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }
}