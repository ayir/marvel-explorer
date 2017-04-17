package com.riya.marvel ;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.riya.marvel.PersonCursorAdapter;
import com.riya.marvel.db.Person;
import com.riya.marvel.db.YourHeroesContract;
import com.riya.marvel.service.YourHeroSearchService;
import com.riya.marvel.service.YourHeroesService;
import com.riya.marvel.utilities.CallBackItemSelection;
import com.riya.marvel.utilities.Tools;

import java.lang.*;
/**
 * Created by RiyaSharma on 16-04-2017.
 */

public class PersonListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener,
        TextView.OnEditorActionListener, LoaderCallbacks<Cursor> {

    private final String LOG_TAG = PersonListFragment.class.getSimpleName();
    public static boolean searchflag=false;
    private static final String PERSONS = "persons";

    private static final int PERSON_LOADER = 0;

    private PersonCursorAdapter personCursorAdapter;
    private TextView edSearch;
    private ImageView btSearch;
    private ListView listPerson;

    private ServiceResponseReceive serviceResponseReceive;
    private Dialog dialog;

    // Columns showed by the loader
    private static final String[] PERSON_COLUMNS = {
            YourHeroesContract.PersonEntry._ID,
            YourHeroesContract.PersonEntry.COLUMN_MARVEL_ID,
            YourHeroesContract.PersonEntry.COLUMN_NAME,
            YourHeroesContract.PersonEntry.COLUMN_DESCRIPTION,
            YourHeroesContract.PersonEntry.COLUMN_URLDETAIL,
            YourHeroesContract.PersonEntry.COLUMN_LANDSCAPESMALL,
            YourHeroesContract.PersonEntry.COLUMN_STANDARDXLARGE
    };
    public static final int COL_PERSON_ID = 0;
    public static final int COL_PERSON__MARVEL_ID = 1;
    public static final int COL_PERSON_NAME = 2;
    public static final int COL_PERSON_DESCRIPTION = 3;
    public static final int COL_PERSON_URLDETAIL = 4;
    public static final int COL_PERSON_LANDSCAPESMALL = 5;
    public static final int COL_PERSON_STANDARDXLARGE = 6;

    public PersonListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.personfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        personCursorAdapter = new PersonCursorAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        edSearch = (EditText) rootView.findViewById(R.id.edsearch);
       edSearch.setOnEditorActionListener(this);
        btSearch = (ImageView) rootView.findViewById(R.id.btsearch);
        btSearch.setOnClickListener(this);
        listPerson = (ListView) rootView.findViewById(R.id.listperson);
        listPerson.setAdapter(personCursorAdapter);
        listPerson.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PERSON_LOADER, null, this);
        Intent intent = new Intent(getActivity(), YourHeroesService.class);
        getActivity().startService(intent);


        dialog = Tools.dialogSpinner(getActivity(), getActivity().getString(R.string.searching));
        dialog.show();
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btsearch:
                searchflag=true;
                seachPerson(edSearch.getText().toString());
                break;
        }
    }

    public void seachPerson(String search) {
        Intent intent = new Intent(getActivity(), YourHeroSearchService.class);
        intent.putExtra(YourHeroSearchService.SEARCH_PARAM, search);
        getActivity().startService(intent);
        getLoaderManager().restartLoader(PERSON_LOADER, null, this);
        dialog = Tools.dialogSpinner(getActivity(), getActivity().getString(R.string.searching));
        dialog.show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = personCursorAdapter.getCursor();
        if (cursor != null) {
            Person person = new Person();
            person.setId(cursor.getInt(COL_PERSON_ID));
            person.setMarvelId(cursor.getInt(COL_PERSON__MARVEL_ID));
            person.setName(cursor.getString(COL_PERSON_NAME));
            person.setDescription(cursor.getString(COL_PERSON_DESCRIPTION));
            person.setURLDetail(cursor.getString(COL_PERSON_URLDETAIL));
            person.setLandscapeSmallImageUrl(cursor.getString(COL_PERSON_LANDSCAPESMALL));
            person.setStandardXLargeImageUrl(cursor.getString(COL_PERSON_STANDARDXLARGE));

            ((CallBackItemSelection) getActivity()).onItemSelected(person);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;

        if (v == edSearch) {
           // seachPerson(edSearch.getText().toString());

            // Hiding the soft keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edSearch.getWindowToken(), 0);

            handled = true;
        }
        return handled;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(serviceResponseReceive);
    }

    @Override
    public void onResume() {
        super.onResume();
        // registering the broadcast to receive signal of the service
        IntentFilter filter = new IntentFilter(ServiceResponseReceive.ACTION_SERVICE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        serviceResponseReceive = new ServiceResponseReceive();
        getActivity().registerReceiver(serviceResponseReceive, filter);

        if (!edSearch.getText().toString().equals("")) {
            getLoaderManager().restartLoader(PERSON_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uriSearch;
        uriSearch = YourHeroesContract.PersonEntry.getBaseUri();
        Log.d(LOG_TAG, "uriSearch: "+uriSearch.toString());

        return new CursorLoader(
                getActivity(),
                uriSearch,
                PERSON_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        personCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        personCursorAdapter.swapCursor(null);
    }

    public class ServiceResponseReceive extends BroadcastReceiver {

        public static final String ACTION_SERVICE = "com.riya.marvel.service.FINISHED";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (dialog != null)
                dialog.dismiss();
        }
    }

}
