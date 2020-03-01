package example.com.gauss.DTO;

public class Item {
    private String itemname;
    private String description;
    private int amount;
    private String username;

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public int getAmount() {
        return amount;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public String getItemname() {
        return itemname;
    }
}
