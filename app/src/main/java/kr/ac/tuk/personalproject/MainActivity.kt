package kr.ac.tuk.personalproject

import android.app.TabActivity
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import okhttp3.*
import okio.ByteString
import java.text.DecimalFormat

@Suppress("deprecation")
class MainActivity : TabActivity() {
    lateinit var goodsView : TextView
    lateinit var goodsbtn : ImageButton
    lateinit var textInputLayout: TextInputLayout
    lateinit var text_item : MaterialAutoCompleteTextView
    lateinit var inputbtn : Button

    lateinit var coinIcon : ImageView
    lateinit var currencyPairView : TextView
    lateinit var currentPriceView : TextView
    lateinit var tradePrice24hView : TextView
    lateinit var changeRateView : TextView
    lateinit var changePriceView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tabHost = this.tabHost

        var tabExchange = tabHost.newTabSpec("Exchange").setIndicator("코인조회")
        tabExchange.setContent(R.id.tabExchange)
        tabHost.addTab(tabExchange)

        var tabCoinInfo = tabHost.newTabSpec("CoinInfo").setIndicator("관심코인")
        tabCoinInfo.setContent(R.id.tabCoinInfo)
        tabHost.addTab(tabCoinInfo)

        var tabTopTrade = tabHost.newTabSpec("TotalPrice").setIndicator("Top거래")
        tabTopTrade.setContent(R.id.tabTopTrade)
        tabHost.addTab(tabTopTrade)

        var tabTotalPrice = tabHost.newTabSpec("TotalPrice").setIndicator("시가총액")
        tabTotalPrice.setContent(R.id.tabTotalPrice)
        tabHost.addTab(tabTotalPrice)

        tabHost.currentTab = 0

        goodsView = findViewById(R.id.goodsView)
        goodsbtn = findViewById(R.id.goodsbtn)

        goodsView.setOnClickListener{
            goodsbtn.callOnClick()
        }

        goodsbtn.setOnClickListener {
            var goodsMenu = PopupMenu(this, goodsView)
            menuInflater.inflate(R.menu.menu_select_goods, goodsMenu.menu)
            goodsMenu.setOnMenuItemClickListener{item ->
                when(item.itemId){
                    R.id.itemKRW -> goodsView.text = "KRW"
                    R.id.itemBTC -> goodsView.text = "BTC"
                    R.id.itemUSDT -> goodsView.text = "USDT"
                }
                false
            }
            goodsMenu.show()
            false
        }
        coinIcon = findViewById(R.id.coinIcon)
        currencyPairView = findViewById(R.id.currencyPairView)
        currentPriceView = findViewById(R.id.currentPriceView)
        tradePrice24hView = findViewById(R.id.tradePrice24hView)
        changeRateView = findViewById(R.id.changeRateView)
        changePriceView = findViewById(R.id.changePriceView)

        var market_items : ArrayList<String> = arrayListOf()
        var upbitmarketCode : ArrayList<String> = arrayListOf()
        runBlocking {
            GlobalScope.async {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.upbit.com/v1/market/all?isDetails=true")
                    .get()
                    .addHeader("Accept", "application/json")
                    .build()
                val response = client.newCall(request).execute()
                var responseStr = response.body!!.string()
                var responseParse = JsonParser().parse(responseStr)

                var upbitTotalMarket : ArrayList<MarketCode> = arrayListOf()
                for(i in 0 until responseParse.asJsonArray.size()){
                    upbitTotalMarket.add(Gson().fromJson(responseParse.asJsonArray[i],MarketCode::class.java))
                }
                for(i in 0 until upbitTotalMarket.size){
                    when{
                        upbitTotalMarket[i].market.contains("KRW-") -> market_items.add("${upbitTotalMarket[i].market.substring(4)} ${upbitTotalMarket[i].korean_name}")
                        upbitTotalMarket[i].market.contains("BTC-") -> market_items.add("${upbitTotalMarket[i].market.substring(4)} ${upbitTotalMarket[i].korean_name}")
                        upbitTotalMarket[i].market.contains("USDT-") -> market_items.add("${upbitTotalMarket[i].market.substring(5)} ${upbitTotalMarket[i].korean_name}")
                    }
                    upbitmarketCode.add(upbitTotalMarket[i].market)
                }
                val setfromlist = market_items.toSet()
                market_items = setfromlist.toList() as ArrayList<String>
            }.await()
        }
        textInputLayout = findViewById(R.id.textinputlayout)
        text_item = findViewById(R.id.text_item)
        val adapter = ArrayAdapter(this, R.layout.item_list, market_items)
        text_item.setAdapter(adapter)


