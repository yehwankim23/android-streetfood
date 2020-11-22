package com.example.yehwankim23.streetfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateUserInfoActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.button_update).setOnClickListener(onClickListener);
        findViewById(R.id.button_cancel).setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DocumentReference docRef = db.collection("user").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                if (document.getData() != null) {
                                    ((EditText) findViewById(R.id.text_email)).setText((String) document.getData().get("email"));
                                    ((EditText) findViewById(R.id.text_password)).setText((String) document.getData().get("password"));
                                    ((EditText) findViewById(R.id.text_username)).setText((String) document.getData().get("username"));
                                }
                            }
                        }
                    } else {
                        if (task.getException() != null) {
                            showToast(task.getException().toString());
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_update:
                    updateUserInfo();
                    break;
                case R.id.button_cancel:
                    startAnActivity(MainActivity.class);
                    break;
            }
        }
    };

    private void updateUserInfo() {
        String email = ((EditText) findViewById(R.id.text_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.text_password)).getText().toString();
        String username = ((EditText) findViewById(R.id.text_username)).getText().toString();

        if (username.length() > 0) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                User user = new User(email, password, username);
                db.collection("user").document(currentUser.getUid())
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast("회원정보를 수정했습니다.");
                                startAnActivity(MainActivity.class);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast(e.getMessage());
                            }
                        });
            }
        } else {
            showToast("회원정보를 입력하세요.");
        }
    }

    private void startAnActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
