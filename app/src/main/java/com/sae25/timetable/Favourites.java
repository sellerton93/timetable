package com.sae25.timetable;


import java.util.ArrayList;

public  class Favourites {

    static ArrayList<String> favourites;

    public static void addFavourite(String id){

        if(favourites == null){
            favourites = new ArrayList<String>();
        }

        if (!(favourites.contains(id))){
            favourites.add(id);
        }
    }


    public static void removeFavourite(String id){

        if(favourites == null){
            favourites = new ArrayList<String>();
        }

        if ((favourites.contains(id))){
            favourites.remove(id);
        }
    }

    public static ArrayList getFavourites(){
        if(favourites ==null){
            favourites = new ArrayList<String>();
        }
        return favourites;
    }
}
