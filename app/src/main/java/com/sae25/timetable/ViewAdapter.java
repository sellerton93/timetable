package com.sae25.timetable;


import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**

The function of this class is to adapt the session class into the cardview layout, to be inflated as a listview item of the main activity.


 https://developer.android.com/reference/android/widget/ArrayAdapter

 https://developer.android.com/reference/android/widget/Filterable



 */
public class ViewAdapter extends ArrayAdapter<Session> implements View.OnClickListener, Filterable {

    DatabaseAccess db;

    private Context context;
    private ArrayList<Session> sessions;
    private ArrayList<Session> allSessions;

    private static class ViewHolder {
        private TextView title, speakername, time, content, twitter, bio, locationName, locationDesc, sessionDate;
        private ImageView speakerImage, favourite;
        public int id;
    }


    public ViewAdapter(ArrayList<Session> sessions, Context context) {
        super(context, R.layout.cardview_layout, sessions);
        this.context = context;
        this.sessions = sessions;
        allSessions = new ArrayList<>(sessions);


    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Session session = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.cardview_layout, parent, false);
            convertView.setTag(viewHolder);

            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.time = convertView.findViewById(R.id.time);
            viewHolder.content = convertView.findViewById(R.id.content);
            viewHolder.locationName = convertView.findViewById(R.id.locationName);
            viewHolder.locationDesc = convertView.findViewById(R.id.locationDesc);
            viewHolder.sessionDate = convertView.findViewById(R.id.date);
            viewHolder.favourite = convertView.findViewById(R.id.favourite);
            viewHolder.twitter = convertView.findViewById(R.id.twitter);
            viewHolder.speakername = convertView.findViewById(R.id.speakername);
            viewHolder.bio = convertView.findViewById(R.id.bio);

            viewHolder.speakerImage = convertView.findViewById(R.id.speakerimage);


        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

            assert session != null;
            if ((session.getSessionType().equals("talk")) || (session.getSessionType().equals("workshop"))) {

                viewHolder.title.setText(session.getSessionTitle());
                viewHolder.speakername.setText(session.getSpeakerName());
                viewHolder.time.setText(session.getSessionStartTime());
                viewHolder.content.setText(session.getContent());
                viewHolder.bio.setText(session.getBiography());
                viewHolder.bio.setMovementMethod(new ScrollingMovementMethod());
                if(session.getTwitter() ==null){
                    viewHolder.twitter.setText("");
                }else {
                    viewHolder.twitter.setText("Twitter: @" + session.getTwitter());
                }
                viewHolder.locationName.setText(session.getName() + " (" + session.getLati() + " / " + session.getLongi() + ")");
                viewHolder.locationDesc.setText(session.getDesc());
                viewHolder.sessionDate.setText(session.getSessionDate());

                int id = context.getResources().getIdentifier(session.getSpeakerId().toLowerCase(), "drawable", context.getPackageName());
                viewHolder.speakerImage.setImageResource(id);
                if (session.isFavourite()){
                    viewHolder.favourite.setImageResource(R.drawable.full_fav);
                }else {
                    viewHolder.favourite.setImageResource(R.drawable.empty_fav);
                }
                viewHolder.favourite.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if ((!session.isFavourite())) {
                            viewHolder.favourite.setImageResource(R.drawable.full_fav);
                            session.setFavourite(true);
                            Favourites.addFavourite(session.getSessionId());

                            Toast.makeText(getContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();


                        } else {
                            viewHolder.favourite.setImageResource(R.drawable.empty_fav);
                            session.setFavourite(false);
                            Favourites.removeFavourite(session.getSessionId());
                            Toast.makeText(getContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            } else {
                viewHolder.speakerImage.setVisibility(GONE);
                viewHolder.speakerImage.getLayoutParams().height = 0;
                viewHolder.speakername.setVisibility(GONE);
                viewHolder.speakername.getLayoutParams().height = 0;
                viewHolder.twitter.setVisibility(GONE);
                viewHolder.twitter.getLayoutParams().height = 0;
                viewHolder.bio.setVisibility(GONE);
                viewHolder.bio.getLayoutParams().height = 0;
                viewHolder.title.setText(session.getSessionTitle());
                viewHolder.locationName.setText(session.getName() + " (" + session.getLati() + " / " + session.getLongi() + ")");
                viewHolder.locationDesc.setText(session.getDesc());
                viewHolder.sessionDate.setText(session.getSessionDate());
                viewHolder.content.setText(session.getContent());
                viewHolder.time.setText(session.getSessionStartTime());
            }
            convertView.setOnClickListener(this);


        return convertView;
    }

    @Override
    public void onClick(View view) {

        if (view.findViewById(R.id.content).getVisibility() == VISIBLE) {
            view.findViewById(R.id.content).setVisibility(GONE);
            view.findViewById(R.id.speakerimage).setVisibility(GONE);
            view.findViewById(R.id.twitter).setVisibility(GONE);
            view.findViewById(R.id.bio).setVisibility(GONE);
            view.findViewById(R.id.locationName).setVisibility(GONE);
            view.findViewById(R.id.locationDesc).setVisibility(GONE);

        } else {
            view.findViewById(R.id.content).setVisibility(VISIBLE);
            view.findViewById(R.id.speakerimage).setVisibility(VISIBLE);
            view.findViewById(R.id.twitter).setVisibility(VISIBLE);
            view.findViewById(R.id.bio).setVisibility(VISIBLE);
            view.findViewById(R.id.locationName).setVisibility(VISIBLE);
            view.findViewById(R.id.locationDesc).setVisibility(VISIBLE);

        }

    }


    public Filter getFilter(){
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Session> filterList = new ArrayList<>();

            if(charSequence ==null || charSequence.length() ==0){
                filterList.addAll(allSessions);
            }else{
                String query = charSequence.toString().toLowerCase().trim();

                for (Session s: allSessions
                     ) { if ((s.getSessionTitle().toLowerCase().contains(query)) || (s.getSpeakerName()!=null && s.getSpeakerName().toLowerCase().contains(query) )){
                         filterList.add(s);
                }

                }

            }
            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            sessions.clear();
            sessions.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    /*
     * These two override methods are to disable the recycling of views as the user scrolls up and down the list, which causes the data to become
     * muddled up between views.
     *
     * I'm aware that this is probably bad practice but I couldn't figure out how to fix the bug by other means.
     */

    @Override

    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
}


