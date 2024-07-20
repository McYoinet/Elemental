package net.palenquemc.elemental.modules.servercontrol.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.palenquemc.elemental.Elemental;
import net.palenquemc.elemental.utils.ChatUtils;

public class ListCommand implements TabExecutor {
    private final Elemental plugin;

    public ListCommand(Elemental plugin) {
        this.plugin = plugin;
    }

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration core = plugin.config.getConfig("core.yml");
        FileConfiguration serverControl = plugin.config.getConfig("server_control.yml");

        ChatUtils chat = new ChatUtils(plugin);

        Player player = null;
        if(sender instanceof Player p) player = p;

        String noPerms = chat.papi(player, core.getString("core_module.insufficient_permissions"));
        String usage = chat.papi(player, serverControl.getString("server_control_module.list.usage"));
        String listHeader = chat.papi(player, serverControl.getString("server_control_module.list.list_header"));
        String listEntry = chat.papi(player, serverControl.getString("server_control_module.list.list_entry"));
        String separator = chat.papi(player, serverControl.getString("server_control_module.list.separator"));
        String listFooter = chat.papi(player, serverControl.getString("server_control_module.list.list_footer"));
        String noPlayers = chat.papi(player, serverControl.getString("server_control_module.list.no_players_connected"));

        if(!sender.hasPermission("elmental.list")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();

        StringBuilder bodySerialized = new StringBuilder();

        MiniMessage serializer = MiniMessage.builder()
                    .tags(TagResolver.builder()
                    .resolver(StandardTags.color())
                    .build())
                    .build();

        switch(args.length) {
            default -> {
                sender.sendMessage(mm.deserialize(usage));
            
                return true;
            }

            case 0 -> {
                if(players.isEmpty()) {
                    sender.sendMessage(mm.deserialize(noPlayers));

                    return true;
                }

                List<List<Player>> playerLists = countAllPlayers();

                for(int i = 0; i < playerLists.get(0).size(); ++i) {
                    bodySerialized.append(serializer.serialize(mm.deserialize(listEntry, Placeholder.unparsed("player", playerLists.get(0).get(i).getName()))));

                    if(i != playerLists.get(0).size() - 1) {
                        bodySerialized.append(serializer.serialize(mm.deserialize(separator)));
                    }
                }

                sender.sendMessage(mm.deserialize(listHeader, Placeholder.unparsed("page", "1"), Placeholder.unparsed("total", String.valueOf(playerLists.size())), Placeholder.unparsed("count", String.valueOf(plugin.getServer().getOnlinePlayers().size()))));
                sender.sendMessage(mm.deserialize(bodySerialized.toString()));
                
                if(!listFooter.equals("")) {
                    sender.sendMessage(listFooter);
                }
                
                return true;
            }

            case 1 -> {
                
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<>();

        if(args.length == 1) {
            arguments.add("[page]");
        }

        return arguments;
    }

    List<List<Player>> countAllPlayers() { // Now this makes so much more sense, how didn't I think of this before is beyond me
        List<Player> playersAsList = new ArrayList<>();
        plugin.getServer().getOnlinePlayers().forEach(p -> playersAsList.add(p));

        List<List<Player>> totalPlayers = new ArrayList<>();

        totalPlayers.add(new ArrayList<>());

        int playerIndex = 0;
        int pageIndex = 0;

        FileConfiguration serverControl = plugin.config.getConfig("server_control.yml");

        int entriesPerPage = serverControl.getInt("server_control_module.list.entries_per_page");

        while(playerIndex < playersAsList.size()) {
            if(totalPlayers.get(pageIndex).size() <= entriesPerPage) {
                totalPlayers.get(pageIndex).add(playersAsList.get(playerIndex));
            } else {
                totalPlayers.add(new ArrayList<>());
                ++pageIndex;

                totalPlayers.get(pageIndex).add(playersAsList.get(playerIndex));
            }

            ++playerIndex;
        }

        return totalPlayers;
    }
}
