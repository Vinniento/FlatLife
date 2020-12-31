package fh.wfp2.flatlife.ui.adapters

interface OnItemClickListener<T> {

    fun onItemClick(instance: T)
    fun onCheckBoxClick(instance: T, isChecked: Boolean)
}