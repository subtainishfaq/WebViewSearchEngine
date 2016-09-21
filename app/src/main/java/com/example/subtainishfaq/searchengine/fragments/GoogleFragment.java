package com.example.subtainishfaq.searchengine.fragments;

/**
 * Created by subtainishfaq on 3/23/16.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.subtainishfaq.searchengine.Home;
import com.example.subtainishfaq.searchengine.R;


public class GoogleFragment extends Fragment {


    private WebView webView;

    public GoogleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.google_fragment, container, false);

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&  event.getAction() == KeyEvent.ACTION_UP) {
                    ((Home) getActivity()).setFragmentId(0);

                    return true;
                }
                return false;
                //
            }
        });


        webView = (WebView) v.findViewById(R.id.webViewGoogle);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.google.com");
        return v;
    }



}