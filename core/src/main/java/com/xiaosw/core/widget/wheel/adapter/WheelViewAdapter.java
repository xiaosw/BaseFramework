/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.xiaosw.core.widget.wheel.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * <p><br/>ClassName : {@link WheelViewAdapter}
 * <br/>Description : 滚轮适配器
 * <br/>　
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2018-01-17</p>
 */
public interface WheelViewAdapter {
    int getItemsCount();

    View getItem(int index, View convertView, ViewGroup parent);

    View getEmptyItem(View convertView, ViewGroup parent);

    /**
     * Register an observer that is called when changes happen to the data used
     * by this mWheelAdapter.
     *
     * @param observer the observer to be registered
     */
    void registerDataSetObserver(DataSetObserver observer);

    /**
     * Unregister an observer that has previously been registered
     *
     * @param observer the observer to be unregistered
     */
    void unregisterDataSetObserver(DataSetObserver observer);

    /**
     * remove all an observer that has previously been registered
     */
    void clearDataSetObserver();

    class WheelScroller {
        /**
         * Scrolling mListener interface
         */
        public interface ScrollingListener {
            /**
             * Scrolling callback called when scrolling is performed.
             *
             * @param distance the distance to scroll
             */
            void onScroll(int distance);

            /**
             * Starting callback called when scrolling is started
             */
            void onStarted();

            /**
             * Finishing callback called after justifying
             */
            void onFinished();

            /**
             * Justifying callback called to justify a view when scrolling is ended
             */
            void onJustify();
        }

        /**
         * Scrolling duration
         */
        private static final int SCROLLING_DURATION = 400;

        /**
         * Minimum delta for scrolling
         */
        public static final int MIN_DELTA_FOR_SCROLLING = 1;

        // Listener
        private ScrollingListener mListener;

        // Context
        private Context context;

        // Scrolling
        private GestureDetector gestureDetector;
        private Scroller mScroller;
        private int lastScrollY;
        private float lastTouchedY;
        private boolean isScrollingPerformed;

        /**
         * Constructor
         *
         * @param context  the current context
         * @param listener the scrolling mListener
         */
        public WheelScroller(Context context, ScrollingListener listener) {
            gestureDetector = new GestureDetector(context, gestureListener);
            gestureDetector.setIsLongpressEnabled(false);

            mScroller = new Scroller(context);

            this.mListener = listener;
            this.context = context;
        }

        /**
         * Set the the specified scrolling interpolator
         *
         * @param interpolator the interpolator
         */
        public void setInterpolator(Interpolator interpolator) {
            mScroller.forceFinished(true);
            mScroller = new Scroller(context, interpolator);
        }

        /**
         * Scroll the wheel
         *
         * @param distance the scrolling distance
         * @param time     the scrolling duration
         */
        public void scroll(int distance, int time) {
            mScroller.forceFinished(true);

            lastScrollY = 0;

            mScroller.startScroll(0, 0, 0, distance, time != 0 ? time : SCROLLING_DURATION);
            setNextMessage(mMessageScroll);

            startScrolling();
        }

        /**
         * Stops scrolling
         */
        public void stopScrolling() {
            mScroller.forceFinished(true);
        }

        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchedY = event.getY();
                    mScroller.forceFinished(true);
                    clearMessages();
                    break;

                case MotionEvent.ACTION_MOVE:
                    // perform scrolling
                    int distanceY = (int) (event.getY() - lastTouchedY);
                    if (distanceY != 0) {
                        startScrolling();
                        mListener.onScroll(distanceY);
                        lastTouchedY = event.getY();
                    }
                    break;
                default:
                    break;
            }

            if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
                justify();
            }

            return true;
        }

        // gesture mListener
        private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Do scrolling in onTouchEvent() since onScroll() are not call
                // immediately
                // when user touch and move the wheel
                return true;
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                lastScrollY = 0;
                final int maxY = 0x7FFFFFFF;
                final int minY = -maxY;
                mScroller.fling(0, lastScrollY, 0, (int) -velocityY, 0, 0, minY, maxY);
                setNextMessage(mMessageScroll);
                return true;
            }
        };

        // Messages
        private final int mMessageScroll = 0;
        private final int mMessageJustify = 1;

        /**
         * Set next message to queue. Clears queue before.
         *
         * @param message the message to set
         */
        private void setNextMessage(int message) {
            clearMessages();
            mAnimationHandler.sendEmptyMessage(message);
        }

        /**
         * Clears messages from queue
         */
        private void clearMessages() {
            mAnimationHandler.removeMessages(mMessageScroll);
            mAnimationHandler.removeMessages(mMessageJustify);
        }

        // animation handler
        private Handler mAnimationHandler = new Handler() {
            public void handleMessage(Message msg) {
                mScroller.computeScrollOffset();
                int currY = mScroller.getCurrY();
                int delta = lastScrollY - currY;
                lastScrollY = currY;
                if (delta != 0) {
                    mListener.onScroll(delta);
                }

                // scrolling is not finished when it comes to final Y
                // so, finish it manually
                if (Math.abs(currY - mScroller.getFinalY()) < MIN_DELTA_FOR_SCROLLING) {
                    currY = mScroller.getFinalY();
                    mScroller.forceFinished(true);
                }
                if (!mScroller.isFinished()) {
                    mAnimationHandler.sendEmptyMessage(msg.what);
                } else if (msg.what == mMessageScroll) {
                    justify();
                } else {
                    finishScrolling();
                }
            }
        };

        /**
         * Justifies wheel
         */
        private void justify() {
            mListener.onJustify();
            setNextMessage(mMessageJustify);
        }

        /**
         * Starts scrolling
         */
        private void startScrolling() {
            if (!isScrollingPerformed) {
                isScrollingPerformed = true;
                mListener.onStarted();
            }
        }

        /**
         * Finishes scrolling
         */
        void finishScrolling() {
            if (isScrollingPerformed) {
                mListener.onFinished();
                isScrollingPerformed = false;
            }
        }
    }

}
