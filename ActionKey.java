public class ActionKey {
    // constants for key mode
    // isPressed() method returns true while key is pressed
    public static final int NORMAL = 0;

    // isPressed() method returns true
    // only when a key is pressed for the first time
    public static final int DETECT_INITIAL_PRESS_ONLY = 1;

    // constants key state
    private static final int STATE_RELEASED = 0;
    private static final int STATE_PRESSED = 1;
    private static final int STATE_WAITING_FOR_RELEASE = 2;

    // current key's mode
    private int mode;
    // the number of the time that the key was pressed
    private int amount;
    // current key's state
    private int state;

    public ActionKey() {
        this(NORMAL);
    }

    public ActionKey(int mode) {
        this.mode = mode;
        reset();
    }

    // reset key's state
    public void reset() {
        state = STATE_RELEASED;
        amount = 0;
    }

    public void press() {
        if (state != STATE_WAITING_FOR_RELEASE) {
            amount++;
            state = STATE_PRESSED;
        }
    }

    public void release() {
        state = STATE_RELEASED;
    }

    public boolean isPressed() {
        if (amount != 0) {
            if (state == STATE_RELEASED) {
                amount = 0;
            } else if (mode == DETECT_INITIAL_PRESS_ONLY) {
                state = STATE_WAITING_FOR_RELEASE;
                amount = 0;
            }
            return true;
        }
        return false;
    }
}
