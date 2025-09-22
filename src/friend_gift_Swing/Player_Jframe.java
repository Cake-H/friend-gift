package friend_gift_Swing;

public class Player_Jframe {
    //储存游客信息

    private String id;
    private String password;

    Player_Jframe(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
