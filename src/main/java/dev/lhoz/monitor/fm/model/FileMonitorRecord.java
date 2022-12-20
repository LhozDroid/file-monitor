/**
 *
 */
package dev.lhoz.monitor.fm.model;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import lombok.Getter;

/**
 * @author Lhoz
 *
 */
public class FileMonitorRecord {
	private final @Getter File file;
	private Date lastUpdate;
	private final @Getter String path;

	/**
	 * @param file
	 */
	public FileMonitorRecord(final File file) {
		this.file = file;
		this.path = FilenameUtils.separatorsToSystem(file.getAbsolutePath());
		this.lastUpdate = new Date(file.lastModified());
	}

	/**
	 * @return
	 */
	public boolean isDeleted() {
		return !this.file.exists();
	}

	/**
	 * @return
	 */
	public boolean isUpdated() {
		boolean isUpdated = false;
		final Date update = new Date(this.file.lastModified());
		if (this.lastUpdate.before(update)) {
			isUpdated = true;
			this.lastUpdate = update;
		}

		return isUpdated;
	}
}
