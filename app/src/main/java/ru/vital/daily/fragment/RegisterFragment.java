package ru.vital.daily.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.R;
import ru.vital.daily.BR;
import ru.vital.daily.activity.CountryCodeActivity;
import ru.vital.daily.databinding.FragmentRegisterBinding;
import ru.vital.daily.fragment.sheet.SimpleSheetFragment;
import ru.vital.daily.repository.api.DailySocket;
import ru.vital.daily.util.BitmapHandler;
import ru.vital.daily.util.ContactsProvider;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.util.StaticData;
import ru.vital.daily.view.model.MainViewModel;
import ru.vital.daily.view.model.RegisterViewModel;

public class RegisterFragment extends BaseFragment<RegisterViewModel, FragmentRegisterBinding> {

    private final int REQUEST_COUNTRY_CODE = 100,
            REQUEST_AVATAR_CODE = 101,
            REQUEST_PHOTO_CODE = 102,
            REQUEST_GALLERY_CODE = 103,
            PERMISSION_EXTERNAL_STORAGE_CODE = 200,
            PERMISSION_READ_CONTACTS_CODE = 201;

    @Inject
    PackageManager packageManager;

    @Inject
    DailySocket dailySocket;

    private File file;

    @Override
    protected RegisterViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(RegisterViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        /*viewModel.countryClickedEvent.observe(this, aVoid -> {
            startActivityForResult(new Intent(getContext(), CountryCodeActivity.class), REQUEST_COUNTRY_CODE);
        });*/
        viewModel.avatarClickedEvent.observe(this, aVoid -> {
            openSheetFragment(SimpleSheetFragment.newInstance(new int[]{R.drawable.ic_camera, R.drawable.ic_media}, new int[]{R.string.sheet_photo, R.string.sheet_avatar_gallery}), viewModel.avatarSheetFragmentTag, REQUEST_AVATAR_CODE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case REQUEST_COUNTRY_CODE:
                    Log.i("my_logs", data.getStringExtra(CountryCodeActivity.COUNTRY_CODE));
                    Log.i("my_logs", data.getStringExtra(CountryCodeActivity.COUNTRY_NAME));
                    break;
                case REQUEST_AVATAR_CODE:
                    switch (data.getIntExtra(Intent.EXTRA_TEXT, R.string.sheet_avatar_gallery)) {
                        case R.string.sheet_photo:
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_EXTERNAL_STORAGE_CODE);
                            else openCamera();
                            break;
                        case R.string.sheet_avatar_gallery:
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setDataAndType(
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(intent, REQUEST_GALLERY_CODE);
                            break;
                    }
                    break;
                case REQUEST_PHOTO_CODE:
                    try {
                        Bitmap bitmap = BitmapHandler.handleSamplingAndRotationBitmap(getContext(), Uri.fromFile(file));
                        BitmapHandler.compress(bitmap, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    viewModel.setAvatar(Uri.fromFile(file));
                    viewModel.notifyChanged(BR.avatar);
                    break;
                case REQUEST_GALLERY_CODE:
                    if (data != null) {
                        Uri result = data.getData();
                        file = FileUtil.createTempFile(getContext(), "avatar", ".jpeg");
                        if (FileUtil.copyFile(getContext(), result, file))
                            try {
                                Bitmap bitmap = BitmapHandler.handleSamplingAndRotationBitmap(getContext(), Uri.fromFile(file));
                                BitmapHandler.compress(bitmap, file);
                                viewModel.setAvatar(Uri.fromFile(file));
                                viewModel.notifyChanged(BR.avatar);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_check_2, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check_2:
                //startActivity(new Intent(getContext(), MainActivity.class));
                if (file != null)
                    viewModel.registerUser(file, FileUtil.getMimeType(getActivity(), viewModel.getAvatar()), user -> {
                        dailySocket.connect(StaticData.getData().key.getAccessKey());
                        if (user.getPhone() != null) {
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.READ_CONTACTS},
                                        PERMISSION_READ_CONTACTS_CODE);
                            else viewModel.syncPhones(ContactsProvider.getContactPhones(getContext()), aVoid -> openMainPage());
                        } else {
                            openMainPage();
                        }
                    });
                else viewModel.errorEvent.setValue(new Throwable(getString(R.string.registration_avatar)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openCamera();
                else
                    viewModel.errorEvent.setValue(new Throwable(getString(R.string.permission_camera_denied)));
                break;
            case PERMISSION_READ_CONTACTS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    viewModel.syncPhones(ContactsProvider.getContactPhones(getContext()), aVoid -> openMainPage());
                else
                    viewModel.errorEvent.setValue(new Throwable(getString(R.string.permission_users_denied)));
                break;
        }
    }

    private void openMainPage() {
        MainViewModel mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mainViewModel.mainShowEvent.call();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(packageManager) != null && (file = FileUtil.createTempFile(getContext(), "avatar", ".jpeg")) != null) {
            Uri photoUri = FileProvider.getUriForFile(getContext(),
                    "ru.vital.daily.fileProvider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        startActivityForResult(intent, REQUEST_PHOTO_CODE);
    }
}
