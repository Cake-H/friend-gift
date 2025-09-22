package friend_gift_Swing;

import java.util.ArrayList;
import java.util.List;

public interface Database_Jframe {
    final List<Player_Jframe> players = new ArrayList<>();//储存所有玩家的信息

    //添加玩家信息
    void add(Player_Jframe player);

    //注销玩家信息
    void delete(Player_Jframe player);

    //检索该账号是否存在
    int findPlayerId(String id);
}