package com.owen.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.owen.criminalintent.Model.Crime;
import com.owen.criminalintent.Model.CrimeLab;
import com.owen.criminalintent.Model.Photo;
import com.owen.criminalintent.Utils.Constant;
import com.owen.criminalintent.Utils.FileUtils;
import com.owen.criminalintent.Utils.PictureUtils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class CrimeFragment extends BaseFragment{
    private static final String TAG = "CrimeFragment";

    private static final String DIALOG_PHOTO = "photo_dialog";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_EXIST_CAMERA = 3;
    private static final int REQUEST_CONTACTS = 4;

    private Crime mCrime;
    private EditText mEditText;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mReportButton;
    private Button mSuspectButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        //通过getArguments()获取初始化参数
        UUID crimeId = (UUID)getArguments().getSerializable(Constant.EXTRA_CRIME_ID);
        mCrime= CrimeLab.getCrimeLab(getActivity()).getCrime(crimeId);

        mTempPicName = getPicName();
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getCrimeLab(getActivity()).saveCrime();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);

        //使菜单生效
        setHasOptionsMenu(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //在操作栏显示向上操作
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mEditText = (EditText) v.findViewById(R.id.crime_title);
        mEditText.setText(mCrime.getTitle());
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getDateString());
//        mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Picker")
                        .setMessage("Please choose picker:")
                        .setPositiveButton("Date Picker", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DatePickerFragment dateDialog = DatePickerFragment.newInstance(mCrime.getDate());
                                //相当于Activity的startActivityForResult()
                                dateDialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                                dateDialog.show(fm, Constant.DIALOG_DATE);
                            }
                        })
                        .setNegativeButton("Time Picker", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                TimePickerFragment timeDialog = TimePickerFragment.newInstance(mCrime.getDate());
                                //相当于Activity的startActivityForResult()
                                timeDialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                                timeDialog.show(fm, Constant.DIALOG_TIME);
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera(true);
            }
        });

        //检测设备相机是否可用
        PackageManager pm = getActivity().getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                                    pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                                    Camera.getNumberOfCameras() > 0;

        if (!hasCamera) {
            mPhotoButton.setEnabled(false);
        }
        mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = mCrime.getPhoto();
                if (photo == null) {
                    return;
                }

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = FileUtils.getFileUtils(getActivity()).getExternalPictruePath(photo.getPicName());
                PhotoDialogFragment dialog = PhotoDialogFragment.newInstance(path);
                dialog.show(fm, DIALOG_PHOTO);
            }
        });
//        mPhotoView.setContextClickable();
        mReportButton = (Button) v.findViewById(R.id.crime_reportButton);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_subject));
                //每次会弹出一个选择菜单，推荐
                intent = Intent.createChooser(intent, getString(R.string.send_report_title));
                if (isIntentSafe(intent)) {
                    startActivity(intent);
                }
            }
        });
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                if (isIntentSafe(intent)) {
                    startActivityForResult(intent, REQUEST_CONTACTS);
                }
            }
        });
        if (!TextUtils.isEmpty(mCrime.getSuspect())) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE || requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(Constant.EXTRA_DATE);

            mCrime.setDate(date);
            mDateButton.setText(mCrime.getDateString());
        } else if (requestCode == REQUEST_PHOTO) {
            String picName = data.getStringExtra(Constant.EXTRA_PHOTO_NAME);
            if (picName != null) {
                Log.e(TAG, "picName:" + picName);
                Photo photo = new Photo(picName);
                mCrime.setPhoto(photo);
                showPhoto();
            }
        } else if (requestCode == REQUEST_EXIST_CAMERA) {
            if (data == null) {
                File file = FileUtils.getFileUtils(getActivity()).getExternalPictrue(mTempPicName);
                if (file.exists()) {
                    String picName = file.getName();
                    Log.e(TAG, "picName:" + picName);
                    Photo photo = new Photo(picName);
                    mCrime.setPhoto(photo);
                    showPhoto();
                }
            } else {
                Uri picUri = data.getData();
                if (picUri != null) {
                    String picName = picUri.getLastPathSegment();
                    Log.e(TAG, "picName:" + picName);
                    Photo photo = new Photo(picName);
                    mCrime.setPhoto(photo);
                    showPhoto();
                }
            }
        } else if (requestCode == REQUEST_CONTACTS) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                return;
            }

            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
            cursor.close();
        }

    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.EXTRA_CRIME_ID, crimeId);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(bundle);

        return crimeFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_delete_crime:
                CrimeLab.getCrimeLab(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.crime_list_item_context, menu);
    }

    private void showPhoto() {
        Photo photo = mCrime.getPhoto();
        Drawable drawable = null;
        if (photo != null) {
            String path = FileUtils.getFileUtils(getActivity())
                                  .getExternalPictruePath(photo.getPicName());
            drawable = PictureUtils.getScaledDrawable(getActivity(), path);
            mPhotoView.setImageDrawable(drawable);
        }
    }

    private void launchCamera(boolean existCamera) {
        if (existCamera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (FileUtils.getFileUtils(getActivity()).isExternalSpaceWritable()) {
                Uri fileUri = getOutputPictureUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQUEST_EXIST_CAMERA);
            }
        } else {
            Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);
            intent.putExtra(Constant.EXTRA_PHOTO, mCrime.getPhoto());
            startActivityForResult(intent, REQUEST_PHOTO);
        }
    }


    private Uri getOutputPictureUri() {
        File file = FileUtils.getFileUtils(getActivity()).getExternalPictrue(mTempPicName);
        return Uri.fromFile(file);
    }

    private String mTempPicName;

    private String getPicName() {
        if (mCrime.getPhoto() == null || TextUtils.isEmpty(mCrime.getPhoto().getPicName())) {
            CharSequence time = DateFormat.format(Constant.PIC_NAME_FORMAT, new Date());
            mTempPicName = UUID.randomUUID().toString() + "_" + time + ".jpg";
        } else {
            mTempPicName = mCrime.getPhoto().getPicName();
        }
        return mTempPicName;
    }

    private String getCrimeReport() {
        return getString(R.string.crime_report, "owen", "commander");
    }

    private boolean isIntentSafe(Intent intent) {
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> results = pm.queryIntentActivities(intent, 0);
        return results.size() > 0;
    }
}