        fun start(params : String){
            val listener : WebSocketListener = mWebSocketListener(params)
            GlobalScope.launch {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("wss://api.upbit.com/websocket/v1")
                    .build()
                client.newWebSocket(request, listener)
                client.dispatcher.executorService.shutdown()
            }
        }
        inputbtn = findViewById(R.id.inputbtn)
        inputbtn.setOnClickListener {
            var coinName : String? = text_item.text.split(" ")[0]
            var goods = goodsView.text.toString()
            var params = "${goods}-${coinName}"
            if(coinName.isNullOrEmpty()){
                Toast.makeText(applicationContext, "코인 이름을 입력해주세요!!", Toast.LENGTH_SHORT).show()
            }
            else if(!upbitmarketCode.contains(params)){
                Toast.makeText(applicationContext, "다른 화폐쌍을 골라주세요!!", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext, "반응이 없으면 다시 한 번 눌러보세요!", Toast.LENGTH_SHORT).show()
                start(params)
            }
        }
    }
    inner class mWebSocketListener(params: String): WebSocketListener() {
        lateinit var upbitTicker : Ticker
        var sendText = "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[${params}],\"isOnlyRealtime\": TRUE,\"isOnlySnapshot\": FALSE}]"

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            webSocket.send(sendText)
            Log.e("send", sendText)

        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            var responseStr = bytes.utf8()
            var responseParse = JsonParser().parse(responseStr)
            upbitTicker = Gson().fromJson(responseParse.asJsonObject, Ticker::class.java)
            Log.e("Ticker Received", upbitTicker.toString())

            when{
                upbitTicker.code.contains("KRW-") -> upbitTicker.photo = upbitTicker.code.substring(4).lowercase()
                upbitTicker.code.contains("BTC-") -> upbitTicker.photo = upbitTicker.code.substring(4).lowercase()
                upbitTicker.code.contains("USDT-") -> upbitTicker.photo = upbitTicker.code.substring(5).lowercase()
            }
            Log.e("Set photo", upbitTicker.photo.toString())

            val drawableResourceId: Int = getResources().getIdentifier(
                upbitTicker.photo, "drawable",
                getPackageName()
            ) // 동적 이미지 경로 세팅
            Log.e("imageResource", drawableResourceId.toString())

            val dec = DecimalFormat("#,###.#") // 가격 포맷
            val dec2 = DecimalFormat("#,###.##") // 등락률 포맷
            val dec3 = DecimalFormat("#,###") // 거래대금 포맷

            runOnUiThread{
                coinIcon.visibility = View.VISIBLE
                coinIcon.setImageResource(drawableResourceId) //코인 이미지
                Log.e("image set", "")
                currencyPairView.text = upbitTicker.code // 화폐쌍
                Log.e("code set", "")
                if(upbitTicker.change == "RISE"){ // 전일 대비 상승일때
                    currentPriceView.text = dec.format(upbitTicker.trade_price).toString() // 현재 가격
                    currentPriceView.setTextColor(0xffde5151.toInt()) // 텍스트 색 빨강

                    tradePrice24hView.text = dec3.format(upbitTicker.acc_trade_price_24h.toInt()).toString() + "억" // 거래 대금

                    changeRateView.text = dec2.format(upbitTicker.signed_change_rate*100).toString() + "%" //등락률
                    changeRateView.setTextColor(0xffde5151.toInt()) // 텍스트 색 빨강

                    changePriceView.text = dec.format(upbitTicker.change_price).toString() // 등락 금액
                    changePriceView.setTextColor(0xffde5151.toInt()) // 텍스트 색 빨강
                }
                else if(upbitTicker.change == "FALL"){ // 전일 대비 하락일때
                    currentPriceView.text = dec.format(upbitTicker.trade_price).toString() // 현재 가격
                    currentPriceView.setTextColor(0xff50bcdf.toInt()) // 텍스트 색 파랑

                    tradePrice24hView.text = dec3.format(upbitTicker.acc_trade_price_24h.toInt()).toString() + "억" // 거래 대금

                    changeRateView.text = dec2.format(upbitTicker.signed_change_rate*100).toString() + "%" //등락률
                    changeRateView.setTextColor(0xff50bcdf.toInt()) // 텍스트 색 파랑

                    changePriceView.text = dec.format(upbitTicker.change_price).toString() // 등락 금액
                    changePriceView.setTextColor(0xff50bcdf.toInt()) // 텍스트 색 파랑
                }
                else{ // 전일 대비 보합일때
                    currentPriceView.text = dec.format(upbitTicker.trade_price).toString() // 현재 가격
                    currentPriceView.setTextColor(0xffffffff.toInt()) // 텍스트 색 파랑

                    tradePrice24hView.text = dec3.format(upbitTicker.acc_trade_price_24h.toInt()).toString() + "억" // 거래 대금
                    currentPriceView.setTextColor(0xffffffff.toInt()) // 텍스트 색 파랑

                    changeRateView.text = dec2.format(upbitTicker.signed_change_rate*100).toString() + "%" //등락률

                    changePriceView.text = dec.format(upbitTicker.change_price).toString() // 등락 금액
                    currentPriceView.setTextColor(0xffffffff.toInt()) // 텍스트 색 파랑
                }
                Log.e("crypto info", "")
            }
            //webSocket.close(1000, null) // NORMAL_CLOSURE_STATUS: 1000, 없을 경우 끊임없이 서버와 통신
            //Log.e("websocket Closed", "")
        }
    }
}
