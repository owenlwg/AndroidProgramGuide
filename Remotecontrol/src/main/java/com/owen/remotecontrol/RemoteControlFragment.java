package com.owen.remotecontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by Owen on 2016/1/8.
 */
public class RemoteControlFragment extends Fragment {
    TextView mSelectedTv;
    TextView mWorkingTv;
    TableLayout mTableLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remote_control, container, false);

        mSelectedTv = (TextView) view.findViewById(R.id.textview_selected);
        mWorkingTv = (TextView) view.findViewById(R.id.textView_working);
        mTableLayout = (TableLayout) view.findViewById(R.id.tablelayout_root);


        View.OnClickListener numberButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button)v;
                String workingText = mWorkingTv.getText().toString();
                String buttonText = button.getText().toString();
                if (workingText.equals("0")) {
                    mWorkingTv.setText(buttonText);
                } else {
                    mWorkingTv.setText(workingText + buttonText);
                }
            }
        };

        int number = 1;
        for (int i = 2; i < mTableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) mTableLayout.getChildAt(i);
            if (i == mTableLayout.getChildCount() - 1) {
                Button deleteButton = (Button) row.getChildAt(0);
                deleteButton.setText("Delete");
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWorkingTv.setText("0");
                    }
                });

                Button zeroButton = (Button) row.getChildAt(1);
                zeroButton.setText("0");
                zeroButton.setOnClickListener(numberButtonListener);

                Button enterButton = (Button) row.getChildAt(2);
                enterButton.setText("Eneter");
                enterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence result = mWorkingTv.getText();
                        if (result.length() > 0) {
                            mSelectedTv.setText(result);
                        }
                        mWorkingTv.setText("0");
                    }
                });
            } else {
                for (int j = 0; j < row.getChildCount(); j++) {
                    Button b = (Button) row.getChildAt(j);
                    b.setText("" + number);
                    b.setOnClickListener(numberButtonListener);
                    number++;
                }
            }

        }


        return view;
    }
}
