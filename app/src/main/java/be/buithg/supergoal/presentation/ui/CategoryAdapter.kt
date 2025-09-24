import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.R

class CategoryAdapter(
    private val items: List<String>,
    var selected: Int,
    private val onSelect: (pos: Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val cb: CheckBox = v.findViewById(R.id.cb)
        val tv: TextView = v.findViewById(R.id.tv)
        val divider: View = v.findViewById(R.id.divider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_single, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, position: Int) {
        h.tv.text = items[position]
        h.cb.isChecked = (position == selected)

        h.divider.visibility = if (position == items.lastIndex) View.GONE else View.VISIBLE

        h.itemView.setOnClickListener {
            if (selected != position) {
                val old = selected
                selected = position
                notifyItemChanged(old)
                notifyItemChanged(selected)
                onSelect(position)
            }
        }
    }

}
