/**
 *
 */
package dev.lhoz.monitor.fm.model;

import lombok.Data;

/**
 * @author Lhoz
 *
 */
@Data
public class FileMonitorConfig {
	private String fileNameRegex = ".*";
	private long interval = 250;
	private String[] paths = { "/" };
	private boolean recursive = false;
}
