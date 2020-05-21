package si.uni_lj.fe.tnuv.hpm;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HOmeActivity<TabLayout> extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ProgressBar cProgressBar, lProgressBar, dProgressBar, predvidena_porabaProgresBar;
    private Handler handler = new Handler();

    TextView tvTrenutnaPoraba, tvTedenskaPoraba, tvDnevnaPoraba, tvProgressBar, tvNumber, tvKlimatskeNaprave, naslovKlimatskeNaprave, vrednostKlimatskeNaprave, vrednostPralnegaStroja,
            vrednostHladilnika, vrednostPomivalnegaStroja, vrednostOsvetljave, vrednostLikalnika, vrednostRacunalnika;
    DatabaseReference reff, reff1;
    public int cprogressStatus, lprogressStatus, rprogressStatus, predvidena_poraba_progressStatus;
    public String string_poraba_za_april;
    ImageButton btCall, btWriteMessage;
    String napovedanaPoraba;

    //viewPager za cardView (nasveti)
    ViewPager viewPager;
    Adpater_za_nasvete adapter;
    List<Model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    //viewPager za aparate
    ViewPager viewPager_aparati;
    Adapter_za_aparate adapter_za_aparate;
    List<Model_aparati> model_aparati;
    ArgbEvaluator argbEvaluator_aparati = new ArgbEvaluator();
    TabLayout tabLayout;
    private ObjectAnimator objectAnimatorcProgressBar, objectAnimatorlProgressBar, objectAnimatorrProgressBar;

    //Bottom toolbar
    BottomNavigationView bottomNavigationView;

    //RecyclerView Racuni
    RecyclerView recyclerViewRacun;
    List<racuni> racuniList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_ome);
        getSupportActionBar().setTitle("Pregled porabe");

        //Recycler View racuni

        recyclerViewRacun = findViewById(R.id.recyclerViewRacuni);
        initData();
        setRecyclerViewRacuni();

        //Recycler View racuni



        //Bottom toolbar
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.home_screen);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_screen:
                        Toast.makeText(HOmeActivity.this, "Home screen", Toast.LENGTH_SHORT).show();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.podrobna_poraba_screen:
                        Toast.makeText(HOmeActivity.this, "Podrobna poraba", Toast.LENGTH_SHORT).show();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.sporocilo_screen:
                        Toast.makeText(HOmeActivity.this, "Pišite nam", Toast.LENGTH_SHORT).show();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.o_nas_screen:
                        Toast.makeText(HOmeActivity.this, "O nas", Toast.LENGTH_SHORT).show();
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
        //Bottom toolbar


        // Context menu on long click listener
        vrednostHladilnika = findViewById(R.id.valueOfHladilnik);
        vrednostPomivalnegaStroja = findViewById(R.id.valueOfPomivalniStroj);
        vrednostOsvetljave = findViewById(R.id.valueOfOstvetljava);
        vrednostLikalnika= findViewById(R.id.valueOfLikalnik);
        naslovKlimatskeNaprave = findViewById(R.id.klimatskeNaprave);
        vrednostPralnegaStroja = findViewById(R.id.valueOfPralniStroj);
        vrednostKlimatskeNaprave = findViewById(R.id.valueOfKlimatskaNaprava);
        vrednostRacunalnika = findViewById(R.id.valueOfRacunalnika);
        registerForContextMenu(naslovKlimatskeNaprave);

        // Context menu on long click listener



        // Klic ob pritisku na ImageButton

        tvNumber = findViewById(R.id.tv_telefonska_stevilka);
        btCall = findViewById(R.id.bt_slika_telefona);

        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = tvNumber.getText().toString();
                String s = "tel:" + phone;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(s));
                if (ActivityCompat.checkSelfPermission(HOmeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(HOmeActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    return;
                }
                startActivity(intent);
            }
        });

        // Klic ob pritisku na ImageButton

        // Napisi sporocilo ob pritisku na gumb
        btWriteMessage = findViewById(R.id.bt_napisi_sporocilo);
        btWriteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HOmeActivity.this, "Napiši sporočilo", Toast.LENGTH_LONG).show();
            }
        });
        // Napisi sporocilo ob pritisku na gumb

