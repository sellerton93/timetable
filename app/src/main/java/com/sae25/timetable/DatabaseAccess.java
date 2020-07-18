package com.sae25.timetable;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseAccess  {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess dbAccess;
    Cursor c = null;


    private DatabaseAccess(Context context){
        this.openHelper=new DatabaseOpenHelper(context);

    }

    static DatabaseAccess getInstance(Context context){
        if(dbAccess==null){
            dbAccess=new DatabaseAccess(context);

        }
        return dbAccess;

    }


    void open(){
        this.db = openHelper.getWritableDatabase();
    }

    void close(){
        if(db!=null){
            this.db.close();
        }
    }

    ArrayList<Session> getSessions(){
        ArrayList<Session> sessions = new ArrayList<>();

        c=db.rawQuery("select sessions.id as id,\n" +
                "sessions.title,\n" +
                "sessions.content,\n" +
                "sessions.sessionDate,\n" +
                "sessions.timeStart,\n" +
                "sessions.timeEnd,\n" +
                "sessions.sessionType,\n" +
                "sessions.speakerId,\n" +
                "locations.name,\n" +
                "locations.id as lid,\n" +
                "locations.description,\n" +
                "locations.latitude,\n" +
                "locations.longitude\n" +
                "from sessions join locations on locations.id = sessions.locationId", null);
        c.moveToFirst();
        while((!(c.isAfterLast())) && (c !=null)){
                Session session = new Session();
                session.setSessionId(c.getString(c.getColumnIndex("id")) );
                session.setSessionTitle(c.getString(c.getColumnIndex("sessions.title")));
                session.setContent(c.getString(c.getColumnIndex("sessions.content")));
                session.setSessionDate(c.getString(c.getColumnIndex("sessions.sessionDate")));
                session.setSessionStartTime(c.getString(c.getColumnIndex("sessions.timeStart")));
                session.setSessionEndTime(c.getString(c.getColumnIndex("sessions.timeEnd")));
                session.setSessionType(c.getString(c.getColumnIndex("sessions.sessionType")));
                session.setSpeakerId(c.getString(c.getColumnIndex("sessions.speakerId")));
                session.setName(c.getString(c.getColumnIndex("name")));
                session.setLati(c.getString(c.getColumnIndex("latitude")));
                session.setLongi(c.getString(c.getColumnIndex("longitude")));
                session.setDesc(c.getString(c.getColumnIndex("description")));

                sessions.add(session);
                c.moveToNext();


        }return sessions;
    }

    HashMap<String,Speaker> getSpeakers(){
        Speaker speaker;
        HashMap<String,Speaker> Speakers = new HashMap<>();
        c=db.rawQuery("select * from speakers" ,null);
        c.moveToFirst();
        String id,bio,name,twitter;
        while((!(c.isAfterLast())) && (c !=null)){
            speaker = new Speaker();
            id = (c.getString(c.getColumnIndex("id")));
            bio = (c.getString(c.getColumnIndex("biography")));
            name = (c.getString(c.getColumnIndex("name")));
            twitter = (c.getString(c.getColumnIndex("twitter")));
            speaker.setBiography(bio);
            speaker.setTwitter(twitter);
            speaker.setName(name);
            speaker.setId(id);

            Speakers.put(id,speaker);
            c.moveToNext();

        }
        return Speakers;
    }

}
