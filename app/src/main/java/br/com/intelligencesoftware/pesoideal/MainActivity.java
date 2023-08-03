package br.com.intelligencesoftware.pesoideal;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText editTextHeight;
    private EditText editTextWeight;
    private RadioGroup radioGroupGender;
    private Button buttonCalculate;
    private TextView textViewAlertMessage;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        textViewAlertMessage = findViewById(R.id.textViewAlertMessage);
        textViewResult = findViewById(R.id.textViewResult);

        // Show the alert message
        textViewAlertMessage.setVisibility(View.INVISIBLE);

        // Show an AlertDialog with the alert message
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage(textViewAlertMessage.getText())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();

        // Set InputFilters to block comma input and handle max values
        editTextHeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(), new MaxValueInputFilter(2.5)});
        editTextWeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(), new MaxValueInputFilter(300)});

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    calculateBMI();
                } else {
                    textViewResult.setText("Por favor, preencha todos os campos e selecione uma opção de gênero.");
                }
            }
        });
    }

    private boolean isFormValid() {
        String heightText = editTextHeight.getText().toString().trim();
        String weightText = editTextWeight.getText().toString().trim();
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();

        return !heightText.isEmpty() && !weightText.isEmpty() && selectedGenderId != -1;
    }

    private void calculateBMI() {
        String heightText = editTextHeight.getText().toString().trim();
        String weightText = editTextWeight.getText().toString().trim();

        double height = Double.parseDouble(heightText);
        double weight = Double.parseDouble(weightText);

        double bmi = weight / (height * height);

        String result;

        if (bmi < 18.5) {
            result = "Abaixo do peso";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            result = "Peso normal";
        } else if (bmi >= 24.9 && bmi < 29.9) {
            result = "Sobrepeso";
        } else {
            result = "Obeso";
        }

        int selectedGender = radioGroupGender.getCheckedRadioButtonId();
        RadioButton radioButtonGender = findViewById(selectedGender);
        String gender = radioButtonGender.getText().toString();

        if (gender.equals("Masculino")) {
            result = "Para um homem, o resultado é: " + result;
        } else {
            result = "Para uma mulher, o resultado é: " + result;
        }

        textViewResult.setText(result);
    }

    // Custom InputFilter to block comma input
    public class DecimalDigitsInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String allowedChars = "0123456789.";
            for (int i = start; i < end; i++) {
                if (!allowedChars.contains(String.valueOf(source.charAt(i)))) {
                    return "";
                }
            }
            return null;
        }
    }

    // Custom InputFilter to handle max values
    public class MaxValueInputFilter implements InputFilter {
        private double maxValue;

        public MaxValueInputFilter(double maxValue) {
            this.maxValue = maxValue;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                double input = Double.parseDouble(dest.toString() + source.toString());
                if (input > maxValue) {
                    return "";
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
            return null;
        }
    }
}
