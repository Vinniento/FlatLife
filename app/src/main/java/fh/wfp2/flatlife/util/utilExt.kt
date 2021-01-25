package fh.wfp2.flatlife.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun <T> List<T>.getItemPositionByName(item: T): Int {
    this.forEachIndexed { index, it ->
        if (it == item)
            return index
    }
    return 0
}

fun <T> List<T>.getItemPositionByNameWithIndexVersion(item: T) = withIndex()
    .first { (_, value) -> item == value }
    .index


//not used but interesting
fun BottomNavigationView.hideLayout() {
    this.visibility = View.GONE
}

fun BottomNavigationView.showLayout() {
    this.visibility = View.VISIBLE
}