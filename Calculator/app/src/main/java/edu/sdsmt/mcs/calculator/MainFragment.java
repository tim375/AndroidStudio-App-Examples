package edu.sdsmt.mcs.calculator;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class MainFragment extends Fragment implements View.OnClickListener
{

    private static final String PLUS = "+";

    private OnFragmentInteractionListener _listener;

    private EditText _resultsText;

    private String _workingNumber = "";

    public MainFragment()
    {
        // Required empty public constructor
    }


    @Override
    public void onClick(final View v)
    {
        switch (v.getId())
        {
            case R.id.buttonPlus:
            {
                // Do not allow multiple addition signs to be entered.
                if (_resultsText.getText().toString().endsWith(PLUS) == true)
                {
                    break;
                }

                // Add the PLUS string value to the results textbox.
                _resultsText.setText(_resultsText.getText() + PLUS);

                // Callback to the parent activity to specify the add button was pressed.
                if (TextUtils.isEmpty(_workingNumber) == false)
                {
                    _listener.onAddButtonPressed(Integer.valueOf(_workingNumber));
                    _workingNumber = "";
                }

                break;
            }
            case R.id.buttonEquals:
            {
                // Remove the last addition sign if one exists.
                if (_resultsText.getText().toString().endsWith(PLUS) == true)
                {
                    _resultsText.setText(_resultsText.getText().delete(_resultsText.length() - 1, _resultsText.length()));
                }

                if (TextUtils.isEmpty(_workingNumber) == false)
                {
                    _listener.onAddButtonPressed(Integer.valueOf(_workingNumber));
                    _workingNumber = "";
                }
                // Callback to the parent activity to specify the equals button was pressed.
                _listener.onEqualsButtonPressed();
                break;
            }
            case R.id.buttonClear:
            {
                _workingNumber = "";
                _resultsText.setText("");
                _listener.onClearButtonPressed();
                break;
            }
            default:
            {
                // Assume the correct value is associated with the text on the
                // button, so just get it and assign it to the edit text view.

                String tempValue = ((Button) v).getText().toString();
                _resultsText.setText(_resultsText.getText() + tempValue);

                _workingNumber = _workingNumber.concat(tempValue);

                break;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Without associating the buttons with a member variable, set the
        // onClick listener on each digit.
        rootView.findViewById(R.id.button0).setOnClickListener(this);
        rootView.findViewById(R.id.button1).setOnClickListener(this);
        rootView.findViewById(R.id.button2).setOnClickListener(this);
        rootView.findViewById(R.id.button3).setOnClickListener(this);
        rootView.findViewById(R.id.button4).setOnClickListener(this);
        rootView.findViewById(R.id.button5).setOnClickListener(this);
        rootView.findViewById(R.id.button6).setOnClickListener(this);
        rootView.findViewById(R.id.button7).setOnClickListener(this);
        rootView.findViewById(R.id.button8).setOnClickListener(this);
        rootView.findViewById(R.id.button9).setOnClickListener(this);

        rootView.findViewById(R.id.buttonPlus).setOnClickListener(this);
        rootView.findViewById(R.id.buttonEquals).setOnClickListener(this);

        rootView.findViewById(R.id.buttonClear).setOnClickListener(this);

        _resultsText = (EditText) rootView.findViewById(R.id.editTextResults);
        _resultsText.setCursorVisible(false);
        _resultsText.setFocusable(false);


        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            _listener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        _listener = null;
    }

}
