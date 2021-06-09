package com.example.saveup.ui.form

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.saveup.R
import kotlinx.android.synthetic.main.fragment_expense.view.*
import java.nio.charset.Charset


class ExpenseFragment : Fragment() {
    private var expense_check = booleanArrayOf(false, false, false, false, false, false, false)
    private val expense_tags_array = arrayOf("car", "groceries","personal hygiene", "cleaning products", "clothes", "rent", "luxury" )
    private val expense_tags_database : MutableList<String> = ArrayList()
    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }


    private fun expenseTags(){

        var expense_count = 0
        while(expense_count < expense_check.size){
            expense_check[expense_count] = false
            expense_count++
        }
        expense_count = 0

        lateinit var expense_tags: AlertDialog


        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Choose tags")
        builder.setMultiChoiceItems(expense_tags_array, expense_check) { dialog, which, isChecked ->
            expense_check[which] = isChecked

        }
        builder.setPositiveButton("OK") { _, _ ->
            Toast.makeText(this.context,"Ok.",Toast.LENGTH_SHORT).show()
            var z = 0
            while(z < expense_tags_array.size){
                if(expense_check[z]){
                    expense_tags_database.add(expense_tags_array[z])
                }
            z++}
        }
        expense_tags = builder.create()
        expense_tags.show()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_expense, container, false)
        val expense_tag_button: View = root.findViewById(R.id.tags_button_expense)

        expense_tag_button.setOnClickListener{
            expenseTags()
        }



        val save_expense: View = root.findViewById(R.id.save_button_expense)

        save_expense.setOnClickListener{
            val type = "expense"
            val expense_text = root.euro_expense.text.toString()
            val date = root.date_input_field_expense.text.toString()
            val account = root.account_input_field_expense.text.toString()
            val category = root.category_input_field_expense.text.toString()
            val description = root.description_input_field_expense.text.toString()

            var z = 0
            var tags = ""
            while(z < expense_tags_database.size){
                if(z == expense_tags_database.size-1){
                    tags += expense_tags_database.get(z)

                }
                else{
                    tags += expense_tags_database.get(z) + ","
                }
                z++
            }
            Toast.makeText(this.context, tags, Toast.LENGTH_SHORT).show()
            val queue = Volley.newRequestQueue(this.context)
            val url = "https://saveup.weisl.cc/userdata"

            val requestBody = "type=" + type + "&amount=" + expense_text + "&date=" + date +
                    "&account=" + account + "&category=" + category +
                    "&description=" + description + "&tags=" + tags

            val stringReq : StringRequest =
                object : StringRequest(Method.POST, url,
                    Response.Listener { response ->
                        // response
                        val strResp = response.toString()

                        Log.d("API", strResp)
                        Toast.makeText(this.context, "Expense stored", Toast.LENGTH_SHORT).show()
                    },
                    Response.ErrorListener { error ->
                        Log.d("API", "error => $error")
                        Toast.makeText(this.context, "Storing failed", Toast.LENGTH_SHORT).show()
                    }
                ){
                    override fun getBody(): ByteArray {
                        return requestBody.toByteArray(Charset.defaultCharset())
                    }
                }
            queue.add(stringReq)

            root.euro_expense.text.clear()
            root.date_input_field_expense.text.clear()
            root.account_input_field_expense.text.clear()
            root.category_input_field_expense.text.clear()
            root.description_input_field_expense.text.clear()
        }


        return root
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): ExpenseFragment {
            return ExpenseFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }


}