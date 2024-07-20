package net.palenquemc.elemental.modules.playercontrol.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.ChatUtils;

public class Speed implements TabExecutor {
    private final Elemental plugin;
    
    public Speed(Elemental plugin) {
        this.plugin = plugin;
    }
    
    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        ChatUtils chat = new ChatUtils(plugin);

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String executableFromPlayer = chat.papi(player, core.getString("core_module.executable_from_player"));
        String invalidValue = chat.papi(player, playerControl.getString("player_control_module.speed.invalid_value"));
        String setFlyingSelf = chat.papi(player, playerControl.getString("player_control_module.speed.set.flying.self"));
        String setWalkingSelf = chat.papi(player, playerControl.getString("player_control_module.speed.set.walking.self"));
        String targetNotFound = chat.papi(player, core.getString("core_module.target_not_found"));
        String usage = chat.papi(player, playerControl.getString("player_control_module.speed.usage"));

        String setFlyingByOther = playerControl.getString("player_control_module.speed.set.flying.by_other");
        String setFlyingToOther = playerControl.getString("player_control_module.speed.set.flying.to_other");
        String setWalkingByOther = playerControl.getString("player_control_module.speed.set.walking.by_other");
        String setWalkingToOther = playerControl.getString("player_control_module.speed.set.walking.to_other");

        if(!sender.hasPermission("elmental.speed")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        switch(args.length) {
            case 1 -> {
                if(player == null) {
                    sender.sendMessage(mm.deserialize(executableFromPlayer));

                    return true;
                }

                float speed = (float) Double.parseDouble(args[0]);

                if(speed < 1 || speed > 10) {
                    sender.sendMessage(mm.deserialize(invalidValue));

                    return true;
                }

                float processedSpeed = speed / 10;

                if(player.isFlying()) {
                    player.setFlySpeed(processedSpeed);

                    player.sendMessage(mm.deserialize(setFlyingSelf, Placeholder.unparsed("amount", String.valueOf(speed))));

                    return true;
                } else {
                    player.setWalkSpeed(processedSpeed);

                    player.sendMessage(mm.deserialize(setWalkingSelf, Placeholder.unparsed("amount", String.valueOf(speed))));
                
                    return true;
                }
            }

            case 2 -> {
                float speed = (float) Double.parseDouble(args[0]);
                Player targetPlayer = plugin.getServer().getPlayer(args[1]);

                if(!sender.hasPermission("elemental.speed.others")) {
                    sender.sendMessage(mm.deserialize(noPerms));
                    
                    return true;
                }
                
                if(targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(targetNotFound));

                    return true;
                }

                if(speed < 1 || speed > 10) {
                    sender.sendMessage(mm.deserialize(invalidValue));

                    return true;
                }

                float processedSpeed = speed / 10;

                if(targetPlayer.isFlying()) {
                    targetPlayer.setFlySpeed(processedSpeed);

                    setFlyingByOther = chat.papi(targetPlayer, setFlyingByOther);
                    setFlyingToOther = chat.papi(targetPlayer, setFlyingToOther);

                    targetPlayer.sendMessage(mm.deserialize(setFlyingByOther, Placeholder.unparsed("command_sender", sender.getName()), Placeholder.unparsed("amount", String.valueOf(speed))));
                    sender.sendMessage(mm.deserialize(setFlyingToOther, Placeholder.unparsed("target_player", targetPlayer.getName()), Placeholder.unparsed("amount", String.valueOf(speed))));

                    return true;
                } else {
                    targetPlayer.setWalkSpeed(processedSpeed);

                    setWalkingByOther = chat.papi(targetPlayer, setWalkingByOther);
                    setWalkingToOther = chat.papi(targetPlayer, setWalkingToOther);

                    targetPlayer.sendMessage(mm.deserialize(setWalkingByOther, Placeholder.unparsed("command_sender", sender.getName()), Placeholder.unparsed("amount", String.valueOf(speed))));
                    sender.sendMessage(mm.deserialize(setWalkingToOther, Placeholder.unparsed("target_player", targetPlayer.getName()), Placeholder.unparsed("amount", String.valueOf(speed))));

                    return true;
                }
            }

            default -> {
                sender.sendMessage(mm.deserialize(usage));

                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            arguments.add("<amount>");
        } else if(args.length == 2) {
            arguments.add("[player]");

            plugin.getServer().getOnlinePlayers().forEach(player -> {
                arguments.add(player.getName());
            });
        }

        return arguments;
    }
}
