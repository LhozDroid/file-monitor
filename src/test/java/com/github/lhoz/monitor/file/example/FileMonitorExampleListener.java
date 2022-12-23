/**
 *
 */
package com.github.lhoz.monitor.file.example;

import com.github.lhoz.monitor.file.FileMonitorListener;
import com.github.lhoz.monitor.file.model.FileMonitorEvent;

import lombok.extern.log4j.Log4j2;

/**
 * @author Lhoz
 *
 */
@Log4j2
public class FileMonitorExampleListener extends FileMonitorListener {

	/**
	 *
	 */
	@Override
	public void onFileChange(final FileMonitorEvent event) {
		FileMonitorExampleListener.LOG.info("File changed " + event.getPath());
	}

	/**
	 *
	 */
	@Override
	public void onFileCreate(final FileMonitorEvent event) {
		FileMonitorExampleListener.LOG.info("File created " + event.getPath());
	}

	/**
	 *
	 */
	@Override
	public void onFileDelete(final FileMonitorEvent event) {
		FileMonitorExampleListener.LOG.info("File deleted " + event.getPath());
	}

	/**
	 *
	 */
	@Override
	public void onStart() {
		FileMonitorExampleListener.LOG.info("Monitor start");
	}

	/**
	 *
	 */
	@Override
	public void onStop() {
		FileMonitorExampleListener.LOG.info("Monitor stop");
	}
}
