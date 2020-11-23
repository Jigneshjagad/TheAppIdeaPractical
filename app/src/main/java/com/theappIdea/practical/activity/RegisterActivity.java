package com.theappIdea.practical.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.theappIdea.practical.R;
import com.theappIdea.practical.databinding.ActivityRegisterBinding;
import com.theappIdea.practical.model.User;
import com.theappIdea.practical.utils.PermissionUtils;
import com.theappIdea.practical.utils.ValidationUtils;
import com.theappIdea.practical.viewmodel.UserViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.theappIdea.practical.utils.Common.IMAGE_DIRECTORY_NAME;
import static com.theappIdea.practical.utils.Utils.createImageFile;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ActivityRegisterBinding binding;
    private UserViewModel userViewModel;

    private Uri fileUri; // file url to store image/video
    private String imagePath = "";

    private String selectedImagePath = "";
    private PermissionUtils permissionUtils;
    private int imageCount = 0;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private User userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() == null) {
            saveVisible();
        } else {
            User user = getIntent().getExtras().getParcelable("data");
            saveGone(user);
        }
        initView();
    }

    private void saveVisible() {
        binding.btnSave.setVisibility(View.VISIBLE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.btnUpdate.setVisibility(View.GONE);
    }

    private void saveGone(User user) {
        binding.btnSave.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.btnUpdate.setVisibility(View.VISIBLE);
        userInfo = user;
        binding.edtFirstName.setText(user.getFirst_name());
        binding.edtLastName.setText(user.getLast_name());
        binding.edtEmail.setText(user.getEmail());
        binding.edtMobileNo.setText(user.getMobile_number());
        selectedImagePath = user.getImage_path();
        if (user.getImage_path() != null && !user.getImage_path().isEmpty()) {
            Uri photoUri = Uri.fromFile(new File(user.getImage_path()));
            Glide.with(this).load(photoUri).into(binding.imgUser);
        }
    }

    private void initView() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        binding.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndSelectImages();
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    saveToDatabase();
                    onClearViews();
                    finish();
                }
            }
        });
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    updateToDatabase();
                    onClearViews();
                    finish();
                }
            }
        });
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.deleteUser(userInfo);
                finish();
            }
        });
    }

    private void updateToDatabase() {
        userInfo.setImage_path(selectedImagePath);
        userInfo.setFirst_name(binding.edtFirstName.getText().toString());
        userInfo.setLast_name(binding.edtLastName.getText().toString());
        userInfo.setEmail(binding.edtEmail.getText().toString());
        userInfo.setMobile_number(binding.edtMobileNo.getText().toString());
        userViewModel.updateUser(userInfo);
    }

    private void saveToDatabase() {
        User user = new User();
        user.setImage_path(selectedImagePath);
        user.setFirst_name(binding.edtFirstName.getText().toString());
        user.setLast_name(binding.edtLastName.getText().toString());
        user.setEmail(binding.edtEmail.getText().toString());
        user.setMobile_number(binding.edtMobileNo.getText().toString());
        userViewModel.insertUser(user);
    }

    private void onClearViews() {
        binding.edtFirstName.requestFocus();
        binding.edtFirstName.setText("");
        binding.edtLastName.setText("");
        binding.edtEmail.setText("");
        binding.edtMobileNo.setText("");
    }

    private void checkPermissionAndSelectImages() {
        permissionUtils = new PermissionUtils(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionUtils.OnPermissionGrantCallback() {
            @Override
            public void onPermissionGranted() {
                selectImage();
            }

            @Override
            public void onPermissionError(String permission) {
                Log.e(TAG, "onPermissionError: " + permission);
            }
        });
    }

    private void selectImage() {
        CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            File file;
                            try {
                                file = createImageFile(RegisterActivity.this);
                                imagePath = file.getAbsolutePath();
                                selectedImagePath = file.getAbsolutePath();
                                fileUri = FileProvider.getUriForFile(RegisterActivity.this, getApplicationContext().getPackageName() + ".provider", file);
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        }
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                        break;
                    case 1:
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), SELECT_FILE);
                        break;
                    case 2:
                        dialog.dismiss();
                        dialog.cancel();
                        break;
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap imageBitmap = null;

            //single image selection
            if (requestCode == SELECT_FILE) {
                if (data != null) {
                    try {
                        selectedImagePath = data.getData().toString();
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        binding.imgUser.setImageBitmap(imageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == REQUEST_CAMERA) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    imageBitmap = BitmapFactory.decodeFile(imagePath, options);
                    selectedImagePath = imagePath;
                } else {
                    imageBitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                    selectedImagePath = fileUri.getPath();
                }
                binding.imgUser.setImageBitmap(imageBitmap);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult requestCode: " + requestCode);
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {
        imageCount++;
        //External sdcard location
        File mediaStorageDir = new File(Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "Oops! Failed create : " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "Zomenu_" + timeStamp + imageCount + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }


    private boolean isValid() {
        if (selectedImagePath.isEmpty()) {
            Toast.makeText(this, "Please upload photo!", Toast.LENGTH_LONG).show();
            return false;
        } else if (binding.edtFirstName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter First Name!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.edtLastName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Last Name!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.edtEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please Enter Email Address!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!ValidationUtils.isValidEmailAddress(binding.edtEmail)) {
            Toast.makeText(this, "Please Enter Valid Email Address!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.edtMobileNo.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please Enter Mobile Number!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.edtMobileNo.getText().toString().length() < 10) {
            Toast.makeText(this, "Please Enter Valid Mobile Number!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getIntent().removeExtra("data");
    }
}