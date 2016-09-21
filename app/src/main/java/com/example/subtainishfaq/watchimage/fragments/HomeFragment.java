package com.example.subtainishfaq.watchimage.fragments;



        import android.content.Intent;
        import android.graphics.Color;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AbsListView;
        import android.widget.AdapterView;
        import android.widget.GridView;


        import com.example.subtainishfaq.watchimage.Fullscreen;
        import com.example.subtainishfaq.watchimage.R;
        import com.example.subtainishfaq.watchimage.adapters.GridViewAdapter;
        import com.example.subtainishfaq.watchimage.utility.Basic_Properties;
        import com.example.subtainishfaq.watchimage.utility.GlobalSettings;
        import com.example.subtainishfaq.watchimage.utility.TinyDB;
        import com.googlecode.flickrjandroid.Flickr;
        import com.googlecode.flickrjandroid.FlickrException;
        import com.googlecode.flickrjandroid.photos.PhotoList;

        import org.json.JSONException;

        import java.io.IOException;
        import java.util.ArrayList;

        import cn.pedant.SweetAlert.SweetAlertDialog;


public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    GridView gridList;
    ArrayList<String> urlPublic;
    ArrayList<String> names=new ArrayList<String>();;
     SweetAlertDialog pDialog;
    private boolean isInflated=false;
    View v;
    TinyDB db;
    RetrieveFeedTask task;
    private RetreivePhotos task2;
    GridViewAdapter Adapter;
    ArrayList <String> urls=new ArrayList<String>();
    private boolean userScrolled;
    private int preLast;
    private boolean isCategory=false;
    private String Category;


    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        db=new TinyDB(getContext());
        if(GlobalSettings.isConnected  && !db.getBoolean("NOTFIRST"))
        {
            v= inflater.inflate(R.layout.home_fragemtn, container, false);
        /* task =  new RetrieveFeedTask();
            task.execute();*/


            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
            gridList= (GridView) v.findViewById(R.id.gridView);
            gridList.setOnItemClickListener(this);

            isInflated=true;
            db.putBoolean("NOTFIRST",true);
        }

        else
        {
            v= inflater.inflate(R.layout.home_fragemtn, container, false);

            gridList= (GridView) v.findViewById(R.id.gridView);
            gridList.setOnItemClickListener(this);

            isInflated=true;
        }

        Adapter=new GridViewAdapter(HomeFragment.this.getContext(),urls);


            gridList.setOnScrollListener(this);
        gridList.setAdapter(Adapter);
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        task =  new RetrieveFeedTask();
        task.execute(1);
        isCategory=false;



        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        if(GlobalSettings.isConnected && !db.getBoolean("NOTFIRST")) {
            Intent intent = new Intent(getContext(), Fullscreen.class);
            intent.putStringArrayListExtra("imageUrls", urlPublic);
            intent.putStringArrayListExtra("names", names);
            intent.putExtra("position", position);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(getContext(), Fullscreen.class);
            intent.putExtra("imageUrls", db.getListString("URLS"));
            intent.putExtra("names", db.getListString("NAMES"));
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    public void refresh()
    {
        if(isInflated )
        {

            gridList.removeAllViews();
            urls.clear();
            names.clear();

           /* if(!isCategory)
            {
*/
                task =  new RetrieveFeedTask();
                task.execute(1);
            visibleThreshold = 6;
            currentPage = 0;
            previousTotal = 0;
            loading = true;
            preLast=0;
            isCategory=true;
            /*}
            else
                new RetreivePhotos().execute(Category);
*/

        }
    }

    public void openCategory(String cat)
    {


            if(GlobalSettings.isConnected )  //netconnected but does not want to refresh
            {

                urls.clear();
                names.clear();
                Adapter.notifyDataSetChanged();
                task2= new RetreivePhotos();
                task2.execute(cat);
                Category=cat;
                visibleThreshold = 6;
                currentPage = 0;
                previousTotal = 0;
                loading = true;
                preLast=0;
                isCategory=true;

            }






    }
    class RetreivePhotos extends AsyncTask<String, Void, ArrayList<String> > {


        String Prams;
        int numberOfPages;
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Flickr f=new Flickr(Basic_Properties.FlickerApi);
            loading=false;
            PhotoList ph=null;
            try {

                    if(currentPage==0 ) {

                        ph = f.getPhotosetsInterface().getPhotos(params[0], 6, 1).getPhotoList();//pages
                    }


                    if(currentPage<=f.getPhotosetsInterface().getPhotos(params[0], 6, 1).getPhotoList().getPages() && currentPage!=1)

                {
                    ph=f.getPhotosetsInterface().getPhotos(params[0], 6, currentPage).getPhotoList();
                }

                //get pages
                //get perpage

            }


                catch (IOException e) {
                e.printStackTrace();
            } catch (FlickrException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }



               if(ph!=null)
                for (int j = 0; j<ph.size(); j++)
                {
                    urls.add(ph.get(j).getMediumUrl());
                    names.add(ph.get(j).getTitle());
                }


            return   urls;
        }



        protected void onPostExecute(ArrayList<String> feed) {

            db.putListString("URLS", urlPublic);
            db.putListString("NAMES",names);
            loading=true;
            urlPublic = feed;
           Adapter.notifyDataSetChanged();
            pDialog.dismissWithAnimation();
            gridList.setVisibility(View.VISIBLE);



        }
    }


    class RetrieveFeedTask extends AsyncTask<Integer, Void, ArrayList<String> > {

        private Exception exception;
        boolean isFirst=false;


        @Override
        protected ArrayList<String> doInBackground(Integer... params) {
            loading=false;
            Flickr f=new Flickr(Basic_Properties.FlickerApi);
            PhotoList ph=null;

//

            try {
                if(params[0]==1)
                {


                ph=f.getPeopleInterface().getPublicPhotos(Basic_Properties.UserFlicker,6,1);//pages

                    Log.d(" Scrolling","executing"+ params[0]);
                for (int i=0; i<ph.size();i++)
                {
                    urls.add(ph.get(i).getMediumUrl());
                    names.add(ph.get(i).getTitle());

                }
                }
//                ph=f.getPeopleInterface().getPublicPhotos(Cons.UserFlicker,6,1);
                // int numberOfPAges= ph.getPages();


                    if(params[0]<=f.getPeopleInterface().getPublicPhotos(Basic_Properties.UserFlicker,6,1).getPages() && params[0]!=1)
                    {
                        ph=f.getPeopleInterface().getPublicPhotos(Basic_Properties.UserFlicker,6,params[0]);

                        for (int j = 0; j<ph.size(); j++)
                        {
                            urls.add(ph.get(j).getMediumUrl());
                            names.add(ph.get(j).getTitle());

                        }

                    }

                //get pages
                //get perpage

            } catch (IOException e) {
                e.printStackTrace();
            } catch (FlickrException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }




          //  publishProgress();

            return   urls;
        }



        protected void onPostExecute(ArrayList<String> feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
            urlPublic=feed;
            db.putListString("URLS", urlPublic);
            db.putListString("NAMES",names);
            loading=true;
            pDialog.dismissWithAnimation();
            Adapter.notifyDataSetChanged();




        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
   if(task!=null)
       task.cancel(true);
    }


    private int visibleThreshold = 6;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        Log.d("scroll", "scrolling "+loading);

        if (loading && userScrolled && firstVisibleItem + visibleItemCount >= totalItemCount )
          {
              if(preLast!= (firstVisibleItem + visibleItemCount))
              {
                  //to avoid multiple calls for last item

                  Log.d("scroll", "scrolling executed "+(currentPage + 1));
                  loading = false;
                  previousTotal = totalItemCount;
                  pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                  pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                  pDialog.setTitleText("Loading");
                  pDialog.setCancelable(false);
                  pDialog.show();

                  if(isCategory)
                  {
                      task2= new RetreivePhotos();
                      task2.execute(Category);
                  }
                  else
                  new RetrieveFeedTask().execute(currentPage + 1);

                  currentPage++;

                  preLast = firstVisibleItem + visibleItemCount;
              }



          }


    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {


        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
            userScrolled = true;
        }
    }
}