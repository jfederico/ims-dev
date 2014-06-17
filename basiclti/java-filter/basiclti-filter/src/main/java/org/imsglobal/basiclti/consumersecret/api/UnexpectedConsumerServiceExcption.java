package org.imsglobal.basiclti.consumersecret.api;

/**
 * RuntimeException indicating that some unexpected and unrecoverable error has occurred in the consumer secret service.
 * 
 * @author Roland Groen (roland@edia.nl)
 *
 */
public class UnexpectedConsumerServiceExcption extends RuntimeException {

	/**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8332692966206669521L;

	public UnexpectedConsumerServiceExcption() {
	    super();
    }

	public UnexpectedConsumerServiceExcption(String message, Throwable cause) {
	    super(message, cause);
    }

	public UnexpectedConsumerServiceExcption(String message) {
	    super(message);
    }

	public UnexpectedConsumerServiceExcption(Throwable cause) {
	    super(cause);
    }

}
