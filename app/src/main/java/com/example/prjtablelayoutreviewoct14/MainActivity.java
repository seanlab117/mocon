package com.example.prjtablelayoutreviewoct14;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prjtablelayoutreviewoct14.model.Menu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView[] listOfTextViews;
    int widgets[] = {R.id.tvBreakfastMonTue, R.id.tvBreakfastWed, R.id.tvBreakfastThuFri,
                     R.id.tvLunchMon, R.id.tvLunchTue, R.id.tvLunchWed, R.id.tvLunchThu,
                     R.id.tvLunchFri, R.id.tvSnackMon, R.id.tvSnackTueWed, R.id.tvSnackThuFri };

    Menu[] listOfMenus;

    TextView clickedTv;

    //in order to return the result from another activity:
    //we need to launch the activity in a different way
    //3 steps:
    //1) declare ActivityResultLauncher
    //2) Register the activity result launcher
    //3) Launch the activity

    //step1: Declare
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        listOfTextViews = new TextView[widgets.length];
        listOfMenus = new Menu[widgets.length];

        listOfMenus[0] = new Menu(0,"Cheerios\n\nBanana\nMilk");
        listOfMenus[1] = new Menu(1,"Pancakes\n\nBlueberries\nMilk");
        listOfMenus[2] = new Menu(2,"Scrambled Eggs\n& Toast\nPotatoes\n100% Juice");
        listOfMenus[3] = new Menu(3,"Mashed\nPotatoes\nPeas & Corn\nBread & Butter\nMilk");
        listOfMenus[4] = new Menu(4,"Tuna Fish\nSandwich\nApplesauce\nCarrot Sticks\nMilk");
        listOfMenus[5] = new Menu(5,"Rice & Chicken\nStew\nCarrot &\nPotatoes\nMilk");
        listOfMenus[6] = new Menu(6,"Macaroni &\nCheese\nFish Sticks\nGreen Beans\nMilk");
        listOfMenus[7] = new Menu(7,"Whole Wheat\nPizza\nSpinach\nOrange Slices\nMilk");
        listOfMenus[8] = new Menu(8,"Crackers With\nPeanut Butter\n\n100% Juice");
        listOfMenus[9] = new Menu(9,"Yogurt\nRaisins /\nPeaches\n\n100% Juice");
        listOfMenus[10] = new Menu(10,"Home-made\nBlueberry\nMuffin\n\n100% Juice");

        for(int i=0;i<widgets.length;i++){
            listOfTextViews[i] = findViewById(widgets[i]);
            Menu aMenu = listOfMenus[i];

            listOfTextViews[i].setText(aMenu.toString());

            //listOfTextViews[i].setTextColor(aMenu.getTextColor());
            //listOfTextViews[i].setBackgroundColor(aMenu.getBackgroundColor());

            listOfTextViews[i].setOnClickListener(this);
        }

        //step2: Register
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if(result.getResultCode()==RESULT_OK && result.getData() != null){
                            Menu changedMenu = (Menu)result.getData().getSerializableExtra("newMenu");
                            clickedTv.setText(changedMenu.toString());
                            clickedTv.setTextColor(changedMenu.getTextColor());
                            clickedTv.setBackgroundColor(changedMenu.getBackgroundColor());
                        }else{
                            Toast.makeText(MainActivity.this, "No Change", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        clickedTv = (TextView)view;
        //Toast.makeText(this, clickedTv.getText(),Toast.LENGTH_SHORT).show();

        Menu aMenu = new Menu(1, clickedTv.getText().toString());
        aMenu.setTextColor(clickedTv.getCurrentTextColor());

        //here try catch is important because we can not convert our background to ColorDrawable first time
        //but if we already set in java then we can convert it into colorDrawable
        try{
            aMenu.setBackgroundColor(((ColorDrawable)clickedTv.getBackground()).getColor());
        }catch(Exception ex){
            clickedTv.setBackgroundColor(Color.WHITE);
            aMenu.setBackgroundColor(Color.WHITE);
        }

        ////simpler way of above line
//        ColorDrawable viewColor = (ColorDrawable) clickedTv.getBackground();
//        int colorId = viewColor.getColor();
//        aMenu.setBackgroundColor(colorId);

        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("selectedMenu", aMenu);

        activityResultLauncher.launch(intent);
    }
}