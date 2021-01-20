package cn.ieway.evmirror.modules.welcome;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import cn.ieway.evmirror.R;

public class GuideViewPagerAdapter extends PagerAdapter {
	private List<View> views;
	private ViewPageClickListener pageClickListener;

	public void setPageClickListener(ViewPageClickListener pageClickListener) {
		this.pageClickListener = pageClickListener;
	}

	public GuideViewPagerAdapter(List<View> views) {
		super();
		this.views = views;
	}

	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager) container).addView(views.get(position), 0);
		View view = views.get(position);
		ImageView imageView = view.findViewById(R.id.iv_guid);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(pageClickListener == null) return;
				pageClickListener.onPageClick(position);
			}
		});
		return views.get(position);
	}


	interface ViewPageClickListener{
		void onPageClick(int position);
	}
}
