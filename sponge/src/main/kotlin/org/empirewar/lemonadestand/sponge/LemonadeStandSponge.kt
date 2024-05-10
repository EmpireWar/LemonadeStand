package org.empirewar.lemonadestand.sponge

import com.google.inject.Inject
import org.empirewar.lemonadestand.LemonadeStand
import org.empirewar.lemonadestand.WebServer
import org.empirewar.lemonadestand.discord.WebhookSender
import org.empirewar.lemonadestand.event.EventCaller
import org.empirewar.lemonadestand.logging.PrettyFormatter
import org.empirewar.lemonadestand.scheduler.PlatformScheduler
import org.empirewar.lemonadestand.sponge.event.KoFiTransactionEvent
import org.empirewar.lemonadestand.sponge.event.SpongeEventCaller
import org.empirewar.lemonadestand.sponge.scheduler.SpongeScheduler
import org.spongepowered.api.Server
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.lifecycle.StartingEngineEvent
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import org.spongepowered.plugin.PluginContainer
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger

class LemonadeStandSponge @Inject constructor(
    pluginContainer: PluginContainer,
    @ConfigDir(sharedRoot = false)
    private val dataFolder: Path,
    private val logger: Logger
) : LemonadeStand<User> {

    lateinit var transactionLogger: Logger
        private set
    private lateinit var webServer: WebServer<User>
    private var webhookSender: WebhookSender? = null

    private var pluginContainer: PluginContainer? = pluginContainer

    fun pluginContainer(): PluginContainer? {
        return pluginContainer
    }

    @Listener
    fun onStarting(event: StartingEngineEvent<Server>) {
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
    }


    @Listener
    fun onStopping(event: StoppingEngineEvent<Server>) {
        // Plugin shutdown logic
        webServer.stop()
        if (webhookSender == null) return
        webhookSender!!.stop()
    }

    private lateinit var loader: ConfigurationLoader<CommentedConfigurationNode>
    private lateinit var rootNode: ConfigurationNode

    private fun loadConfig() {
        try {
            val configPath: Path = dataFolder.resolve("config.yml")
            try {
                Files.copy(
                    pluginContainer!!.openResource(URI.create("/config.yml")).orElseThrow(),
                    configPath
                )
            } catch (ignored: FileAlreadyExistsException) {
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            loader = YamlConfigurationLoader.builder().path(configPath).build()
            rootNode = loader.load()
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Error loading config", e)
        }
    }

    @Listener
    fun onWebHookReceive(event: KoFiTransactionEvent) {
        webhookSender?.apply {
            logger().info("Sending webhook...")
            sendEmbed(event.player.name(), event.player.uniqueId(), event.shopOrder)
        }
    }

    companion object {

        private var INSTANCE: LemonadeStand<User>? = null

        @JvmStatic
        fun get(): LemonadeStand<User> {
            return INSTANCE!!
        }
    }

    override fun getOfflinePlayer(username: String): User? {
        return Sponge.server().userManager().load(username).join().orElse(null)
    }

    override fun getUsername(player: User): String {
        return player.name()
    }

    private val eventCaller: EventCaller<User> = SpongeEventCaller(this)

    override fun eventCaller(): EventCaller<User> {
        return eventCaller
    }

    private val scheduler: PlatformScheduler = SpongeScheduler(this)

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