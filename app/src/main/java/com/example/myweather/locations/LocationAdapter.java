package com.example.myweather.locations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myweather.R;
import com.example.myweather.View.WeatherActivity;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
        private List<Location> mLocationList;

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView locName;
            TextView parentLoc;
            View mLocationView;

            public ViewHolder(View view) {
                super(view);
                mLocationView = view;
                locName = view.findViewById(R.id.loc_name);
                parentLoc = view.findViewById(R.id.parent_loc);
            }
        }

        public LocationAdapter(List<Location>locationList){
            mLocationList = locationList;
        }

        @NonNull//三重写
        @Override
        public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location,parent,false);
            final ViewHolder holder= new ViewHolder(view);
            holder.mLocationView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position=holder.getAdapterPosition();
                    Location location = mLocationList.get(position);
                    SharedPreferences.Editor editor = v.getContext().getSharedPreferences("Weather", Context.MODE_PRIVATE).edit();
                    editor.putString("adress",location.getId());
                    editor.apply();
                    Intent intent = new Intent(v.getContext(),WeatherActivity.class);
                    intent.putExtra("reload",true);
                    v.getContext().startActivity(intent);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Location location=mLocationList.get(position);
            holder.locName.setText(location.getLocName());
            holder.parentLoc.setText(location.getParentLoc());
        }

        @Override
        public int getItemCount() {
            if(mLocationList!=null)
                return mLocationList.size();
            else return 0;
        }

}
