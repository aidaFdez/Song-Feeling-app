import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spotiexperiment.R

class FeelingAdapter (feelings: ArrayList<String>): RecyclerView.Adapter<FeelingAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val feelingView = itemView.findViewById<CheckBox>(R.id.checkBox_feeling)
    }

    private var feelings:ArrayList<String> = feelings

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        //Inflate the custom layout
        val feelingView = inflater.inflate(R.layout.feeling_card, parent, false)
        return ViewHolder(feelingView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feeling = feelings.get(position)
        val tvFeeling = holder.feelingView

        tvFeeling.setText(feeling)
    }

    override fun getItemCount(): Int {
        return feelings.size
    }
}