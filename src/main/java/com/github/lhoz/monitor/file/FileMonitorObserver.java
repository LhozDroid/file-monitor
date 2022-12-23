/**
 *
 */
package com.github.lhoz.monitor.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.lhoz.monitor.file.model.FileMonitorEvent;
import com.github.lhoz.monitor.file.model.FileMonitorRecord;

/**
 * @author Lhoz
 *
 */
public class FileMonitorObserver {
	private final List<FileMonitorListener> listeners = Collections.synchronizedList(new ArrayList<FileMonitorListener>());

	/**
	 * @param listener
	 */
	public void add(final FileMonitorListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}

	/**
	 *
	 */
	public void clear() {
		this.listeners.clear();
	}

	/**
	 * @param record
	 */
	public void notifyOnFileChange(final FileMonitorRecord record) {
		this.listeners.parallelStream().forEach(listener -> listener.onFileChange(new FileMonitorEvent(record.getFile())));
	}

	/**
	 * @param record
	 */
	public void notifyOnFileCreate(final FileMonitorRecord record) {
		this.listeners.parallelStream().forEach(listener -> listener.onFileCreate(new FileMonitorEvent(record.getFile())));
	}

	/**
	 * @param record
	 */
	public void notifyOnFileDelete(final FileMonitorRecord record) {
		this.listeners.parallelStream().forEach(listener -> listener.onFileDelete(new FileMonitorEvent(record.getFile())));
	}

	/**
	 *
	 */
	public void notifyOnStart() {
		this.listeners.parallelStream().forEach(FileMonitorListener::onStart);
	}

	/**
	 *
	 */
	public void notifyOnStop() {
		this.listeners.parallelStream().forEach(FileMonitorListener::onStop);
	}

	/**
	 * @param listener
	 */
	public void remove(final FileMonitorListener listener) {
		if (listener != null && this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}
}
