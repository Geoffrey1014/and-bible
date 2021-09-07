/*
 * Copyright (c) 2018 Martin Denham, Tuomas Airaksinen and the And Bible contributors.
 *
 * This file is part of And Bible (http://github.com/AndBible/and-bible).
 *
 * And Bible is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * And Bible is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with And Bible.
 * If not, see http://www.gnu.org/licenses/.
 *
 */

package net.bible.android.view.activity.bookmark;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import net.bible.android.activity.R;
import net.bible.android.control.bookmark.BookmarkControl;
import net.bible.android.view.activity.base.Callback;
import net.bible.service.db.bookmark.LabelDto;

import javax.inject.Inject;

/**
 * Label dialogs - edit or create label.  Used in a couple of places so extracted.
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 */
public class LabelDialogs {

	private final BookmarkControl bookmarkControl;

	private static final String TAG = "LabelDialogs";

	@Inject
	public LabelDialogs(BookmarkControl bookmarkControl) {
		this.bookmarkControl = bookmarkControl;
	}

	public void createLabel(Context context, final LabelDto label, final Callback onCreateCallback) {
		showDialog(context, R.string.new_label, label, onCreateCallback);
	}

	public void editLabel(Context context, final LabelDto label, final Callback onCreateCallback) {
		if (label.getName().equals("")){
			Log.i("Themis", "editLabel: step 9: edit 空 label");
		}
		showDialog(context, R.string.edit, label, onCreateCallback);
	}

	private void showDialog(Context context, int titleId, final LabelDto label, final Callback onCreateCallback) {
		Log.i(TAG, "Edit label clicked");

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.bookmark_label_edit, null);
		final EditText labelName = (EditText)view.findViewById(R.id.labelName);
		labelName.setText(label.getName());

		final BookmarkStyleAdapter adp = new BookmarkStyleAdapter(context, android.R.layout.simple_spinner_item);
		final Spinner labelStyle = (Spinner)view.findViewById(R.id.labelStyle);
		labelStyle.setAdapter(adp);
		labelStyle.setSelection(adp.getBookmarkStyleOffset(label.getBookmarkStyle()));

		AlertDialog.Builder alert = new AlertDialog.Builder(context)
				.setTitle(titleId)
				.setView(view);

		alert.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String name = labelName.getText().toString();
				label.setName(name);
				label.setBookmarkStyle(adp.getBookmarkStyleForOffset(labelStyle.getSelectedItemPosition()));

				if (name.equals("")){
					Log.i("Themis", "setPositiveButton onClick: step 6: 创建一个空 label");
				}
				else{
					Log.i("Themis", "setPositiveButton onClick: step 7/9: 创建一个非空 label/修改一个空 label为非空");
				}
				try{
					bookmarkControl.saveOrUpdateLabel(label);
				}catch (kotlin.KotlinNullPointerException e){
					Log.i("Themis", "setPositiveButton onClick: step last: bomb!");
					throw e;
				}


				onCreateCallback.okay();
			}
		});

		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Log.i("Themis", "setNegativeButton onClick: step 6/7/9: Warning: 放弃创建/修改 label ");
				// Canceled.
			}
		});

		AlertDialog dialog = alert.show();
	}
}
