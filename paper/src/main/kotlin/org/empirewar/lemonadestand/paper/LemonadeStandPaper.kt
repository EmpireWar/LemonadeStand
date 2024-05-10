package org.empirewar.lemonadestand.paper

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.empirewar.lemonadestand.LemonadeStand
import org.empirewar.lemonadestand.WebServer
import org.empirewar.lemonadestand.discord.WebhookSender
import org.empirewar.lemonadestand.event.EventCaller
import org.empirewar.lemonadestand.logging.PrettyFormatter
import org.empirewar.lemonadestand.paper.event.KoFiTransactionEvent
import org.empirewar.lemonadestand.paper.event.PaperEventCaller
import org.empirewar.lemonadestand.paper.scheduler.PaperScheduler
import org.empirewar.lemonadestand.scheduler.PlatformScheduler
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger

class LemonadeStandPaper: JavaPlugin(), LemonadeStand<OfflinePlayer>, Listener {

    lateinit var transactionLogger: Logger
        private set
    private lateinit var webServer: WebServer<OfflinePlayer>
    private var webhookSender: WebhookSender? = null

    override fun onEnable() {
        // Plugin startup logic
        INSTANCE = this

        try {
            val logsFolder = File(dataFolder.toString() + File.separator + "logs")
            if (!logsFolder.exists()) {
                logsFolder.mkdirs()
            }

            val logFile = File(logsFolder.toString() + File.separator + "transactions.log")
            if (!logFile.exists()) {
                logFile.createNewFile()
            }

            transactionLogger = Logger.getLogger("TransactionLogger")
            val fileHandler = FileHandler(logFile.path, true)
            fileHandler.formatter = PrettyFormatter()
            transactionLogger.addHandler(fileHandler)
            transactionLogger.useParentHandlers = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.loadConfig()

        logger().info("Starting web server...")
        webServer = WebServer(this)
        webServer.start()
        logger().info("Web server started!")

        // Initialise webhook sending, if valid
        try {
            webhookSender = WebhookSender(this)
        } catch (e: MalformedURLException) {
            // There isn't a valid webhook
            logger().warning("Webhook URL was invalid: ${e.message}")
        }

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        webServer.stop()
        if (webhookSender == null) return
        webhookSender!!.stop()
    }

    private lateinit var loader: ConfigurationLoader<CommentedConfigurationNode>
    private lateinit var rootNode: ConfigurationNode

    private fun loadConfig() {
        try {
            val configPath: File = dataFolder.resolve("config.yml")
            saveResource("config.yml", false)

            loader = YamlConfigurationLoader.builder().path(configPath.toPath()).build()
            rootNode = loader.load()
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Error loading config", e)
        }
    }

    @EventHandler
    fun onWebHookReceive(event: KoFiTransactionEvent) {
        webhookSender?.apply {
            logger().info("Sending webhook...")
            sendEmbed(event.player.name!!, event.player.uniqueId, event.shopOrder)
        }
    }

    companion object {

        private var INSTANCE: LemonadeStand<OfflinePlayer>? = null

        @JvmStatic
        fun get(): LemonadeStand<OfflinePlayer> {
            return INSTANCE!!
        }
    }

    override fun getOfflinePlayer(username: String): OfflinePlayer? {
        return Bukkit.getOfflinePlayerIfCached(username)
    }

    override fun getUsername(player: OfflinePlayer): String {
        return player.name!!
    }

    private val eventCaller: EventCaller<OfflinePlayer> = PaperEventCaller(this)

    override fun eventCaller(): EventCaller<OfflinePlayer> {
        return eventCaller
    }

    private val scheduler: PlatformScheduler = PaperScheduler(this)

    override fun scheduler(): PlatformScheduler {
        return scheduler
    }

    override fun config(): ConfigurationNode {
        return rootNode
    }

    override fun logger(): Logger {
        return logger
    }

    override fun transactionLogger(): Logger {
        return transactionLogger
    }
}