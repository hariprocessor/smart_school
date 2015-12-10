package emos.absence;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import emos.absence.fragments.RegisterStudentFragment;
import emos.absence.fragments.MainFragment;
import emos.absence.fragments.StudentListFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    final static SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
    public static final String CHARS = "0123456789ABCDEF";

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private static String tagNum=null;

    private MainFragment mainFragment;
    private RegisterStudentFragment registerStudentFragment;
    private StudentListFragment studentListFragment;

    @InjectView(R.id.main_tool_bar)
    Toolbar toolBar;
    @InjectView(R.id.main_drawer_view)
    NavigationView navigationView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;
    public int id = R.id.mainMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, SplashActivity.class));

        ButterKnife.inject(this);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mainFragment = MainFragment.newInstance();
        registerStudentFragment = RegisterStudentFragment.newInstance();
        studentListFragment = StudentListFragment.newInstance();

        setSupportActionBar(toolBar);

        drawerToggle
                = new ActionBarDrawerToggle(this, drawerLayout, toolBar,
                R.string.app_name, R.string.app_name);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    private String currentDate() {
        Calendar calendar = Calendar.getInstance();
        return simpleDateFormat.format(calendar.getTimeInMillis());
    }

    private String currentTime() {
        Calendar calendar = Calendar.getInstance();
        return simpleDateFormatTime.format(calendar.getTimeInMillis());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if(tag != null) {
            byte[] tagId = tag.getId();
            switch (id) {
                case R.id.mainMenuItem:
                    mainFragment.setTagIDTextView(toHexString(tagId));
                    mainFragment.attendStudentCommunication(toHexString(tagId), currentDate(),
                            currentTime(), mainFragment.isLate());
                    break;
                case R.id.registerStudentMenuItem:
                    registerStudentFragment.setTagID(toHexString(tagId));
                    break;
            }
        }
    }

    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; ++i)
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(CHARS.charAt(data[i] & 0x0F));
        return sb.toString();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();

        if (mainFragment == null) mainFragment = MainFragment.newInstance();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.main_frame, mainFragment)
                .commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        this.id = id;
        Fragment fragment = null;
        switch (id) {
            case R.id.mainMenuItem:
                fragment = mainFragment;
                getSupportActionBar().setTitle(R.string.app_name);
                break;
            case R.id.registerStudentMenuItem:
                fragment = registerStudentFragment;
                getSupportActionBar().setTitle("Register Student");
                break;
            case R.id.studentListMenuItem:
                fragment = studentListFragment;
                getSupportActionBar().setTitle("Attendance Status");
                break;
        }

        if (fragment != null) {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.main_frame, fragment)
                    .commit();

            drawerLayout.closeDrawers();
            menuItem.setChecked(true);
        }
        return true;
    }
}
