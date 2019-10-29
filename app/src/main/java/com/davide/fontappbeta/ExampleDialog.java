package com.davide.fontappbeta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ExampleDialog extends AppCompatDialogFragment {

    private EditText email;
    private ExampleDialogListener listener;

    private final static CharSequence EMAIL_ERROR = "Email necessaria";
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.password_popup, null);

        builder.setView(view)
                .setTitle("Recupero Password")
                .setNegativeButton("Indietro", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = email.getText().toString();
                        if(username.isEmpty() || !isAnEmail(username)){
                            email.setError(EMAIL_ERROR);
                            return;
                        }
                        listener.applyEmail(username);
                    }
                });

        email = view.findViewById(R.id.editEmail);
        return builder.create();
    }

    private boolean isAnEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + "Must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener{
        void applyEmail(String email);
    }
}
