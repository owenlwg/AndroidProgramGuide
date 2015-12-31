package com.owen.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

import com.owen.criminalintent.Utils.Constant;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Owen on 2015/12/14.
 */
public class TimePickerFragment extends DialogFragment {

    private Date mDate;

    public static TimePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.DIALOG_TIME, date);

        TimePickerFragment dialog = new TimePickerFragment();
        dialog.setArguments(bundle);

        return dialog;
    }

    private GregorianCalendar mCalendar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDate = (Date) getArguments().getSerializable(Constant.DIALOG_TIME);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);

        TimePicker timePicker = (TimePicker) view.findViewById(R.id.dialog_time_timePicker);

        mCalendar = new GregorianCalendar();
        mCalendar.setTime(mDate);

        timePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);

                mDate = mCalendar.getTime();
            }
        });



        return new AlertDialog.Builder(getActivity())
                .setTitle("time")
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_DATE, mDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
