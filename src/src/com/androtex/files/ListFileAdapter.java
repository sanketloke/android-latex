package com.androtex.files;

import com.androtex.ActivityFileList;

import com.androtex.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;

/**
 * Class used to create a visible list
 * 
 * @author Kevin Le Perf
 * 
 */
public class ListFileAdapter extends BaseAdapter {
	private ActivityFileList _principal;
	private FileArray _item;

	public ListFileAdapter(ActivityFileList activity, FileArray item) {
		_principal = activity;
		_item = item;
	}

	/**
	 * getView
	 * 
	 * @param pos
	 *            the current position in the ListView
	 * @param inView
	 *            the inView in which we will write
	 * @param parent
	 *            the parent caller
	 */
	public View getView(final int pos, View inView, ViewGroup parent) {
		if (inView == null) {
			LayoutInflater inflater = (LayoutInflater) _principal
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inView = inflater.inflate(R.layout.list_project, null);
		}

		final View v = inView;

		// we update the name
		TextView bTitle = (TextView) v.findViewById(R.list_project.name);
		bTitle.setText(_item.get(pos).getName());

		// and also the onClick management
		ImageView _v = (ImageView)v.findViewById(R.list.remove);
		_v.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				_principal.deleteFileFromProject(_item.get(pos).getName());
			}
		});
		
		_v = (ImageView)v.findViewById(R.list.rename);
		_v.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				_principal.renameFileFromProject(_item.get(pos).getName());
			}
		});
		
		v.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				_principal.getFile(_item.get(pos).getName());
			}
		});

		// and return it
		return (v);
	}

	public int getCount() {
		return _item.length();
	}

	public Object getItem(int pos) {
		return _item.get(pos);
	}

	public long getItemId(int pos) {
		return pos;
	}
}
