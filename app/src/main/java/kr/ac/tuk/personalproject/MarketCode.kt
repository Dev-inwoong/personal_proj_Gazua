package kr.ac.tuk.personalproject

import com.google.gson.annotations.SerializedName


data class MarketCode (
    @SerializedName("market_warning")
    var market_warning : String,
    @SerializedName("market")
    var market : String,
    @SerializedName("korean_name")
    var korean_name : String,
    @SerializedName("english_name")
    var english_name : String

    )