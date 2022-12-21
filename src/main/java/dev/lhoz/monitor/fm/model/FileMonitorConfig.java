/**
 *
 */
package dev.lhoz.monitor.fm.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;

/**
 * @author Lhoz
 *
 */
@Data
public class FileMonitorConfig {
	private final List<File> directories = Collections.synchronizedList(new ArrayList<File>());
	private String fileNameRegex = ".*";
	private long interval = 250;
	private boolean recursive = false;
}
