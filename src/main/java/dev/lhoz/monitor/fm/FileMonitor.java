/**
 *
 */
package dev.lhoz.monitor.fm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import dev.lhoz.monitor.fm.model.FileMonitorConfig;
import dev.lhoz.monitor.fm.model.FileMonitorRecord;
import lombok.extern.log4j.Log4j2;

/**
 * @author Lhoz
 *
 */
@Log4j2
public class FileMonitor {
	private final FileMonitorObserver observer = new FileMonitorObserver();

	private final Map<String, FileMonitorRecord> records = Collections.synchronizedMap(new HashMap<String, FileMonitorRecord>());
	private final AtomicReference<FileMonitorStatus> status = new AtomicReference<>(FileMonitorStatus.INACTIVE);

	protected final FileMonitorConfig config = new FileMonitorConfig();

	/**
	 *
	 */
	protected FileMonitor() {
	}

	/**
	 * @param listener
	 */
	public void add(final FileMonitorListener listener) {
		if (listener != null) {
			this.observer.add(listener);
		}
	}

	/**
	 *
	 */
	public void clear() {
		this.observer.clear();
	}

	/**
	 *
	 */
	public void pause() {
		this.status.set(FileMonitorStatus.PAUSE);
	}

	/**
	 * @param listener
	 */
	public void remove(final FileMonitorListener listener) {
		this.observer.remove(listener);
	}

	/**
	 *
	 */
	public void start() {
		if (FileMonitorStatus.INACTIVE.equals(this.status.get())) {
			Executors.newSingleThreadExecutor().execute(() -> {
				this.status.set(FileMonitorStatus.ACTIVE);

				// Does the initial scan
				this.registerFiles();

				// Starts the monitoring
				this.observer.notifyOnStart();
				while (FileMonitorStatus.ACTIVE.equals(this.status.get()) || FileMonitorStatus.PAUSE.equals(this.status.get())) {
					if (FileMonitorStatus.ACTIVE.equals(this.status.get())) {
						// Gets the files
						final Collection<File> files = this.getFiles();

						// Checks for deleted files
						final List<FileMonitorRecord> deleted = this.records.values().parallelStream().filter(FileMonitorRecord::isDeleted).collect(Collectors.toList());
						deleted.parallelStream()//
								.forEach(record -> {
									this.records.remove(record.getPath());
									this.observer.notifyOnFileDelete(record);
								});

						// Checks for modified files
						this.records.values().parallelStream()//
								.filter(FileMonitorRecord::isUpdated)//
								.forEach(record -> this.observer.notifyOnFileChange(record));

						// Checks for new files
						files.parallelStream()//
								.filter(file -> !this.records.containsKey(FilenameUtils.separatorsToSystem(file.getAbsolutePath())))//
								.map(FileMonitorRecord::new)//
								.forEach(record -> {
									this.records.put(record.getPath(), record);
									this.observer.notifyOnFileCreate(record);
								});
					}

					this.sleep();

				}

				this.status.set(FileMonitorStatus.INACTIVE);
				this.observer.notifyOnStop();
			});
		}
	}

	/**
	 *
	 */
	public void stop() {
		this.status.set(FileMonitorStatus.INACTIVE);
	}

	/**
	 *
	 */
	public void unpause() {
		this.status.set(FileMonitorStatus.ACTIVE);
	}

	/**
	 * @return
	 */
	private Collection<File> getFiles() {
		final Collection<File> files = Collections.synchronizedCollection(new ArrayList<File>());

		this.config.getDirectories().parallelStream().forEach(directory -> {
			final IOFileFilter fileFilter = new RegexFileFilter(this.config.getFileNameRegex());
			files.addAll(FileUtils.listFiles(directory, fileFilter, this.config.isRecursive() ? TrueFileFilter.TRUE : null));
		});

		return files;
	}

	/**
	 *
	 */
	private void registerFiles() {
		this.config.getDirectories().parallelStream().forEach(directory -> {
			final Collection<File> files = this.getFiles();
			files.parallelStream()//
					.map(FileMonitorRecord::new)//
					.forEach(record -> this.records.put(record.getPath(), record));
		});
	}

	/**
	 * 
	 */
	private void sleep() {
		try {
			Thread.sleep(this.config.getInterval());
		} catch (final Exception e) {
			FileMonitor.LOG.error(e.getLocalizedMessage(), e);
		}
	}
}
