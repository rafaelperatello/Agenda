package br.edu.ifspsaocarlos.agenda.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;

import br.edu.ifspsaocarlos.agenda.R;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.hide();

        Intent intent = getIntent();
        handleIntent(intent);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            buildSearchListView(query);

        }

    }
}
