package com.itti7.itimeu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itti7.itimeu.data.ItemContract;
import com.itti7.itimeu.data.ItemDbHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

    /*Setting UI*/
    public static final String PREFNAME = "pref";
    public static int SESSION=4;
    private TextView mTimeText;
    private TextView mItemNameText;

    private ProgressBar mProgressBar;
    private Button mStateBttn;
    /*timer Service Component*/
    private TimerService mTimerService;
    boolean mBound = true;
    private TimerHandler handler;
    private int progressBarValue = 0;
    public int runTime; // minute
    /*timer calc*/
    private Intent intent;
    private ServiceConnection conn;
    private Thread mReadThread;
    /*store  time count*/
    private int mCountTimer;

    // Item info come from ListView
    private int mId;
    private int mStatus;
    private int mUnit;
    private int mTotalUnit;
    private String mName;

    // For access ITimeU database
    ItemDbHelper dbHelper;
    SQLiteDatabase db;
    String query;

    public TimerFragment() {
        // Required empty public constructor
    }

    BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("TimerFragment", "------------------------------------------------------->TimerFragment onCreateView()");
        View timerView = inflater.inflate(R.layout.fragment_timer, container, false);

        // get Timer tag and set to TimerTag
        String timerTag = getTag();
        ((MainActivity) getActivity()).setTimerTag(timerTag);

        //get ItemDbHelper to get SQLITEDB.getWritableDB()
        dbHelper = new ItemDbHelper(getActivity());

        mItemNameText = timerView.findViewById(R.id.job_name_txt);
        /*progressBar button init*/
        mProgressBar = (ProgressBar) timerView.findViewById(R.id.progressBar);
        mStateBttn = (Button) timerView.findViewById(R.id.state_bttn_view);
        mStateBttn.setOnClickListener(stateChecker);
        mStateBttn.setEnabled(false);
        /*Time Text Initialize */
        mTimeText = (TextView) timerView.findViewById(R.id.time_txt_view);
        /*progressBar button init*/
        mProgressBar = (ProgressBar) timerView.findViewById(R.id.progressBar);
        mProgressBar.bringToFront(); // bring the progressbar to the top

        /*동적 리시버 구현 */
        mReceiver =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("TimerFragment", "------------------------------------------------------->TimerFragment onReceive()");

                // UPDATE mCountTimner range 1..8
                // if Long Break Time has just finished, change to 1
                if (mCountTimer == (SESSION*2))
                    mCountTimer = 1;
                else
                    mCountTimer++;

                SharedPreferences pref = getActivity().getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("COUNT", mCountTimer);
                editor.commit();

                //change the button text to 'start'
                mStateBttn.setText("start");

                //set the ListItemText for the next session
                if (mCountTimer % 2 == 1)
                    mItemNameText.setText(mName);
                else {
                    mUnit++; //if the last session WAS work ,increase mUnit
                    if (mCountTimer % 8 == 0)
                        mItemNameText.setText("Long Break Time");
                    else
                        mItemNameText.setText("Break Time");
                }

                //store mUnit and mStatus

                db = dbHelper.getWritableDatabase();
                query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET unit = '" + mUnit + "', status = '";
                // if all the units are  completed
                if (mUnit==mTotalUnit) {
                    //UPDATE DB  mStatus = 2
                    query = query + ItemContract.ItemEntry.STATUS_DONE + "' WHERE _ID = '" + mId + "';";
                    // if the last break of the list just end go back to the listFragment
                    if (mCountTimer == mUnit * 2) {
                        //if finished, set the button disable
                        mStateBttn.setEnabled(false);
                        // Change Fragment TimerFragment -> ListItemFragment ->
                        MainActivity mainActivity = (MainActivity) getActivity();
                        (mainActivity).getViewPager().setCurrentItem(0);
                    }
                } else {
                    //UPDATE DB  mStatus = 0
                    query = query + ItemContract.ItemEntry.STATUS_TODO + "' WHERE _ID = '" + mId + "';";
                }

                db.execSQL(query);
                db.close();

            /*List Item unit count update*/
                MainActivity mainActivity = (MainActivity) getActivity();
                String listTag = mainActivity.getListTag();
                ListItemFragment listItemFragment = (ListItemFragment)mainActivity.getSupportFragmentManager().findFragmentByTag(listTag);
                listItemFragment.listUiUpdateFromDb();
            }
        };
        return timerView;
    }
    @Override
    public void onStart() {
        Log.i("TimerFragment", "------------------------------------------------------->TimerFragment onStart()");
        super.onStart();
        intent = new Intent(getActivity(), TimerService.class);
        /*init timer count */
        mCountTimer = 1;
        /*TimerService connection*/
        conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mTimerService = null;
                mBound = false;
                mTimerService.stopTimer();
                mProgressBar.setProgress(0);
                handler.removeMessages(0);
                mItemNameText.setText("");
                mStateBttn.setEnabled(false);
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("TimerFragment", "------------------------------------------------------->TimerFragment onServiceConnected()");
                mTimerService = ((TimerService.MyBinder) service).getService();
                mBound = true;
            }
        };
        /*TimerService Intent Listener*/
        getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);

        /*init shared prefernce*/
        SharedPreferences pref = getActivity().getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("COUNT", mCountTimer);
        editor.commit();
    }
    @Override
    public void onResume() {
        Log.i("TimerFragment", "------------------------------------------------------->TimerFragment onResume()");
        super.onResume();
        getActivity().registerReceiver(mReceiver,new IntentFilter(mTimerService.strReceiver));
    }
    Button.OnClickListener stateChecker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mStateBttn.getText().toString().equals("start")) { // checked
                Log.i("TimerFragment", "------------------------------------------------------->TimerFragment stateChecker() Start");
                ////--------------------------------------------------------------------------------------------------------------
                //mUnit will be intialize when list item is clicked
                if (mBound) {
                /* set mStatus DB to DO(1)*/
                    db = dbHelper.getWritableDatabase();
                    query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET unit = '" + mUnit + "' WHERE _ID = '" + mId + "';";
                    db.execSQL(query);
                    db.close();

                    startTimer();
                }
            } else {
                Log.i("TimerFragment", "------------------------------------------------------->TimerFragment stateChecker() Stop");
                Log.i("TimerFragment", "----------------------->Timer Stopped");
                getActivity().stopService(intent); //stop service
                mReadThread.interrupt();
                mTimerService.stopTimer();
                mProgressBar.setProgress(0);
                handler.removeMessages(0);
                progressBarValue = 0; //must be set 0
                Log.i("TimerFragment", "----------------------->Service stop");
                mStateBttn.setText(R.string.start);

                /*set mStatus to TO DO(0)*/
                db = dbHelper.getWritableDatabase();
                query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET status = '" + ItemContract.ItemEntry.STATUS_TODO + "' WHERE _ID = '" + mId + "';";
                db.execSQL(query);
                db.close();
            }
        }
    };
    public void startTimer() {
        Log.i("Fragment", "--------------------------------------------->startTimer()");
        //read mCountTimer
        //
        SharedPreferences pref = getActivity().getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        mCountTimer = pref.getInt("COUNT", 1);

/*        String mWorkTime = "1";
        String mBreakTime = "1";
        String mLongBreakTime = "1";*/
        SESSION=pref.getInt("session",4);
        if (mCountTimer % (SESSION*2) == 0) // assign time by work,short & long break
            runTime = pref.getInt("longbreaktime", 20);
        else if (mCountTimer % 2 == 1)
            runTime = pref.getInt("worktime", 25);
        else
            runTime = pref.getInt("breaktime", 5);

        mProgressBar.setMax(runTime * 60 + 2); // setMax by sec
        handler = new TimerHandler();
        updateTimerText();
        mTimerService.setTimeName(runTime,mItemNameText.getText().toString());
        mStateBttn.setText(R.string.stop);
        handler.sendEmptyMessage(0);
    }

    public void updateTimerText() {
        mReadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTimeText.setText(mTimerService.getTime());
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace(); //back to list
                    }
                }
            }
        });
        Log.i("TimerFragment", "------------------------------------------------------->TimerFragment ReadThreadStart()");
        mReadThread.start();
    }


    public class TimerHandler extends Handler {
        TimerHandler() {
            super();
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            if (mTimerService.getRun()) {
                progressBarValue++;
                mProgressBar.bringToFront();
                mProgressBar.setProgress(progressBarValue);
                handler.sendEmptyMessageDelayed(0, 1000); //increase by sec
            } else { // Timer must be finished
                mProgressBar.setProgress(0);
                progressBarValue = 0;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            getActivity().unbindService(conn);
            mBound = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBound) {
            getActivity().unregisterReceiver(mReceiver);
            mBound = false;
        }
    }

    /**
     * This function set item name in TextView(job_txt_view)
     */
    public void nameUpdate() {
        mItemNameText.setText(mName);

        // test code
        Toast.makeText(getContext(), "ID: " + mId + ", Name: " + mName + ", Status: " + mStatus +
                ", Unit: " + mUnit, Toast.LENGTH_SHORT).show();
    }

    /**
     * Setter
     */
    public void setmId(int mId) {
        this.mId = mId;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public void setmUnit(int mUnit) {
        this.mUnit = mUnit;
    }

    public void setmTotalUnit(int mTotalUnit) {
        this.mTotalUnit = mTotalUnit;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
    public void setButton(Boolean flag){
        this.mStateBttn.setEnabled(flag);
    }
}

