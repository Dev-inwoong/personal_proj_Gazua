package kr.ac.tuk.personalproject

import com.google.gson.annotations.SerializedName
import java.util.*

data class Ticker(
    @SerializedName("code")
    var code : String,
    @SerializedName("change")
    var change : String,
    @SerializedName("change_price")
    var change_price : Double,
    @SerializedName("signed_change_rate")
    var signed_change_rate : Double,
    @SerializedName("acc_trade_price_24h")
    var acc_trade_price_24h : Double,
    @SerializedName("trade_price")
    var trade_price : Double,

    var photo : String,


    @Transient
    @SerializedName("stream_type")
    var stream_type : String,
    @Transient
    @SerializedName("market_warning")
    var market_warning : String,
    @Transient
    @SerializedName("delisting_date")
    var delisting_date : Date,
    @Transient
    @SerializedName("is_trading_suspended")
    var is_trading_suspended : Boolean,
    @Transient
    @SerializedName("market_state_for_ios")
    var market_state_for_ios : String,
    @Transient
    @SerializedName("market_state")
    var market_state : String,
    @Transient
    @SerializedName("trade_status")
    var trade_status : String,
    @Transient
    @SerializedName("acc_bid_volume")
    var acc_bid_volume : Double,
    @Transient
    @SerializedName("acc_ask_volume")
    var acc_ask_volume : Double,
    @Transient
    @SerializedName("ask_bid")
    var ask_bid : String,
    @Transient
    @SerializedName("trade_timestamp")
    var trade_timestamp : Long,
    @Transient
    @SerializedName("change_rate")
    var change_rate : String,
    @Transient
    @SerializedName("type")
    var type : String,
    @Transient
    @SerializedName("trade_date")
    var trade_date : String,
    @Transient
    @SerializedName("trade_time")
    var trade_time : String,
    @Transient
    @SerializedName("trade_date_kst")
    var trade_date_kst : String,
    @Transient
    @SerializedName("trade_time_kst")
    var trade_time_kst : String,
    @Transient
    @SerializedName("high_price")
    var high_price : Double,
    @Transient
    @SerializedName("low_price")
    var low_price : Double,
    @Transient
    @SerializedName("opening_price")
    var opening_price : Double,
    @Transient
    @SerializedName("prev_closing_price")
    var prev_closing_price : Double,
    @Transient
    @SerializedName("signed_change_price")
    var signed_change_price : Double,
    @Transient
    @SerializedName("trade_volume")
    var trade_volume : Double,
    @Transient
    @SerializedName("acc_trade_price")
    var acc_trade_price : Double,
    @Transient
    @SerializedName("acc_trade_volume")
    var acc_trade_volume : Double,
    @Transient
    @SerializedName("highest_52_week_price")
    var highest_52_week_price : Double,
    @Transient
    @SerializedName("acc_trade_volume_24h")
    var acc_trade_volume_24h : Double,
    @Transient
    @SerializedName("lowest_52_week_price")
    var lowest_52_week_price : Double,
    @Transient
    @SerializedName("highest_52_week_date")
    var highest_52_week_date : String,
    @Transient
    @SerializedName("lowest_52_week_date")
    var lowest_52_week_date : String,
    @Transient
    @SerializedName("timestamp")
    var timestamp : Long
)