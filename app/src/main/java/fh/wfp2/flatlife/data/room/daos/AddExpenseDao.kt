package fh.wfp2.flatlife.data.room.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.data.room.entities.mapping.ExpenseCategoryWithFinanceActivity

@Dao
interface AddExpenseDao : AbstractDao<FinanceActivity> {

}