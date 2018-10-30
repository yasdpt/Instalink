package com.instalink.archive;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.instalink.archive.helpers.DatabaseHelper;
import com.instalink.archive.helpers.PrefManage;
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
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }
    Unbinder unbinder;

    @BindView(R.id.btnLogin)
    Button _btnLogin;
    @BindView(R.id.eet_login_email)
    ExtendedEditText _edEmail;
    @BindView(R.id.eet_login_password)
    ExtendedEditText _edPassword;
    @BindView(R.id.loginLayout)
    FrameLayout loginLayout;
    @BindView(R.id.tvPassRecovery)
    TextView tvPassRecovery;

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog pDialog;
    private DatabaseHelper db;
    private String txtEmail,txtPass,recoveryEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        unbinder = ButterKnife.bind(this,rootView);

        //ViewCompat.setLayoutDirection(loginLayout,ViewCompat.LAYOUT_DIRECTION_RTL);
        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable())
                {
                    txtEmail = _edEmail.getText().toString();
                    txtPass = _edPassword.getText().toString();
                    if (!(txtEmail.equals("")) && !(txtPass.equals(""))){
                        login();
                    } else {
                        Toast.makeText(getActivity(),"لطفا مقادیر را وارد کنید!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(),"لطفا اینترنت خود را متصل کنید!",Toast.LENGTH_LONG).show();
                }

            }
        });

        tvPassRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecoveryRequest();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (validate() == false) {
            onLoginFailed();
            return;
        }

        //_loginButton.setEnabled(false);

        loginByServer();
    }

    public void sendRecoveryRequest() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());

        View view = layoutInflaterAndroid.inflate(R.layout.category_dialog, null);

        ViewCompat.setLayoutDirection(view,ViewCompat.LAYOUT_DIRECTION_RTL);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(view.getContext());
        alertDialogBuilderUserInput.setView(view);

        final ExtendedEditText inputNote = view.findViewById(R.id.eet_category_add);
        final TextFieldBoxes inputTFB = view.findViewById(R.id.tfb_category_add);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText("بازیابی کلمه عبور");
        inputTFB.setLabelText("آدرس ایمیل");



        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("ارسال", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("بیخیال",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable())
                {
                    recoveryEmail = inputNote.getText().toString();
                    if (recoveryEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(recoveryEmail).matches()) {
                        inputNote.setError("لطفا یک آدرس ایمیل معتبر وارد کنید");
                    } else {
                        _edEmail.setError(null);
                        alertDialog.dismiss();
                        // send recovery request
                        recoverPassword();
                    }


                } else {
                    Toast.makeText(getActivity(),"لطفا اینترنت خود را متصل کنید!",Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void recoverPassword() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setIndeterminate(true);
        pDialog.setMessage("در حال ارسال درخواست...");
        pDialog.setCancelable(false);

        showpDialog();

        String email = recoveryEmail;


        ApiService service = ApiClient.getClient().create(ApiService.class);

        Call<MSG> userCall = service.passRecovery(email);

        userCall.enqueue(new Callback<MSG>() {
            @Override
            public void onResponse(Call<MSG> call, Response<MSG> response) {
                hidepDialog();
                //onSignupSuccess();
                Log.d("onResponse", "" + response.body().getMessage());


                if(response.body().getSuccess() == 1) {
                    Toast.makeText(getActivity(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MSG> call, Throwable t) {
                hidepDialog();
                Log.d("onFailure", t.toString());
                Toast.makeText(getActivity(), "مشکل در اتصال به سرور!" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginByServer() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setIndeterminate(true);
        pDialog.setMessage("در حال ورود به حساب...");
        pDialog.setCancelable(false);

        showpDialog();

        String email = _edEmail.getText().toString();
        String password = _edPassword.getText().toString();


        ApiService service = ApiClient.getClient().create(ApiService.class);

        Call<MSG> userCall = service.userLogIn(email,password);

        userCall.enqueue(new Callback<MSG>() {
            @Override
            public void onResponse(Call<MSG> call, Response<MSG> response) {
                hidepDialog();
                //onSignupSuccess();
                Log.d("onResponse", "" + response.body().getMessage());


                if(response.body().getSuccess() == 1) {
                    Toast.makeText(getActivity(), "با موفقیت وارد شدید!", Toast.LENGTH_SHORT).show();
                    PrefManage prefManage = new PrefManage(getActivity());
                    if ((prefManage.isFirstTimeToLaunch())) {
                        prefManage.setFirstTimeLaunch(false);
                        db = new DatabaseHelper(getActivity());
                        db.insertCategory("Instagram");
                    }
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }else {
                    Toast.makeText(getActivity(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MSG> call, Throwable t) {
                hidepDialog();
                Log.d("onFailure", t.toString());
                Toast.makeText(getActivity(), "مشکل در اتصال به سرور!" , Toast.LENGTH_SHORT).show();
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


    public void onLoginSuccess() {
        _btnLogin.setEnabled(true);
        getActivity().finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getContext(), "ورود ناموفق بود!", Toast.LENGTH_LONG).show();

        _btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _edEmail.getText().toString();
        String password = _edPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _edEmail.setError("لطفا یک آدرس ایمیل معتبر وارد کنید");
            valid = false;
        } else {
            _edEmail.setError(null);
        }

        if (password.isEmpty()) {
            _edPassword.setError("کلمه عبور خالی است");
            valid = false;
        } else {
            _edPassword.setError(null);
        }

        return valid;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}