/**
 * 
 */
package com.expert.util;

import java.util.Date;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年5月15日
 * @ClassName SyncTest
 */
public class SyncTest {
	public static void main(String[] args) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SyncClazz.test1();
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				SyncClazz.test0();
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				SyncClazz.test2();
			}
		}).start();
	}
}

class SyncClazz {

	public static void test0() {
		System.out.println(new Date() + "----test0----Start");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(new Date() + "----test0----End");
	}

	public static synchronized void test1() {
		System.out.println(new Date() + "----test1----Start");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(new Date() + "----test1----End");
	}

	public static synchronized void test2() {
		System.out.println(new Date() + "----test2----Start");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(new Date() + "----test2----End");
	}
}
