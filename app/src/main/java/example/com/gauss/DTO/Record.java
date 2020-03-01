package example.com.gauss.DTO;

public class Record {
    private String itemname;
    private String username;
    private String last_use_time;
    private int state;

    public void setState(int state) {
        this.state = state;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLast_use_time(String last_use_time) {
        this.last_use_time = last_use_time;
    }

    public String getItemname() {
        return itemname;
    }

    public String getUsername() {
        return username;
    }

    public String getLast_use_time() {
        return last_use_time;
    }

    public int getState() {
        return state;
    }
}
