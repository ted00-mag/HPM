package si.uni_lj.fe.tnuv.hpm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.zxing.Result;

public class LoginActivity<GoogleSignInClient> extends AppCompatActivity {

    Button btnPrijava, btnScanQR;
    public EditText uporabniskoIme, geslo;
    TextView tvRegistracija;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private com.google.android.gms.auth.api.signin.GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButtonWithGoogle;
    private int RC_SIGN_IN = 1;
    public static String textFromQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        uporabniskoIme = findViewById(R.id.uporabniskoIme);
        geslo = findViewById(R.id.geslo);
        btnPrijava = findViewById(R.id.prijava);
        tvRegistracija = findViewById(R.id.TextZaRegistracijo);
        signInButtonWithGoogle = findViewById(R.id.signInWithGoogle);
        btnScanQR = findViewById(R.id.btn_scan_qr);


        //Skeniranje QR kode
        btnScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    return;
                }
                startActivity(new Intent(getApplicationContext(), ScanQRActivity.class));
            }
        });

        //

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail(). build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButtonWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser != null){
                    Toast.makeText(LoginActivity.this, "Uspešno ste vpisani!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HOmeActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Vpišite se!", Toast.LENGTH_SHORT).show();
                }



            }
        };


            btnPrijava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = uporabniskoIme.getText().toString();
                String pwd = geslo.getText().toString();
                if(email.isEmpty()){
                    uporabniskoIme.setError("Prosim vpišite uporabniško ime");
                    uporabniskoIme.requestFocus();
                }
                else if(pwd.isEmpty()){
                    geslo.setError("Prosim vpišite vaše geslo");
                    geslo.requestFocus();
                }
                else if (email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Polja so prazna!", Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login Error, Please login again!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Intent intToHome = new Intent(LoginActivity.this, HOmeActivity.class);
                                startActivity(intToHome);
                            }
                        }
                    });

                }
                else {
                    Toast.makeText(LoginActivity.this, "Napaka!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvRegistracija.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intRegistracija = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intRegistracija);
            }
        });
    }

    //Tukaj si zunaj onCreate------------------------------------------------------------------------------------

    // Login with Google -----------------------------------------------------------------------------
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handlesSignInResult(task);
        }
    }

    private void handlesSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(this, R.string.UspesnoPrijavljeni,Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(this, R.string.NapakPriPrijavi,Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, R.string.Uspesno,Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Toast.makeText(LoginActivity.this, R.string.Neuspesno, Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser fUser) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account != null){
            String personName = account.getDisplayName();
            String personalGivenName = account.getGivenName();
            String personalFamilyName = account.getFamilyName();
            String personalEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            Toast.makeText(LoginActivity.this, personName + personalEmail, Toast.LENGTH_SHORT).show();
        }
    }
    // Login with google --------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }



}
