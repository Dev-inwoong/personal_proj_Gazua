package kr.ac.tuk.personalproject

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class MainRvAdapter(val context : Context) : RecyclerView.Adapter<MainRvAdapter.Holder>(){
    var data : ArrayList<Ticker> = arrayListOf()
    private lateinit var binding : ViewDataBinding

    inner class Holder(itemView: ViewDataBinding) : RecyclerView.ViewHolder(itemView.root){
        val coinIcon = itemView.root.findViewById<ImageView>(R.id.coinIcon)
        val currencyPairView = itemView.root.findViewById<TextView>(R.id.currencyPairView)
        val currentPriceView = itemView.root.findViewById<TextView>(R.id.currentPriceView)
        val tradePrice24hView = itemView.root.findViewById<TextView>(R.id.tradePrice24hView)
        val changeRateView = itemView.root.findViewById<TextView>(R.id.changeRateView)
        val changePriceView = itemView.root.findViewById<TextView>(R.id.changePriceView)

        fun bind(item : Ticker, context : Context){
            if(item.photo != ""){
                val resourceId = context.resources.getIdentifier(item.photo, "drawble", context.packageName)
                coinIcon?.setImageResource(resourceId)
            }else{
                coinIcon?.setImageResource(R.mipmap.ic_launcher)
            }
            currencyPairView.text = item.code
            currentPriceView.text = item.trade_price.toString()
            tradePrice24hView.text = item.acc_trade_price_24h.toString()
            changeRateView.text = item.signed_change_rate.toString()
            changePriceView.text = item.change_price.toString()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), R.layout.main_rv_item, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(data[position],context)

    }


}