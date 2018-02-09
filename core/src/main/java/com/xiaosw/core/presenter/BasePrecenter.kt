package com.xiaosw.core.presenter

import com.xiaosw.core.activity.view.IView
import com.xiaosw.core.model.IModel
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference
import java.util.*

/**
 * @ClassName {@link BasePrecenter}
 * @Description
 *
 * @Date 2018-02-09.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
abstract class BasePrecenter<V : IView, M : IModel> {

    protected var mView: V? = null
    protected var mModel: M? = null
    private val mDisposables by lazy {
        ArrayList<WeakReference<Disposable>>()
    }

    fun bindView(view: V) {
        this.mView = view
        if (isBindView()) {
            mModel = bindModel()
        }
    }

    fun unbindView() {
        mModel?.cancel()
        this.mModel = null
        this.mView = null
        clear()
    }

    fun isBindView() = mView != null

    fun isBindModel() = mModel != null

    fun getView() = mView

    fun getModel() = mModel

    protected abstract fun bindModel() : M?

    // Disposable manager
    @Synchronized
    fun addDisposable(disposable: Disposable): Boolean {
        var needAdd = true
        if (mDisposables.size == 0) {
            mDisposables.add(WeakReference(disposable))
        } else {
            var recycler: MutableList<WeakReference<Disposable>>? = null
            for (ref in mDisposables) {
                if (ref.get() == null || ref.get()!!.isDisposed) {
                    if (null == recycler) {
                        recycler = ArrayList()
                    }
                    recycler.add(ref)
                } else if (ref.get() === disposable) {
                    needAdd = false
                }
            }
        }
        if (needAdd) {
            mDisposables.add(WeakReference(disposable))
        }
        return needAdd
    }

    @Synchronized
    fun removeDisposable(disposable: Disposable) {
        val recycler = ArrayList<WeakReference<Disposable>>()
        for (ref in mDisposables) {
            if (ref.get() == null
                    || ref.get()!!.isDisposed
                    || ref.get() === disposable) {
                recycler.add(ref)
            }
        }
        recycler.removeAll(recycler)
    }

    fun clear() {
        for (ref in mDisposables) {
            if (ref != null) {
                val disposable = ref.get()
                if (null != disposable && !disposable.isDisposed) {
                    disposable.dispose()
                }
            }
        }
        if (mDisposables.size > 0) {
            mDisposables.clear()
        }
    }

}