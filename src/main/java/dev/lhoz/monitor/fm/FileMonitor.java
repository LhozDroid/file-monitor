/**
 *
 */
package dev.lhoz.monitor.fm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

import dev.lhoz.monitor.fm.exception.InvalidPathException;
import dev.lhoz.monitor.fm.model.FileMonitorConfig;
import dev.lhoz.monitor.fm.model.FileMonitorEvent;
import dev.lhoz.monitor.fm.model.FileMonitorRecord;
import lombok.extern.log4j.Log4j2;

/**
 * @author Lhoz
 *
 */
@Log4j2
public class FileMonitor {
	private final FileMonitorConfig config;
	private final List<File> directories = Collections.synchronizedList(new ArrayList<File>());
	private final List<FileMonitorListener> listeners = Collections.synchronizedList(new ArrayList<FileMonitorListener>());
	private final Map<String, FileMonitorRecord> records = Collections.synchronizedMap(new HashMap<String, FileMonitorRecord>());
	private final AtomicReference<FileMonitorStatus> status = new AtomicReference<>(FileMonitorStatus.INACTIVE);

	/**
	 * @param config
	 * @throws InvalidPathException
	 */
	public FileMonitor(final FileMonitorConfig config) throws InvalidPathException {
		this.config = config;
		this.validatePaths();
		this.monitor();
	}

	/**
	 * @param listener
	 */
	public void addListener(final FileMonitorListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}

	/**
	 *
	 */
	public void clearListeners() {
		this.listeners.clear();
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
	public void removeListener(final FileMonitorListener listener) {
		if (listener != null && this.listeners.contains(listener)) {
			this.listeners.remove(listener);
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

		this.directories.parallelStream().forEach(directory -> {
			final IOFileFilter fileFilter = new RegexFileFilter(this.config.getFileNameRegex());
			files.addAll(FileUtils.listFiles(directory, fileFilter, this.config.isRecursive() ? TrueFileFilter.TRUE : null));
		});

		return files;
	}

	/**
	 *
	 */
	private void monitor() {
		Executors.newSingleThreadExecutor().execute(() -> {
			this.status.set(FileMonitorStatus.ACTIVE);

			// Does the initial scan
			this.registerFiles();

			// Starts the monitoring
			this.notifyOnStart();
			while (FileMonitorStatus.ACTIVE.equals(this.status.get()) || FileMonitorStatus.PAUSE.equals(this.status.get())) {
				if (FileMonitorStatus.ACTIVE.equals(this.status.get())) {
					// Gets the files
					final Collection<File> files = this.getFiles();

					// Checks for deleted files
					final List<FileMonitorRecord> deleted = this.records.values().parallelStream().filter(FileMonitorRecord::isDeleted).collect(Collectors.toList());
					deleted.parallelStream()//
							.forEach(record -> {
								this.records.remove(record.getPath());
								this.notifyOnFileDelete(record);
							});

					// Checks for modified files
					this.records.values().parallelStream()//
							.filter(FileMonitorRecord::isUpdated)//
							.forEach(this::notifyOnFileChange);

					// Checks for new files
					files.parallelStream()//
							.filter(file -> !this.records.containsKey(FilenameUtils.separatorsToSystem(file.getAbsolutePath())))//
							.map(FileMonitorRecord::new)//
							.forEach(record -> {
								this.records.put(record.getPath(), record);
								this.notifyOnFileCreate(record);
							});
				}

				// Sleeps
				try {
					Thread.sleep(this.config.getInterval());
				} catch (final Exception e) {
					FileMonitor.LOG.error(e.getLocalizedMessage(), e);
				}

			}

			this.status.set(FileMonitorStatus.INACTIVE);
			this.notifyOnStop();
		});
	}

	/**
	 * @param record
	 */
	private void notifyOnFileChange(final FileMonitorRecord record) {
		this.listeners.parallelStream().forEach(listener -> listener.onFileChange(new FileMonitorEvent(record.getFile())));
	}

	/**
	 * @param record
	 */
	private void notifyOnFileCreate(final FileMonitorRecord record) {
		this.listeners.parallelStream().forEach(listener -> listener.onFileCreate(new FileMonitorEvent(record.getFile())));
	}

	/**
	 * @param record
	 */
	private void notifyOnFileDelete(final FileMonitorRecord record) {
		this.listeners.parallelStream().forEach(listener -> listener.onFileDelete(new FileMonitorEvent(record.getFile())));
	}

	/**
	 *
	 */
	private void notifyOnStart() {
		this.listeners.parallelStream().forEach(FileMonitorListener::onStart);
	}

	/**
	 *
	 */
	private void notifyOnStop() {
		this.listeners.parallelStream().forEach(FileMonitorListener::onStop);
	}

	/**
	 *
	 */
	private void registerFiles() {
		this.directories.parallelStream().forEach(directory -> {
			final Collection<File> files = this.getFiles();
			files.parallelStream()//
					.map(FileMonitorRecord::new)//
					.forEach(record -> this.records.put(record.getPath(), record));
		});
	}

	/**
	 * @throws InvalidPathException
	 */
	private void validatePaths() throws InvalidPathException {
		final AtomicReference<InvalidPathException> exception = new AtomicReference<>();

		try {
			Arrays.stream(this.config.getPaths()).parallel().forEach(path -> {
				final File file = FileUtils.getFile(path);
				InvalidPathException e = null;

				if (!file.exists()) {
					e = new InvalidPathException("Invalid path " + path, new Exception());
				} else if (!file.isDirectory()) {
					e = new InvalidPathException("The path " + path + " is not a directory", new Exception());
				} else {
					this.directories.add(file);
				}

				if (e != null) {
					exception.set(e);
					throw new RuntimeException(e);
				}
			});
		} catch (final Exception e) {
			FileMonitor.LOG.error(e.getLocalizedMessage(), e);
		}

		if (exception.get() != null) {
			throw exception.get();
		}
	}
}
