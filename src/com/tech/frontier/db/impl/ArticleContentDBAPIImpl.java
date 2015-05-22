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

package com.tech.frontier.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tech.frontier.db.ArticleContentDBAPI;
import com.tech.frontier.db.cmd.Command;
import com.tech.frontier.db.cmd.Command.NoReturnCmd;
import com.tech.frontier.db.helper.DatabaseHelper;
import com.tech.frontier.entities.ArticleDetail;
import com.tech.frontier.listeners.DataListener;

/**
 * 操作文章内容相关的数据库实现
 * 
 * @author mrsimple
 */
class ArticleContentDBAPIImpl extends AbsDBAPI<String> implements ArticleContentDBAPI {

    public ArticleContentDBAPIImpl() {
        super(DatabaseHelper.TABLE_ARTICLE_CONTENT);
    }

    @Override
    public void saveContent(final ArticleDetail detail) {
        sDbExecutor.execute(new NoReturnCmd() {
            @Override
            protected Void doInBackground(SQLiteDatabase database) {
                database.insertWithOnConflict(mTableName, null,
                        toContentValues(detail),
                        SQLiteDatabase.CONFLICT_REPLACE);
                return null;
            }
        });
    }

    protected ContentValues toContentValues(ArticleDetail detail) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("post_id", detail.postId);
        contentValues.put("content", detail.content);
        return contentValues;
    }

    @Override
    public void loadArticleContent(final String postId, final DataListener<String> listener) {
        sDbExecutor.execute(new Command<String>(listener) {
            @Override
            protected String doInBackground(SQLiteDatabase database) {
                Cursor cursor = database.query(mTableName, new String[] {
                        "content"
                }, "post_id=?", new String[] {
                        postId
                }, null, null, null);
                String result = queryArticleCotent(cursor);
                cursor.close();
                return result;
            }
        });
    }

    private String queryArticleCotent(Cursor cursor) {
        return cursor.moveToNext() ? cursor.getString(0) : "";
    }
}
