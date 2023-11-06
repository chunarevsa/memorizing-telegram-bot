package org.memorizing.model.menu;

public enum EMode {
    FORWARD_TESTING(true),
    FORWARD_SELF_CHECK(true),
    FORWARD_MEMORIZING(true),
    BACKWARD_TESTING(false),
    BACKWARD_SELF_CHECK(false),
    BACKWARD_MEMORIZING(false);

    private final boolean isFromKeyMode;

    EMode(boolean isFromKeyMode) {
        this.isFromKeyMode = isFromKeyMode;
    }

    public boolean isFromKeyMode() {
        return isFromKeyMode;
    }

    public static EMode getModeByMenu(EMenu menu) {
        switch (menu) {
            case FORWARD_TESTING: return FORWARD_TESTING;
            case FORWARD_SELF_CHECK: return FORWARD_SELF_CHECK;
            case FORWARD_MEMORIZING: return FORWARD_MEMORIZING;
            case BACKWARD_TESTING: return BACKWARD_TESTING;
            case BACKWARD_SELF_CHECK: return BACKWARD_SELF_CHECK;
            case BACKWARD_MEMORIZING: return BACKWARD_MEMORIZING;
        }
        return null;
    }

}
