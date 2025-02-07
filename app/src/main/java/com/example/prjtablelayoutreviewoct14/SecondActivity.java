package com.example.prjtablelayoutreviewoct14;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.prjtablelayoutreviewoct14.model.Menu;

import java.util.Locale;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edDescription;
    Button btnReturn;
    RadioGroup radGrTextColor, radGrBackColor;
    RadioButton rbRed, rbGreen, rbMagenta, rbYellow, rbWhite;

    Menu aMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initialize();
    }

    private void initialize() {
        edDescription = findViewById(R.id.edDescription);
        btnReturn = findViewById(R.id.btnReturn);

        radGrBackColor = findViewById(R.id.radGrBackColor);
        radGrTextColor = findViewById(R.id.radGrTextColor);

        rbRed = findViewById(R.id.rbRed);
        rbGreen = findViewById(R.id.rbGreen);
        rbMagenta = findViewById(R.id.rbMagenta);
        rbYellow = findViewById(R.id.rbYellow);
        rbWhite = findViewById(R.id.rbWhite);

        aMenu = (Menu)getIntent().getSerializableExtra("selectedMenu");

        edDescription.setText(aMenu.toString());

        btnReturn.setOnClickListener(this);

        //for text color
        if(aMenu.getTextColor() == Color.GREEN){
            rbGreen.setChecked(true);
        }else if(aMenu.getTextColor() == Color.MAGENTA){
            rbMagenta.setChecked(true);
        }else{
            rbRed.setChecked(true);
        }

        //for background color
        if(aMenu.getBackgroundColor() == Color.YELLOW){
            rbYellow.setChecked(true);
        }else{
            rbWhite.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnReturn.getId()){
            String editedDescription = edDescription.getText().toString();
            Intent intent = new Intent();

            if(editedDescription.equals(aMenu.getDescription())&&
                isSameTextColor() && isSameBackgroundColor()){
                setResult(RESULT_CANCELED, intent);
            }else{
                aMenu.setDescription(editedDescription);
                assignSelectedColor();

                intent.putExtra("newMenu", aMenu);

                setResult(RESULT_OK,intent);
            }
            finish();
        }
    }

    private boolean isSameBackgroundColor() {
        RadioButton aRb = findViewById(radGrBackColor.getCheckedRadioButtonId());
        if(aMenu.getBackgroundColor() == getColorCode(aRb)){
            return true;
        }
        return false;
    }


    private boolean isSameTextColor() {
        RadioButton aRb = findViewById(radGrTextColor.getCheckedRadioButtonId());
        if(aMenu.getTextColor() == getColorCode(aRb)){
            return true;
        }
        return false;
    }

    /**
     *this will take a radioButton and returns its color in int
     * @return Color code as int
     */
    private int getColorCode(RadioButton aRb) {
        switch (aRb.getText().toString().toLowerCase()){
            case "red":
                return Color.RED;
            case "green":
                return Color.GREEN;
            case "magenta":
                return Color.MAGENTA;
            case "yellow":
                return Color.YELLOW;
            case "white":
                return Color.WHITE;
        }
        return 0;
    }


    private void assignSelectedColor() {
        //for text color
        if(rbRed.isChecked()){
            aMenu.setTextColor(Color.RED);
        }else if(rbGreen.isChecked()){
            aMenu.setTextColor(Color.GREEN);
        }else if(rbMagenta.isChecked()){
            aMenu.setTextColor(Color.MAGENTA);
        }

        //for background color
        if(rbYellow.isChecked()){
            aMenu.setBackgroundColor(Color.YELLOW);
        }else if(rbWhite.isChecked()){
            aMenu.setBackgroundColor(Color.WHITE);
        }
    }
}