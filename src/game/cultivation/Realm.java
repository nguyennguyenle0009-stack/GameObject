package game.cultivation;

public enum Realm {
    LUYEN_KHI,
    TRUC_CO;

    public Realm next() {
        return switch (this) {
            case LUYEN_KHI -> TRUC_CO;
            case TRUC_CO -> TRUC_CO;
        };
    }
}
