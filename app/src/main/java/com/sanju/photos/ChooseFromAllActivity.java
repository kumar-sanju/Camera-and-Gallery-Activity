package com.sanju.photos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class ChooseFromAllActivity extends AppCompatActivity {

    private RecyclerView rec_photo;
    private PhotoAdapter photoAdapter;
    private Button btn_multi_show;
    List<Uri> selectedUriList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_from_all);

        rec_photo = findViewById(R.id.rec_photo);
        btn_multi_show = findViewById(R.id.btn_multi_show);

        photoAdapter = new PhotoAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3, LinearLayoutManager.VERTICAL, false);
        rec_photo.setLayoutManager(gridLayoutManager);
        rec_photo.setFocusable(false);
        rec_photo.setAdapter(photoAdapter);

        btn_multi_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });

    }

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openButtonPicker();
                Toast.makeText(ChooseFromAllActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ChooseFromAllActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void openButtonPicker() {

        TedBottomPicker.with(ChooseFromAllActivity.this)
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .setSelectedUriList(selectedUriList)
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        // here is selected image uri list
                        photoAdapter.setData(uriList);
                    }
                });
    }


}









