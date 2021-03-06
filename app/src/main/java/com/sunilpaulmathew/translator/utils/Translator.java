/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.translator.BuildConfig;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.adapters.RecycleViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 03, 2020
 */

public class Translator {

    public static List<String> mData = new ArrayList<>();
    public static RecyclerView mRecyclerView;
    public static String mKeyText;

    public static void saveString(View view, Activity context) {
        if (Utils.isStorageWritePermissionDenied(context)) {
            ActivityCompat.requestPermissions(context, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Utils.showSnackbar(view, context.getString(R.string.permission_denied_write_storage));
            return;
        }
        Utils.dialogEditText("strings-" + java.util.Locale.getDefault().getLanguage(), context.getString(R.string.save), view,
                (dialogInterface2, iii) -> {},
                text -> {
                    if (text.isEmpty()) {
                        Utils.showSnackbar(view, context.getString(R.string.name_empty));
                        return;
                    }
                    if (!text.endsWith(".xml")) {
                        text += ".xml";
                    }
                    if (Utils.exist(Environment.getExternalStorageDirectory().toString() + "/" + text)) {
                        Utils.showSnackbar(view, context.getString(R.string.already_exists, text));
                        return;
                    }
                    String mString = Environment.getExternalStorageDirectory().toString() + "/" + text;
                    Utils.create(getStrings(context), mString);
                    new MaterialAlertDialogBuilder(context)
                            .setMessage(context.getString(R.string.save_string_message, mString))
                            .setNegativeButton(context.getString(R.string.cancel), (dialogInterface, i) -> {
                            })
                            .setPositiveButton(context.getString(R.string.share), (dialogInterface, i) -> {
                                Uri uriFile = FileProvider.getUriForFile(context,
                                        BuildConfig.APPLICATION_ID + ".provider", new File(mString));
                                Intent shareScript = new Intent(Intent.ACTION_SEND);
                                shareScript.setType("application/xml");
                                shareScript.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.shared_by, new File(mString).getName()));
                                shareScript.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message, BuildConfig.VERSION_NAME));
                                shareScript.putExtra(Intent.EXTRA_STREAM, uriFile);
                                shareScript.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                context.startActivity(Intent.createChooser(shareScript, context.getString(R.string.share_with)));
                            })
                            .show();
                }, context).setOnDismissListener(dialogInterface2 -> {
        }).show();
    }

    public static String getStrings(Context context) {
        List<String> mData = new ArrayList<>();
        if (Utils.exist(context.getFilesDir().toString() + "/strings.xml")) {
            for (String line : Objects.requireNonNull(Utils.read(context.getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    mData.add(line);
                }
            }
        }
        return "<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">\n<!--Created by The Translator <https://play.google.com/store/apps/details?id=com.sunilpaulmathew.translator>-->\n\n" +
                mData.toString().replace("[","").replace("]","").replace(",","\n") + "\n</resources>";
    }

    public static void deleteSingleString(String string, Context context) {
        for (String line : Objects.requireNonNull(Utils.read(context.getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
            if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                if (line.endsWith(string)) {
                    Utils.create(Objects.requireNonNull(Utils.read(context.getFilesDir().toString() + "/strings.xml")).replace(line, ""), context.getFilesDir().toString() + "/strings.xml");
                }
            }
        }
    }

    public static boolean checkIllegalCharacters(String string) {
        String[] array = string.trim().split("\\s+");
        for (String s : array) {
            if (!s.matches("&gt;|&lt;|&amp;") && s.startsWith("&")
                    || s.startsWith("<") && s.length() == 1 || s.startsWith(">") && s.length() == 1
                    || s.startsWith("<b") && s.length() <= 3 || s.startsWith("</") && s.length() <= 4
                    || s.startsWith("<i") && s.length() <= 3 || s.startsWith("\"") || s.startsWith("'"))
                return true;
        }
        return false;
    }

    public static String getSpecialCharacters(String string) {
        StringBuilder sb = new StringBuilder();
        if (string.contains("%s")) {
            sb.append(" - %s");
        }
        if (string.contains("\n")) {
            sb.append(" - \n");
        }
        if (string.contains("\\'")) {
            sb.append(" - \\'");
        }
        if (string.contains("<b>")) {
            sb.append(" - <b>");
        }
        if (string.contains("</b>")) {
            sb.append(" - </b>");
        }
        if (string.contains("<i>")) {
            sb.append(" - <i>");
        }
        if (string.contains("</i>")) {
            sb.append(" - </i>");
        }
        return sb.toString().replaceFirst(" - ","");
    }

    public static List<String> getData(Context context) {
        mData.clear();
        if (Utils.exist(context.getFilesDir().toString() + "/strings.xml")) {
            for (String line : Objects.requireNonNull(Utils.read(context.getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    String[] finalLine = line.split("\">");
                    if (mKeyText == null) {
                        mData.add(finalLine[1].replace("</string>", ""));
                    } else if (finalLine[1].toLowerCase().contains(mKeyText)) {
                        mData.add(finalLine[1].replace("</string>", ""));
                    }
                }
            }
        }
        return mData;
    }

    public static void reloadUI(Context context) {
        mRecyclerView.setAdapter(new RecycleViewAdapter(getData(context)));
    }

}