package com.example.subtainishfaq.watchimage.fragments;

/**
 * Created by subtainishfaq on 3/23/16.
 */
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.subtainishfaq.watchimage.Fullscreen;
import com.example.subtainishfaq.watchimage.Home;
import com.example.subtainishfaq.watchimage.R;

import com.example.subtainishfaq.watchimage.adapters.CustomListAdapter;
import com.example.subtainishfaq.watchimage.adapters.GridViewAdapter;
import com.example.subtainishfaq.watchimage.utility.GlobalSettings;
import com.example.subtainishfaq.watchimage.utility.TinyDB;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;

import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.groups.GroupList;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.Photosets;


import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CategoryFragment extends Fragment implements AdapterView.OnItemClickListener {



    ListView lv;
    Collection<Group> groupList=null;
   List<Photoset> groupLista=null;
    GridView gridList;
    ArrayList<String> urlPublic;
    ArrayList<String> names;
    SweetAlertDialog pDialog;
    private boolean isInflated=false;
    int countback=0;
    private TinyDB db;
    RetrieveFeedTask task1;
    private RetreivePhotos task2;
    private boolean isFromRefresh=false;
    ArrayList <String> grouids;


    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db=new TinyDB(getContext());
        View v=inflater.inflate(R.layout.category_fragment, container, false);

        lv=(ListView) v.findViewById(R.id.categoryList);
        lv.setOnItemClickListener(this);

        gridList= (GridView) v.findViewById(R.id.gridViewGroup);
        gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(GlobalSettings.isConnected)
                {
                    Intent intent = new Intent(getContext(), Fullscreen.class);
                    intent.putExtra("imageUrl", urlPublic.get(position));
                    intent.putExtra("name", names.get(position));
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getContext(), Fullscreen.class);
                    intent.putExtra("imageUrl", db.getListString(db.getListString("GROUPIDS").get(position)+"CATURLS").get(position));
                    intent.putExtra("name", db.getListString(db.getListString("GROUPIDS").get(position)+"CATNAMES").get(position));
                    startActivity(intent);
                }
            }
        });


        if(GlobalSettings.isConnected  && !db.getBoolean("NOTFIRSTCAT"))
        {
            task1=  new RetrieveFeedTask();
            task1.execute();
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
            db.putBoolean("NOTFIRSTCAT", true);
        }
        else
        {
            lv.setAdapter(new CustomListAdapter(getContext(), db.getListString("GROUPS")));
        }





        isInflated=true;

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&  event.getAction() == KeyEvent.ACTION_UP) {
                    countback++;
                    if(countback==2 || (lv.getVisibility()==View.VISIBLE))
                    {
                    ((Home) getActivity()).setCurrentHome();
                        countback=0;
                    }

                    lv.setVisibility(View.VISIBLE);
                    lv.setAdapter(new CustomListAdapter(getContext(), db.getListString("GROUPS")));
                    gridList.setVisibility(View.GONE);
                    return true;
                }
                return false;
                //
            }
        });
        return v;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(GlobalSettings.isConnected && isFromRefresh)  //netconnected but does not want to refresh
        {
        lv.setVisibility(View.GONE);
       task2= new RetreivePhotos();
       task2.execute(groupLista.get(position).getId());
            isFromRefresh=false;
        }
        else if(db.getBoolean(db.getListString("GROUPIDS").get(position)))
        {
       // db.putListString("CATURLS", feed)
       //
       //
            lv.setVisibility(View.GONE);
            gridList.setAdapter(new GridViewAdapter(getContext(), db.getListString(db.getListString("GROUPIDS").get(position)+"CATURLS")));
            gridList.setVisibility(View.VISIBLE);

        }
        else if(GlobalSettings.isConnected && !db.getBoolean(db.getListString("GROUPIDS").get(position)))
        {
            lv.setVisibility(View.GONE);
            task2= new RetreivePhotos();
            task2.execute(db.getListString("GROUPIDS").get(position));
        }


    }

    public void refresh()
    {
        if(isInflated) {
            task1=  new RetrieveFeedTask();
            task1.execute();
            isFromRefresh=true;

        }
    }


    class RetreivePhotos extends AsyncTask<String, Void, ArrayList<String> > {


String Prams;
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Flickr f=new Flickr(Cons.FlickerApi);
            PhotoList ph=null;
            try {
                ph=f.getPhotosetsInterface().getPhotos(params[0], 8, 1).getPhotoList();//pages
                Prams=params[0];
                int numberOfPages=ph.getPages();
                db.putBoolean(params[0],true);
                for (int i=2; i<=numberOfPages;i++)
                {
                    ph.addAll(f.getPhotosetsInterface().getPhotos(params[0], 8, i).getPhotoList());
                }
                Log.d("group",params[0]);
                //get pages
                //get perpage

            } catch (IOException e) {
                e.printStackTrace();
            } catch (FlickrException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList <String> urls=new ArrayList<String>();
            names=new ArrayList<String>();


            if(ph!=null)
            for (Photo photo:ph)
            {
                urls.add(photo.getMediumUrl());
                names.add(photo.getTitle());
                Log.d("group",photo.getTitle());
            }


            return   urls;
        }

        protected void onPostExecute(ArrayList<String> feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
            urlPublic = feed;
            gridList.setAdapter(new GridViewAdapter(getContext(), feed));
            gridList.setVisibility(View.VISIBLE);
            db.putListString(Prams+"CATURLS", feed);
            db.putListString(Prams+"CATNAMES", names);

        }
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, ArrayList<String> > {

        private Exception exception;


        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            Flickr f=new Flickr(Cons.FlickerApi);
            Photosets groupList=null;
            try {
                groupList=  f.getPhotosetsInterface().getList(Cons.UserFlicker);
                groupLista=new ArrayList<Photoset>(groupList.getPhotosets());
                grouids=new ArrayList<String>();

                //get pages
                //get perpage

            } catch (IOException e) {
                e.printStackTrace();
            } catch (FlickrException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList <String> urls=new ArrayList<String>();
           // names=new ArrayList<String>();

            for (Photoset photo:groupLista)
            {
                urls.add(photo.getTitle());
                Log.d("group", photo.getTitle());
                grouids.add(photo.getId());
               // names.add(photo.getTitle());
            }


            return   urls;
        }

        protected void onPostExecute(ArrayList<String> feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
           /* urlPublic=feed;
            gridList.setAdapter(new GridViewAdapter(getContext(),feed));*/

            lv.setAdapter(new CustomListAdapter(getContext(), feed));
            db.putListString("GROUPS", feed);
            db.putListString("GROUPIDS", grouids);
          if(pDialog!=null)
            pDialog.dismissWithAnimation();


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(task1!=null)
        task1.cancel(true);
        if(task2!=null)
        task2.cancel(true);
    }
}