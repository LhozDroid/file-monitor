/**
 *
 */
package com.github.lhoz.monitor.file.example;

import com.github.lhoz.monitor.file.FileMonitor;
import com.github.lhoz.monitor.file.FileMonitorBuilder;
import com.github.lhoz.monitor.file.exception.InvalidPathException;

/**
 * @author Lhoz
 *
 */
public class FileMonitorExample {

	/**
	 * @param args
	 * @throws InvalidPathException
	 * @throws InterruptedException
	 */
	public static void main(final String[] args) throws InvalidPathException, InterruptedException {
		final FileMonitorExampleListener listener = new FileMonitorExampleListener();

		final FileMonitor monitor = new FileMonitorBuilder()//
				.withPaths("C:/test")//
				.withInterval(500)//
				.withRecursivity(false)//
				.withFileNameRegex(".*\\.txt")//
				.build();

		monitor.add(listener);
		monitor.start();

		while (true) {
			Thread.sleep(500);
		}
	}

}
