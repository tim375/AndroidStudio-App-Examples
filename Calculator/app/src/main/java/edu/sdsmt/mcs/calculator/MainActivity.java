package edu.sdsmt.mcs.calculator;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity implements OnFragmentInteractionListener
{

    private long _total = 0;

    @Override
    public void onAddButtonPressed(int value)
    {
//        DEBUG:
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("ADD");
//        builder.setMessage("are you there");
//
//        builder.create().show();

        _total += value;
    }

    @Override
    public void onEqualsButtonPressed()
    {
        getFragmentManager()
          .beginTransaction()
          .replace(R.id.container, ResultsFragment.newInstance(String.valueOf(_total)))
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .addToBackStack(null)
          .commit();
    }

    @Override
    public void onClearButtonPressed()
    {
        _total = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null)
        {
            getFragmentManager()
              .beginTransaction()
              .add(R.id.container, new MainFragment())
              .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
              .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
