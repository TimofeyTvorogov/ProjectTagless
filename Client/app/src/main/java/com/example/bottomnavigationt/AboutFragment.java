package com.example.bottomnavigationt;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    protected Switch adminSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);
        adminSwitch = view.findViewById(R.id.admin_switch);
        TextView vkTv = view.findViewById(R.id.vk_tv),
                tgTv = view.findViewById(R.id.tg_tv),
                mailTv = view.findViewById(R.id.mail_tv);
        vkTv.setMovementMethod(LinkMovementMethod.getInstance());
        tgTv.setMovementMethod(LinkMovementMethod.getInstance());
        mailTv.setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager)requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("",mailTv.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast toast = Toast.makeText(getContext(),"почта скопирована", Toast.LENGTH_SHORT);
            toast.show();

            return true;
        });

        return view;
    }
}