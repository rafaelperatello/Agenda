package br.edu.ifspsaocarlos.agenda.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import br.edu.ifspsaocarlos.agenda.R
import br.edu.ifspsaocarlos.agenda.model.Contact

class ContactArrayAdapter(activity: Activity, objects: List<Contact>) : ArrayAdapter<Contact?>(activity, R.layout.item_contact, objects) {

    private val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = inflater.inflate(R.layout.item_contact, parent, false)
            holder = ViewHolder(
                view.findViewById<View>(R.id.name) as TextView,
                view.findViewById<View>(R.id.phone) as TextView
            )
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        getItem(position)?.let {
            holder.name.text = it.name
            holder.phone.text = it.phone
        }

        return view!!
    }

    internal class ViewHolder(
        var name: TextView,
        var phone: TextView
    )
}