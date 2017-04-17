package com.riya.marvel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.riya.marvel.db.Comic;
import com.riya.marvel.db.Person;

/**
 * Created by RiyaSharma on 16-04-2017.
 */
public class DetailPersonActivity extends AppCompatActivity{

    private static final String LOG_TAG = DetailPersonActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_person);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {

            Person person = (Person) getIntent().getSerializableExtra("person");
            Comic comic=(Comic)getIntent().getSerializableExtra("comic");
            Bundle args = new Bundle();
            args.putSerializable("person", person);
            args.putSerializable("comic",comic);
            DetailPersonFragment detailFragment = new DetailPersonFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.person_container, detailFragment)
                    .commit();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_detail_person, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

                return super.onOptionsItemSelected(item);
        }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
