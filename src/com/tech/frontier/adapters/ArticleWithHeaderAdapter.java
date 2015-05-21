/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.tech.frontier.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tech.frontier.R;
import com.tech.frontier.listeners.DataListener;
import com.tech.frontier.models.entities.Article;
import com.tech.frontier.models.entities.Recommend;
import com.tech.frontier.net.RecomendAPI;
import com.tech.frontier.net.RecomendAPIImpl;
import com.tech.frontier.widgets.AutoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class ArticleWithHeaderAdapter extends ArticleAdapter {

    private static final int HEADER = 0;

    RecomendAPI api = new RecomendAPIImpl();

    public ArticleWithHeaderAdapter(List<Article> dataSet) {
        super(dataSet);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ArticleViewHolder) {
            bindViewForArticle(viewHolder, position);
        } else if (viewHolder instanceof HeaderViewHolder) {
            bindViewForHeader(viewHolder);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == HEADER) {
            return createHeaderViewHolder(viewGroup);
        }
        return createArticleViewHolder(viewGroup);
    }

    private HeaderViewHolder createHeaderViewHolder(ViewGroup viewGroup) {
        View headerView = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.auto_slider, viewGroup, false);
        return new HeaderViewHolder(headerView);
    }
    final List<Recommend> recommends = new ArrayList<Recommend>();
    
    private void bindViewForHeader(ViewHolder viewHolder) {
        Log.e("", "### 获取header 数据 : ");
        final HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        if (recommends.size() == 0) {
            api.fetchRecomends(new DataListener<List<Recommend>>() {

                @Override
                public void onComplete(List<Recommend> result) {
                    Log.e("", "### 已经获取header 数据 : ");
                    initAutoSlider(headerViewHolder, result);
                }
            });
        }
    }

    HeaderImageAdapter mImagePagerAdapter;

    private void initAutoSlider(HeaderViewHolder headerViewHolder, List<Recommend> result) {
        AutoScrollViewPager viewPager = headerViewHolder.autoScrollViewPager;
        if (mImagePagerAdapter == null) {
            mImagePagerAdapter = new HeaderImageAdapter(viewPager, result);
            mImagePagerAdapter.setInfiniteLoop(true);
            // 设置ViewPager
            viewPager.setInterval(15000);
            if (result.size() > 0) {
                viewPager.startAutoScroll();
                viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2
                        % result.size());
            }

            viewPager.setAdapter(mImagePagerAdapter);
        }

    }

    /*
     * 多个了一个Header
     * @see com.tech.frontier.adapters.BaseArticleAdapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        return mArticles == null ? 1 : mArticles.size() + 1;
    }

    @Override
    protected Article getItem(int position) {
        // 因为第一个添加了一个Header,因此数据索引要减去1
        return mArticles.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (HEADER == position) {
            return 0;
        }

        return 1;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        AutoScrollViewPager autoScrollViewPager;

        public HeaderViewHolder(View view) {
            super(view);
            autoScrollViewPager = (AutoScrollViewPager) view.findViewById(R.id.slide_viewpager);
        }
    }

}