package com.example.sensoractivity;

import static com.example.sensoractivity.SensorDetailsActivity.EXTRA_SENSOR_TYPE_PARAMETER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SensorActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private RecyclerView recyclerView;
    private SensorAdapter adapter = null;
    private boolean countVisible = false;
    private final List<Integer> sensorsWithDetails = Arrays.asList(Sensor.TYPE_LIGHT, Sensor.TYPE_AMBIENT_TEMPERATURE);
    public static final int SENSOR_DETAILS_ACTIVITY_REQUEST_CODE = 1;
    private final int magnetometer = Sensor.TYPE_MAGNETIC_FIELD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager (new LinearLayoutManager(this));

        if (adapter == null){
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        }
        else adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        countVisible = !countVisible;
        String string = getString(R.string.sensors_count, sensorList.size());
        if (countVisible)getSupportActionBar().setSubtitle(string);
        else getSupportActionBar().setSubtitle(null);
        return true;
    }

    public class SensorHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView iconView;

        public SensorHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.sensor_name);
            iconView  = itemView.findViewById(R.id.sensor_image);
        }

        public void bind(Sensor sensor) {
            nameTextView.setText(sensor.getName());
            iconView.setImageResource(R.drawable.ic_sensor);
            View itemContainer = itemView.findViewById(R.id.sensor_item);

            if (magnetometer == sensor.getType()){
                itemContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(SensorActivity.this, LocationActivity.class);
                    intent.putExtra(EXTRA_SENSOR_TYPE_PARAMETER, sensor.getType());
                    startActivityForResult(intent, SENSOR_DETAILS_ACTIVITY_REQUEST_CODE);
                });
            }

            if (sensorsWithDetails.contains(sensor.getType())) {
                itemContainer.setBackgroundColor(getResources().getColor(R.color.purple_200));  //print green colour if this 2 will be disoplayed
                itemContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                    intent.putExtra(EXTRA_SENSOR_TYPE_PARAMETER, sensor.getType());
                    startActivityForResult(intent, SENSOR_DETAILS_ACTIVITY_REQUEST_CODE);
                });
            }
        }


    }


    class SensorAdapter extends  RecyclerView.Adapter<SensorHolder> {
        private List<Sensor> sensors;

        public SensorAdapter(List<Sensor> sens) {
            sensors = sens;
        }

        @Override
        public SensorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.sensor_list_item, parent, false);//////////

            SensorHolder sensorHolder = new SensorHolder(contactView);
            return sensorHolder;
        }

        @Override
        public void onBindViewHolder(SensorHolder holder, int position) {
            Sensor sns = sensors.get(position);
            ImageView imgView = holder.iconView;
            imgView.setImageResource(R.drawable.ic_sensor);
            TextView textView = holder.nameTextView;
            textView.setText(sns.getName());
            Logger logger = Logger.getLogger(SensorActivity.class.getName());
            logger.log(Level.WARNING, "Sensor information: " + sns.getName() + " " + sns.getVendor() + " " + sns.getMaximumRange());
            holder.bind(sns);
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }
    }
}