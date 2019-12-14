package com.vivekmohanan.finalassignment.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.vivekmohanan.finalassignment.R;
import com.vivekmohanan.finalassignment.models.SensorValues;
import com.vivekmohanan.finalassignment.adapters.ViewHistoryAdapter;

public class ViewHistory extends AppCompatActivity {

    java.util.ArrayList<SensorValues> ArrayList;
    private RecyclerView history;
    private RecyclerView.Adapter historyAdapter;
    private RecyclerView.LayoutManager historyLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        history = findViewById(R.id.recyclerView_History);

        Bundle c = this.getIntent().getExtras();
        ArrayList = c.getParcelableArrayList( "Sensor Values");

        historyLayoutManager = new LinearLayoutManager(this);
        historyAdapter = new ViewHistoryAdapter(ArrayList);

        history.setLayoutManager(historyLayoutManager);
        history.setAdapter(historyAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
