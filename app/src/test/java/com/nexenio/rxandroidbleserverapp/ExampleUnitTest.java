package com.nexenio.rxandroidbleserverapp;

import org.junit.Test;

import io.reactivex.rxjava3.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void publishSubjects() {
        PublishSubject<Integer> firstPublishSubject = PublishSubject.create();
        PublishSubject<Integer> secondPublishSubject = PublishSubject.create();

        firstPublishSubject.subscribe(secondPublishSubject);
        secondPublishSubject.subscribe(integer -> System.out.println(1));

        firstPublishSubject.onNext(1);
    }

}