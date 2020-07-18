package com.sae25.timetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    HashMap<String, Speaker> Speakers;
    ArrayList<Session> sessions;
    DatabaseAccess dba;
    ListView listView;
    public static final String FILE_NAME = "Favourites.txt";
    public boolean showingFavourites;
    ViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("All Sessions");
        showingFavourites = false;
        dba = DatabaseAccess.getInstance(getApplicationContext());
        dba.open();
        sessions = dba.getSessions();
        Speakers = dba.getSpeakers();
        dba.close();

        loadFavs();
        ArrayList f = Favourites.getFavourites();
        for (Session s:sessions
        ) { if((Speakers.get(s.getSpeakerId())!=null)){
            if(f.contains(s.getSessionId())){
                s.setFavourite(true);
            }
            String speaker = Speakers.get(s.getSpeakerId()).getName();
            String bio = Speakers.get(s.getSpeakerId()).getBiography();
            String twitter = Speakers.get(s.getSpeakerId()).getTwitter();
            s.setSpeakerName(speaker);
            s.setBiography(bio);
            s.setTwitter(twitter);
        }
        }


        showAllSessions(sessions);


    }

    public void showAllSessions(ArrayList<Session> sessions){

        ActionBar ab = getSupportActionBar();
        findViewById(R.id.listView);
        listView = findViewById(R.id.listView);
        adapter = new ViewAdapter(sessions, getApplicationContext());
        listView.setAdapter(adapter);
        ab.setTitle("All Sessions");
    }

    public void showOnlyFavourites(ArrayList<Session> sessions){
        ArrayList f = Favourites.getFavourites();
        ArrayList<Session> favourites = new ArrayList<>();
        ActionBar ab = getSupportActionBar();
        if(!(f.isEmpty())) {
            for (Session s : sessions
            ) {
                if (f.contains(s.getSessionId())) {
                    favourites.add(s);
                }
            }

            findViewById(R.id.listView);
            listView = findViewById(R.id.listView);
            adapter = new ViewAdapter(favourites, getApplicationContext());
            listView.setAdapter(adapter);

            ab.setTitle("Favourites");
        }
        else{
            Toast.makeText(this.getApplicationContext(), "You have no favourites yet.", Toast.LENGTH_LONG).show();
            ab.setTitle("All Sessions");
        }

    }

    public void loadFavs(){
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while((text = br.readLine()) != null){
                Favourites.addFavourite(text);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis !=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void save(){
        FileOutputStream fos =null;
        ArrayList fav = Favourites.getFavourites();
        StringBuilder sb = new StringBuilder();
        String txt;
        for (Object f: fav
             ) {sb.append(f).append("\n");

        }
        txt = sb.toString();
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(txt.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(fos !=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_main,menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                adapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id ==R.id.action_favourites){

            if(!showingFavourites) {
                ArrayList f = Favourites.getFavourites();
                if(f.isEmpty()){
                    Toast.makeText(this.getApplicationContext(), "You have no favourites yet.", Toast.LENGTH_LONG).show();
                    item.setIcon(R.drawable.favourite_option_full);
                }else {
                    item.setIcon(R.drawable.favourite_option_empty);
                    showingFavourites = true;
                    showOnlyFavourites(sessions);
                }


            }else{
                item.setIcon(R.drawable.favourite_option_full);
                showingFavourites=false;
                showAllSessions(sessions);

            }
        }
        if(id == R.id.action_search){



        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save();


    }
}
