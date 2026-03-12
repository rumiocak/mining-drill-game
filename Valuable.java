import javafx.scene.image.Image;

public class Valuable {
    private String name;
    private Image image;
    private int haul;
    private int money;

    /**
     * Constructs a valuable item with the specified attributes.
     *
     * @param name The name of the valuable item.
     * @param image The image associated with the valuable item.
     * @param haul The haul value of the valuable item.
     * @param money The money value of the valuable item.
     */
    public Valuable(String name, Image image, int haul, int money) {
        this.name = name;
        this.image = image;
        this.haul = haul;
        this.money = money;
    }

    /**
     * Retrieves the image associated with the valuable item.
     *
     * @return The Image object representing the valuable item's image.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Retrieves the haul value of the valuable item.
     *
     * @return The haul value of the valuable item.
     */
    public int getHaul() {
        return haul;
    }

    /**
     * Retrieves the money value of the valuable item.
     *
     * @return The money value of the valuable item.
     */
    public int getMoney() {
        return money;
    }
}
