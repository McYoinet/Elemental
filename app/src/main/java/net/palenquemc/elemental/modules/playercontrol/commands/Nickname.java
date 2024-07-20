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
import net.palenquemc.elemental.utils.NameUtils;

public class Nickname implements TabExecutor {
    private final Elemental plugin;

    public Nickname(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        NameUtils names = new NameUtils(plugin);
        
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration playerControl = plugin.config.getConfig("player_control.yml");

        ChatUtils chat = new ChatUtils(plugin);

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String executableFromPlayer = chat.papi(player, core.getString("core_module.executable_from_player"));
        String noNickSelf = chat.papi(player, playerControl.getString("player_control_module.nickname.get_nick.no_nickname.self"));
        String getNickSelf = chat.papi(player, playerControl.getString("player_control_module.nickname.get_nick.get.self"));
        String clearSelf = chat.papi(player, playerControl.getString("player_control_module.nickname.clear.self"));
        String noNickInUse = chat.papi(player, playerControl.getString("player_control_module.nickname.get_nick.list.no_nicknames_are_being_used"));
        String listHeader = chat.papi(player, playerControl.getString("player_control_module.nickname.get_nick.list.list_header"));
        String usage = chat.papi(player, playerControl.getString("player_control_module.nickname.usage"));
        String nickMustNotBeNameSelf = chat.papi(player, playerControl.getString("player_control_module.nickname.nickname_must_not_be_name.self"));
        String noTagsAllowed = chat.papi(player, playerControl.getString("player_control_module.nickname.no_tags_allowed"));
        String nickSetSelf = chat.papi(player, playerControl.getString("player_control_module.nickname.set.self"));
        String nickInUse = chat.papi(player, playerControl.getString("player_control_module.nickname.already_in_use"));
        String notInUse = chat.papi(player, playerControl.getString("player_control_module.nickname.get_nick.get.not_in_use"));
        String keepDisabled = chat.papi(player, playerControl.getString("player_control_module.nickname.keep_disabled"));

        String noNickOther = playerControl.getString("player_control_module.nickname.get_nick.no_nickname.other");
        String nickGetOther = playerControl.getString("player_control_module.nickname.get_nick.get.other");
        String nickBelongsTo = playerControl.getString("player_control_module.nickname.get_nick.get.belongs_to");
        String clearToOther = playerControl.getString("player_control_module.nickname.clear.to_other");
        String clearByOther = playerControl.getString("player_control_module.nickname.clear.by_other");
        String nickMustNotBeNameOther = playerControl.getString("player_control_module.nickname.nickname_must_not_be_name.other");
        String nickSetByOther = playerControl.getString("player_control_module.nickname.set.by_other");
        String nickSetToOther = playerControl.getString("player_control_module.nickname.set.to_other");

        if(!sender.hasPermission("elmental.nickname")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        switch(args.length) {
            // Cases: /nickname get {self} & /nickname clear {self} & /nickname list
            case 1 -> {
                if(player == null) {
                    sender.sendMessage(mm.deserialize(executableFromPlayer));

                    return true;
                }
                
                switch(args[0]) {
                    case "get" -> {
                        if(!sender.hasPermission("elmental.nickname.get.self")) {
                            sender.sendMessage(mm.deserialize(noPerms));
                            
                            return true;
                        }

                        if(!names.hasNickname(player.getName())) {
                            sender.sendMessage(mm.deserialize(noNickSelf));

                            return true;
                        } else {
                            sender.sendMessage(mm.deserialize(getNickSelf,
                                Placeholder.parsed("nickname", names.getNickname(player.getName()))));

                            return true;
                        }
                    }

                    case "clear" -> {
                        if(!sender.hasPermission("elmental.nickname.set.self")) {
                            sender.sendMessage(mm.deserialize(noPerms));
                            
                            return true;
                        }

                        if(!names.hasNickname(player.getName())) {
                            sender.sendMessage(mm.deserialize(noNickSelf));

                            return true;
                        } else {
                            names.clearNickname(player.getName());
                            
                            sender.sendMessage(mm.deserialize(clearSelf));

                            return true;
                        }
                    }

                    case "list" -> {
                        if(!sender.hasPermission("elmental.nickname.get.list")) {
                            sender.sendMessage(mm.deserialize(noPerms));
                            
                            return true;
                        }

                        if(names.getNicknamesHashMap().isEmpty()) {
                            sender.sendMessage(mm.deserialize(noNickInUse));

                            return true;
                        }

                        sender.sendMessage(mm.deserialize(listHeader));

                        names.getNicknamesHashMap().forEach((playername, nickname) -> {
                            String entryFormat = chat.papi(plugin.getServer().getPlayer(playername), playerControl.getString("player_control_module.nickname.get_nick.list.list_entry"));

                            sender.sendMessage(mm.deserialize(entryFormat, Placeholder.unparsed("player", playername), Placeholder.parsed("nickname", nickname)));
                        });

                        String listFooter = playerControl.getString("player_control_module.nickname.get_nick.list.list_footer");

                        if(!listFooter.equals("")) {
                            sender.sendMessage(mm.deserialize(listFooter));
                        }
                    }

                    default -> {
                        sender.sendMessage(mm.deserialize(usage));
                    }
                }
            }

            // Cases: /nickname set (nickname) {self} & /nickname get (player or nick) {other}
            case 2 -> {
                switch(args[0]) {
                    case "set" -> {
                        if(!sender.hasPermission("elmental.nickname.set.self")) {
                            sender.sendMessage(mm.deserialize(noPerms));
                            
                            return true;
                        }

                        if(player == null) {
                            sender.sendMessage(mm.deserialize(executableFromPlayer));
        
                            return true;
                        }
        
                        String nickname = args[1];

                        if(nickname.equals(player.getName())) {
                            sender.sendMessage(mm.deserialize(nickMustNotBeNameSelf));
                        
                            return true;
                        }

                        if(names.containsTags(nickname)) {
                            sender.sendMessage(mm.deserialize(noTagsAllowed));

                            return true; 
                        }

                        boolean success = names.setNickname(player.getName(), nickname);

                        if(success) {
                            sender.sendMessage(mm.deserialize(nickSetSelf, Placeholder.parsed("nickname", nickname)));
                        } else{
                            sender.sendMessage(mm.deserialize(nickInUse, Placeholder.parsed("nickname", nickname)));
                        }

                        return true;
                    }

                    case "get" -> {
                        if(!sender.hasPermission("elmental.nickname.get.others")) {
                            sender.sendMessage(mm.deserialize(noPerms));
                            
                            return true;
                        }

                        String target = args[1];
                        Player targetPlayer = plugin.getServer().getPlayer(target);

                        if(targetPlayer != null) {
                            if(names.hasNickname(target)) {
                                nickGetOther = chat.papi(targetPlayer, nickGetOther);

                                sender.sendMessage(mm.deserialize(nickGetOther, Placeholder.parsed("target_player", targetPlayer.getName()), Placeholder.parsed("nickname", names.getNickname(target))));
                                
                                return true;
                            } else if(names.isNickname(target)) {
                                nickBelongsTo = chat.papi(plugin.getServer().getPlayer(names.getRealName(target)), nickBelongsTo);

                                sender.sendMessage(mm.deserialize(nickBelongsTo, Placeholder.parsed("target_player", names.getRealName(target)), Placeholder.parsed("nickname", names.getNickname(names.getRealName(target)))));
                            
                                return true;
                            } else {
                                noNickOther = chat.papi(targetPlayer, noNickOther);

                                sender.sendMessage(mm.deserialize(noNickOther, Placeholder.parsed("target_player", targetPlayer.getName())));
                                
                                return true;
                            }
                        } else {
                            if(names.isNickname(target)) {
                                nickBelongsTo = chat.papi(plugin.getServer().getPlayer(names.getRealName(target)), nickBelongsTo);

                                sender.sendMessage(mm.deserialize(nickBelongsTo, Placeholder.parsed("target_player", names.getRealName(target)), Placeholder.parsed("nickname", names.getNickname(names.getRealName(target)))));
                            
                                return true;
                            } else if(names.hasNickname(target)){
                                nickGetOther = chat.papi(plugin.getServer().getPlayer(target), nickGetOther);

                                sender.sendMessage(mm.deserialize(nickGetOther, Placeholder.parsed("target_player", target), Placeholder.parsed("nickname", names.getNickname(target))));
                                
                                return true;
                            } else {
                                sender.sendMessage(mm.deserialize(notInUse, Placeholder.parsed("nickname", target)));
                            
                                return true;
                            }
                        }
                    }

                    case "clear" -> {
                        if(!sender.hasPermission("elmental.nickname.set.others")) {
                            sender.sendMessage(mm.deserialize(noPerms));
                            
                            return true;
                        }

                        String target = args[1];

                        if(names.hasNickname(target)) {
                            String nickname = names.getNickname(target);
                            Player targetPlayer = plugin.getServer().getPlayer(target);

                            names.clearNickname(target);

                            clearToOther = chat.papi(targetPlayer, clearToOther);
                            
                            sender.sendMessage(mm.deserialize(clearToOther, Placeholder.unparsed("target_player", target), Placeholder.parsed("nickname", nickname)));
                            
                            if(targetPlayer != null) {
                                clearByOther = chat.papi(targetPlayer, clearByOther);

                                targetPlayer.sendMessage(mm.deserialize(clearByOther, Placeholder.unparsed("command_sender", sender.getName())));
                            }

                            return true;
                        } else if(names.isNickname(target)) {
                            String realname = names.getRealName(target);

                            Player targetPlayer = plugin.getServer().getPlayer(names.getRealName(target));

                            names.clearNickname(realname);

                            clearToOther = chat.papi(targetPlayer, clearToOther);
                            clearByOther = chat.papi(targetPlayer, clearByOther);

                            sender.sendMessage(mm.deserialize(clearToOther, Placeholder.unparsed("target_player", realname), Placeholder.unparsed("nickname", target)));

                            if(targetPlayer != null) {
                                targetPlayer.sendMessage(mm.deserialize(clearByOther, Placeholder.unparsed("command_sender", sender.getName())));
                            }

                            return true;
                        } else {
                            noNickOther = chat.papi(null, noNickOther);

                            sender.sendMessage(mm.deserialize(noNickOther, Placeholder.parsed("target_player", target)));
                            
                            return true;
                        }
                    }
                }
            }

            case 3 -> {
                switch(args[0]) {
                    case "set" -> {
                        if(!sender.hasPermission("elmental.nickname.set.others")) {
                            sender.sendMessage(mm.deserialize(noPerms));
                            
                            return true;
                        }

                        String target = args[1];
                        String nick = args[2];

                        if(target.equals(nick)) {
                            nickMustNotBeNameOther = chat.papi(plugin.getServer().getPlayer("target"), nickMustNotBeNameOther);

                            sender.sendMessage(mm.deserialize(nickMustNotBeNameOther));
                        
                            return true;
                        }

                        if(names.containsTags(nick)) {
                            sender.sendMessage(mm.deserialize(noTagsAllowed));

                            return true; 
                        }

                        Player targetPlayer = plugin.getServer().getPlayer(target);

                        if(targetPlayer == null && !playerControl.getBoolean("player_control_module.nickname.keep_nickname_after_disconnect")) {
                            sender.sendMessage(mm.deserialize(keepDisabled, Placeholder.unparsed("target_player", target)));

                            return true;
                        }

                        names.setNickname(target, nick);

                        if(targetPlayer != null) {
                            nickSetByOther = chat.papi(targetPlayer, nickSetByOther);

                            targetPlayer.sendMessage(mm.deserialize(nickSetByOther, Placeholder.parsed("command_sender", sender.getName()), Placeholder.parsed("nickname", nick)));
                        }

                        nickSetToOther = chat.papi(targetPlayer, nickSetToOther);

                        sender.sendMessage(mm.deserialize(nickSetToOther, Placeholder.parsed("nickname", nick), Placeholder.unparsed("target_player", target)));
                    }

                    default -> {
                        sender.sendMessage(mm.deserialize(usage));
                    
                        return true;
                    }
                }
            }

            default -> {
                sender.sendMessage(mm.deserialize(usage));
            
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
                arguments.add("[player]");
                
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