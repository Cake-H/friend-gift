package friend_gift_JavaFX;

import java.util.ArrayList;
import java.util.List;

public interface Database {
    final List<Player> players = new ArrayList<>();//储存所有玩家的信息

    //添加玩家信息
    void add(Player player);

    //注销玩家信息
    void delete(Player player);

    //检索该账号是否存在
    int findPlayerId(String id);
}