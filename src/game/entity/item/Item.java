package game.entity.item;

public class Item {
    private String name;
    
    private String decription; 

    public Item(String name, String decription) {
        this.name = name;
        this.decription = decription;
    }

    public String getName() { return name; }
	public String getDecription() { return decription; }
	public Item setDecription(String decription) { this.decription = decription; return this; }
	public Item setName(String name) { this.name = name; return this; }
    
}
