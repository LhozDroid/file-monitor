/**
 *
 */
package com.github.lhoz.monitor.file;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FileUtils;

import com.github.lhoz.monitor.file.exception.InvalidPathException;

import lombok.extern.log4j.Log4j2;

/**
 * @author Lhoz
 *
 */
@Log4j2
public class FileMonitorBuilder extends FileMonitor {
	/**
	 *
	 */
	public FileMonitorBuilder() {
	}

	/**
	 * @return
	 */
	public FileMonitor build() {
		return this;
	}

	/**
	 * @param regex
	 * @return
	 */
	public FileMonitorBuilder withFileNameRegex(final String regex) {
		this.config.setFileNameRegex(regex);
		return this;
	}

	/**
	 * @param interval
	 * @return
	 */
	public FileMonitorBuilder withInterval(final int interval) {
		if (interval > 0) {
			this.config.setInterval(interval);
		}
		return this;
	}

	/**
	 * @param paths
	 * @return
	 * @throws InvalidPathException
	 */
	public FileMonitorBuilder withPaths(final String... paths) throws InvalidPathException {
		if (paths.length > 0) {
			this.config.getDirectories().clear();

			final AtomicReference<InvalidPathException> exception = new AtomicReference<>();
			try {
				Arrays.stream(paths).parallel().forEach(path -> {
					final File file = FileUtils.getFile(path);
					InvalidPathException e = null;

					if (!file.exists()) {
						e = new InvalidPathException("Invalid path " + path, new Exception());
					} else if (!file.isDirectory()) {
						e = new InvalidPathException("The path " + path + " is not a directory", new Exception());
					} else {
						this.config.getDirectories().add(file);
					}

					if (e != null) {
						exception.set(e);
						throw new RuntimeException(e);
					}
				});
			} catch (final Exception e) {
				FileMonitorBuilder.LOG.debug(e.getLocalizedMessage(), e);
				FileMonitorBuilder.LOG.error(e.getLocalizedMessage());
			}

			if (exception.get() != null) {
				this.config.getDirectories().clear();
				throw exception.get();
			}
		}

		return this;
	}

	/**
	 * @param recursive
	 * @return
	 */
	public FileMonitorBuilder withRecursivity(final boolean recursive) {
		this.config.setRecursive(recursive);
		return this;
	}
}
