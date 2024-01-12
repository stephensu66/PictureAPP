package com.example.myapplication;

import static android.R.layout.simple_spinner_item;
import static com.example.myapplication.R.id.*;
import static com.example.myapplication.R.id.button;

import static com.example.myapplication.R.id.gridLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R.id;
import com.google.android.material.chip.Chip;

import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class ChordActivity2 extends AppCompatActivity {


    //private SparseArray<Integer> buttonImageMap = new SparseArray<>();//define the map of images of the chordButtons
    //private HashMap<Integer, String> buttonTextMap = new HashMap<>();

    private HashMap<String, List<Integer>> savedSelections = new HashMap<>();//define the savebutton as a map
    private ArrayAdapter<String> spinnerAdapter;
    private int saveCounter = 1;
    private int displayTime = 1;
    private Queue<Button> selectedButtons = new LinkedList<>();
    private int columnCount = 4;// multiple button within gridlayout
    private int rowCount = 20;
    private GridLayout gridLayout;
    private Button saveButton;
    private Button resetButton;
    private Spinner spinner;
    private Switch showImageSwitch;
    private Log log;

    private Map<String, Integer> buttonImageMap;
    private ArrayList<String> nameArrayList;
    int nameIndex = 0; //count the name in the loop of naming btttons

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord2);


        //buttonImageMap.put(R.id.button11, R.drawable.image1);
        //buttonTextMap.put(id.button9, button9.getText().toString());
        loadButtonImageMap();//calling the method of load image and name to button

        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        gridLayoutButton();//calling the gridlayout method

        saveButton = (Button) findViewById(id.saveButton);
        saveButton.setOnClickListener(v -> saveCurrentSelection());//calling the method of saveCurrentSelection

        spinner = (Spinner) findViewById(id.spinner);
        spinnerAdapterSelection();//calling the method of spinnerAdapterSelection

        resetButton = (Button) findViewById(id.resetButton);
        resetButton.setOnClickListener(v -> resetSelection());//callint the method of reset selection

        showImageSwitch =  findViewById(show_images_swithch);
        setDisplaceTime(); // calling the method of displaytime


    }
    // method of reading json file to map
    private void loadButtonImageMap() {
        buttonImageMap = new HashMap<>();
        nameArrayList = new ArrayList<>();
        try {
            InputStream is = getAssets().open("buttonNameImages.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");

                nameArrayList.add(name);
                String imageFile = obj.getString("image");
                int imageResId = getResources().getIdentifier(imageFile, "drawable", getPackageName());
                buttonImageMap.put(name, imageResId);

            }
        }catch (Exception e){
            e.printStackTrace();
            }
    }



    //the method of reset selection
    private void resetSelection() {
        for (int i = 0; i < gridLayout.getChildCount();i++){
            View view = gridLayout.getChildAt(i);
            if(view instanceof Button){
                Button button = (Button) view;
                button.setSelected(false);
                button.setBackgroundColor(Color.GRAY);
            }
        }
    }

    private void spinnerAdapterSelection() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                applySavedSelection(name);//calling the applySavedSelection method
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    // the method of apply savedSelection from the spinnerv
    private void applySavedSelection(String name) {

        List<Integer> selectedButtonIds = savedSelections.get(name);
        if (selectedButtonIds != null){
            for (int i = 0; i < gridLayout.getChildCount(); i++){
                View view = gridLayout.getChildAt(i);
                if (view instanceof Button){
                    view.setSelected(selectedButtonIds.contains(view.getId()));
                    view.setBackgroundColor(view.isSelected() ? Color.GREEN : Color.GRAY);
                }
            }
        }
    }

    //method of remembering the selected buttons and set up the name
    private void saveCurrentSelection() {
        String name = "Selection" + saveCounter;
        saveCounter++;
        List<Integer> selectedButtonsIDs = new ArrayList<>();

        for (int i = 0; i < gridLayout.getChildCount(); i++){
            View view = gridLayout.getChildAt(i);
            if (view instanceof Button && view.isSelected()){
                selectedButtonsIDs.add(view.getId());
            }
        }

        savedSelections.put(name,selectedButtonsIDs);
        spinnerAdapter.add(name);
        spinnerAdapter.notifyDataSetChanged();//???

        //log.d("SaveButton","save button clicked");

    }

    //the method of gridLayoutButton
    private void gridLayoutButton(){
        //creat the first row of empty chip button
        for(int c = 0; c < columnCount;c++ ){
            Chip chip = new Chip(this);
            chip.setTag(column_tag, c);
            chip.setTag(row_tag,0);
            chip.setBackgroundColor(Color.GRAY);

            GridLayout.LayoutParams layoutParams= new GridLayout.LayoutParams();
            layoutParams.rowSpec = GridLayout.spec(0);
            layoutParams.columnSpec = GridLayout.spec(c);
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.setGravity(Gravity.CENTER);
            layoutParams.setMargins(10,10,10,10);
            chip.setLayoutParams(layoutParams);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick((Button)v, gridLayout, 0);
                }
            });
            gridLayout.addView(chip); //add the chip to the gridLayout
        }
        //creat the rest row of more than 0 of button
        for (int r = 1; r <= rowCount; r++){
            //name in json file is limited and stop when necessary. note!!!: .size()-1
            if(nameIndex > nameArrayList.size()-1){
                break;
            }

            for (int c= 0; c< columnCount; c++){
                //name in json file is limited and stop when necessary. note!!!: .size()-1
                if(nameIndex > nameArrayList.size()-1){
                    break;
                }
                Button button = new Button(this);
                //set up the buttons' propertie
                button.setTag(row_tag, r);//set the tag of button to control for the first row buttons' control
                button.setTag(column_tag, c);

                String buttonName = nameArrayList.get(nameIndex);//set the name from json to button text
                button.setText(buttonName);
                nameIndex++;
                //set the image from the json link to the button
                if(buttonImageMap.containsKey(buttonName)){
                    int imageResId = buttonImageMap.get(buttonName);
                    button.setBackgroundResource(imageResId);
                    button.setTag(R.id.image_tag, imageResId); // Set tag for image resource ID
                }
                button.setBackgroundColor(Color.GRAY);

                //set button layout parameters// layoutparams if necessary
                GridLayout.LayoutParams layoutParams= new GridLayout.LayoutParams();
                layoutParams.rowSpec = GridLayout.spec(r);
                layoutParams.columnSpec = GridLayout.spec(c);
                layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.setMargins(10,10,10,10);
                button.setLayoutParams(layoutParams);
                //set up buttons' onclick features
                int finalR = r;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleButtonClick((Button)v, gridLayout, finalR);//calling the method of onclick
                    }
                });
                gridLayout.addView(button);

            }
        }
    }
    //the method of onclick of the button
    private void handleButtonClick(Button clickedButton, GridLayout gridLayout, int clickedRow) {
        //check if the first row
        if(clickedRow == 0){
            int clickedColumn = (int) clickedButton.getTag(column_tag);
            handleColumnSelection(clickedColumn, gridLayout); //calling the method of selecting all column through first row
        }else{
            toggleButtonState(clickedButton);
            updateQueue(clickedButton);
        }

    }

    private void handleColumnSelection(int clickedColumn, GridLayout gridLayout) {
        for (int i = 0; i< gridLayout.getChildCount(); i++) {
            View view = gridLayout.getChildAt(i);
            if (view instanceof Button && view.getTag(column_tag).equals(clickedColumn)) {
                Button button = (Button) view;
                toggleButtonState(button);
                updateQueue(button);//calling the method of updateQueue
            }
        }
    }


    private void updateQueue(Button button) {
        if (button.isSelected()){
            selectedButtons.add(button);
        }else {
            selectedButtons.remove(button);
        }
    }

    private void toggleButtonState(Button button) {
        button.setSelected(!button.isSelected());
        button.setBackgroundColor(button.isSelected() ? Color.GREEN : Color.GRAY);
    }



    //the method of setting up the displaytime
    private void setDisplaceTime(){
        SeekBar timeSeekBar = findViewById(R.id.timeSeekBar);
        TextView timeTextView = findViewById(R.id.timeTextView);
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                displayTime = progress;
                timeTextView.setText(displayTime + " s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //onclick on the play button to launch a new activity
    public void onClickPlay(View view) {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> images = new ArrayList<>();

        for (Button button : selectedButtons){
            names.add(button.getText().toString());
            images.add((Integer) button.getTag(R.id.image_tag));
        }

        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putStringArrayListExtra("names", names);
        intent.putIntegerArrayListExtra("images", images);
        intent.putExtra("showImagesIf", showImageSwitch.isChecked());

        //pass the displaytime of the seekbar chosen to the displayActivity
        intent.putExtra("DisplayTime", displayTime);
        startActivity(intent);
    }


}