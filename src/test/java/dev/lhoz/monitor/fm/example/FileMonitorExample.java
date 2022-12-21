/**
 *
 */
package dev.lhoz.monitor.fm.example;

import dev.lhoz.monitor.fm.FileMonitor;
import dev.lhoz.monitor.fm.FileMonitorBuilder;
import dev.lhoz.monitor.fm.exception.InvalidPathException;

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
