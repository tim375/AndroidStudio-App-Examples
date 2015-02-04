package edu.sdsmt.mcs.calculator;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsFragment extends Fragment
{

    private static final String ARG_TOTAL = "arg_total";

    private TextView _textViewResults;

    private String _totalValue;

    public ResultsFragment()
    {
        // Required empty public constructor
    }

    public static ResultsFragment newInstance(String total)
    {
        ResultsFragment fragment = new ResultsFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TOTAL, total);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            _totalValue = getArguments().getString(ARG_TOTAL);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);

        _textViewResults = (TextView) rootView.findViewById(R.id.textViewResults);
        _textViewResults.setText(_totalValue);

        return rootView;
    }


}
