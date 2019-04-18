/**
 * 
 */
package com.outsourcing.industry.restaurant.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.outsourcing.industry.restaurant.R;
import com.outsourcing.industry.restaurant.entity.Dish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * @author Tianhua, Pan
 * 
 */
public class GalleryImageAdapter extends BaseAdapter {
	private Context context;
	private List<Dish> list_dishes;

	public GalleryImageAdapter(Context context) {
		this.context = context;
		this.list_dishes = new ArrayList<Dish>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return list_dishes.size();
	}

	public void addDish(Dish dish) {
		list_dishes.add(dish);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int pos) {
		if (list_dishes.size() - 1 >= pos) {
			return list_dishes.get(pos);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int pos) {
		return pos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		if (null == view) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.gallery_item, null);
		}
		ImageView imgview_galleryItem = (ImageView) view
				.findViewById(R.id.imgview_galleryitem);
		if (list_dishes.size() > position) {
			imgview_galleryItem.setImageBitmap(loadBitmap(list_dishes
					.get(position)));
		}
		return view;
	}

	private Bitmap loadBitmap(Dish dish) {
		File cache_dir = new File(context.getFilesDir(), "imgcache");
		if(!cache_dir.exists())
			cache_dir.mkdirs();
		File dishitem_cachefile = new File(cache_dir, dish
				.getImgfilename());
		return BitmapFactory.decodeFile(dishitem_cachefile.getAbsolutePath());

	}

}
