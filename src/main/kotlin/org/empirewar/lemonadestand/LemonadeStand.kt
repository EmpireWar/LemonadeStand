package org.empirewar.lemonadestand

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.empirewar.lemonadestand.discord.WebhookSender
import org.empirewar.lemonadestand.event.KoFiTransactionEvent
import org.empirewar.lemonadestand.logging.PrettyFormatter
import java.io.File
import java.net.MalformedURLException
import java.util.logging.FileHandler
import java.util.logging.Logger

class LemonadeStand : JavaPlugin(), Listener {

    lateinit var transactionLogger: Logger
        private set
    private lateinit var webServer: WebServer
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

        saveDefaultConfig()

        getLogger().info("Starting web server...")
        webServer = WebServer(this)
        webServer.start()
        getLogger().info("Web server started!")

        // Initialise webhook sending, if valid
        try {
            webhookSender = WebhookSender(this)
        } catch (e: MalformedURLException) {
            // There isn't a valid webhook
            getLogger().warning("Webhook URL was invalid: ${e.message}")
        }

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        webServer.stop()
        if (webhookSender == null) return
        webhookSender!!.stop()
    }

    @EventHandler
    fun onWebHookReceive(event: KoFiTransactionEvent) {
        if (webhookSender == null) return
        getLogger().info("Sending webhook...")
        webhookSender!!.sendWebhook(event)
    }

    companion object {

        private var INSTANCE: LemonadeStand? = null

        @JvmStatic
        fun get(): LemonadeStand {
            return INSTANCE!!
        }
    }
}
