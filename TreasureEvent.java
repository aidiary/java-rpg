public class TreasureEvent extends Event {
    private String itemName;

    public TreasureEvent(int x, int y, String itemName) {
        super(x, y, 17, false);
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public String toString() {
        return "TREASURE:" + super.toString() + ":" + itemName;
    }
}
