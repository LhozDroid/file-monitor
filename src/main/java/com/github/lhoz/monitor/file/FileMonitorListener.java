/**
 *
 */
package com.github.lhoz.monitor.file;

import com.github.lhoz.monitor.file.model.FileMonitorEvent;

/**
 * @author Lhoz
 *
 */
public abstract class FileMonitorListener {
	/**
	 * @param event
	 */
	public abstract void onFileChange(FileMonitorEvent event);

	/**
	 * @param event
	 */
	public abstract void onFileCreate(FileMonitorEvent event);

	/**
	 * @param event
	 */
	public abstract void onFileDelete(FileMonitorEvent event);

	/**
	 *
	 */
	public abstract void onStart();

	/**
	 *
	 */
	public abstract void onStop();
}
