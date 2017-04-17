package com.riya.marvel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.riya.marvel.db.Comic;
import com.riya.marvel.db.Person;
import com.riya.marvel.utilities.ImageLoader;
import com.riya.marvel.utilities.Tools;
/**
 * Created by RiyaSharma on 16-04-2017.
 */

public class DetailPersonFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = DetailPersonFragment.class.getSimpleName();

    private TextView mName;
    private ImageView mImagePerson;
    private ProgressBar mProgress;
    private TextView mDescription;
    private TextView mMoreDetails;

    private Person person;
    private Comic comic;
    public DetailPersonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            person = (Person) args.getSerializable("person");
            comic=(Comic)args.getSerializable("comic");
        } else {
            person = null;
            comic=null;

        }

        View rootView = inflater.inflate(R.layout.fragment_detail_person, container, false);
        mName = (TextView) rootView.findViewById(R.id.fragment_detail_name);
        mImagePerson = (ImageView) rootView.findViewById(R.id.fragment_detail_image);
        mProgress = (ProgressBar) rootView.findViewById(R.id.fragment_detail_progress);
        mDescription = (TextView) rootView.findViewById(R.id.fragment_detail_description);
        mMoreDetails = (TextView) rootView.findViewById(R.id.seedetails);
        mMoreDetails.setOnClickListener(this);

        if (person != null) {
            mName.setText(person.getName());
            getActivity().setTitle(person.getName());
            mImagePerson.setTag(person.getStandardXLargeImageUrl());
            mDescription.setText(person.getDescription());

            ImageLoader imageLoader = new ImageLoader(getActivity());
            imageLoader.displayImage(person.getStandardXLargeImageUrl(), mImagePerson, mProgress);
        }else{
            mName.setText(comic.getName());
            getActivity().setTitle(comic.getName());
            mImagePerson.setTag(comic.getStandardXLargeImageUrl());
            mDescription.setText(comic.getDescription());

            ImageLoader imageLoader = new ImageLoader(getActivity());
            imageLoader.displayImage(comic.getStandardXLargeImageUrl(), mImagePerson, mProgress);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.seedetails) {
            Intent intent;
            if(person!=null) {
                if ((person.getURLDetail() == null) || (person.getURLDetail().equals("")))
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Tools.URL_MARVEL));
                else
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(person.getURLDetail())); //+ Tools.genKeyUser()));
            }
            else{
                if ((comic.getURLDetail() == null) || (comic.getURLDetail().equals("")))
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Tools.URL_MARVEL));
                else
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(comic.getURLDetail()));
            }
            startActivity(intent);

        }
    }

}
