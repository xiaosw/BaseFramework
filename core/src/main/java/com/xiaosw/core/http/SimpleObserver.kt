package com.xiaosw.core.http

import android.os.Handler
import android.os.Looper

import com.xiaosw.common.util.LogUtil
import com.xiaosw.core.presenter.BasePrecenter

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @ClassName [SimpleObserver]
 * @Description
 *
 * @Date 2018-02-09.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */

open class SimpleObserver<T : Result<*>>(private var mPresenter: BasePrecenter<*, *>?) : Observer<T> {

    private var mDisposable: Disposable? = null

    private val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun onSubscribe(d: Disposable) {
        LogUtil.d(TAG, "onSubscribe: $d")
        mDisposable = d
        mPresenter?.apply {
            if (null != mDisposable) {
                addDisposable(mDisposable!!)
            }
            if (isBindView()) {
                handleLoading(true)
            }
        }
    }

    override fun onNext(result: T) {
        dissmiss()
        when {
            result == null ->
                LogUtil.e(TAG, "onNext: result is null!")
            result.code != HttpConfig.HTTP_OK ->
                LogUtil.e(TAG, "onNext: ${result.code}")
            else ->
                doNext(result)
        }
    }

    override fun onError(e: Throwable) {
        dissmiss()
        LogUtil.e(TAG, "onError: ", e)
    }

    override fun onComplete() {}

    open fun doNext(result: T) {}

    protected fun dissmiss() {
        mDisposable?.apply {
            if (!isDisposed) {
                dispose()
            }
        }
        mPresenter?.apply {
            mDisposable?.also {
                removeDisposable(it)
            }
            if(isBindView()) {
                handleLoading(false)
            }
        }
        mDisposable = null
        mPresenter = null
    }

    /**
     * showLoading and dessmissLoading in main thread.
     *
     * @param isShow
     */
    private fun handleLoading(isShow: Boolean) {
        if (Thread.currentThread().id == 1L) {
            handlerLoadingImpl(isShow)
        } else {
            mHandler.post { handlerLoadingImpl(isShow) }
        }
    }

    private fun handlerLoadingImpl(isShow: Boolean) {
        mPresenter?.getView()?.apply {
            if (isShow) {
                showLoading()
            } else {
                dissmissLoading()
            }
        }
    }

    companion object {
        private val TAG = "SimpleObserver"
    }

}
