package me.corriekay.pokegoutil.DATA.models;

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
     * Get the error message.
     *
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Is result successful.
     *
     * @return result state
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Set the error message.
     *
     * @param errorMessage error message
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Set the success state of result.
     *
     * @param success success state of result
     */
    public void setSuccess(final boolean success) {
        this.success = success;
    }
}
