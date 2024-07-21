package net.palenquemc.elemental.modules.servercontrol.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
        int entriesPerPage = serverControl.getInt("server_control_module.list.entries_per_page");
        String pageNotFound = chat.papi(player, serverControl.getString("server_control_module.list.page_not_found"));

        if(!sender.hasPermission("elmental.list")) {
            sender.sendMessage(mm.deserialize(noPerms));
            
            return true;
        }

        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();

        int pages = Math.ceilDivExact(players.size(), entriesPerPage);

        if(pages == 0) pages = 1;

        StringBuilder body = new StringBuilder();

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
                for(Iterator<? extends Player> it = players.stream().limit((long) entriesPerPage).iterator(); it.hasNext();) {
                    body.append(serializer.serialize(mm.deserialize(listEntry, Placeholder.unparsed("player", it.next().getName()))));

                    if(it.hasNext()) {
                        body.append(serializer.serialize(mm.deserialize(separator)));
                    }
                }

                sender.sendMessage(mm.deserialize(listHeader,
                    Placeholder.unparsed("page", "1"),
                    Placeholder.unparsed("total", String.valueOf(pages)),
                    Placeholder.unparsed("count", String.valueOf(players.size()))));

                sender.sendMessage(mm.deserialize(body.toString()));
                
                if(!listFooter.equals("")) {
                    sender.sendMessage(listFooter);
                }

                return true;
            }

            case 1 -> {
                int page = Integer.parseInt(args[0]);

                if(page > pages || page < 1) {
                    sender.sendMessage(mm.deserialize(pageNotFound));

                    return true;
                }

                for(Iterator<? extends Player> it = players.stream().skip((long) entriesPerPage * page).limit((long) entriesPerPage).iterator(); it.hasNext();) {
                    body.append(serializer.serialize(mm.deserialize(listEntry, Placeholder.unparsed("player", it.next().getName()))));

                    if(it.hasNext()) {
                        body.append(serializer.serialize(mm.deserialize(separator)));
                    }
                }

                sender.sendMessage(mm.deserialize(listHeader,
                    Placeholder.unparsed("page", "1"),
                    Placeholder.unparsed("total", String.valueOf(pages)),
                    Placeholder.unparsed("count", String.valueOf(players.size()))));

                sender.sendMessage(mm.deserialize(body.toString()));
                
                if(!listFooter.equals("")) {
                    sender.sendMessage(listFooter);
                }

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
}
