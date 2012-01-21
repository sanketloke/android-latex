package com.androtex.projects;

import com.androtex.ActivityFileList;
import com.androtex.ActivityProjectList;

import com.androtex.R;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;

/**
 * Class representing an Adapter (and Android list) of component
 * 
 * @see BaseAdapter
 * 
 * @author Kevin Le Perf
 * 
 */
public class ListProjectAdapter extends BaseAdapter {
	/**
	 * The parent Activity which created the ListView's Adapter
	 */
	private ActivityProjectList _principal;

	/**
	 * The project array representing this Adapter
	 */
	private ProjectArray _item;

	/**
	 * ListProjectAdapter Construct a new Adapter with a project list specified
	 * 
	 * @param principal
	 *            The main activity
	 * @param project_array
	 *            the array representing the instance
	 */
	public ListProjectAdapter(ActivityProjectList principal,
			ProjectArray project_array) {
		_principal = principal;
		_item = project_array;
	}

	/**
	 * getView return a view initialized with proper infos
	 * 
	 * @param pos
	 *            the current position we want to render
	 * @param inView
	 *            the view in which we will write
	 * @param parent
	 *            in case of parenting, get the parent caller
	 */

	public View getView(final int pos, View inView, ViewGroup parent) {
		// null? when we initialize an graphic item list
		if (inView == null) {
			LayoutInflater inflater = (LayoutInflater) _principal
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inView = inflater.inflate(R.layout.list_project, null);
		}

		final View v = inView;

		// update the text displayed
		TextView bTitle = (TextView) v.findViewById(R.list_project.name);
		bTitle.setText(_item.get(pos).getName());

		ImageView _v = (ImageView)v.findViewById(R.list.remove);
		_v.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				_principal.deleteProject(_item.get(pos).getName());
			}
		});
		
		_v = (ImageView)v.findViewById(R.list.rename);
		_v.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				_principal.renameProject(_item.get(pos).getName());
			}
		});
		// create the click event
		v.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// create and intent
				Intent intent = new Intent(_principal, ActivityFileList.class);
				// put the different basic informations
				intent.putExtra("login", _principal.getLogin());
				intent.putExtra("password", _principal.getPassword());
				intent.putExtra("project", _item.get(pos).getName());
				// go
				_principal.startActivity(intent);
			}
		});
		return (v);
	}

	/**
	 * getCount return the number of element in the Adapter
	 * 
	 * @return the number on an int
	 */
	public int getCount() {
		return _item.length();
	}

	/**
	 * getItem
	 * 
	 * @return the item at the <b>pos</b> position
	 */
	public Object getItem(int pos) {
		return _item.get(pos);
	}

	public long getItemId(int pos) {
		return pos;
	}
}
