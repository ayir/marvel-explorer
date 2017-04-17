package com.riya.marvel;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.riya.marvel.db.Comic;
import com.riya.marvel.db.Person;
import com.riya.marvel.utilities.CallBackItemSelection;
/**
 * Created by RiyaSharma on 16-04-2017.
 */

public class MainActivity extends AppCompatActivity implements CallBackItemSelection,TabLayout.OnTabSelectedListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mListWithDetail;
    private TabLayout tabLayout;
    private static final int PERMISSION_REQUEST_CODE = 1;

    //This is our viewPager
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {}
            else {
                requestPermission(); // Code for permission
            }

        }
        else{}
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

//        //Adding the tabs using addTab() method
       tabLayout.addTab(tabLayout.newTab().setText("CHARACTERS"));
        tabLayout.addTab(tabLayout.newTab().setText("COMICS"));


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(viewPager);

        if (findViewById(R.id.person_container) != null) {
            mListWithDetail = true;



            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.person_container, new DetailPersonFragment())
                        .commit();
            }

        } else
            mListWithDetail = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Person person) {
        if (mListWithDetail) {

            Bundle args = new Bundle();
            args.putSerializable("person", person);

            DetailPersonFragment detailFragment = new DetailPersonFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.person_container, detailFragment)
                    .commit();

        } else {
            Intent i = new Intent(this, DetailPersonActivity.class);
            i.putExtra("person", person);
            startActivity(i);
        }
    }

    @Override
    public void onItemSelected(Comic comic) {
        if (mListWithDetail) {

            Bundle args = new Bundle();
            args.putSerializable("comic",comic);

            DetailPersonFragment detailFragment = new DetailPersonFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.person_container, detailFragment)
                    .commit();

        } else {
            Intent i = new Intent(this, DetailPersonActivity.class);
            i.putExtra("comic",comic);
            startActivity(i);
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                    requestPermission();
                }
                break;
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    public class Pager extends FragmentStatePagerAdapter {
        private String[] tabTitles = new String[]{"CHARACTERS", "COMICS"};
        //integer to count number of tabs
        int tabCount;

        //Constructor to the class
        public Pager(FragmentManager fm, int tabCount) {
            super(fm);
            //Initializing tab count
            this.tabCount= tabCount;
        }

        //Overriding method getItem
        @Override
        public Fragment getItem(int position) {
            //Returning the current tabs
            switch (position) {
                case 0:
                    PersonListFragment personListFragment=new PersonListFragment();
                    return personListFragment;
                case 1:
                    ComicListFragment comicListFragment=new ComicListFragment();
                    return comicListFragment;

                default:
                    return null;
            }
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        //Overriden method getCount to get the number of tabs
        @Override
        public int getCount() {
            return tabCount;
        }
    }
}
