package org.wheel.expenses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.support.v7.app.AlertDialog.BUTTON_POSITIVE;

public class CreateRoomDialogFragment extends DialogFragment {

    @BindView(R.id.create_room_name)
    EditText roomName;

    @BindView(R.id.create_room_password)
    EditText roomPassword;

    private CreateRoomDialogFragmentPresenter mPresenter;
    private Button mPositiveButton;
    private Button mNegativeButton;

    public CreateRoomDialogFragment() {
        mPresenter = new CreateRoomDialogFragmentPresenter(this, WheelClient.getInstance(),
                                                           WheelAPI.getInstance());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_create_room, null);
        ButterKnife.bind(this, v);
        builder.setView(v)
               .setTitle(R.string.create_room)
               .setPositiveButton(R.string.create_room_create, null)
               .setNegativeButton(R.string.create_room_cancel, null);
        AlertDialog d = builder.create();
        mPositiveButton = d.getButton(BUTTON_POSITIVE);
        mNegativeButton = d.getButton(BUTTON_NEGATIVE);
        d.setOnShowListener(dialogInterface -> {
            mPositiveButton.setEnabled(false);
            mPositiveButton
                    .setOnClickListener(v1 -> mPresenter.onCreateRoomClicked(
                            roomName.getText().toString(),
                            roomPassword.getText().toString()));
            roomName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence,
                                              int i,
                                              int i1,
                                              int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i,
                                          int i1,
                                          int i2) {
                    d.getButton(BUTTON_POSITIVE).setEnabled(!charSequence.toString().isEmpty());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        });


        return d;
    }

    public void setListener(CreateRoomDialogFragmentListener listener) {
        mPresenter.setListener(listener);
    }

    public void disableButtons() {
        mPositiveButton.setEnabled(false);
        mNegativeButton.setEnabled(false);
    }

    public void enableButtons() {
        mPositiveButton.setEnabled(true);
        mNegativeButton.setEnabled(true);
    }

    public void dismissDialog() {
        this.dismiss();
    }

    public interface CreateRoomDialogFragmentListener {
        void onSuccess();
    }

}
