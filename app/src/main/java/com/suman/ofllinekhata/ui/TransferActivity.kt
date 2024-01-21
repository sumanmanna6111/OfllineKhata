package com.suman.ofllinekhata.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.ByteBufferList
import com.koushikdutta.async.DataEmitter
import com.koushikdutta.async.callback.CompletedCallback
import com.koushikdutta.async.callback.DataCallback
import com.koushikdutta.async.callback.ListenCallback
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback
import com.koushikdutta.async.http.WebSocket
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.databinding.ActivityTransferBinding
import com.suman.ofllinekhata.repository.TransferRepository
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.room.entity.TransactionEntity
import com.suman.ofllinekhata.viewmodel.CustomerViewModel
import com.suman.ofllinekhata.viewmodel.TransferViewModel
import com.suman.ofllinekhata.viewmodelfactory.TransferViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.Executor


class TransferActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransferBinding
    private lateinit var viewModel: TransferViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer)
        val database = AppDatabase.getDataBase(applicationContext)
        val repository = TransferRepository(database)
        viewModel = ViewModelProvider(
            this,
            TransferViewModelFactory(repository)
        )[TransferViewModel::class.java]

        binding.transferVm = viewModel
        binding.lifecycleOwner = this

        val wifiMgr = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        val ipAddress: String = Formatter.formatIpAddress(wifiInfo.ipAddress)
        val host = Formatter.formatIpAddress(wifiMgr.dhcpInfo.gateway)
        binding.textView2.text = host
        generateQR(host)


        binding.btnSend.setOnClickListener {
            if (!wifiMgr.isWifiEnabled) {
                createServer()
                binding.btnReceive.isEnabled = false
                binding.btnSend.isEnabled = false
            } else {
                Toast.makeText(this, "Turn on WiFi Hotspot", Toast.LENGTH_SHORT).show()
            }
        }
        binding.imageView.setOnClickListener {

        }
        binding.btnReceive.setOnClickListener {
            if (wifiMgr.isWifiEnabled) {
                connect(host)
            } else {
                Toast.makeText(this, "Enable WIFI", Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun generateQR(text: String) {
        val windowManager: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        val point: Point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        var dimen = if (width < height) width else height
        dimen = dimen * 4 / 4
        val qrEncoder = QRGEncoder(text, null, QRGContents.Type.TEXT, dimen)
        qrEncoder.colorWhite = Color.BLACK
        qrEncoder.colorBlack = Color.WHITE
        try {
            val bitmap = qrEncoder.bitmap
            binding.imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createServer() {
        val server = AsyncHttpServer()
        val _sockets = ArrayList<WebSocket>()
        /*server["/", HttpServerRequestCallback { request, response ->
            response.send(JSONObject().put("key", "value")) }]
        server.listen(5000);*/

        server.listen(AsyncServer.getDefault(), 5000)
        server.websocket("/ws", AsyncHttpServer.WebSocketRequestCallback { webSocket, request ->

            //Log.d("TAG", "createServer r: $request")
            _sockets.add(webSocket)

            webSocket.closedCallback = CompletedCallback { ex ->
                try {
                    Log.d("TAG", "createServer: closedCallback")
                    if (ex != null) Log.e("WebSocket", "An error occurred", ex)
                } finally {
                    _sockets.remove(webSocket)
                }
            }

            webSocket.stringCallback = WebSocket.StringCallback { s ->
                Log.d("TAG", "createServer: stringCallback")
                if ("customer" == s) {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel._status.postValue("Transferring ...")

                        val customers = viewModel.getAllCustomer()
                        for (customer in customers) {
                            val jsonObject = JSONObject()
                            jsonObject.put("table", "customer")
                            jsonObject.put("name", customer.name)
                            jsonObject.put("number", customer.number)
                            jsonObject.put("amount", customer.amount)
                            jsonObject.put("time", customer.time)
                            webSocket.send(jsonObject.toString())
                            delay(100)
                        }
                    }

                } else if ("transaction" == s) {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel._status.postValue("Transferring ...")
                        val transactions = viewModel.getAllTransaction()
                        for (transaction in transactions) {
                            val jsonObject = JSONObject()
                            jsonObject.put("table", "transaction")
                            jsonObject.put("uid", transaction.uid)
                            jsonObject.put("amount", transaction.amount)
                            jsonObject.put("description", transaction.description)
                            jsonObject.put("type", transaction.type)
                            jsonObject.put("received", transaction.received)
                            jsonObject.put("clear", transaction.clear)
                            jsonObject.put("time", transaction.time)
                            webSocket.send(jsonObject.toString())
                            delay(100)
                        }
                        viewModel._status.postValue("Transfer Completed")
                        webSocket.send(JSONObject().put("table", "finished").toString())
                    }

                }


            }

            webSocket.dataCallback = DataCallback { emitter, bb ->
                Log.d("TAG", "createServer: dataCallback")
            }
            webSocket.setClosedCallback {
                Log.d("TAG", "createServer: setClosedCallback")
            }
            webSocket.setEndCallback { ex ->
                Log.d("TAG", "end callback: $ex")
            }

            /*webSocket.setStringCallback { s ->
                if ("Hello Server" == s) webSocket.send("Welcome Client!")
            }*/
        })
    }

    private fun connect(host: String) {
        Log.d("TAG", "ws://$host:5000/ws")
        AsyncHttpClient.getDefaultInstance().websocket("ws://$host:5000/ws", null,
            WebSocketConnectCallback { ex, webSocket ->
                if (ex != null) {
                    ex.printStackTrace()
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            this@TransferActivity,
                            "Connection Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@WebSocketConnectCallback
                } else {
                    runOnUiThread {
                        binding.btnSend.isEnabled = false
                        binding.btnReceive.isEnabled = false
                    }

                }
                webSocket.send("customer")
                webSocket.send("transaction")

                webSocket.setStringCallback {
                    try {
                        viewModel._status.postValue("Receiving ...")
                        val jsonObject = JSONObject(it)
                        if (jsonObject.getString("table").equals("customer")) {
                            val obj = CustomerEntity(
                                0,
                                jsonObject.getString("name"),
                                jsonObject.getString("number"),
                                jsonObject.getInt("amount").toFloat(),
                                jsonObject.getLong("time")
                            )
                            viewModel.insertCustomer(obj)
                        } else if (jsonObject.getString("table").equals("transaction")) {
                            val obj = TransactionEntity(
                                0,
                                jsonObject.getInt("uid"),
                                jsonObject.getInt("type"),
                                if (jsonObject.isNull("desc")) null else jsonObject.getString("desc"),
                                jsonObject.getInt("amount").toFloat(),
                                jsonObject.getInt("received"),
                                if (jsonObject.has("clear")) jsonObject.getLong("clear") else null,
                                jsonObject.getLong("time")
                            )
                            viewModel.insertTransaction(obj)
                        } else if (jsonObject.getString("table").equals("finished")) {
                            viewModel._status.postValue("Transfer Completed")
                        }


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }


                }

                webSocket.setDataCallback { emitter, bb ->
                    Log.d("TAG", "connect : setDataCallback: $emitter")
                    bb.recycle()
                }

                webSocket.setClosedCallback {
                    Log.d("TAG", "connect: setClosedCallback")
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.btnSend.isEnabled = true
                        binding.btnReceive.isEnabled = true
                        Toast.makeText(
                            this@TransferActivity,
                            "Connection Close",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                webSocket.setEndCallback {
                    Log.d("TAG", "connect: setEndCallback")
                }
            })
    }


}