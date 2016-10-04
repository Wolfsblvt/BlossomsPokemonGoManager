package me.corriekay.pokegoutil.data.models;

/**
 * Used for returning the result of operations in BPM.
 */
public class BpmResult {
    private boolean success;
    private String errorMessage;

    /**
     * Instantiate a success BpmResult.
     */
    public BpmResult() {
        success = true;
    }

    /**
     * Instantiate a non success BpmOperationResult with an error message and error type.
     *
     * @param errorMessage error message
     */
    public BpmResult(final String errorMessage) {
        success = false;
        this.errorMessage = errorMessage;
    }

    /**
     * Get the result error message.
     *
     * @return result error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get if result is successful.
     *
     * @return is result successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Set the result error message.
     *
     * @param errorMessage result error message
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Set if result is successful
     *
     * @param success is result successful
     */
    public void setSuccess(final boolean success) {
        this.success = success;
    }
}
