package com.example.subtainishfaq.searchengine.fragments;



        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.KeyEvent;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import com.example.subtainishfaq.searchengine.Home;
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

        Button Google= (Button) v.findViewById(R.id.Google);
        Button Bing= (Button) v.findViewById(R.id.Bing);

        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ((Home) getActivity()).onBackPressed();
                    return true;
                }
                return false;
                //
            }
        });

        Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ( (Home) getActivity()).setFragmentId(1);

            }
        });
        Bing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ((Home) getActivity()).setFragmentId(2);
            }
        });
        /* task =  new RetrieveFeedTask();
            task.execute();*/




        return v;
    }




}