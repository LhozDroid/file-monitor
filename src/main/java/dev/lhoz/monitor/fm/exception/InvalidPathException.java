/**
 *
 */
package dev.lhoz.monitor.fm.exception;

/**
 * @author Lhoz
 *
 */
public class InvalidPathException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidPathException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
