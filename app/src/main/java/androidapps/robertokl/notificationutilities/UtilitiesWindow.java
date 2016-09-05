package androidapps.robertokl.notificationutilities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import bsh.EvalError;
import bsh.Interpreter;

public class UtilitiesWindow extends AppCompatActivity {

    private static Spinner spinnerOperationType;
    private static LinearLayout linearLayoutMath;
    private static LinearLayout linearLayoutUnit;
    private static LinearLayout linearLayoutCurrency;
    private static EditText editTextExpression;
    private static TextView textViewExpression;
    private static EditText editTextUnit1;
    private static Spinner spinnerUnit1;
    private static EditText editTextUnit2;
    private static Spinner spinnerUnit2;
    private static final String[] LENGTH_UNITS = new String[] {
            "km", "m", "cm", "mm", "mile", "yard", "ft", "inch"
    };
    private static Interpreter interpreter = new Interpreter();
    private static Button buttonClose;
    private static ClipboardManager clipboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public static void onCreate(View view, final FloatingWindow floatingWindow) {
        spinnerOperationType = (Spinner) view.findViewById(R.id.spinnerOperationType);
        linearLayoutMath = (LinearLayout) view.findViewById(R.id.linearLayoutMath);
        linearLayoutUnit = (LinearLayout) view.findViewById(R.id.linearLayoutUnit);
        linearLayoutCurrency = (LinearLayout) view.findViewById(R.id.linearLayoutCurrency);
        editTextExpression = (EditText) view.findViewById(R.id.editTextExpression);
        textViewExpression = (TextView) view.findViewById(R.id.textViewExpression);
        editTextUnit1 = (EditText) view.findViewById(R.id.editTextUnit1);
        spinnerUnit1 = (Spinner) view.findViewById(R.id.spinnerUnit1);
        editTextUnit2 = (EditText) view.findViewById(R.id.editTextUnit2);
        spinnerUnit2 = (Spinner) view.findViewById(R.id.spinnerUnit2);
        buttonClose = (Button) view.findViewById(R.id.buttonClose);
        clipboard = floatingWindow.getClipboardService();

        ArrayAdapter<CharSequence> operationTypeAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.operation_types, android.R.layout.simple_spinner_item);
        operationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOperationType.setAdapter(operationTypeAdapter);
        spinnerOperationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                linearLayoutMath.setVisibility(View.GONE);
                linearLayoutUnit.setVisibility(View.GONE);
                linearLayoutCurrency.setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        linearLayoutMath.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        linearLayoutUnit.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        linearLayoutCurrency.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_dropdown_item_1line, LENGTH_UNITS);
        spinnerUnit1.setAdapter(unitAdapter);
        spinnerUnit2.setAdapter(unitAdapter);

        editTextExpression.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String expr = editTextExpression.getText().toString();
                if(expr.length() > 0 && !expr.matches(".*[a-z]+.*")) {
                    try {
                        Object answer = interpreter.eval(expr);
                        textViewExpression.setTextColor(Color.WHITE);
                        textViewExpression.setText(answer.toString());
                    } catch (EvalError evalError) {
                        setErr();
                    }
                } else {
                    setErr();
                }
            }

            public void setErr() {
                textViewExpression.setTextColor(Color.RED);
                textViewExpression.setText("Err.");
            }
        });

        textViewExpression.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData clip = ClipData.newPlainText("Notification Utilities Math Expression Result", textViewExpression.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingWindow.hide();
            }
        });
    }
}
