package me.Negai.WGProtectorBlock.API;

import me.Negai.WGProtectorBlock.Main;

public class AchievementCommand {

    public String name;

    public AchievementCommand(String name) {
        this.name = name;
    }

    public int getRadius() {
        return Main.getInstance().getConfig().getInt("achieving-commands."+name+".radius");
    }

    public int getDelay() {
        return Main.getInstance().getConfig().getInt("achieving-commands."+name+".delay");
    }

    public String getDelayMessage(String timeLeft) {
        return Main.replaceTextColor(Main.getInstance().getConfig().getString("achieving-commands."+name+".delaymessage")).replace("%time_left%", timeLeft).replace("\\n", "\n");
    }

    public String getMessage() {
        return Main.replaceTextColor(Main.getInstance().getConfig().getString("achieving-commands."+name+".message")).replace("\\n", "\n");
    }

    public String getPermissionNode() {
        return Main.getInstance().getConfig().getString("achieving-commands."+name+".permission");
    }

}
