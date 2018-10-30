package com.instalink.archive;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.instalink.archive.model.MSG;
import com.instalink.archive.util.ApiClient;
import com.instalink.archive.util.ApiService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

import static android.app.Activity.RESULT_OK;


public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    Unbinder unbinder;
    @BindView(R.id.btnRegister)
    Button _btnRegister;
    @BindView(R.id.eetRegisterEmail)
    ExtendedEditText _edRegisterEmail;
    @BindView(R.id.eet_register_password)
    ExtendedEditText _edRegisterPassword;
    @BindView(R.id.eet_register_repassword)
    ExtendedEditText _edRegisterReEnterPassword;



    private static final String TAG = "SignupActivity";
    private ProgressDialog pDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this,rootView);
        FrameLayout registerLayout = rootView.findViewById(R.id.registerLayout);
        //ViewCompat.setLayoutDirection(registerLayout,ViewCompat.LAYOUT_DIRECTION_RTL);

        _btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable())
                {
                    signup();
                } else {
                    Toast.makeText(getActivity(),"لطفا اینترنت خود را متصل کنید!",Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (validate() == false) {
            onSignupFailed();
            return;
        }

        saveToServerDB();

    }


    public void onSignupSuccess() {
        _btnRegister.setEnabled(true);
        getActivity().setResult(RESULT_OK, null);
        getActivity().finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getActivity(), "ثبت نام ناموفق بود!", Toast.LENGTH_LONG).show();

        _btnRegister.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _edRegisterEmail.getText().toString();
        String password = _edRegisterPassword.getText().toString();
        String reEnterPassword = _edRegisterReEnterPassword.getText().toString();



        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _edRegisterEmail.setError("لطفا یک آدرس ایمیل معتبر وارد کنید");
            valid = false;
        } else {
            _edRegisterEmail.setError(null);
        }


        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _edRegisterPassword.setError("باید بین 4 تا 10 کاراکتر تشکیل شده از اعداد و حروف باشد");
            valid = false;
        } else {
            _edRegisterPassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _edRegisterReEnterPassword.setError("کلمه های عبور همخوانی ندارند");
            valid = false;
        } else {
            _edRegisterReEnterPassword.setError(null);
        }

        return valid;
    }

    private void saveToServerDB() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setIndeterminate(true);
        pDialog.setMessage("در حال ساخت حساب");
        pDialog.setCancelable(false);

        showpDialog();

        String email = _edRegisterEmail.getText().toString();
        String password = _edRegisterPassword.getText().toString();


        ApiService service = ApiClient.getClient().create(ApiService.class);
        //User user = new User(name, email, password);


        Call<MSG> userCall = service.userSignUp(email, password);

        userCall.enqueue(new Callback<MSG>() {
            @Override
            public void onResponse(Call<MSG> call, Response<MSG> response) {
                hidepDialog();
                //onSignupSuccess();
                Log.d("onResponse", "" + response.body().getMessage());


                if(response.body().getSuccess() == 1) {
                    Toast.makeText(getActivity(),"کاربر با موفقیت ثبت نام شد",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MSG> call, Throwable t) {
                hidepDialog();
                Log.d("onFailure", t.toString());
            }
        });
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}