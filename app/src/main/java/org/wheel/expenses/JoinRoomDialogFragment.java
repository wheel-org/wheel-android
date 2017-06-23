package org.wheel.expenses;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.support.v7.app.AlertDialog.BUTTON_POSITIVE;

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

public class JoinRoomDialogFragment extends DialogFragment {

    @BindView(R.id.join_room_id)
    EditText roomId;

    @BindView(R.id.join_room_password)
    EditText roomPassword;

    private Button mPositiveButton;

    private Button mNegativeButton;

    private JoinRoomDialogFragmentPresenter mPresenter;
    private String mInitialValue;

    public JoinRoomDialogFragment() {
        mPresenter = new JoinRoomDialogFragmentPresenter(this, WheelClient.getInstance(),
                WheelAPI.getInstance());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_join_room, null);
        ButterKnife.bind(this, v);
        if (mInitialValue != null) {
            roomId.setText(mInitialValue);
        }
        builder.setView(v)
                .setTitle(R.string.join_room)
                .setPositiveButton(R.string.join_room_join, null)
                .setNegativeButton(R.string.create_room_cancel,
                        (dialog, id) -> JoinRoomDialogFragment.this.getDialog()
                                .cancel());
        AlertDialog d = builder.create();
        d.setOnShowListener(dialogInterface -> {
            mPositiveButton = d.getButton(BUTTON_POSITIVE);
            mNegativeButton = d.getButton(BUTTON_NEGATIVE);
            if (mInitialValue == null) {
                mPositiveButton.setEnabled(false);
            }
            mPositiveButton
                    .setOnClickListener(v1 -> mPresenter.onJoinRoomClicked(roomId.getText()
                                    .toString(),
                            roomPassword.getText()
                                    .toString()));
            roomId.addTextChangedListener(new TextWatcher() {
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

    public void setListener(JoinRoomDialogFragmentListener listener) {
        mPresenter.setListener(listener);
    }

    public interface JoinRoomDialogFragmentListener {
        void onSuccess();
    }

    public void setInitalValue(String initialValue) {
        mInitialValue = initialValue;
    }
}
