package com.example.subtainishfaq.searchengine.fragments;



        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;


        import com.example.subtainishfaq.searchengine.R;


public class HomeFragment extends Fragment  {




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

         View   v = inflater.inflate(R.layout.home_fragemtn, container, false);
        /* task =  new RetrieveFeedTask();
            task.execute();*/




        return v;
    }




}