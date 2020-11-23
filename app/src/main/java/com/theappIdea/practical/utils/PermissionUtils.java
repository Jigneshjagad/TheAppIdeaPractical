package com.theappIdea.practical.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {
    private static final String TAG = PermissionUtils.class.getSimpleName();
    private Activity mActivity;
    private Fragment mFragment;
    private OnPermissionGrantCallback mListener;
    private final int PERMISSION_CODE = 1001;

    public PermissionUtils(Activity activity, String[] permission, OnPermissionGrantCallback listener) {
        this.mActivity = activity;
        mListener = listener;
        onPermissionCheck(permission);
    }

    public PermissionUtils(Fragment fragment, String[] permission, OnPermissionGrantCallback listener) {
        this.mFragment = fragment;
        mListener = listener;
        onPermissionCheck(permission);
    }

    private void onPermissionCheck(String[] permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> reqPermissions = requestForPermission(permission);
            if (reqPermissions != null && reqPermissions.size() > 0) {
                if (mActivity != null) {
                    ActivityCompat.requestPermissions(mActivity, reqPermissions
                            .toArray(new String[reqPermissions.size()]), PERMISSION_CODE);
                } else {
                    mFragment.requestPermissions(reqPermissions
                            .toArray(new String[reqPermissions.size()]), PERMISSION_CODE);
                }
            } else {
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onPermissionGranted();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<String> requestForPermission(String[] permission) {
        List<String> reqPermission = new ArrayList<>();
        for (int i = 0; i < permission.length; i++) {
            if (mActivity != null) {
                if (mActivity.checkSelfPermission(permission[i])
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission is granted ==> " + permission[i]);
                } else {
                    reqPermission.add(permission[i]);
                }
            } else {
                if (ContextCompat.checkSelfPermission(mFragment.getActivity(),
                        permission[i])
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission is granted ==> " + permission[i]);
                } else {
                    reqPermission.add(permission[i]);
                }
            }

        }
        return reqPermission;
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            boolean isGranted = true;
            String missedPermission = "";

            if (grantResults != null && permissions != null) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Not Granted Permission is == > " + grantResults[i]);
                        missedPermission = permissions[i];
                        isGranted = false;
                        break;
                    }
                }
            }

            if (isGranted) {
                if (mListener != null)
                    mListener.onPermissionGranted();
            } else {
                if (mListener != null)
                    mListener.onPermissionError(missedPermission);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onPermissionError(e.getMessage());
            }
        }
    }


    public interface OnPermissionGrantCallback {

        void onPermissionGranted();

        void onPermissionError(String permission);

    }
}
