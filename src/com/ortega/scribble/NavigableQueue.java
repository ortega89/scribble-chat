package com.ortega.scribble;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ortega.scribble.data.Message;

public class NavigableQueue extends AbstractQueue<Message> {

	private CopyOnWriteArrayList<Message> list;
	private int index;
	
	public NavigableQueue(CopyOnWriteArrayList<Message> list) {
		this.list = list;
		index = 0;
	}
	
	@Override
	public int size() {
		return list.size()-index;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override public boolean offer(Message e) {
		return list.add(e);
	}

	@Override
	public Message poll() {
		Message out = peek();
		if (out != null)
			index++;
		return out;
	}

	@Override
	public Message element() {
		if (list.isEmpty())
			throw new RuntimeException("No more elements in this queue");
		else
			return list.get(index);
	}

	@Override
	public Message peek() {
		Message msg = null;
		if (list.isEmpty())
			return null;
		else {
			while (index < list.size() && (msg = list.get(index)) == null)
				index++;
			if (index < list.size())
				return msg;
			else
				return null;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	public void clearDrawings() {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) != null) {
				switch (list.get(i).getType()) {
					case PENDOWN:
					case PENMOVE:
					case CLEAR:
						list.set(i, null);
				}
			}
		}
	}

	@Override
	public Iterator<Message> iterator() {
		return list.iterator();
	}

}
