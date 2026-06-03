package it.kynos.kynoslib.commands;

import it.kynos.kynoslib.utils.ColorUtils;
import it.kynos.kynoslib.utils.MessageUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Base abstract class for all commands using the KynosLib framework.
 * Optimized with modern Java 21 features and precise collection sizing.
 */
public abstract class KynosCommand implements CommandExecutor, TabCompleter {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface SubCommand {
        String name();
        String permission() default "";
        boolean playerOnly() default false;
        String[] tabArgs() default {};
    }

    protected final JavaPlugin plugin;
    protected final MessageUtils msg;
    private final String commandName;

    private String requiredPermission = null;
    private boolean onlyPlayers = false;

    private final Map<String, Method> subCommands = new LinkedHashMap<>();
    private final Map<String, SubCommand> subMeta = new LinkedHashMap<>();

    public KynosCommand(JavaPlugin plugin, String commandName) {
        this.plugin = plugin;
        this.commandName = commandName;
        this.msg = new MessageUtils(plugin);
        scanSubCommands();
        register();
    }

    protected KynosCommand permission(String permission) {
        this.requiredPermission = permission;
        return this;
    }

    protected KynosCommand requirePlayer(boolean value) {
        this.onlyPlayers = value;
        return this;
    }

    protected boolean execute(CommandSender sender, String[] args) {
        if (!this.subCommands.isEmpty()) {
            sender.sendMessage(ColorUtils.translateToString("&7Available sub-commands: &e" + String.join("&7, &e", this.subCommands.keySet())));
        }
        return true;
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (this.requiredPermission != null && !sender.hasPermission(this.requiredPermission)) {
            this.msg.sendNoPermission(sender);
            return true;
        }
        if (this.onlyPlayers && !(sender instanceof Player)) {
            this.msg.sendPlayerOnly(sender);
            return true;
        }

        if (args.length > 0) {
            final String sub = args[0].toLowerCase();
            final Method method = this.subCommands.get(sub);
            if (method != null) {
                final SubCommand meta = this.subMeta.get(sub);

                if (!meta.permission().isEmpty() && !sender.hasPermission(meta.permission())) {
                    this.msg.sendNoPermission(sender);
                    return true;
                }
                if (meta.playerOnly() && !(sender instanceof Player)) {
                    this.msg.sendPlayerOnly(sender);
                    return true;
                }

                try {
                    final String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                    return (boolean) method.invoke(this, sender, (Object) subArgs);
                } catch (Exception e) {
                    this.plugin.getLogger().severe("Error executing sub-command '" + sub + "': " + e.getMessage());
                    e.printStackTrace();
                }
                return true;
            }
        }

        return execute(sender, args);
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // Pre-size collection capacity to reduce memory allocation footprint
            final List<String> completions = new ArrayList<>(this.subMeta.size());
            final String input = args[0].toLowerCase();

            for (Map.Entry<String, SubCommand> entry : this.subMeta.entrySet()) {
                final String sub = entry.getKey();
                final SubCommand meta = entry.getValue();
                if (!sub.startsWith(input)) continue;
                if (!meta.permission().isEmpty() && !sender.hasPermission(meta.permission())) continue;
                completions.add(sub);
            }

            final List<String> custom = tabComplete(sender, args);
            if (custom != null) completions.addAll(custom);
            return completions;
        }

        if (args.length >= 2) {
            final String sub = args[0].toLowerCase();
            final SubCommand meta = this.subMeta.get(sub);
            if (meta != null) {
                final String[] tabArgs = meta.tabArgs();
                final int argIndex = args.length - 2;
                if (argIndex < tabArgs.length) {
                    final String hint = tabArgs[argIndex];

                    if (!hint.startsWith("<") && !hint.startsWith("[")) {
                        final String input = args[args.length - 1].toLowerCase();
                        return Arrays.stream(hint.split("\\|"))
                                .filter(v -> v.toLowerCase().startsWith(input))
                                .toList(); // Java 16+ unmodifiable list optimization
                    }
                    return List.of(hint); // Low-overhead single element list
                }
            }
            final List<String> custom = tabComplete(sender, args);
            return custom != null ? List.copyOf(custom) : List.of();
        }

        return List.of();
    }

    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    private void scanSubCommands() {
        for (final Method method : this.getClass().getDeclaredMethods()) {
            final SubCommand ann = method.getAnnotation(SubCommand.class);
            if (ann == null) continue;

            final Class<?>[] params = method.getParameterTypes();
            if (params.length != 2
                    || !CommandSender.class.isAssignableFrom(params[0])
                    || !String[].class.equals(params[1])) {
                this.plugin.getLogger().warning("[KynosCommand] @SubCommand '" + method.getName()
                        + "' has an invalid signature. Expected: boolean method(CommandSender, String[])");
                continue;
            }

            method.setAccessible(true);
            final String name = ann.name().toLowerCase();
            this.subCommands.put(name, method);
            this.subMeta.put(name, ann);
        }
    }

    private void register() {
        final PluginCommand cmd = this.plugin.getCommand(this.commandName);
        if (cmd == null) {
            this.plugin.getLogger().severe("[KynosCommand] Command '" + this.commandName
                    + "' not found in plugin.yml! Please declare it under the 'commands' section.");
            return;
        }
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    protected final boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    protected final Player asPlayer(CommandSender sender) {
        return (sender instanceof Player p) ? p : null; // Java 14+ Pattern Matching
    }

    protected final Set<String> getSubCommandNames() {
        return Collections.unmodifiableSet(this.subCommands.keySet());
    }
}