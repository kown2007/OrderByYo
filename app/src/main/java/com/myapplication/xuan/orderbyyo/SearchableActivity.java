package com.myapplication.xuan.orderbyyo;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SearchableActivity extends AppCompatActivity {
    TextView txt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        txt1 = (TextView) findViewById(R.id.txt1);

        // 注意這一行指令
        handleIntent(getIntent());
    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra(SearchManager.QUERY);
            txt1.setText("傳遞的查詢字串為 "+query.toString());
        }
    }
}
