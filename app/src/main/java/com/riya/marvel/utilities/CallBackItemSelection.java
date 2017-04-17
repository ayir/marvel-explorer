package com.riya.marvel.utilities;

import com.riya.marvel.db.Comic;
import com.riya.marvel.db.Person;
/**
 * Created by RiyaSharma on 16-04-2017.
 */
public interface CallBackItemSelection {

    public void onItemSelected(Person person);
    public void  onItemSelected(Comic comic);

}
