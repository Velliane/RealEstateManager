package com.openclassrooms.realestatemanager.controller.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R


class EditDialogFragment: DialogFragment() {

    private var content: String? = null

    companion object{
        fun newInstance(content: String): EditDialogFragment{

            val fragment = EditDialogFragment()
            val args = Bundle()
            args.putString("content", content)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments!!.getString("content")

        val style = STYLE_NORMAL
        val theme = R.style.AppTheme
        setStyle(style, theme)
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val builder = AlertDialog.Builder(activity)
//        builder.setTitle("Edit")
//        builder.setPositiveButton("Cool", object: DialogInterface.OnClickListener {
//            override fun onClick(dialog:DialogInterface, which:Int) {
//                dismiss()
//            }
//        })
//        builder.setNegativeButton("Cancel", object: DialogInterface.OnClickListener {
//            override fun onClick(dialog:DialogInterface, which:Int) {
//                dismiss()
//            }
//        })
//        return builder.create()
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.custom_edit_property, container, false)

        // Views //
        val editType = view.findViewById<View>(R.id.edit_type) as TextInputEditText

        return view
    }

}