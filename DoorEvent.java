public class DoorEvent extends Event {
    public DoorEvent(int x, int y) {
        super(x, y, 18, true);
    }

    public String toString() {
        return "DOOR:" + super.toString();
    }
}
