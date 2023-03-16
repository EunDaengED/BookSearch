package com.eundaeng.booksearch


import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.jsoup.Jsoup
import org.json.JSONArray
import org.json.JSONObject
import kotlin.arrayOf
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun sans(arr: JSONArray): ArrayList<ArrayList<String>> {
    val result: ArrayList<ArrayList<String>> = arrayListOf(arrayListOf("책이름", "대여", "책 번호", "http://vz.kro.kr/small.jpg"))
    for (i in 0 until arr.length()) {
            var b =  arr.getJSONObject(i)
            result.add(arrayListOf(b.getString("title").toString(),  b.getString("canRental").toString(), b.getString("callNumber").toString(), b.getString("previewImage").toString()))
        }
    return result
    //return "$result";
}

fun tost(Liist: ArrayList<Book>, re: ArrayList<ArrayList<String>>, adaptt: MainRvAdapter){
    Liist.clear()
    for (i in 0 until re.size) {
        Liist.add(Book(re[i][0], re[i][1], re[i][2], re[i][3]))
    }
    adaptt.notifyDataSetChanged()
}

class MainActivity : AppCompatActivity() {

    val list_of_items = arrayOf("강원", "서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종", "경기", "충북", "충남", "전북", "전남", "경북", "경남")

    var bookList = arrayListOf<Book>(
        Book("책이름", "대여", "책 번호", "http://vz.kro.kr/image/small.jpg"),
        Book("어린왕자", "가능", "ㄱ 5678", "http://vz.kro.kr/image/small.jpg")
    )

//    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mRecyclerView = findViewById<RecyclerView>(R.id.recyclerv)
        val mAdapter = MainRvAdapter(this, bookList)
        mRecyclerView.adapter = mAdapter

        val lm = LinearLayoutManager(this)
        mRecyclerView.layoutManager = lm
        mRecyclerView.setHasFixedSize(true)

        val school = findViewById<EditText>(R.id.school)
        val book = findViewById<EditText>(R.id.bookname)
        val local = findViewById<Spinner>(R.id.local)
        local.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_items)
        val button = findViewById<Button>(R.id.button)
        val textView = findViewById<TextView>(R.id.textview)

        button.setOnClickListener() {
            CoroutineScope(IO).launch {
                //var res:String? = "BookList"
                val parse = Jsoup.connect("http://vz.kro.kr/?book="+book.getText()+"&school="+school.getText()+"&local="+local.selectedItem)
                    .ignoreContentType(true).get().text()
                if (JSONObject(parse).getString("status") == "success") {
                    val bresult: JSONArray = JSONObject(parse).getJSONArray("result")
                    val ress = sans(bresult)
                   CoroutineScope(Main).launch  {
                        delay(50)
                        textView.setTextColor(Color.parseColor("#3fd467"))
                        textView.setText("성공")
                        tost(bookList, ress, mAdapter)
                    }
                } else {
                    val ress = JSONObject(parse).getString("result")
                    CoroutineScope(Main).launch {
                        delay(50)
                        bookList.clear()
                        bookList.add(Book("책이름", "대여", "책 번호", "http://vz.kro.kr/image/small.jpg"))
                        mAdapter.notifyDataSetChanged()
                        textView.setTextColor(Color.parseColor("#ff2424"))
                        textView.setText(ress)
                    }
                }
            }
        }
    }
}