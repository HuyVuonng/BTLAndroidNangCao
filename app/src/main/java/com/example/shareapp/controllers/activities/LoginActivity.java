package com.example.shareapp.controllers.activities;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import static com.example.shareapp.controllers.constant.AuthenticateConstant.GOOGLE_REQUEST_CODE_SIGN_IN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareapp.R;
import com.example.shareapp.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101010;

    EditText Email, Password;
    TextView regesterBtn, forgotPasswordBtn;
    Button loginbtn;
    ImageView googlebtn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    private GoogleSignInClient client;
    GoogleSignInOptions gso;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    SharedPreferences sharedPreferences;
    boolean passWordVisible;
    private DatabaseReference mDatabase;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getViews();
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        Email.setText(email);
        Password.setText("");

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, options);

        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = client.getSignInIntent();
                startActivityForResult(i, GOOGLE_REQUEST_CODE_SIGN_IN);
            }
        });

        Password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= Password.getRight() - Password.getCompoundDrawables()[right].getBounds().width() - 40) {
                        int selection = Password.getSelectionEnd();
                        if (passWordVisible) {
                            Password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, R.drawable.baseline_visibility_off_24, 0);
                            Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passWordVisible = false;
                        } else {
                            Password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, R.drawable.baseline_visibility_24, 0);
                            Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passWordVisible = true;
                        }
                        Password.setSelection(selection);
                        return true;
                    }
                }

                return false;
            }
        });
        regesterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginWithEmailAndPassWord();
            }

            private void LoginWithEmailAndPassWord() {
                String email = Email.getText().toString().trim();
                String Pass = Password.getText().toString().trim();

                SharedPreferences editor = LoginActivity.this.getSharedPreferences("dataPass", MODE_PRIVATE);
                editor.edit().clear().commit();
                if (TextUtils.isEmpty(email)) {
                    Email.setError("Nhập Email");
                    return;
                }
                if (TextUtils.isEmpty(Pass)) {
                    Password.setError("Nhập mật khẩu");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean Verified = FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();
                            if (!Verified) {
                                progressBar.setVisibility(View.INVISIBLE);
                                showDialog();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                sharedPreferences = getSharedPreferences("dataPass", MODE_PRIVATE);
                                sharedPreferences.edit().putString("password", Password.getText().toString().trim()).apply();
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Sai email đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Email.setError("Nhập Email");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        showDialogForgotPass();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "Không gửi được email xác thực " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            this.loginWithBiometric();
//        }

        loginWithBiometric();

    }

    private void getViews() {
        regesterBtn = findViewById(R.id.register);
        forgotPasswordBtn = findViewById(R.id.forgotpass);
        loginbtn = findViewById(R.id.loginbtn);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar2);
        googlebtn = findViewById(R.id.google_btn);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void loginWithBiometric() {
        //Đn vân tay
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(LoginActivity.this, "Không có cảm biến vân tay", Toast.LENGTH_LONG);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                Toast.makeText(LoginActivity.this, "Cảm biến vân tay không dùng được hoặc đang bận", Toast.LENGTH_LONG);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                SharedPreferences editor = LoginActivity.this.getSharedPreferences("data", MODE_PRIVATE);
                editor.edit().clear().commit();

                SharedPreferences editor1 = LoginActivity.this.getSharedPreferences("dataPass", MODE_PRIVATE);
                editor1.edit().clear().commit();
//                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                String type = sharedPreferences.getString("type", "");
                if (type.equals("google")) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        Log.d("here", "1");
                    }
                } else {
                    sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                    String email = sharedPreferences.getString("email", "");
                    sharedPreferences = getSharedPreferences("dataPass", MODE_PRIVATE);
                    String Pass = sharedPreferences.getString("password", "");

                    mAuth.signInWithEmailAndPassword(email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }

                Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sharehub")
                .setSubtitle("Xác thực bằng vân tay, vui lòng chạm vào cảm biến vân tay")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.

//    hetDn van Tay
    }

    protected void onStart() {
        super.onStart();
        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);
        if (isLogin) {
            biometricPrompt.authenticate(promptInfo);
        }
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Thông báo");
        builder.setMessage("Vui lòng vào địa chỉ email để xác thực tài khoản.Bạn sẽ không đăng nhập được nếu không xác thực tài khoản.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Gửi lại mail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LoginActivity.this, "Đã gửi email xác nhận", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Không gửi được email xác thực " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    ;

    public void showDialogForgotPass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Thông báo");
        builder.setMessage("Đã gửi mail đổi mật khẩu. Vui lòng vào mail để đổi lại mật khẩu của bạn");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    ;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String fullname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
                                    String email = acct.getEmail();
                                    ;

                                    chechUserLoginGGExists(uid, fullname, "", "", email);

                                } else {
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }


    public void chechUserLoginGGExists(String uid, String fullName, String phoneNumber, String address, String email) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        mDatabase.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        User user = new User(fullName, phoneNumber, address, email, uid);
                        mDatabase.child(uid).setValue(user);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Không đọc được", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}