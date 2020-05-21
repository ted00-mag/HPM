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

public class RegisterActivity extends AppCompatActivity {

    public EditText uporabniskoIme, geslo;
    Button btnRegistracija, btnScanQR;
    TextView tvRegistracija;
    FirebaseAuth mFirebaseAuth;
    private SignInButton signInButtonWithGoogle;
    private com.google.android.gms.auth.api.signin.GoogleSignInClient mGoogleSignInClient;
    private String TAG = "MainActivity";
    private int RC_SIGN_IN = 1;
    public static String textFromQRCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    mFirebaseAuth = FirebaseAuth.getInstance();
    uporabniskoIme = findViewById(R.id.uporabniskoIme);
    geslo = findViewById(R.id.geslo);
    btnRegistracija = findViewById(R.id.prijava);
    tvRegistracija = findViewById(R.id.TextZaRegistracijo);
    signInButtonWithGoogle = findViewById(R.id.signInWithGoogle);
    btnScanQR = findViewById(R.id.btn_scan_qr);


    //Skeniranje QR kode
    btnScanQR.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                return;
            }
            startActivity(new Intent(getApplicationContext(), ScanQRActivity.class));
        }
    });

    //Skeniranje QR kode

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

        btnRegistracija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = uporabniskoIme.getText().toString();
                String pwd = geslo.getText().toString();
                if(email.isEmpty()){
                    uporabniskoIme.setError(getString(R.string.ProsimVpišiteUI));
                    uporabniskoIme.requestFocus();
                }
                else if(pwd.isEmpty()){
                    geslo.setError(getString(R.string.ProsimVpisitePWD));
                    geslo.requestFocus();
                }
                else if (email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(RegisterActivity.this, R.string.PoljaSoPrazna, Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, R.string.NeuspesnaPrijava, Toast.LENGTH_LONG).show();
                            }
                            else {
                                startActivity(new Intent(RegisterActivity.this, HOmeActivity.class));
                            }
                        }
                    });

                }
                else {
                    Toast.makeText(RegisterActivity.this, R.string.NapakPriVpisu, Toast.LENGTH_SHORT).show();
                }
            }
        });

            tvRegistracija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }
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
                    Toast.makeText(RegisterActivity.this, R.string.Uspesno,Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Toast.makeText(RegisterActivity.this, R.string.Neuspesno, Toast.LENGTH_SHORT).show();
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

            Toast.makeText(RegisterActivity.this, personName + personalEmail, Toast.LENGTH_SHORT).show();
        }
    }
}
