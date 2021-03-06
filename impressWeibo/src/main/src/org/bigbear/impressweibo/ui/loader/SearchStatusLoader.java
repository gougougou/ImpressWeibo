package org.bigbear.impressweibo.ui.loader;

import org.bigbear.impressweibo.bean.SearchStatusListBean;
import org.bigbear.impressweibo.dao.search.SearchDao;
import org.bigbear.impressweibo.support.error.WeiboException;

import android.content.Context;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: qii
 * Date: 13-5-12
 */
public class SearchStatusLoader extends AbstractAsyncNetRequestTaskLoader<SearchStatusListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String searchWord;
    private String page;

    public SearchStatusLoader(Context context, String token, String searchWord, String page) {
        super(context);
        this.token = token;
        this.searchWord = searchWord;
        this.page = page;
    }

    public SearchStatusListBean loadData() throws WeiboException {
        SearchDao dao = new SearchDao(token, searchWord);
        dao.setPage(page);

        SearchStatusListBean result = null;
        lock.lock();

        try {
            result = dao.getStatusList();
        } finally {
            lock.unlock();
        }

        return result;
    }
}


