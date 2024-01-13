package br.edu.ifspsaocarlos.agenda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import br.edu.ifspsaocarlos.agenda.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DetalheActivity.class);
                startActivityForResult(i, 0);

            }
        });

        buildListView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        buildListView();
        searchView.clearFocus();

    }
}
