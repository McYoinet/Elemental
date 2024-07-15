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
import net.palenquemc.elemental.utils.NameUtils;

public class Nickname implements TabExecutor {
    private Elemental plugin;

    public Nickname(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        NameUtils names = new NameUtils(plugin);
        
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        if(!sender.hasPermission("elmental.nickname")) {
            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
            
            return true;
        }

        switch(args.length) {
            // Cases: /nickname get {self} & /nickname clear {self}
            case 1 -> {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));

                    return true;
                }

                Player player = (Player) sender;
                
                switch(args[0]) {
                    case "get" -> {
                        if(!sender.hasPermission("elmental.nickname.get.self")) {
                            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                            
                            return true;
                        }

                        if(!names.hasNickname(player.getName())) {
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.get_nick.no_nickname.self")));

                            return true;
                        } else {
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.get_nick.get.self"),
                                Placeholder.parsed("nickname", names.getNickname(player.getName()))));

                            return true;
                        }
                    }

                    case "clear" -> {
                        if(!sender.hasPermission("elmental.nickname.set.self")) {
                            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                            
                            return true;
                        }

                        if(!names.hasNickname(player.getName())) {
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.get_nick.no_nickname.self")));

                            return true;
                        } else {
                            names.clearNickname(player.getName());
                            
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.clear.self")));

                            return true;
                        }
                    }

                    default -> {
                        sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.usage")));
                    }
                }
            }

            // Cases: /nickname set (nickname) {self} & /nickname get (player or nick) {other}
            case 2 -> {
                switch(args[0]) {
                    case "set" -> {
                        if(!sender.hasPermission("elmental.nickname.set.self")) {
                            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                            
                            return true;
                        }

                        if(!(sender instanceof Player)) {
                            sender.sendMessage(mm.deserialize(core.getString("core_module.executable_from_player")));
        
                            return true;
                        }
        
                        Player player = (Player) sender;
                        String nickname = args[1];

                        if(names.containsTags(nickname)) {
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.no_tags_allowed")));

                            return true; 
                        }

                        boolean success = names.setNickname(player.getName(), nickname);

                        if(success) {
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.set.self"), Placeholder.parsed("nickname", nickname)));
                        } else{
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.already_in_use"), Placeholder.parsed("nickname", nickname)));
                        }

                        return true;
                    }

                    case "get" -> {
                        if(!sender.hasPermission("elmental.nickname.get.others")) {
                            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                            
                            return true;
                        }

                        String target = args[1];
                        Player targetPlayer = plugin.getServer().getPlayer(target);

                        if(targetPlayer != null) {
                            if(names.hasNickname(target)) {
                                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.get_nick.get.other"), Placeholder.parsed("target_player", targetPlayer.getName()), Placeholder.parsed("nickname", names.getNickname(target))));
                                
                                return true;
                            } else {
                                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.get_nick.no_nickname.other"), Placeholder.parsed("target_player", targetPlayer.getName())));
                                
                                return true;
                            }
                        } else {
                            if(names.isNickname(target)) {
                                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.get_nick.get.other"), Placeholder.parsed("target_player", names.getRealName(target)), Placeholder.parsed("nickname", names.getNickname(target))));
                            
                                return true;
                            } else {
                                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.get_nick.get.not_in_use"), Placeholder.parsed("nickname", target)));
                            
                                return true;
                            }
                        }
                    }

                    case "clear" -> {
                        if(!sender.hasPermission("elmental.nickname.set.others")) {
                            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                            
                            return true;
                        }

                        String target = args[1];

                        if(names.hasNickname(target)) {
                            names.clearNickname(target);
                            
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.clear.to_other"), Placeholder.unparsed("target_player", args[1])));
                            
                            Player targetPlayer = plugin.getServer().getPlayer(target);
                            
                            if(targetPlayer != null) {
                                targetPlayer.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.clear.by_other"), Placeholder.unparsed("command_sender", sender.getName())));
                            }

                            return true;
                        } else {
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.get_nick.no_nickname.other"), Placeholder.parsed("target_player", target)));
                            
                            return true;
                        }
                    }
                }
            }

            case 3 -> {
                switch(args[0]) {
                    case "set" -> {
                        if(!sender.hasPermission("elmental.nickname.set.others")) {
                            sender.sendMessage(mm.deserialize(core.getString("core_module.insufficient_permissions")));
                            
                            return true;
                        }

                        String target = args[1];
                        String nick = args[2];

                        if(names.containsTags(nick)) {
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.no_tags_allowed")));

                            return true; 
                        }

                        Player targetPlayer = plugin.getServer().getPlayer(target);

                        if(targetPlayer == null && !playerControl.getBoolean("player_control_module.nickname.keep_nickname_after_disconnect")) {
                            sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.keep_disabled"), Placeholder.unparsed("target_player", target)));

                            return true;
                        }

                        names.setNickname(target, nick);

                        if(targetPlayer != null) {
                            targetPlayer.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.set.by_other"), Placeholder.parsed("command_sender", sender.getName()), Placeholder.parsed("nickname", nick)));
                        }

                        sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.set.to_other"), Placeholder.parsed("nickname", nick), Placeholder.unparsed("target_player", target)));
                    }

                    default -> {
                        sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.usage")));
                    
                        return true;
                    }
                }
            }

            default -> {
                sender.sendMessage(mm.deserialize(playerControl.getString("player_control_module.nickname.usage")));
            
                return true;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        switch(args.length) {
            case 1 -> {
                arguments.add("set");
                arguments.add("get");
                arguments.add("clear");
            }

            case 2 -> {
                arguments.add("[nickname]");
                
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    arguments.add(player.getName());
                });
            }

            case 3 -> {
                arguments.add("[nickname]");
            }
        }

        return arguments;
    }
}