/*
        //Slider za aparate
        model_aparati = new ArrayList<>();
        model_aparati.add(new Model_aparati(R.drawable.circle, "24€", "18€"));

        adapter_za_aparate = new Adapter_za_aparate(model_aparati, this);

        //viewPager_aparati = findViewById(R.id.viewPager_aparati);
        viewPager_aparati.setAdapter(adapter_za_aparate);
        //viewPager_aparati.setPageMargin(20);
        viewPager_aparati.setPadding(0, 0 ,130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.color1),
        };

        colors = colors_temp;

        viewPager_aparati.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position < (adapter_za_aparate.getCount() -1) && position < (colors.length -1)){
                    viewPager_aparati.setBackgroundColor((Integer) argbEvaluator_aparati.evaluate(positionOffset, colors[position], colors[position]));
                }else {
                    viewPager_aparati.setBackgroundColor(colors[colors.length -1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //Slider za aparate
*/
        // Slider za nasvete -----------------------------------------------------------------------------------------------------------
        models = new ArrayList<>();
        models.add(new Model(R.drawable.nasveti_cards_background, "01", getString(R.string.nasvet1)));
        models.add(new Model(R.drawable.nasveti_cards_background, "02", getString(R.string.nasvet2)));
        models.add(new Model(R.drawable.nasveti_cards_background, "03", getString(R.string.nasvet3)));
        models.add(new Model(R.drawable.nasveti_cards_background, "04", getString(R.string.nasvet4)));

        adapter = new Adpater_za_nasvete(models, this);

        viewPager = findViewById(R.id.viewPager_nasveti);
        viewPager.setAdapter(adapter);
        viewPager.setPageMargin(20);
        viewPager.setPadding(130, 0 ,130, 0);
        Integer[] colors_temp = {
                getResources().getColor(R.color.color1),
        };

        colors = colors_temp;



        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                int pageWidth = viewPager.getMeasuredWidth() -
                        viewPager.getPaddingLeft() - viewPager.getPaddingRight();
                int pageHeight = viewPager.getHeight();
                int paddingLeft = viewPager.getPaddingLeft();
                float transformPos = (float) (page.getLeft() -
                        (viewPager.getScrollX() + paddingLeft)) / pageWidth;
                if (transformPos < -1)
                {
                    // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.setAlpha(0.6f);// to make left transparent
                    page.setScaleY(0.7f);
                }
                else if (transformPos <= 1)
                {
                    // [-1,1]
                    page.setAlpha(1f);
                    page.setScaleY(1f);
                }
                else
                {
                    // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.setAlpha(0.6f);// to make right transparent
                    page.setScaleY(0.7f);
                }
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position < (adapter.getCount() -1) && position < (colors.length -1)){
                    viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position]));
                }else {
                    viewPager.setBackgroundColor(colors[colors.length -1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // Slider za nasvete -----------------------------------------------------------------------------------------------------------



        // progress bar -----------------------------------------------------------------------------------------------------------
        cProgressBar = (ProgressBar) findViewById(R.id.circle_progress_bar_center);
        lProgressBar = (ProgressBar) findViewById(R.id.circle_progress_bar_left);
        dProgressBar = (ProgressBar) findViewById(R.id.circle_progress_bar_right);
        predvidena_porabaProgresBar = (ProgressBar) findViewById(R.id.circle_progress_bar_predvidena_poraba);
        // progress bar -----------------------------------------------------------------------------------------------------------

        final TextView tvTrenutnaPoraba = findViewById(R.id.tv_mesecna_poraba);
        final TextView tvTedenskaPoraba = findViewById(R.id.tv_tedenska_poraba);
        final TextView tvDnevnaPoraba = findViewById(R.id.tv_dnevna_poraba);

        reff = FirebaseDatabase.getInstance().getReference().child("Poraba_MAJ").child("2");
        reff.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String trenutna_poraba = dataSnapshot.child("now_poraba").getValue().toString();
                String tedenska_poraba = dataSnapshot.child("now_tedenska_poraba").getValue().toString();
                String dnevna_poraba = dataSnapshot.child("now_dnevna_poraba").getValue().toString();
                tvTrenutnaPoraba.setText(trenutna_poraba + "€");
                tvTedenskaPoraba.setText(tedenska_poraba + "€");
                tvDnevnaPoraba.setText(dnevna_poraba + "€");

                float tmp_poraba = Float.parseFloat(trenutna_poraba);
                float x = tmp_poraba * 10;
                float tmp_tedenska_poraba = Float.parseFloat(tedenska_poraba);
                float y = tmp_tedenska_poraba * 10;
                float tmp_dnevna_poraba = Float.parseFloat(dnevna_poraba);
                float z = tmp_dnevna_poraba * 10;

                cprogressStatus = (int) x;
                lprogressStatus = (int) y;
                rprogressStatus = (int) z;
                startProgressBar();
                objectAnimatorcProgressBar = ObjectAnimator.ofInt(cProgressBar, "progress", 0, (int) x);
                objectAnimatorcProgressBar.setDuration(2500);
                objectAnimatorcProgressBar.start();
                objectAnimatorlProgressBar = ObjectAnimator.ofInt(lProgressBar, "progress", 0, (int) z);
                objectAnimatorlProgressBar.setDuration(3000);
                objectAnimatorlProgressBar.start();
                objectAnimatorrProgressBar = ObjectAnimator.ofInt(dProgressBar, "progress", 0, (int) y);
                objectAnimatorrProgressBar.setDuration(3000);
                objectAnimatorrProgressBar.start();


                // Napovedovanje porabe

                Drawable draw_up = getDrawable(R.drawable.napovedovanje_progress_bar); // spreminjanje barve pri napovedovanju-progressBaru
                Drawable draw_down = getDrawable(R.drawable.napovedovanje_progress_bar_2); // spreminjanje barve pri napovedovanju-progressBaru
                Drawable draw_flat = getDrawable(R.drawable.napovedovanje_progress_bar_3); // spreminjanje barve pri napovedovanju-progressBaru

                final TextView tvNapovedovanje_na_sredini_PB = findViewById(R.id.tv_Prikaz_predvidene_porabe_PB);
                ImageView ivNapovedovanje_slika = findViewById(R.id.ivNapovedovanje_icon);
                TextView tvNapovedovanje_desno_od_slike = findViewById(R.id.tvNapovedovanje);

                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                SimpleDateFormat df = new SimpleDateFormat("dd");
                String formattedDate = df.format(c);
                System.out.println("Current time => " + formattedDate);
                Float trenuten_dan = Float.parseFloat(formattedDate);

                float stevilo_dni_v_mesecu = 31;
                float float_napovedana_poraba;
                float_napovedana_poraba = tmp_poraba / trenuten_dan;
                float nap_poraba = float_napovedana_poraba * stevilo_dni_v_mesecu;
                nap_poraba = round(nap_poraba);
                //Za testiranje
                //nap_poraba = 80;
                napovedanaPoraba = String.valueOf(nap_poraba);

                // ProgressBar za napovedovanje
                predvidena_poraba_progressStatus = (int) (nap_poraba *10);
                objectAnimatorcProgressBar = ObjectAnimator.ofInt(predvidena_porabaProgresBar, "progress", 0, predvidena_poraba_progressStatus);
                objectAnimatorcProgressBar.setDuration(3500);
                objectAnimatorcProgressBar.start();

                int int_poraba_april = Integer.parseInt("39");
                //Za testiranje
                //int_poraba_april = 130;
                tvNapovedovanje_na_sredini_PB.setText(napovedanaPoraba + "€");

                float prikazi_razliko_v_porabi = int_poraba_april - nap_poraba;
                String tv_razlika = String.valueOf(prikazi_razliko_v_porabi);
                Float razlika = Float.parseFloat(tv_razlika);
                razlika = round(razlika);
                tv_razlika = String.valueOf(razlika);

                if (nap_poraba < int_poraba_april){
                    ivNapovedovanje_slika.setImageResource(R.drawable.ic_trending_down);
                    tvNapovedovanje_desno_od_slike.setText(tv_razlika + "€");
                    tvNapovedovanje_desno_od_slike.setTextColor(R.color.tvNapovedovanje_down);

                    predvidena_porabaProgresBar.setProgressDrawable(draw_down);
                    // Če želiš brez gradientne barve - fiksna vrednost
                    //predvidena_porabaProgresBar.getProgressDrawable().setColorFilter(Color.parseColor(getString(R.string.brisi2)), android.graphics.PorterDuff.Mode.SRC_IN);
                    tvNapovedovanje_na_sredini_PB.setTextColor(getResources().getColor(R.color.tvNapovedovanje_down));
                }else if(nap_poraba > int_poraba_april){
                    ivNapovedovanje_slika.setImageResource(R.drawable.ic_trending_up);
                    tvNapovedovanje_desno_od_slike.setText(tv_razlika + "€");
                    tvNapovedovanje_desno_od_slike.setTextColor(R.color.tvNapovedovanje_up);

                    predvidena_porabaProgresBar.setProgressDrawable(draw_up);
                    //predvidena_porabaProgresBar.getProgressDrawable().setColorFilter(Color.parseColor(getString(R.string.brisi)), android.graphics.PorterDuff.Mode.SRC_IN);
                    tvNapovedovanje_na_sredini_PB.setTextColor(getResources().getColor(R.color.tvNapovedovanje_up));
                }else{
                    ivNapovedovanje_slika.setImageResource(R.drawable.ic_trending_flat);
                    tvNapovedovanje_desno_od_slike.setText(R.string.tvPorabaNapovedovanje);
                    tvNapovedovanje_desno_od_slike.setTextColor(R.color.tvNapovedovanje_flat);

                    predvidena_porabaProgresBar.setProgressDrawable(draw_flat);
                    tvNapovedovanje_na_sredini_PB.setTextColor(getResources().getColor(R.color.tvNapovedovanje_flat));
                }

                float tren_poraba = Float.parseFloat(trenutna_poraba);
                float procentKlimatskeNaprave = (float) 0.35;
                float procentPralnega = (float) 0.20;
                float procentHladilnika = (float) 0.09;
                float procentPomivalnega = (float) 0.15;
                float procentOsvetljava = (float) 0.10;
                float procentLikalnika = (float) 0.05;
                float procentRacunalnika = (float) 0.10;

                float valKlimatske = tren_poraba * procentKlimatskeNaprave;
                float valPralnega = tren_poraba * procentPralnega;
                float valHladilnika = tren_poraba * procentHladilnika;
                float valPomivalnega = tren_poraba * procentPomivalnega;
                float valOsvetljava = tren_poraba * procentOsvetljava;
                float valLikalnik = tren_poraba * procentLikalnika;
                float valRacunalnika = tren_poraba * procentRacunalnika;


                vrednostKlimatskeNaprave.setText((int) valKlimatske + "€");
                vrednostPralnegaStroja.setText((int) valPralnega + "€");
                vrednostHladilnika.setText((int) valHladilnika + "€");
                vrednostPomivalnegaStroja.setText((int) valPomivalnega + "€");
                vrednostOsvetljave.setText((int) valOsvetljava + "€");
                vrednostLikalnika.setText((int) valLikalnik + "€");
                vrednostRacunalnika.setText((int) valRacunalnika + "€");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







    }

    private void initData() {

        racuniList = new ArrayList<>();
        racuniList.add(new racuni("April 2020", "35,86 €", "Zapadlost: 6.05.2020", "Razčlenjen račun.pdf", ""));
        racuniList.add(new racuni("Marec 2020", "37,25 €", "Zapadlost: 6.04.2020", "Razčlenjen račun.pdf", "(Že plačano)"));
        racuniList.add(new racuni("Februar 2020", "39,12 €", "Zapadlost: 6.03.2020", "Razčlenjen račun.pdf", "(Že plačano)"));
        racuniList.add(new racuni("Januar 2020", "38,63 €", "Zapadlost: 6.02.2020", "Razčlenjen račun.pdf", "(Že plačano)"));
        racuniList.add(new racuni("December 2019", "35,05 €", "Zapadlost: 6.01.2020", "Razčlenjen račun.pdf", "(Že plačano)"));

    }

    private void setRecyclerViewRacuni() {
        racuniAdapter racuniAdapter = new racuniAdapter(racuniList);
        recyclerViewRacun.setAdapter(racuniAdapter);
        recyclerViewRacun.setHasFixedSize(true);
    }
    // IZ onCreate ----------------------------------------------------------------------------------------------------


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.menu_najbolj_potratni_aparati, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_odstrani_aparat:
                Toast.makeText(this, R.string.menu_potratni_aparati_odstrani_napravo, Toast.LENGTH_LONG).show();
                naslovKlimatskeNaprave.setText("Toplotna črpalka");
                vrednostPralnegaStroja.setText("20€");
                vrednostPralnegaStroja.setText("12€");
                vrednostHladilnika.setText("6€");
                vrednostPomivalnegaStroja.setText("18€");
                vrednostOsvetljave.setText("3€");
                vrednostLikalnika.setText("2€");
                return true;
            case R.id.menu_dodaj_aparat:
                Toast.makeText(this, R.string.menu_potratni_aparati_dodaj_napravo, Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void startProgressBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                // Update progress bar
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        cProgressBar.setProgress(cprogressStatus);
                        lProgressBar.setProgress(lprogressStatus);
                        dProgressBar.setProgress(rprogressStatus);
                        predvidena_porabaProgresBar.setProgress(predvidena_poraba_progressStatus);
                    }
                });

            }
        }).start();
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

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.home_meni, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_moj_racun:
                Toast.makeText(this, "Moj račun", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_naprava:
                Toast.makeText(this, "Naprava", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_nastavitve:
                Toast.makeText(this, "Nastavitve", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_o_nas:
                Toast.makeText(this, "O nas", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_pomoc:
                Toast.makeText(this, "Pomoč", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_splosno:
                Toast.makeText(this, "Splošno", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_odjava:
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HOmeActivity.this, LoginActivity.class);
                startActivity(intToMain);
                Toast.makeText(this, "Uspešno ste izpisani", Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static float round(float f){
        return Math.round(f * 10) / 10.0F;
    }

}
