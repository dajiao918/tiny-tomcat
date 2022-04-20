package org.apache.catalina.util;

/**
 * 线程安全的队列
 * @author: Mr.Yu
 * @create: 2022-03-30 16:51
 **/
public class SynchronizedQueue<E> {

    private Object[] es;
    private int insert;
    private int remove;
    private int size;
    private int capacity;

    public SynchronizedQueue(){
        this(10);
    }

    public SynchronizedQueue(int capacity) {
        this.capacity = capacity;
        es = new Object[capacity];
        insert = remove = 0;
        size = 0;
    }

    public synchronized void offer(E e) {
        es[insert] = e;
        insert = (insert + 1) % capacity;
        size ++;
        if (insert == remove) {
            resize(capacity * 2);
        }
    }

    public synchronized E pop() {
        if (insert == remove) {
            return null;
        }
        E e = (E) es[remove];
        remove = (remove + 1) % size;
        size --;
        return e;
    }

    private void resize(int newCapacity) {
        Object[] newEs = new Object[newCapacity];
        System.arraycopy(es, insert, newEs, 0, size);
        es = newEs;
        insert = size;
        remove = 0;
        capacity = newCapacity;
    }

    public int getSize() {
        return size;
    }
}
