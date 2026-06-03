# 🚀 **KynosLib**

A lightweight, ultra-optimized, and feature-rich **core framework** designed for Bukkit/Paper plugins. Fully built on **Java 21** and tightly integrated with the native **Paper/Adventure API**.

---

## 🛠️ **Full Feature & Architecture Breakdown**

### 📦 **Core & File Management**
* 📁 **`KynosFile`** - Advanced custom **YAML configuration handler** with automated UTF-8 resource stream default injections and memory-safe save/reload mechanics.
* ⚙️ **`ModuleManager`** - Dynamic **runtime toggle system** for framework modules (`GuiManager`, `PluginDisabler`, `PluginEnabler`, `FileManager`), enforcing strict synchronization with local configurations.

### 🛡️ **Commands & Protection**
* 🎮 **`KynosCommand` & Framework** - Elegant command registration pipeline utilizing reflection to handle sub-commands, permissions, and tab-completions automatically.
* 🛑 **`CommandProtectionListener`** - Hardcoded high-priority pipeline ensuring that the core `/kynoslib` structure **bypasses early cancellations** from third-party management plugins.

### 👥 **Advanced Listeners**
* 🚪 **`PlayerJoinListener`** - Performance-driven join event handler leveraging the native Kyori Component flow to parse placeholders, colors, and configuration settings instantly.
* 🖥️ **`GuiListener`** - Specialized inventory interaction router that intercepts clicks and drags, passing validated inputs directly to active GUI holders.

### 🖥️ **Menu & GUI System**
* 🎨 **`KynosGui`** - Abstract UI structure with built-in item builders (`buildItem`), automated border generators (`fillBorder`), and empty-slot populators (`fillEmpty`).
* 📦 **`KynosLibTabCompleter` & `StopPluginTabCompleter`** - Optimized tab-completion fallbacks filtering sub-commands and active plugin lists based on sender permissions.

### ⚙️ **Utilities & API Bridges**
* 🌈 **`ColorUtils`** - High-speed translation engine supporting both **legacy codes** (`&`) and modern **HEX formatting** (`&#RRGGBB`) with optimized regex caching.
* ⏱️ **`CooldownManager`** - Thread-safe runtime cache handling player-specific action cooldowns with custom configurable message alerts.
* 🎵 **`SoundManager`** - Dynamic sound loading framework from config files with built-in **cross-version legacy fallbacks** to prevent sound crashes on older server setups.
* 🌍 **`KynosWorldGuard` & `WorldGuardHook`** - Completely decoupled API bridge designed to safely check regions and layout metrics **without classpath crashes** if WorldGuard is missing.

---

## 📦 **Dependency Setup (pom.xml)**

To implement **KynosLib** within your development workspace, add the following blocks to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>kynos-repo</id>
        <url>[https://repo.kynos.it/repository/maven-public/](https://repo.kynos.it/repository/maven-public/)</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>it.kynos</groupId>
        <artifactId>KynosLib</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
