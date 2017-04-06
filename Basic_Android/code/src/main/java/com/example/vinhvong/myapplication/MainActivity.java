package com.example.vinhvong.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    private HashMap<Integer, Double> translate;
    private ArrayList<TextView> extras;

    private double calories_per_exercise = 100;

    private double reps_pushup = 350;
    private double reps_situps = 200;
    private double reps_squats = 225;

    private double mins_leglifts = 25;
    private double mins_planks = 25;
    private double mins_jumpingjacks = 10;

    private double reps_pullups = 100;
    private double mins_cycling = 12;
    private double mins_walking = 20;

    private double mins_jogging = 12;
    private double mins_swimming = 13;
    private double mins_climbing = 15;

    private RadioButton pushups;
    private RadioButton situps;
    private RadioButton squats;

    private RadioButton leglifts;
    private RadioButton planks;
    private RadioButton jumpingjackss;

    private RadioButton pullups;
    private RadioButton cycling;
    private RadioButton walking;

    private RadioButton jogging;
    private RadioButton swimming;
    private RadioButton climbing;

    private TextView extra0val;
    private TextView extra1val;
    private TextView extra2val;

    private TextView extra3val;
    private TextView extra4val;
    private TextView extra5val;

    private TextView extra6val;
    private TextView extra7val;
    private TextView extra8val;

    private TextView extra9val;
    private TextView extra10val;
    private TextView extra11val;

    private TextView caloriescalculated;
    private EditText exercisescalculated;
    private EditText number;

    private RadioButton curr_on;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pushups = (RadioButton) findViewById(R.id.pushups);
        situps = (RadioButton) findViewById(R.id.situps);
        squats = (RadioButton) findViewById(R.id.squats);

        leglifts = (RadioButton) findViewById(R.id.leglifts);
        planks = (RadioButton) findViewById(R.id.planking);
        jumpingjackss = (RadioButton) findViewById(R.id.jumpingjacks);

        pullups = (RadioButton) findViewById(R.id.pullups);
        cycling = (RadioButton) findViewById(R.id.cycling);
        walking = (RadioButton) findViewById(R.id.walking);

        jogging = (RadioButton) findViewById(R.id.jogging);
        swimming = (RadioButton) findViewById(R.id.swimming);
        climbing = (RadioButton) findViewById(R.id.stairclimbing);

        extra0val = (TextView) findViewById(R.id.extra0value);
        extra1val = (TextView) findViewById(R.id.extra1value);
        extra2val = (TextView) findViewById(R.id.extra2value);

        extra3val = (TextView) findViewById(R.id.extra3value);
        extra4val = (TextView) findViewById(R.id.extra4value);
        extra5val = (TextView) findViewById(R.id.extra5value);

        extra6val = (TextView) findViewById(R.id.extra6value);
        extra7val = (TextView) findViewById(R.id.extra7value);
        extra8val = (TextView) findViewById(R.id.extra8value);

        extra9val = (TextView) findViewById(R.id.extra9value);
        extra10val = (TextView) findViewById(R.id.extra10value);
        extra11val = (TextView) findViewById(R.id.extra11value);

        translate = new LinkedHashMap<>();

        translate.put(R.id.pushups, reps_pushup);
        translate.put(R.id.situps, reps_situps);
        translate.put(R.id.squats, reps_squats);

        translate.put(R.id.leglifts, mins_leglifts);
        translate.put(R.id.planking, mins_planks);
        translate.put(R.id.jumpingjacks, mins_jumpingjacks);

        translate.put(R.id.pullups, reps_pullups);
        translate.put(R.id.cycling, mins_cycling);
        translate.put(R.id.walking, mins_walking);

        translate.put(R.id.jogging, mins_jogging);
        translate.put(R.id.swimming, mins_swimming);
        translate.put(R.id.stairclimbing, mins_climbing);

        extras = new ArrayList<>();

        extras.add(extra0val);
        extras.add(extra1val);
        extras.add(extra2val);

        extras.add(extra3val);
        extras.add(extra4val);
        extras.add(extra5val);

        extras.add(extra6val);
        extras.add(extra7val);
        extras.add(extra8val);

        extras.add(extra9val);
        extras.add(extra10val);
        extras.add(extra11val);

        caloriescalculated = (TextView) findViewById(R.id.caloriescalculated);
        exercisescalculated = (EditText) findViewById(R.id.exercisescalculated);
        number = (EditText) findViewById(R.id.number);

        curr_on = null;



    }


    public void generalclick(View view) {

        if (curr_on == null) {
            curr_on = (RadioButton) findViewById(view.getId());
            curr_on.setChecked(true);
        } else {
            curr_on.setChecked(false);
            curr_on = (RadioButton) findViewById(view.getId());
        }

    }

    public void calculateCalories(View view) {

        String svalue = number.getText().toString();

        if (svalue.compareToIgnoreCase("") != 0 && curr_on != null) {

            exercisescalculated.setText("");

            int value = Integer.parseInt(svalue);

            double caloriesburned = calories_per_exercise * (value / this.translate.get(curr_on.getId()));

            caloriescalculated.setText(this.roundTwoDecimals(caloriesburned) + " calories were burned");

            this.burncalories(caloriesburned);

        }

    }

    public void calculateExercises(View view) {

        String svalue = exercisescalculated.getText().toString();

        if (svalue.compareToIgnoreCase("") != 0) {

            number.setText("");
            caloriescalculated.setText("");

            this.burncalories(Integer.parseInt(svalue));
        }

    }

    public void burncalories(double caloriesburned) {
        int count = 0;

        if (caloriesburned > calories_per_exercise) {
            Toast.makeText(this, "YOU GOT THIS!, GO GET SOME!!! HEHE", Toast.LENGTH_SHORT).show();
        }

        for (Integer val : translate.keySet()) {
            extras.get(count).setText(String.valueOf(roundTwoDecimals((caloriesburned / calories_per_exercise) * translate.get(val))));
            count += 1;
        }
    }


    public double roundTwoDecimals(double value) {
        DecimalFormat twoform = new DecimalFormat("#.##");
        return Double.valueOf(twoform.format(value));
    }


}
